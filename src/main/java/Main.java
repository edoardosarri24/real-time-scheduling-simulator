import java.math.BigDecimal;
import java.time.Duration;


import org.oristool.simulator.samplers.UniformSampler;

import utils.Utils;
import utils.sampler.SampleDuration;

public class Main {
    public static void main(String[] args) {
        Duration sample = SampleDuration.sample((
            new UniformSampler(
                new BigDecimal(5),
                new BigDecimal(10))));
    Utils.durationPrinter(sample);
    }

}