package org.zaproxy.addon.grpc.internal.fuzz;

import org.zaproxy.zap.extension.fuzz.payloads.DefaultPayload;
import org.zaproxy.zap.extension.fuzz.payloads.ui.processors.AbstractCharsetProcessorUIPanel;
import org.zaproxy.zap.extension.fuzz.payloads.ui.processors.PayloadProcessorUI;
import org.zaproxy.zap.extension.fuzz.payloads.ui.processors.PayloadProcessorUIHandler;

import org.zaproxy.zap.extension.fuzz.payloads.ui.processors.AbstractCharsetProcessorUIPanel.AbstractCharsetProcessorUI;

import org.zaproxy.addon.grpc.internal.fuzz.GrpcPayloadProcessorUIHandler.GrpcPayloadProcessorUI;

import javax.swing.JPanel;
import java.nio.charset.Charset;

public class GrpcPayloadProcessorUIHandler
        implements PayloadProcessorUIHandler<
        DefaultPayload, GrpcPayloadProcessor, GrpcPayloadProcessorUI> {


    @Override
    public String getName() {
        return "Toller Grpc IU Handler";
    }

    @Override
    public Class<GrpcPayloadProcessorUI> getPayloadProcessorUIClass() {
        return GrpcPayloadProcessorUI.class;
    }

    @Override
    public Class<GrpcPayloadProcessorUIPanel> getPayloadProcessorUIPanelClass() {
        return GrpcPayloadProcessorUIPanel.class;
    }


    @Override
    public GrpcPayloadProcessorUIPanel createPanel() {
        return new GrpcPayloadProcessorUIPanel();
    }

    public static class GrpcPayloadProcessorUI
            extends AbstractCharsetProcessorUI<DefaultPayload, GrpcPayloadProcessor> {

        public GrpcPayloadProcessorUI(Charset charset){
            super(charset);
        }

        @Override
        public Class<GrpcPayloadProcessor> getPayloadProcessorClass() {
            return GrpcPayloadProcessor.class;
        }

        @Override
        public String getName() {
            return "Toller GrpcProcessorIU";
        }

        @Override
        public String getDescription() {
            return "GrpcPayloadProcessorUI";
        }

        @Override
        public boolean isMutable() {
            return true;
        }

        @Override
        public GrpcPayloadProcessor getPayloadProcessor() {
            return new GrpcPayloadProcessor();
        }

        @Override
        public PayloadProcessorUI<DefaultPayload, GrpcPayloadProcessor> copy() {
            return this;
        }
    }

    public static class GrpcPayloadProcessorUIPanel
            extends  AbstractCharsetProcessorUIPanel<
            DefaultPayload, GrpcPayloadProcessor, GrpcPayloadProcessorUI>{

        private final JPanel fieldsPanel;

        public GrpcPayloadProcessorUIPanel(){
            fieldsPanel = createDefaultFieldsPanel();
        }

        @Override
        public JPanel getComponent() {
            return fieldsPanel;
        }

        @Override
        public GrpcPayloadProcessorUI getPayloadProcessorUI() {
            return new GrpcPayloadProcessorUI((Charset) getCharsetComboBox().getSelectedItem());
        }

        @Override
        public GrpcPayloadProcessor getPayloadProcessor() {
            return new GrpcPayloadProcessor();
        }
    }
}
