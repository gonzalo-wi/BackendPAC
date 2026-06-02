package com.el_jumillano.pac.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI pacOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PAC — Plataforma de Conciliación Financiera-Operativa")
                        .description("API para conciliar depósitos reales del Minibank contra " +
                                "los montos esperados del sistema Aguas.")
                        .version("0.0.1")
                        .contact(new Contact().name("El Jumillano")))
                .tags(List.of(
                        new Tag().name("Health").description("Estado de la aplicación"),
                        new Tag().name("Sync").description("Sincronización manual con Minibank y Aguas"),
                        new Tag().name("Reconciliations").description("Gestión de conciliaciones"),
                        new Tag().name("Deposits").description("Ajustes de depósitos"),
                        new Tag().name("Integrations").description("Estado de las integraciones externas"),
                        new Tag().name("Audit").description("Auditoría de operaciones")
                ));
    }
}
