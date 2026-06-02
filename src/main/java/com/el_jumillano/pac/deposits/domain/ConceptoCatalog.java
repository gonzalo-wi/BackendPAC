package com.el_jumillano.pac.deposits.domain;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class ConceptoCatalog {

    public static final List<ConceptoCode> CHEQUES = List.of(
            new ConceptoCode("CHE", "CHEQUE DIFERIDO"),
            new ConceptoCode("CHC", "CHEQUE COMUN"),
            new ConceptoCode("CNO", "CHEQUE NO A LA ORDEN"),
            new ConceptoCode("CTJ", "BCO CIUDAD CTAS JUDICIALES"),
            new ConceptoCode("DBF", "BCO. FRANCES 1382/2"),
            new ConceptoCode("DF2", "BBVA 0579/9 (JUMI)"),
            new ConceptoCode("DF3", "NACION 3275/1 (JUMI)"),
            new ConceptoCode("DF5", "SUPERVIELLE 5121/2 (LUFRAN)"),
            new ConceptoCode("ECH", "E-CHECK"),
            new ConceptoCode("EF2", "EFECTIVO CAJA 02"),
            new ConceptoCode("EFE", "EFECTIVO"),
            new ConceptoCode("GRP", "GROUPON"),
            new ConceptoCode("PMC", "PAGOMISCUENTAS")
    );

    public static final List<ConceptoCode> RETENCIONES = List.of(
            new ConceptoCode("RCI", "RETENCION TISH CIUDA."),
            new ConceptoCode("RGA", "RETENCION GANANCIAS"),
            new ConceptoCode("RIB", "RETENCION IIBB BS.AS"),
            new ConceptoCode("RIC", "RETENCION IIBB CABA"),
            new ConceptoCode("RIV", "RETENCION DE IVA"),
            new ConceptoCode("RLP", "RETENCION TISH LP"),
            new ConceptoCode("RMI", "RETENCIONES IB MISIONES"),
            new ConceptoCode("RSU", "RETENCION SUSS"),
            new ConceptoCode("CTJ", "BCO CIUDAD CTAS JUDICIALES"),
            new ConceptoCode("DBF", "BCO. FRANCES 1382/2"),
            new ConceptoCode("DF2", "BBVA 0579/9 (JUMI)"),
            new ConceptoCode("DF3", "NACION 3275/1 (JUMI)"),
            new ConceptoCode("DF5", "SUPERVIELLE 5121/2 (LUFRAN)"),
            new ConceptoCode("ECH", "E-CHECK"),
            new ConceptoCode("EF2", "EFECTIVO CAJA 02"),
            new ConceptoCode("EFE", "EFECTIVO"),
            new ConceptoCode("GRP", "GROUPON"),
            new ConceptoCode("PMC", "PAGOMISCUENTAS"),
            new ConceptoCode("CHC", "RETENCION TISH CIUDA")
    );

    private static final Set<String> VALID_CHEQUES =
            CHEQUES.stream().map(ConceptoCode::codigo).collect(Collectors.toSet());

    private static final Set<String> VALID_RETENCIONES =
            RETENCIONES.stream().map(ConceptoCode::codigo).collect(Collectors.toSet());

    public static boolean isValidCheque(String codigo) {
        return VALID_CHEQUES.contains(codigo);
    }

    public static boolean isValidRetencion(String codigo) {
        return VALID_RETENCIONES.contains(codigo);
    }

    private ConceptoCatalog() {}
}
