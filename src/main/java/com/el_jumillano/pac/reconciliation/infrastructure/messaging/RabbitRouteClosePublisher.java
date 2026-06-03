package com.el_jumillano.pac.reconciliation.infrastructure.messaging;

import com.el_jumillano.pac.reconciliation.application.RouteClosePublisherPort;
import com.el_jumillano.pac.shared.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitRouteClosePublisher implements RouteClosePublisherPort {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(RouteCloseMessage message) {
        log.info("Publicando cierre de reparto {} en cola {}", message.getRouteNumber(), RabbitMQConfig.QUEUE_CLOSE);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.QUEUE_CLOSE, message);
    }
}
