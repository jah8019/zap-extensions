package org.zaproxy.addon.grpc.internal.fuzz;

import org.parosproxy.paros.control.Control;
import org.parosproxy.paros.extension.ExtensionAdaptor;
import org.parosproxy.paros.extension.ExtensionHook;
import org.zaproxy.addon.grpc.ExtensionGrpc;
import org.zaproxy.addon.grpc.internal.HttpPanelGrpcView;
import org.zaproxy.addon.grpc.internal.VariantGrpc;
import org.zaproxy.zap.extension.fuzz.ExtensionFuzz;
import org.zaproxy.zap.extension.fuzz.MessagePanelManager;
import org.zaproxy.zap.extension.fuzz.payloads.ui.processors.PayloadProcessorUIHandlersRegistry;
import org.zaproxy.zap.extension.httppanel.component.split.request.RequestSplitComponent;
import org.zaproxy.zap.extension.httppanel.component.split.response.ResponseSplitComponent;
import org.zaproxy.zap.extension.httppanel.view.HttpPanelView;
import org.zaproxy.zap.view.HttpPanelManager;

import java.util.function.Consumer;

public class ExtensionGrpcFuzz extends ExtensionAdaptor {

    public static final String NAME = "ExtensionGrpcFuzz";

    public ExtensionGrpcFuzz() {
        super(NAME);
    }

    @Override
    public void hook(ExtensionHook extensionHook) {
        super.hook(extensionHook);


        var processor = new GrpcPayloadProcessorUIHandler();
        PayloadProcessorUIHandlersRegistry payloadProcessorsUIRegistry =
                PayloadProcessorUIHandlersRegistry.getInstance();

        payloadProcessorsUIRegistry.registerProcessorUIHandler(GrpcPayloadProcessor.class, processor);

        if (hasView()) {
            HttpPanelManager manager = HttpPanelManager.getInstance();
            manager.addRequestViewFactory("RequestSplit", new GrpcRequestSplitBodyViewFactory());
        }

        extensionHook.addVariant(VariantGrpc.class);
    }

    private static void withExtensionFuzz(Consumer<ExtensionFuzz> consumer) {
        ExtensionFuzz extFuzz =
                Control.getSingleton().getExtensionLoader().getExtension(ExtensionFuzz.class);
        if (extFuzz != null) {
            consumer.accept(extFuzz);
        }
    }

    @Override
    public boolean canUnload() {
        return true;
    }

    @Override
    public void unload() {
        if (hasView()) {
            ExtensionFuzz extensionFuzz =
                    Control.getSingleton().getExtensionLoader().getExtension(ExtensionFuzz.class);

            MessagePanelManager panelManager = extensionFuzz.getClientMessagePanelManager();
            // remove views and their factories
            panelManager.removeViewFactory(
                    RequestSplitComponent.NAME, ExtensionGrpc.RequestGrpcViewFactory.NAME);
            panelManager.removeViews(
                    RequestSplitComponent.NAME,
                    HttpPanelGrpcView.NAME,
                    RequestSplitComponent.ViewComponent.BODY);
            panelManager.removeViewFactory(
                    ResponseSplitComponent.NAME, ExtensionGrpc.ResponseGrpcViewFactory.NAME);
            panelManager.removeViews(
                    ResponseSplitComponent.NAME,
                    HttpPanelGrpcView.NAME,
                    ResponseSplitComponent.ViewComponent.BODY);
        }
    }

    @Override
    public String getDescription() {
        return "TestExtensionGrpcIUDesc";
    }

    @Override
    public String getUIName() {
        return "TestExtensionGrpcIUName";
    }


    private static final class GrpcRequestSplitBodyViewFactory implements HttpPanelManager.HttpPanelViewFactory {

        public static final String NAME = "RequestSplitBodyViewFactory";

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public HttpPanelView getNewView() {
            return new GrpcRequestBodyPanelSyntaxHighlightTextView(
                    new GrpcRequestBodyStringHttpPanelViewModel());
        }

        @Override
        public Object getOptions() {
            return RequestSplitComponent.ViewComponent.BODY;
        }
    }
}
