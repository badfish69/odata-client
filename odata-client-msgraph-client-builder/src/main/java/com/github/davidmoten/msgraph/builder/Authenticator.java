package com.github.davidmoten.msgraph.builder;

import java.util.List;

import com.github.davidmoten.odata.client.RequestHeader;

@FunctionalInterface
public interface Authenticator {

    List<RequestHeader> authenticate(List<RequestHeader> requestHeaders);

}
