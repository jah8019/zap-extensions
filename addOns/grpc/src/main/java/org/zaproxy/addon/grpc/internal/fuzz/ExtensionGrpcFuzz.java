package org.zaproxy.addon.grpc.internal.fuzz;

import org.parosproxy.paros.control.Control;
import org.parosproxy.paros.extension.ExtensionAdaptor;
import org.parosproxy.paros.extension.ExtensionHook;
import org.zaproxy.zap.extension.fuzz.httpfuzzer.ExtensionHttpFuzzer;
import org.zaproxy.zap.extension.httppanel.component.split.request.RequestSplitComponent;
import org.zaproxy.zap.extension.httppanel.view.HttpPanelView;
import org.zaproxy.zap.extension.httppanel.view.impl.models.http.request.RequestBodyStringHttpPanelViewModel;
import org.zaproxy.zap.view.HttpPanelManager;


public class ExtensionGrpcFuzz extends ExtensionAdaptor {

    public static final String NAME = "ExtensionGrpcFuzz";

    public ExtensionGrpcFuzz() {
        super(NAME);
    }

    @Override
    public void hook(ExtensionHook extensionHook) {
        super.hook(extensionHook);

        ExtensionHttpFuzzer extensionHttpFuzzer =
                Control.getSingleton().getExtensionLoader().getExtension(ExtensionHttpFuzzer.class);

        if (hasView()) {
            HttpPanelManager manager = HttpPanelManager.getInstance();
            manager.addRequestViewFactory("RequestSplit", new GrpcRequestSplitBodyViewFactory());

            var grpcMessageProcessor = new GrpcMessageProcessorUIHandler();
            extensionHttpFuzzer.addFuzzerMessageProcessorUIHandler(grpcMessageProcessor);
        }
    }

    @Override
    public boolean canUnload() {
        return true;
    }

    @Override
    public void unload() {
        if (hasView()) {
         //TODO
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
                    new RequestBodyStringHttpPanelViewModel());
        }

        @Override
        public Object getOptions() {
            return RequestSplitComponent.ViewComponent.BODY;
        }
    }
}
