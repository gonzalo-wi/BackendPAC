package com.el_jumillano.pac.integrations.minibank;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Cliente HTTP hacia Minibank. Devuelve el XML crudo como String.
 * Resilience4j (circuit breaker + retry) se configura en application.properties.
 */
@FeignClient(name = "minibank-xml-client", url = "${pac.integrations.minibank.url}", configuration = MinibankFeignConfig.class)
public interface MinibankXmlClient {

    /**
     * @param stIdentifier identificador del cajero (p. ej. "L-EJU-002")
     * @param date         fecha en formato MM/dd/yyyy
     */
    @GetMapping("/api/v3/deposits/byday")
    String getDepositsXml(
            @RequestParam("stIdentifier") String stIdentifier,
            @RequestParam("date") String date);
}
