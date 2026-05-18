package com.olympics.winter;

import com.olympics.winter.entity.SkiSlalomResult;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class SkiSlalomResultTest {

    @Test
    void finalTimeIsSum_ofBothRuns() {
        SkiSlalomResult result = new SkiSlalomResult();
        result.setFirstRunTime(52.34);
        result.setSecondRunTime(51.10);
        result.setFirstRunDnf(false);
        result.setSecondRunDnf(false);

        result.calculateFinalTime();

        assertThat(result.getFinalTime()).isEqualTo(52.34 + 51.10, within(0.001));
    }

    @Test
    void firstRunDnf_finalTimeIsNull() {
        SkiSlalomResult result = new SkiSlalomResult();
        result.setFirstRunDnf(true);
        result.setSecondRunTime(51.0);
        result.setSecondRunDnf(false);

        result.calculateFinalTime();

        assertThat(result.getFinalTime()).isNull();
    }

    @Test
    void secondRunDnf_finalTimeIsNull() {
        SkiSlalomResult result = new SkiSlalomResult();
        result.setFirstRunTime(52.0);
        result.setFirstRunDnf(false);
        result.setSecondRunDnf(true);

        result.calculateFinalTime();

        assertThat(result.getFinalTime()).isNull();
    }
}
