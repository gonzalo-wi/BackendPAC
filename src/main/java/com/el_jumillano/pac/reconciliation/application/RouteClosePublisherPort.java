package com.el_jumillano.pac.reconciliation.application;

import com.el_jumillano.pac.reconciliation.infrastructure.messaging.RouteCloseMessage;

public interface RouteClosePublisherPort {
    void publish(RouteCloseMessage message);
}
