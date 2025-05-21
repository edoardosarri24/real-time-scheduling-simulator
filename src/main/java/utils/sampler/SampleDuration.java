package utils.sampler;

import java.math.BigDecimal;
import java.time.Duration;
import org.oristool.simulator.samplers.Sampler;

public class SampleDuration {

    /**
     * Samples a duration in milliseconds from the given sampler and returns it as a {@link Duration} object.
     * @param sampler the sampler from which to sample the value
     * @return the sampled duration as a {@link Duration} object
     */
    public static Duration sample(Sampler sampler) {
        BigDecimal sample = sampler.getSample();
        return Duration.ofMillis(sample.longValue());
    }
    
}