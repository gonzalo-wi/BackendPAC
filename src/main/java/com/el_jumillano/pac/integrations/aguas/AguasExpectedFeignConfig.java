package com.el_jumillano.pac.integrations.aguas;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import org.springframework.context.annotation.Bean;

/**
 * Feign configuration for the Aguas expected-values client.
 *
 * Aguas' ASMX endpoint returns JSON without a Content-Type header, so Feign's
 * default Jackson decoder rejects the response.  This decoder forces JSON
 * deserialization regardless of the Content-Type header.
 *
 * NOTE: No {@code @Configuration} annotation — Spring Cloud registers this as
 * a local Feign context so it does NOT become a global bean. The child context
 * is fully isolated, so ObjectMapper must be instantiated locally.
 */
public class AguasExpectedFeignConfig {

    @Bean
    public Decoder aguasForceJsonDecoder() {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return (response, type) -> {
            if (response.body() == null) {
                return null;
            }
            try {
                return objectMapper.readValue(
                        response.body().asInputStream(),
                        objectMapper.constructType(type));
            } catch (Exception e) {
                throw new DecodeException(
                        response.status(),
                        "Error decoding Aguas response as JSON: " + e.getMessage(),
                        response.request(),
                        e);
            }
        };
    }
}
