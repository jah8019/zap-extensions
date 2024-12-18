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
package org.zaproxy.addon.grpc.internal.fuzz.processors;

import org.parosproxy.paros.Constant;
import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.addon.grpc.internal.fuzz.AbstractGrpcFuzzerMessageProcessorUIPanel;
import org.zaproxy.addon.grpc.internal.fuzz.GrpcFuzzerMessageProcessorUI;
import org.zaproxy.addon.grpc.internal.fuzz.GrpcFuzzerMessageProcessorUIHandler;

import javax.swing.JPanel;

public class RequestContentLengthUpdaterProcessorUIHandler
        implements GrpcFuzzerMessageProcessorUIHandler<
        RequestContentLengthUpdaterProcessor, RequestContentLengthUpdaterProcessorUIHandler.GrpcRequestContentLengthUpdatedProcessorUI> {

    @Override
    public String getName() {
        return RequestContentLengthUpdaterProcessor.NAME;
    }

    @Override
    public boolean isEnabled(HttpMessage message) {
        return true;
    }

    @Override
    public boolean isDefault() {
        return true;
    }

    @Override
    public GrpcRequestContentLengthUpdatedProcessorUI createDefault() {
        return GrpcRequestContentLengthUpdatedProcessorUI.INSTANCE;
    }

    @Override
    public Class<HttpMessage> getMessageType() {
        return HttpMessage.class;
    }

    @Override
    public Class<RequestContentLengthUpdaterProcessor> getFuzzerMessageProcessorType() {
        return RequestContentLengthUpdaterProcessor.class;
    }

    @Override
    public Class<GrpcRequestContentLengthUpdatedProcessorUI> getFuzzerMessageProcessorUIType() {
        return GrpcRequestContentLengthUpdatedProcessorUI.class;
    }

    @Override
    public GrpcRequestContentLengthUpdatedProcessorUIPanel createPanel() {
        return new GrpcRequestContentLengthUpdatedProcessorUIPanel();
    }

    public static class GrpcRequestContentLengthUpdatedProcessorUI
            implements GrpcFuzzerMessageProcessorUI<RequestContentLengthUpdaterProcessor> {

        public static final GrpcRequestContentLengthUpdatedProcessorUI INSTANCE =
                new GrpcRequestContentLengthUpdatedProcessorUI();

        public GrpcRequestContentLengthUpdatedProcessorUI() {
        }

        @Override
        public String getName() {
            return RequestContentLengthUpdaterProcessor.NAME;
        }

        @Override
        public boolean isMutable() {
            return false;
        }

        @Override
        public String getDescription() {
            return Constant.messages.getString(
                    "fuzz.httpfuzzer.processor.requestContentLengthUpdater.description");
        }

        @Override
        public RequestContentLengthUpdaterProcessor getFuzzerMessageProcessor() {
            return RequestContentLengthUpdaterProcessor.getInstance();
        }

        @Override
        public GrpcRequestContentLengthUpdatedProcessorUI copy() {
            return this;
        }
    }

    public static class GrpcRequestContentLengthUpdatedProcessorUIPanel
            extends AbstractGrpcFuzzerMessageProcessorUIPanel<
            RequestContentLengthUpdaterProcessor, GrpcRequestContentLengthUpdatedProcessorUI> {

        private JPanel fieldsPanel;

        public GrpcRequestContentLengthUpdatedProcessorUIPanel() {
            fieldsPanel = new JPanel();
        }

        @Override
        public JPanel getComponent() {
            return fieldsPanel;
        }

        @Override
        public void setFuzzerMessageProcessorUI(
                GrpcRequestContentLengthUpdatedProcessorUI payloadProcessorUI) {
        }

        @Override
        public GrpcRequestContentLengthUpdatedProcessorUI getFuzzerMessageProcessorUI() {
            return GrpcRequestContentLengthUpdatedProcessorUI.INSTANCE;
        }
    }
}
