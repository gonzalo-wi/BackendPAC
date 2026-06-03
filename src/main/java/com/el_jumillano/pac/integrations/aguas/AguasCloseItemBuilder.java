package com.el_jumillano.pac.integrations.aguas;

import com.el_jumillano.pac.deposits.infrastructure.CheckJpaEntity;
import com.el_jumillano.pac.deposits.infrastructure.WithholdingJpaEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Construye los strings de cheques y retenciones en el formato
 * JSON-like que espera el servicio reparto_cerrar de Aguas.
 *
 * Cheques:    [{nrocta:1,concepto:"CHE",banco12:BBVA,sucursal:"001",localidad:"1234",nro_cheque:"00011086",nro_cuenta:1234,titular:"",fecha:"15/07/2025",importe:15000}]
 * Retenciones:[{nrocta:1,concepto:RIB,nro_retencion:11086,fecha:15/07/2025,importe:15000}]
 * Sin ítems:  0
 */
@Component
public class AguasCloseItemBuilder {

    private static final DateTimeFormatter OUTPUT_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final List<DateTimeFormatter> INPUT_FMTS = List.of(
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ofPattern("d/M/yyyy")
    );

    public String buildChecks(List<CheckJpaEntity> checks) {
        if (checks == null || checks.isEmpty()) return "0";

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < checks.size(); i++) {
            if (i > 0) sb.append(",");
            CheckJpaEntity c = checks.get(i);
            sb.append("{");
            sb.append("nrocta:").append(orZero(c.getAccountNumber())).append(",");
            sb.append("concepto:\"").append(safe(c.getConcepto())).append("\",");
            sb.append("banco12:").append(safe(c.getBank())).append(",");
            sb.append("sucursal:\"").append(safe(c.getBranch())).append("\",");
            sb.append("localidad:\"").append(safe(c.getLocality())).append("\",");
            sb.append("nro_cheque:\"").append(safe(c.getCheckNumber())).append("\",");
            sb.append("nro_cuenta:").append(c.getAccountCode() != null ? c.getAccountCode() : 0).append(",");
            sb.append("titular:\"").append(safe(c.getHolder())).append("\",");
            sb.append("fecha:\"").append(formatDate(c.getPaymentDate())).append("\",");
            sb.append("importe:").append(c.getAmount().toPlainString());
            sb.append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    public String buildWithholdings(List<WithholdingJpaEntity> withholdings) {
        if (withholdings == null || withholdings.isEmpty()) return "0";

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < withholdings.size(); i++) {
            if (i > 0) sb.append(",");
            WithholdingJpaEntity w = withholdings.get(i);
            sb.append("{");
            sb.append("nrocta:").append(orZero(w.getAccountNumber())).append(",");
            sb.append("concepto:").append(safe(w.getConcepto())).append(",");
            sb.append("nro_retencion:").append(safe(w.getWithholdingNumber())).append(",");
            sb.append("fecha:").append(formatDate(w.getPaymentDate())).append(",");
            sb.append("importe:").append(w.getAmount().toPlainString());
            sb.append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    private String formatDate(String raw) {
        if (raw == null || raw.isBlank()) return "";
        for (DateTimeFormatter fmt : INPUT_FMTS) {
            try {
                return LocalDate.parse(raw.trim(), fmt).format(OUTPUT_FMT);
            } catch (Exception ignored) {}
        }
        return raw;
    }

    private String safe(String v) {
        return v != null ? v : "";
    }

    private long orZero(Long v) {
        return v != null ? v : 0L;
    }
}
