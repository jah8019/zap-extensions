/*
 * Zed Attack Proxy (ZAP) and its related class files.
 *
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2015 The ZAP Development Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zaproxy.addon.grpc.internal.fuzz;

import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.addon.grpc.internal.EncoderUtils;
import org.zaproxy.addon.grpc.internal.InvalidProtobufFormatException;
import org.zaproxy.zap.extension.fuzz.httpfuzzer.HttpFuzzResult;
import org.zaproxy.zap.extension.fuzz.httpfuzzer.HttpFuzzerMessageProcessor;
import org.zaproxy.zap.extension.fuzz.httpfuzzer.HttpFuzzerTaskProcessorUtils;
import org.zaproxy.addon.grpc.internal.ProtoBufMessageEncoder;

import java.io.IOException;
import java.util.Base64;

public class GrpcProcessor implements HttpFuzzerMessageProcessor {

    public static final String NAME = "A toll, grpc";
            //ToDo:
            //Constant.messages.getString(
            //        "fuzz.httpfuzzer.processor.requestContentLengthUpdater.name");

    private static GrpcProcessor instance;

    private final String method;

    public static GrpcProcessor getInstance() {
        if (instance == null) {
            createInstance();
        }
        return instance;
    }

    private static synchronized void createInstance() {
        if (instance == null) {
            instance = new GrpcProcessor();
        }
    }

    public GrpcProcessor() {
        this(null);
    }

    public GrpcProcessor(String method) {
        this.method = method;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public HttpMessage processMessage(HttpFuzzerTaskProcessorUtils utils, HttpMessage message) {
        if (method != null && !method.equals(message.getRequestHeader().getMethod())) {
            return message;
        }

        String rawMessage = message.getRequestBody().toString();
        ProtoBufMessageEncoder encoder = new ProtoBufMessageEncoder();
        try {
            encoder.encode(EncoderUtils.parseIntoList(rawMessage));
        } catch (InvalidProtobufFormatException | IOException e) {
            throw new RuntimeException(e);
        }
        byte[] encodedMessage = encoder.getOutputEncodedMessage();
        message.setRequestBody(Base64.getEncoder().encode(encodedMessage));

        return message;
    }

    @Override
    public boolean processResult(HttpFuzzerTaskProcessorUtils utils, HttpFuzzResult fuzzResult) {
        return true;
    }
}
