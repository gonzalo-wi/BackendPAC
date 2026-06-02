package com.el_jumillano.pac.integrations.minibank;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Propiedades de Minibank. El mapa cashierIdentifiers asocia externalCashierNumber → stIdentifier
 * (p. ej. 1 → "L-EJU-001", 2 → "L-EJU-002", ...).
 */
@Component
@ConfigurationProperties(prefix = "pac.integrations.minibank")
@Data
public class MinibankProperties {

    private String username;
    private String password;

    /** Map de nro. externo de cajero → stIdentifier de Minibank (L-EJU-00x). */
    private Map<Integer, String> cashierIdentifiers = new HashMap<>();
}
