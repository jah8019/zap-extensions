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

import org.parosproxy.paros.Constant;
import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.extension.fuzz.httpfuzzer.AbstractHttpFuzzerMessageProcessorUIPanel;
import org.zaproxy.zap.extension.fuzz.httpfuzzer.HttpFuzzerMessageProcessorUI;
import org.zaproxy.zap.extension.fuzz.httpfuzzer.HttpFuzzerMessageProcessorUIHandler;

import javax.swing.JPanel;

public class GrpcProcessorUIHandler
        implements HttpFuzzerMessageProcessorUIHandler<
        GrpcProcessor, GrpcProcessorUIHandler.GrpcProcessorUI> {

    @Override
    public String getName() {
        return "Toll Toll Grpc";
        // ToDo:
        //return GrpcProcessor.NAME;
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
    public GrpcProcessorUI createDefault() {
        return GrpcProcessorUI.INSTANCE;
    }

    @Override
    public Class<HttpMessage> getMessageType() {
        return HttpMessage.class;
    }

    @Override
    public Class<GrpcProcessor> getFuzzerMessageProcessorType() {
        return GrpcProcessor.class;
    }

    @Override
    public Class<GrpcProcessorUI> getFuzzerMessageProcessorUIType() {
        return GrpcProcessorUI.class;
    }

    @Override
    public GrpcProcessorUIPanel createPanel() {
        return new GrpcProcessorUIPanel();
    }

    public static class GrpcProcessorUI
            implements HttpFuzzerMessageProcessorUI<GrpcProcessor> {

        public static final GrpcProcessorUI INSTANCE =
                new GrpcProcessorUI();

        public GrpcProcessorUI() {}

        @Override
        public String getName() {
            return GrpcProcessor.NAME;
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
        public GrpcProcessor getFuzzerMessageProcessor() {
            return GrpcProcessor.getInstance();
        }

        @Override
        public GrpcProcessorUI copy() {
            return this;
        }
    }

    public static class GrpcProcessorUIPanel
            extends AbstractHttpFuzzerMessageProcessorUIPanel<
            GrpcProcessor, GrpcProcessorUI> {

        private JPanel fieldsPanel;

        public GrpcProcessorUIPanel() {
            fieldsPanel = new JPanel();
        }

        @Override
        public JPanel getComponent() {
            return fieldsPanel;
        }

        @Override
        public void setFuzzerMessageProcessorUI(
                GrpcProcessorUI payloadProcessorUI) {}

        @Override
        public GrpcProcessorUI getFuzzerMessageProcessorUI() {
            return GrpcProcessorUI.INSTANCE;
        }
    }
}
