package com.el_jumillano.pac.integrations.aguas;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "aguas-detail-client",
        url = "${pac.integrations.aguas.cobranza-url}",
        configuration = AguasExpectedFeignConfig.class
)
public interface AguasDetailJsonClient {

    /**
     * Detalle de cobros de un reparto: qué cliente pagó con qué forma de pago.
     *
     * @param reparto      número de reparto
     * @param fecha        fecha en formato YYYY-MM-DD
     * @param esfecharuta  siempre true
     */
    @GetMapping("/getcobranzadetalle")
    AguasRouteDetailDto getRouteDetail(
            @RequestParam("reparto") Integer reparto,
            @RequestParam("fecha") String fecha,
            @RequestParam("esfecharuta") boolean esfecharuta);
}
