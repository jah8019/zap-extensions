package org.zaproxy.addon.grpc.internal.fuzz;

import java.awt.Component;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.view.View;
import org.zaproxy.addon.grpc.internal.ProtoBufMessageDecoder;
import org.zaproxy.zap.extension.httppanel.Message;
import org.zaproxy.zap.extension.httppanel.view.syntaxhighlight.AutoDetectSyntaxHttpPanelTextArea;
import org.zaproxy.zap.extension.httppanel.view.syntaxhighlight.ContentSplitter;
import org.zaproxy.zap.extension.httppanel.view.syntaxhighlight.HttpPanelSyntaxHighlightTextArea;
import org.zaproxy.zap.extension.httppanel.view.syntaxhighlight.HttpPanelSyntaxHighlightTextView;
import org.zaproxy.zap.model.DefaultTextHttpMessageLocation;
import org.zaproxy.zap.model.HttpMessageLocation;
import org.zaproxy.zap.model.MessageLocation;
import org.zaproxy.zap.model.TextHttpMessageLocation;
import org.zaproxy.zap.view.messagecontainer.http.SelectableContentHttpMessageContainer;
import org.zaproxy.zap.view.messagelocation.MessageLocationHighlight;
import org.zaproxy.zap.view.messagelocation.MessageLocationHighlightsManager;
import org.zaproxy.zap.view.messagelocation.MessageLocationProducerFocusListener;
import org.zaproxy.zap.view.messagelocation.MessageLocationProducerFocusListenerAdapter;
import org.zaproxy.zap.view.messagelocation.TextMessageLocationHighlight;
import org.zaproxy.zap.view.messagelocation.TextMessageLocationHighlightsManager;
import org.zaproxy.zap.extension.search.SearchMatch;

@SuppressWarnings("serial")
public class GrpcRequestBodyPanelSyntaxHighlightTextView extends HttpPanelSyntaxHighlightTextView
        implements SelectableContentHttpMessageContainer {

    public static final String NAME = "GrpcRequestBodySyntaxTextView";

    private MessageLocationProducerFocusListenerAdapter focusListenerAdapter;
    private ContentSplitter contentSplitter;

    public GrpcRequestBodyPanelSyntaxHighlightTextView(GrpcRequestBodyStringHttpPanelViewModel model) {
        super(model);

        getHttpPanelTextArea()
                .setComponentPopupMenu(
                        new CustomPopupMenu() {
                            private static final long serialVersionUID = 1L;

                            @Override
                            public void show(Component invoker, int x, int y) {
                                if (!getHttpPanelTextArea().isFocusOwner()) {
                                    getHttpPanelTextArea().requestFocusInWindow();
                                }
                                View.getSingleton().getPopupMenu().show(
                                        GrpcRequestBodyPanelSyntaxHighlightTextView.this,
                                        x, y);
                            }
                        });
    }

    @Override
    protected HttpPanelSyntaxHighlightTextArea createHttpPanelTextArea() {
        contentSplitter = new ContentSplitter(getMainPanel());
        HttpPanelSyntaxHighlightTextArea textArea =
                new GrpcRequestBodyPanelSyntaxHighlightTextArea(contentSplitter);

        contentSplitter.setTextArea(textArea);
        return textArea;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Class<HttpMessage> getMessageClass() {
        return HttpMessage.class;
    }

    @Override
    public Class<? extends MessageLocation> getMessageLocationClass() {
        return TextHttpMessageLocation.class;
    }

    @Override
    public MessageLocation getSelection() {

        //ToDo:
        return  new DefaultTextHttpMessageLocation(HttpMessageLocation.Location.REQUEST_BODY, 0);
    }


    @Override
    public MessageLocationHighlight highlight(MessageLocation location) {
        if (!supports(location)) {
            return null;
        }
        TextHttpMessageLocation textLocation = (TextHttpMessageLocation) location;
        return null;
    }

    @Override
    public MessageLocationHighlight highlight(MessageLocation location, MessageLocationHighlight highlight) {
        if (!supports(location) || !(highlight instanceof TextMessageLocationHighlight)) {
            return null;
        }
        TextHttpMessageLocation textLocation = (TextHttpMessageLocation) location;
        TextMessageLocationHighlight textHighlight = (TextMessageLocationHighlight) highlight;
        return null; // getHttpPanelTextArea().highlightImpl(textLocation, textHighlight);
    }

    @Override
    public void removeHighlight(MessageLocation location, MessageLocationHighlight highlightReference) {
        if (!(highlightReference instanceof TextMessageLocationHighlight)) {
            return;
        }
        getHttpPanelTextArea()
                .removeHighlight(((TextMessageLocationHighlight) highlightReference).getHighlightReference());
    }

    @Override
    public boolean supports(MessageLocation location) {
        //TODO
//        if (!(location instanceof TextHttpMessageLocation)) {
//            return false;
//        }
//        return ((TextHttpMessageLocation) location).getLocation()
//                == TextHttpMessageLocation.Location.REQUEST_BODY;
        return true;
    }

    @Override
    public boolean supports(Class<? extends MessageLocation> classLocation) {
        return (TextHttpMessageLocation.class.isAssignableFrom(classLocation));
    }

    @Override
    public void addFocusListener(MessageLocationProducerFocusListener focusListener) {
        getFocusListenerAdapter().addFocusListener(focusListener);
    }

    @Override
    public void removeFocusListener(MessageLocationProducerFocusListener focusListener) {
        getFocusListenerAdapter().removeFocusListener(focusListener);

        if (!getFocusListenerAdapter().hasFocusListeners()) {
            getHttpPanelTextArea().removeFocusListener(focusListenerAdapter);
            focusListenerAdapter = null;
        }
    }

    @Override
    public MessageLocationHighlightsManager create() {
        return null;
    }

    @Override
    public HttpMessage getMessage() {
        return (HttpMessage) getHttpPanelTextArea().getMessage();
    }

    @Override
    public Component getComponent() {
        return getHttpPanelTextArea();
    }

    @Override
    public boolean isEmpty() {
        return getHttpPanelTextArea().getMessage() == null;
    }

    private MessageLocationProducerFocusListenerAdapter getFocusListenerAdapter() {
        if (focusListenerAdapter == null) {
            focusListenerAdapter = new MessageLocationProducerFocusListenerAdapter(this);
            getHttpPanelTextArea().addFocusListener(focusListenerAdapter);
        }
        return focusListenerAdapter;
    }

    @Override
    protected void setModelData(String data) {
        // Hier wäre der Ort, an dem wir den gRPC-Body verarbeiten können,
        // bevor er angezeigt wird.
        // TODO: gRPC Decoding der im Model enthaltenen Daten (falls nötig)
        super.setModelData(contentSplitter.process(data));
    }

    private static class GrpcRequestBodyPanelSyntaxHighlightTextArea extends AutoDetectSyntaxHttpPanelTextArea {

        private static final long serialVersionUID = 1L;

        private static ProtoBufMessageDecoder decoder;

        private static CustomTokenMakerFactory tokenMakerFactory = null;
        private final ContentSplitter contentSplitter;

        public GrpcRequestBodyPanelSyntaxHighlightTextArea(ContentSplitter contentSplitter) {
            this.contentSplitter = contentSplitter;

            decoder = new ProtoBufMessageDecoder();
            // Falls du eigene Syntax-Stile für gRPC einführen möchtest,
            // kannst du sie hier registrieren.

            setCodeFoldingAllowed(true);
        }

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public HttpMessage getMessage() {
            var message = (HttpMessage) super.getMessage();

            if (message == null)
                return null;

            decoder.decode(message.getRequestBody().getBytes());
            return message;
        }

        @Override
        public void setMessage(Message aMessage) {
            // Wenn wir gRPC-spezifische Änderungen am Message-Objekt vornehmen müssen,
            // könnten wir das hier tun.
            // TODO: gRPC Decoding falls notwendig, bevor die Nachricht dargestellt wird.

            if (aMessage != null) {
                decoder.decode(((HttpMessage) aMessage).getRequestBody().getBytes());
                ((HttpMessage) aMessage).setRequestBody(decoder.getDecodedOutput());
            }
            super.setMessage(aMessage);
        }

        @Override
        protected String detectSyntax(HttpMessage httpMessage) {
            // Hier kannst du entscheiden, ob du überhaupt Syntax-Highlighting für gRPC machst.
            // Da gRPC oft binär und nicht textbasiert ist, ist hier evtl. nur Base64 oder Hex sinnvoll.
            // Wir lassen es einfach leer oder geben einen Standard zurück.
            return httpMessage.getResponseBody().toString();
        }

        protected MessageLocation getSelection() {
            int start = getSelectionStart();
            int end = getSelectionEnd();
            if (start == end) {
                return new DefaultTextHttpMessageLocation(
                        HttpMessageLocation.Location.REQUEST_BODY, start);
            }
            return new DefaultTextHttpMessageLocation(
                    HttpMessageLocation.Location.REQUEST_BODY, start, end, getSelectedText());
        }

        protected MessageLocationHighlightsManager create() {
            return new TextMessageLocationHighlightsManager();
        }

        protected MessageLocationHighlight highlightImpl(
                TextHttpMessageLocation textLocation, TextMessageLocationHighlight textHighlight) {
            textHighlight.setHighlightReference(highlight(textLocation.getStart(), textLocation.getEnd(), textHighlight));
            return textHighlight;
        }

        @Override
        public void search(Pattern p, List<SearchMatch> matches) {
            Matcher m = p.matcher(getText());
            while (m.find()) {
                matches.add(new SearchMatch(SearchMatch.Location.REQUEST_BODY, m.start(), m.end()));
            }
        }

        @Override
        public void highlight(SearchMatch sm) {
            if (!SearchMatch.Location.REQUEST_BODY.equals(sm.getLocation())) {
                return;
            }

            int len = getText().length();
            if (sm.getStart() > len || sm.getEnd() > len) {
                return;
            }

            int[] offsets = contentSplitter.highlightOffsets(sm.getStart(), sm.getEnd());
            highlight(offsets[0], offsets[1]);
        }

        @Override
        protected synchronized CustomTokenMakerFactory getTokenMakerFactory() {
            if (tokenMakerFactory == null) {
                tokenMakerFactory = new CustomTokenMakerFactory() {
                    @Override
                    protected void initTokenMakerMap() {
                        // Wenn du einen speziellen TokenMaker für gRPC hättest, könntest du ihn hier registrieren.
                    }
                };
            }
            return tokenMakerFactory;
        }
    }
}
