package com.el_jumillano.pac.integrations.aguas;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "aguas-close-client", url = "${pac.integrations.aguas.url}")
public interface AguasCloseXmlClient {

    @PostMapping(value = "/close", consumes = MediaType.APPLICATION_XML_VALUE,
                 produces = MediaType.APPLICATION_XML_VALUE)
    String closeRoute(@RequestBody String xmlPayload);
}
