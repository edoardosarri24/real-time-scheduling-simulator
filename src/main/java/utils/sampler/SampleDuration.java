package utils.sampler;

import java.math.BigDecimal;
import java.time.Duration;
import org.oristool.simulator.samplers.Sampler;

/**
 * Samples a duration in milliseconds from the given sampler and returns it as a {@link Duration} object.
 * @param sampler the sampler from which to sample the value
 * @return the sampled duration as a {@link Duration} object
 */
public class SampleDuration {

    public static Duration sample(Sampler sampler) {
        BigDecimal sample = sampler.getSample();
        sample = sample.multiply(BigDecimal.TEN.pow(6));
        return Duration.ofNanos(sample.longValue());
    }

}