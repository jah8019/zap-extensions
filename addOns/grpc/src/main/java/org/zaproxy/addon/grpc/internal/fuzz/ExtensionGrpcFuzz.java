package org.zaproxy.addon.grpc.internal.fuzz;

import org.parosproxy.paros.control.Control;
import org.parosproxy.paros.extension.Extension;
import org.parosproxy.paros.extension.ExtensionAdaptor;
import org.parosproxy.paros.extension.ExtensionHook;
import org.parosproxy.paros.extension.ViewDelegate;
import org.zaproxy.addon.grpc.ExtensionGrpc;

import org.zaproxy.addon.grpc.internal.DecoderUtils;
import org.zaproxy.addon.grpc.internal.HttpPanelGrpcView;
import org.zaproxy.addon.grpc.internal.fuzz.processors.RequestContentLengthUpdaterProcessorUIHandler;
import org.zaproxy.zap.extension.fuzz.ExtensionFuzz;
import org.zaproxy.zap.extension.fuzz.httpfuzzer.processors.HttpFuzzerReflectionDetectorStateHighlighter;
import org.zaproxy.zap.extension.fuzz.httpfuzzer.processors.tagcreator.HttpFuzzerMessageProcessorTagStateHighlighter;
import org.zaproxy.zap.extension.fuzz.httpfuzzer.ui.HttpFuzzResultToHistoryPopupMenuItem;
import org.zaproxy.zap.extension.fuzz.httpfuzzer.ui.HttpFuzzerResultStateHighlighter;
import org.zaproxy.zap.extension.httppanel.component.split.request.RequestSplitComponent;
import org.zaproxy.zap.extension.httppanel.view.HttpPanelView;
import org.zaproxy.zap.extension.httppanel.view.impl.models.http.request.RequestBodyByteHttpPanelViewModel;
import org.zaproxy.zap.extension.search.ExtensionSearch;
import org.zaproxy.zap.view.HttpPanelManager;


import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExtensionGrpcFuzz extends ExtensionAdaptor {
    public static final String NAME = "ExtensionGrpcFuzz";
    private static final List<Class<? extends Extension>> DEPENDENCIES =
            List.of(ExtensionGrpc.class, ExtensionFuzz.class);

    private final Logger logger;

    private GrpcFuzzHandler grpcFuzzerHandler;

    public ExtensionGrpcFuzz() {
        super(NAME);
        logger = Logger.getLogger(ExtensionGrpcFuzz.class.getName());
    }

    @Override
    public List<Class<? extends Extension>> getDependencies() {
        return DEPENDENCIES;
    }

    @Override
    public void init() {
        grpcFuzzerHandler = new GrpcFuzzHandler();
        logger.log(Level.INFO, "ExtensionGrpcFuzz initialized");
        //    MessageLocationReplacers.getInstance()
        //            .addReplacer(HttpMessage.class, new TextHttpMessageLocationReplacerFactory());
    }

    @Override
    public void initView(ViewDelegate view) {
        super.initView(view);

    }

    @Override
    public void hook(ExtensionHook extensionHook) {
        super.hook(extensionHook);
        ExtensionFuzz extensionFuzz =
                Control.getSingleton().getExtensionLoader().getExtension(ExtensionFuzz.class);
        extensionFuzz.addFuzzerHandler(grpcFuzzerHandler);

        if (hasView()) {
            extensionHook
                    .getHookMenu()
                    .addPopupMenuItem(
                            new GrpcFuzzAttackPopupMenuItem(extensionFuzz, grpcFuzzerHandler));

            extensionHook
                    .getHookMenu()
                    .addPopupMenuItem(new HttpFuzzResultToHistoryPopupMenuItem());
            ExtensionSearch extensionSearch =
                    Control.getSingleton().getExtensionLoader().getExtension(ExtensionSearch.class);
//            if (extensionSearch != null) {
//                httpFuzzerSearcher = new ExtensionHttpFuzzer.HttpFuzzerSearcher(extensionFuzz);
//                extensionSearch.addCustomHttpSearcher(httpFuzzerSearcher);
//            }

            grpcFuzzerHandler.addFuzzerMessageProcessorUIHandler(
                    new RequestContentLengthUpdaterProcessorUIHandler());
//            grpcFuzzerHandler.addFuzzerMessageProcessorUIHandler(
//                    new HttpFuzzerReflectionDetectorUIHandler());

            addFuzzResultStateHighlighter(new HttpFuzzerReflectionDetectorStateHighlighter());


//            ExtensionUserManagement extensionUserManagement =
//                    Control.getSingleton()
//                            .getExtensionLoader()
//                            .getExtension(ExtensionUserManagement.class);


//            if (extensionUserManagement != null) {
//                grpcFuzzerHandler.addFuzzerMessageProcessorUIHandler(
//                        new UserHttpFuzzerMessageProcessorUIHandler(extensionUserManagement));
//            }

//            ExtensionAntiCSRF extensionAntiCSRF =
//                    Control.getSingleton()
//                            .getExtensionLoader()
//                            .getExtension(ExtensionAntiCSRF.class);
//            if (extensionAntiCSRF != null) {
//                grpcFuzzerHandler.addFuzzerMessageProcessorUIHandler(
//                        new AntiCsrfHttpFuzzerMessageProcessorUIHandler(extensionAntiCSRF));
//            }
//
//            grpcFuzzerHandler.addFuzzerMessageProcessorUIHandler(
//                    new HttpFuzzerMessageProcessorTagUIHandler());
            addFuzzResultStateHighlighter(new HttpFuzzerMessageProcessorTagStateHighlighter());
        }
    }

    public void addFuzzResultStateHighlighter(HttpFuzzerResultStateHighlighter highlighter) {
        grpcFuzzerHandler
                .getGrpcFuzzResultsContentPanel()
                .addFuzzResultStateHighlighter(highlighter);
    }

    private static final class FuzzGrpcViewFactory implements HttpPanelManager.HttpPanelViewFactory {

        @Override
        public String getName() {
            return "FuzzGrpcViewFactory";
        }

        @Override
        public HttpPanelView getNewView() {
            return new HttpPanelGrpcView(
                    new RequestBodyByteHttpPanelViewModel(),
                    DecoderUtils.DecodingMethod.BASE64_ENCODED);
        }

        @Override
        public Object getOptions() {
            return RequestSplitComponent.ViewComponent.BODY;
        }


    }
}
