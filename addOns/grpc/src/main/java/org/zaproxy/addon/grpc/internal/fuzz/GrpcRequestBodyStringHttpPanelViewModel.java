package org.zaproxy.addon.grpc.internal.fuzz;

import org.zaproxy.zap.extension.httppanel.view.impl.models.http.AbstractHttpStringHttpPanelViewModel;

public class GrpcRequestBodyStringHttpPanelViewModel extends AbstractHttpStringHttpPanelViewModel {

    @Override
    public String getData() {
        if (httpMessage == null) {
            return "";
        }

        return httpMessage.getRequestBody().toString();
    }

    @Override
    public void setData(String data) {
        if (httpMessage == null) {
            return;
        }

        httpMessage.setRequestBody(data);
    }
}
