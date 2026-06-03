package com.el_jumillano.pac.integrations.aguas;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "aguas-close-client", url = "${pac.integrations.aguas.url}")
public interface AguasCloseHttpClient {

    @PostMapping(value = "/reparto_cerrar", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String closeRoute(
            @RequestParam("idreparto")        Integer idReparto,
            @RequestParam("fecha")            String  fecha,
            @RequestParam("ajustar_envases")  Integer ajustarEnvases,
            @RequestParam("efectivo_importe") String  efectivoImporte,
            @RequestParam("retenciones")      String  retenciones,
            @RequestParam("cheques")          String  cheques,
            @RequestParam("usuario")          String  usuario
    );
}
