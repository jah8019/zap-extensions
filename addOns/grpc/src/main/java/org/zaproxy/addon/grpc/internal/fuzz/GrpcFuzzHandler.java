package org.zaproxy.addon.grpc.internal.fuzz;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.view.View;
import org.zaproxy.addon.grpc.internal.DecoderUtils;
import org.zaproxy.addon.grpc.internal.ProtoBufMessageDecoder;
import org.zaproxy.zap.extension.fuzz.FuzzResultsContentPanel;
import org.zaproxy.zap.extension.fuzz.FuzzerHandler;
import org.zaproxy.zap.extension.fuzz.FuzzerOptions;
import org.zaproxy.zap.extension.fuzz.MessageSelectorPanel;
import org.zaproxy.zap.extension.fuzz.httpfuzzer.HttpFuzzerHandlerOptionsPanel;
import org.zaproxy.zap.extension.fuzz.httpfuzzer.HttpFuzzerOptions;
import org.zaproxy.zap.extension.fuzz.httpfuzzer.ui.HttpMessageSelectorPanel;
import org.zaproxy.zap.extension.fuzz.impl.FuzzerDialog;
import org.zaproxy.zap.extension.fuzz.messagelocations.MessageLocationReplacement;
import org.zaproxy.zap.extension.fuzz.messagelocations.MessageLocationReplacementGenerator;
import org.zaproxy.zap.extension.fuzz.messagelocations.MessageLocationReplacer;
import org.zaproxy.zap.extension.fuzz.messagelocations.MessageLocationReplacers;
import org.zaproxy.zap.extension.fuzz.messagelocations.MessageLocationsReplacementStrategy;
import org.zaproxy.zap.extension.fuzz.messagelocations.MultipleMessageLocationsBreadthFirstReplacer;
import org.zaproxy.zap.extension.fuzz.messagelocations.MultipleMessageLocationsDepthFirstReplacer;
import org.zaproxy.zap.extension.fuzz.messagelocations.MultipleMessageLocationsReplacer;
import org.zaproxy.zap.extension.fuzz.payloads.PayloadGeneratorMessageLocation;
import org.zaproxy.zap.view.messagecontainer.MessageContainer;
import org.zaproxy.zap.view.messagecontainer.SelectableContentMessageContainer;
import org.zaproxy.zap.view.messagecontainer.http.HttpMessageContainer;
import org.zaproxy.zap.view.messagecontainer.http.MultipleHttpMessagesContainer;
import org.zaproxy.zap.view.messagecontainer.http.SingleHttpMessageContainer;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class GrpcFuzzHandler implements FuzzerHandler<HttpMessage, GrpcFuzzer> {

    private static final Logger LOGGER = LogManager.getLogger(GrpcFuzzHandler.class);

    private GrpcFuzzResultsContentPanel grpcFuzzResultsContentPanel;

    private final List<GrpcFuzzerMessageProcessorUIHandler<GrpcFuzzerMessageProcessor, ?>>
            messageProcessors = new ArrayList<>();

    @Override
    public Class<GrpcFuzzer> getFuzzerClass() {
        return GrpcFuzzer.class;
    }

    @Override
    public String getUIName() {
        return "GrpcFuzzerTestName";
    }

    @Override
    public MessageSelectorPanel<HttpMessage> createMessageSelectorPanel() {
        return new HttpMessageSelectorPanel();
    }

    @Override
    public GrpcFuzzer showFuzzerDialog(HttpMessage message, FuzzerOptions defaultOptions) {
        return showFuzzerDialogImpl(message, null, defaultOptions);
    }

    @Override
    public GrpcFuzzer showFuzzerDialog(MessageContainer<HttpMessage> messageContainer, FuzzerOptions defaultOptions) {
        if (!canFuzz(messageContainer)) {
            return null;
        }
        HttpMessage message = extractMessage((HttpMessageContainer) messageContainer);
        if (message != null) {

            return showFuzzerDialogImpl(message, null, defaultOptions);
        }
        return null;
    }

    @Override
    public GrpcFuzzer showFuzzerDialog(SelectableContentMessageContainer<HttpMessage> messageContainer, FuzzerOptions defaultOptions) {
        if (!canFuzz(messageContainer)) {
            return null;
        }

        HttpMessage message = extractMessage((HttpMessageContainer) messageContainer);
        if (message != null) {
            return showFuzzerDialogImpl(message, null, defaultOptions);
        }
        return null;
    }

    @Override
    public FuzzResultsContentPanel<HttpMessage, GrpcFuzzer> getResultsContentPanel() {
        if (grpcFuzzResultsContentPanel == null) {
            grpcFuzzResultsContentPanel = new GrpcFuzzResultsContentPanel();
        }
        return grpcFuzzResultsContentPanel;
    }

    @Override
    public void scannerRemoved(GrpcFuzzer fuzzer) {
    }

    @Override
    public boolean canHandle(MessageContainer<?> invoker) {
        return false;
    }

    @Override
    public boolean canFuzz(MessageContainer<?> invoker) {
        return true;
    }

    @Override
    public HttpMessage getMessage(MessageContainer<HttpMessage> invoker) {
        return null;
    }


    private static HttpMessage extractMessage(HttpMessageContainer messageContainer) {
        if (messageContainer instanceof SingleHttpMessageContainer) {
            return ((SingleHttpMessageContainer) messageContainer).getMessage();
        }

        if (messageContainer instanceof MultipleHttpMessagesContainer) {
            return ((MultipleHttpMessagesContainer) messageContainer).getMessage();
        }
        return null;
    }


    private GrpcFuzzer showFuzzerDialogImpl(
            HttpMessage message,
            SelectableContentMessageContainer<HttpMessage> container,
            FuzzerOptions defaultOptions) {


        // if (!isValidGrpcMessage(msg.getResponseHeader(), msg.getResponseBody())) {
        //     return;
        // }

        ProtoBufMessageDecoder protoBufMessageDecoder = new ProtoBufMessageDecoder();

        try {
            byte[] body =
                    DecoderUtils.splitMessageBodyAndStatusCode(message.getResponseBody().getBytes());
            body = Base64.getDecoder().decode(body);
            byte[] payload = DecoderUtils.extractPayload(body);
            protoBufMessageDecoder.decode(payload);
            message.getResponseBody().setBody(protoBufMessageDecoder.getDecodedOutput());
        } catch (UnsupportedEncodingException | IllegalArgumentException e) {
            LOGGER.warn("Error decoding the Response Body: {}", e.getMessage());
        }

        try {
            byte[] body =
                    DecoderUtils.splitMessageBodyAndStatusCode(message.getRequestBody().getBytes());
            body = Base64.getDecoder().decode(body);
            byte[] payload = DecoderUtils.extractPayload(body);
            protoBufMessageDecoder.decode(payload);
            message.getRequestBody().setBody(protoBufMessageDecoder.getDecodedOutput());
        } catch (UnsupportedEncodingException | IllegalArgumentException e) {
            LOGGER.warn("Error decoding the Response Body: {}", e.getMessage());
        }

        FuzzerDialog<HttpMessage, HttpFuzzerOptions, GrpcFuzzerMessageProcessor> fuzzDialogue
                = new FuzzerDialog<>(
                View.getSingleton().getMainFrame(),
                defaultOptions,
                message,
                true,
                new HttpFuzzerHandlerOptionsPanel(),
                new GrpcFuzzerMessageProcessorCollection(message, messageProcessors)
        );

        if (container != null) {
            if (fuzzDialogue.setSelectedContainer(container.getName())) {
                fuzzDialogue.addMessageLocation(container.getSelection());
            }
        }

        fuzzDialogue.setVisible(true);
        fuzzDialogue.dispose();
        return createFuzzer(
                message,
                fuzzDialogue.getFuzzLocations(),
                fuzzDialogue.getFuzzerOptions(),
                fuzzDialogue.getFuzzerMessageProcessors());
    }

    @SuppressWarnings("unchecked")
    private GrpcFuzzer createFuzzer(
            HttpMessage message,
            List<PayloadGeneratorMessageLocation<?>> fuzzLocations,
            HttpFuzzerOptions options,
            List<GrpcFuzzerMessageProcessor> processors) {
        if (fuzzLocations.isEmpty()) {
            return null;
        }

        MessageLocationReplacer<HttpMessage> replacer =
                MessageLocationReplacers.getInstance()
                        .getMLR(
                                HttpMessage.class,
                                fuzzLocations.get(0).getMessageLocation().getClass());

        replacer.init(message);

        MultipleMessageLocationsReplacer<HttpMessage> multipleMessageLocationsReplacer;
        if (MessageLocationsReplacementStrategy.DEPTH_FIRST
                == options.getPayloadsReplacementStrategy()) {
            multipleMessageLocationsReplacer = new MultipleMessageLocationsDepthFirstReplacer<>();
        } else {
            multipleMessageLocationsReplacer = new MultipleMessageLocationsBreadthFirstReplacer<>();
        }
        SortedSet<MessageLocationReplacementGenerator<?, ?>> messageLocationReplacementGenerators =
                new TreeSet<>();

        messageLocationReplacementGenerators.addAll(fuzzLocations);
        multipleMessageLocationsReplacer.init(replacer, messageLocationReplacementGenerators);

        return new GrpcFuzzer(
                createFuzzerName(message),
                options,
                message,
                (List<MessageLocationReplacementGenerator<?, MessageLocationReplacement<?>>>)
                        (ArrayList) fuzzLocations,
                multipleMessageLocationsReplacer,
                processors);
    }

    private String createFuzzerName(HttpMessage message) {
        //TBD:
        return "GrpcTestFuzzerName";
    }

    @SuppressWarnings("unchecked")
    protected <T1 extends GrpcFuzzerMessageProcessor, T2 extends GrpcFuzzerMessageProcessorUI<T1>>
    void addFuzzerMessageProcessorUIHandler(
            GrpcFuzzerMessageProcessorUIHandler<T1, T2> processorUIHandler) {
        messageProcessors.add(
                (GrpcFuzzerMessageProcessorUIHandler<GrpcFuzzerMessageProcessor, ?>)
                        processorUIHandler);
    }

    protected <T1 extends GrpcFuzzerMessageProcessor, T2 extends GrpcFuzzerMessageProcessorUI<T1>>
    void removeFuzzerMessageProcessorUIHandler(
            GrpcFuzzerMessageProcessorUIHandler<T1, T2> processorUIHandler) {
        messageProcessors.remove(processorUIHandler);
    }

    protected GrpcFuzzResultsContentPanel getGrpcFuzzResultsContentPanel() {
        if (grpcFuzzResultsContentPanel == null) {
            grpcFuzzResultsContentPanel = new GrpcFuzzResultsContentPanel();
        }
        return grpcFuzzResultsContentPanel;
    }
}

