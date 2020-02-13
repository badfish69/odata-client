package com.github.davidmoten.odata.client;

import java.util.Map;

import com.github.davidmoten.odata.client.internal.RequestHelper;

public final class ActionRequestNoReturn extends ActionRequestBase<ActionRequestNoReturn> {
    
    public ActionRequestNoReturn(ContextPath contextPath, Map<String, Object> parameters) {
        super(parameters, contextPath);
    }

    public void call() {
        RequestOptions requestOptions = RequestOptions.EMPTY;
        RequestHelper.post(parameters, contextPath, requestOptions);
    }

}