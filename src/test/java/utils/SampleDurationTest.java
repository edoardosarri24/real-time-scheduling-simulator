package utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Duration;

import org.junit.Test;
import org.oristool.simulator.samplers.UniformSampler;

import utils.sampler.SampleDuration;

public class SampleDurationTest {

    @Test
    public void sample() {
        UniformSampler sampler = new UniformSampler(new BigDecimal(5), new BigDecimal(10));
        Duration sample = SampleDuration.sample(sampler);
        assertThat(sample)
            .isGreaterThanOrEqualTo(Duration.ofMillis(5))
            .isLessThanOrEqualTo((Duration.ofMillis(10)));
    }
    
}