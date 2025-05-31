package utils;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.oristool.simulator.samplers.UniformSampler;

import taskSet.Chunk;
import taskSet.Task;
import utils.sampler.SampleDuration;

import static org.assertj.core.api.Assertions.*;

public class UtilsTest {

    @Test
    public void durationPrinter() {
        MyClock.reset();
        MyClock.getInstance().advanceTo(Duration.ofMillis(5));
        assertThat(Utils.printCurrentTime())
            .isEqualTo("5.000");
    }

    @Test
    public void generatePeriodsUpToLCM1() {
        List<Duration> input = List.of(
            Duration.ofMillis(5),
            Duration.ofMillis(10),
            Duration.ofMillis(3)
        );
        List<Duration> output = Utils.generatePeriodUpToLCM(input);
        assertThat(output).containsExactly(
            Duration.ofMillis(3),
            Duration.ofMillis(5),
            Duration.ofMillis(6),
            Duration.ofMillis(9),
            Duration.ofMillis(10),
            Duration.ofMillis(12),
            Duration.ofMillis(15),
            Duration.ofMillis(18),
            Duration.ofMillis(20),
            Duration.ofMillis(21),
            Duration.ofMillis(24),
            Duration.ofMillis(25),
            Duration.ofMillis(27),
            Duration.ofMillis(30)
        );
    }

    @Test
    public void generatePeriodsUpToLCM2() {
        List<Duration> input = List.of(
            Duration.ofMillis(750),
            Duration.ofMillis(75)
        );
        List<Duration> output = Utils.generatePeriodUpToLCM(input);
        assertThat(output).containsExactly(
            Duration.ofMillis(75),
            Duration.ofMillis(150),
            Duration.ofMillis(225),
            Duration.ofMillis(300),
            Duration.ofMillis(375),
            Duration.ofMillis(450),
            Duration.ofMillis(525),
            Duration.ofMillis(600),
            Duration.ofMillis(675),
            Duration.ofMillis(750)
        );
    }

    @Test
    public void generatePeriodsUpToLCM3() {
        List<Duration> input = List.of(
            Duration.ofMillis(4),
            Duration.ofMillis(4),
            Duration.ofMillis(4)
        );
        List<Duration> output = Utils.generatePeriodUpToLCM(input);
        assertThat(output).containsExactly(
            Duration.ofMillis(4)
        );
    }

    @Test
    @Ignore
    public void generatePeriodsUpToLCM4() {
        List<Duration> input = List.of(
            SampleDuration.sample(new UniformSampler(new BigDecimal(1), new BigDecimal(2))),
            SampleDuration.sample(new UniformSampler(new BigDecimal(1), new BigDecimal(2)))
        );
        List<Duration> output = Utils.generatePeriodUpToLCM(input);
        assertThat(output).containsExactly(
            Duration.ofMillis(4)
        );
    }

    @Test
    public void generateDeadlineUpToLCM() {
        Chunk chunk = new Chunk(0, Duration.ofMillis(10));
        Set<Task> input = Set.of(
            new Task(Duration.ofMillis(10), Duration.ofMillis(8), List.of(chunk)),
            new Task(Duration.ofMillis(15), Duration.ofMillis(5), List.of(chunk))
        );
        List<Duration> output = Utils.generateDeadlineUpToLCM(input);
        assertThat(output).containsExactly(
            Duration.ofMillis(5),
            Duration.ofMillis(8),
            Duration.ofMillis(18),
            Duration.ofMillis(20),
            Duration.ofMillis(28)
        );
    }

}