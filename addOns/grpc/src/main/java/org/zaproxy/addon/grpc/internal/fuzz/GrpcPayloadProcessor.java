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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zaproxy.addon.grpc.internal.EncoderUtils;
import org.zaproxy.addon.grpc.internal.InvalidProtobufFormatException;
import org.zaproxy.addon.grpc.internal.ProtoBufMessageEncoder;
import org.zaproxy.zap.extension.fuzz.payloads.DefaultPayload;
import org.zaproxy.zap.extension.fuzz.payloads.processor.AbstractCharsetProcessor;
import org.zaproxy.zap.extension.fuzz.payloads.processor.DefaultPayloadProcessor;
import org.zaproxy.zap.extension.fuzz.payloads.processor.PayloadProcessingException;
import org.zaproxy.zap.extension.fuzz.payloads.processor.PayloadProcessor;

import java.io.IOException;
import java.util.Base64;

public class GrpcPayloadProcessor extends AbstractCharsetProcessor<DefaultPayload>
        implements DefaultPayloadProcessor {

    private static final Logger LOGGER = LogManager.getLogger(GrpcPayloadProcessor.class);


    @Override
    public DefaultPayload process(DefaultPayload payload) throws PayloadProcessingException {
        String rawMessage = payload.getValue();
        ProtoBufMessageEncoder encoder = new ProtoBufMessageEncoder();
        try {
            encoder.encode(EncoderUtils.parseIntoList(rawMessage));
        } catch (InvalidProtobufFormatException | IOException e) {
            throw new RuntimeException(e);
        }
        byte[] encodedMessage = encoder.getOutputEncodedMessage();
        payload.setValue(Base64.getEncoder().encodeToString(encodedMessage));

        return payload;
    }

    @Override
    public PayloadProcessor<DefaultPayload> copy() {
        return this;
    }
}
