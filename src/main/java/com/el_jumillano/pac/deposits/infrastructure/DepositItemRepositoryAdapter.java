package com.el_jumillano.pac.deposits.infrastructure;

import com.el_jumillano.pac.deposits.domain.Check;
import com.el_jumillano.pac.deposits.domain.Withholding;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DepositItemRepositoryAdapter {

    private final CheckJpaRepository checkRepo;
    private final WithholdingJpaRepository withholdingRepo;
    private final DepositItemMapper mapper;

    // ── Checks ──────────────────────────────────────────────────────────────

    public Check saveCheck(Check check) {
        return mapper.toDomain(checkRepo.save(mapper.toEntity(check)));
    }

    public List<Check> findChecksByDepositId(Long depositId) {
        return checkRepo.findByDepositId(depositId).stream().map(mapper::toDomain).toList();
    }

    public void deleteCheck(Long checkId) {
        checkRepo.deleteById(checkId);
    }

    public BigDecimal sumChecksByRouteAndPlantAndDate(Integer routeNumber, Long plantId, LocalDate date) {
        BigDecimal result = checkRepo.sumAmountByRouteAndPlantAndDate(routeNumber, plantId, date);
        return result != null ? result : BigDecimal.ZERO;
    }

    // ── Withholdings ─────────────────────────────────────────────────────────

    public Withholding saveWithholding(Withholding withholding) {
        return mapper.toDomain(withholdingRepo.save(mapper.toEntity(withholding)));
    }

    public List<Withholding> findWithholdingsByDepositId(Long depositId) {
        return withholdingRepo.findByDepositId(depositId).stream().map(mapper::toDomain).toList();
    }

    public void deleteWithholding(Long withholdingId) {
        withholdingRepo.deleteById(withholdingId);
    }

    public BigDecimal sumWithholdingsByRouteAndPlantAndDate(Integer routeNumber, Long plantId, LocalDate date) {
        BigDecimal result = withholdingRepo.sumAmountByRouteAndPlantAndDate(routeNumber, plantId, date);
        return result != null ? result : BigDecimal.ZERO;
    }
}
