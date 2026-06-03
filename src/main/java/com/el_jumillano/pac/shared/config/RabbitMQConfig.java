package com.el_jumillano.pac.shared.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE    = "pac.direct";
    public static final String QUEUE_CLOSE = "pac.route.close";

    @Bean
    public DirectExchange pacExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue routeCloseQueue() {
        return QueueBuilder.durable(QUEUE_CLOSE).build();
    }

    @Bean
    public Binding routeCloseBinding(Queue routeCloseQueue, DirectExchange pacExchange) {
        return BindingBuilder.bind(routeCloseQueue).to(pacExchange).with(QUEUE_CLOSE);
    }

    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         JacksonJsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
