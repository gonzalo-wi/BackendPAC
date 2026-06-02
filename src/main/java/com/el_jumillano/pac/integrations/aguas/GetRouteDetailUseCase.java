package com.el_jumillano.pac.integrations.aguas;

import com.el_jumillano.pac.reconciliation.domain.Reconciliation;
import com.el_jumillano.pac.reconciliation.infrastructure.ReconciliationRepositoryAdapter;
import com.el_jumillano.pac.shared.exception.EntityNotFoundException;
import com.el_jumillano.pac.shared.exception.IntegrationUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetRouteDetailUseCase {

    private final AguasDetailJsonClient detailClient;
    private final ReconciliationRepositoryAdapter reconciliationRepository;

    @CircuitBreaker(name = "aguas", fallbackMethod = "fallback")
    @Retry(name = "aguas")
    public RouteDetailResponse execute(Long reconciliationId) {
        Reconciliation rec = reconciliationRepository.findById(reconciliationId)
                .orElseThrow(() -> new EntityNotFoundException("Reconciliation", reconciliationId));

        // Este endpoint usa fecha en formato YYYY-MM-DD (ISO), distinto al dd/MM/yyyy de los otros
        String fecha = rec.getDate().toString();

        AguasRouteDetailDto dto = detailClient.getRouteDetail(rec.getRouteNumber(), fecha, true);

        if (!dto.isSuccess() || dto.getResultado() == null) {
            log.warn("[RouteDetail] Aguas devolvió success=false para reparto {} fecha {}", rec.getRouteNumber(), fecha);
            return new RouteDetailResponse(rec.getRouteNumber(), fecha, null, List.of());
        }

        List<RouteDetailResponse.ClientPaymentResponse> payments = dto.getResultado().stream()
                .filter(c -> c.getDetalle() != null && !c.getDetalle().isEmpty())
                .map(c -> new RouteDetailResponse.ClientPaymentResponse(
                        c.getNrocta(),
                        c.getDetalle().stream()
                                .map(item -> new RouteDetailResponse.PaymentItemResponse(
                                        item.getMonto(), item.getTipoValor()))
                                .toList()
                ))
                .toList();

        return new RouteDetailResponse(rec.getRouteNumber(), fecha, dto.getTotal(), payments);
    }

    public RouteDetailResponse fallback(Long reconciliationId, Throwable t) {
        throw new IntegrationUnavailableException("Aguas (detalle)", t);
    }
}
