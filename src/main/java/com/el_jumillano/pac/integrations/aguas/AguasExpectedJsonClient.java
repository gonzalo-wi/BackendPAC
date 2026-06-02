package com.el_jumillano.pac.integrations.aguas;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "aguas-expected-client", url = "${pac.integrations.aguas.url}", configuration = AguasExpectedFeignConfig.class)
public interface AguasExpectedJsonClient {

    /**
     * @param idreparto número de reparto, o 0 para traer todos los que tienen valor.
     * @param fecha     fecha en formato dd/MM/yyyy
     */
    @GetMapping("/reparto_get_valores")
    List<AguasExpectedDto> getExpected(
            @RequestParam("idreparto") Integer idreparto,
            @RequestParam("fecha") String fecha);
}
