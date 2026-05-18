package com.olympics.winter;

import com.olympics.winter.entity.BiathlonResult;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class BiathlonResultTest {

    @Test
    void penaltyCalculation_addsCorrectPenaltyPerMiss() {
        BiathlonResult result = new BiathlonResult();
        result.setSkiingTime(1500.0);
        result.setMissedShots(3);
        result.setDnf(false);

        result.calculateFinalTime();

        // 3 misses * 60s = 180s penalty
        assertThat(result.getPenaltyTime()).isEqualTo(180.0);
        assertThat(result.getFinalTime()).isEqualTo(1680.0);
    }

    @Test
    void penaltyCalculation_zeroMisses_noExtraPenalty() {
        BiathlonResult result = new BiathlonResult();
        result.setSkiingTime(1200.0);
        result.setMissedShots(0);
        result.setDnf(false);

        result.calculateFinalTime();

        assertThat(result.getPenaltyTime()).isEqualTo(0.0);
        assertThat(result.getFinalTime()).isEqualTo(1200.0);
    }

    @Test
    void dnf_resultIsNull() {
        BiathlonResult result = new BiathlonResult();
        result.setSkiingTime(500.0);
        result.setMissedShots(1);
        result.setDnf(true);

        result.calculateFinalTime();

        assertThat(result.getFinalTime()).isNull();
        assertThat(result.getPenaltyTime()).isNull();
    }
}
