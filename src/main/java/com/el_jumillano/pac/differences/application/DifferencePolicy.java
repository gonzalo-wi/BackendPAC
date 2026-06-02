package com.el_jumillano.pac.differences.application;
import com.el_jumillano.pac.differences.domain.DifferenceType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;


@Service
public class DifferencePolicy {

    private final BigDecimal surplusHighThreshold;
    private final BigDecimal shortageCriticalThreshold;

    public DifferencePolicy(
            @Value("${pac.difference.surplus-high-threshold:5000}") BigDecimal surplusHighThreshold,
            @Value("${pac.difference.shortage-critical-threshold:5000}") BigDecimal shortageCriticalThreshold) {
        this.surplusHighThreshold = surplusHighThreshold;
        this.shortageCriticalThreshold = shortageCriticalThreshold;
    }

    public DifferenceType classify(BigDecimal differenceAmount) {
        if (differenceAmount == null || differenceAmount.compareTo(BigDecimal.ZERO) == 0) {
            return DifferenceType.NONE;
        }
        if (differenceAmount.compareTo(BigDecimal.ZERO) > 0) {
            return differenceAmount.compareTo(surplusHighThreshold) >= 0
                    ? DifferenceType.SURPLUS_HIGH
                    : DifferenceType.SURPLUS_NORMAL;
        }
    
        BigDecimal absolute = differenceAmount.abs();
        return absolute.compareTo(shortageCriticalThreshold) >= 0
                ? DifferenceType.SHORTAGE_CRITICAL
                : DifferenceType.SHORTAGE;
    }
}
