package utils;

import java.math.BigDecimal;
import java.time.Duration;
import org.junit.Test;
import utils.sampler.ConstantSampler;
import utils.sampler.SampleDuration;
import static org.assertj.core.api.Assertions.*;

public class UtilsTest {

    @Test
    public void durationPrinter() {
        Duration sample = SampleDuration.sample(new ConstantSampler(new BigDecimal(1.1234567)));
        assertThat(Utils.durationPrinter(sample))
            .isEqualTo("1.123");
    }

}