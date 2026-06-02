package com.el_jumillano.pac.integrations.health;

import com.el_jumillano.pac.differences.domain.DifferenceRecord;

public interface DifferenceClient {

    void notifyDifference(DifferenceRecord difference);
}
