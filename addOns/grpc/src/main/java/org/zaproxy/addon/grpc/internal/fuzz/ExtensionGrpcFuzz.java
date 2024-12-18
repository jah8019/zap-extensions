package org.zaproxy.addon.grpc.internal.fuzz;

import org.parosproxy.paros.control.Control;
import org.parosproxy.paros.extension.ExtensionAdaptor;
import org.parosproxy.paros.extension.ExtensionHook;
import org.zaproxy.zap.extension.fuzz.httpfuzzer.ExtensionHttpFuzzer;
import org.zaproxy.zap.extension.fuzz.payloads.ui.PayloadGeneratorUIHandlersRegistry;
import org.zaproxy.zap.extension.fuzz.payloads.ui.processors.PayloadProcessorUIHandlersRegistry;

public class ExtensionGrpcFuzz extends ExtensionAdaptor {

    public static final String NAME = "ExtensionGrpcFuzz";


    @Override
    public void hook(ExtensionHook extensionHook) {
        super.hook(extensionHook);


        var processor = new GrpcPayloadProcessorUIHandler();

        PayloadProcessorUIHandlersRegistry payloadProcessorsUIRegistry =
                PayloadProcessorUIHandlersRegistry.getInstance();

        payloadProcessorsUIRegistry.registerProcessorUIHandler(GrpcPayloadProcessor.class, processor);

        if(hasView()){

        }

        // ExtensionHttpFuzzer extensionFuzz = Control.getSingleton().getExtensionLoader().getExtension(ExtensionHttpFuzzer.class);
        // var processor = new GrpcProcessorUIHandler();
        // extensionFuzz.addFuzzerMessageProcessorUIHandler(processor);
    }
}
