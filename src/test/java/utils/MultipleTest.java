package utils;

import static org.assertj.core.api.Assertions.*;

import java.time.Duration;
import java.util.List;

import org.junit.Test;

public class MultipleTest {

    @Test
    public void generateMultiplesUpToLCM1() {
        List<Duration> input = List.of(
            Duration.ofSeconds(5),
            Duration.ofSeconds(10),
            Duration.ofSeconds(3)
        );
        List<Duration> output = Multiple.generateMultiplesUpToLCM(input);
        assertThat(output).containsExactly(
            Duration.ofSeconds(3),
            Duration.ofSeconds(5),
            Duration.ofSeconds(6),
            Duration.ofSeconds(9),
            Duration.ofSeconds(10),
            Duration.ofSeconds(12),
            Duration.ofSeconds(15),
            Duration.ofSeconds(18),
            Duration.ofSeconds(20),
            Duration.ofSeconds(21),
            Duration.ofSeconds(24),
            Duration.ofSeconds(25),
            Duration.ofSeconds(27),
            Duration.ofSeconds(30)
        );
    }

    @Test
    public void generateMultiplesUpToLCM2() {
        List<Duration> input = List.of(
            Duration.ofMillis(750),
            Duration.ofSeconds(2)
        );
        List<Duration> output = Multiple.generateMultiplesUpToLCM(input);
        assertThat(output).containsExactly(
            Duration.ofMillis(750),
            Duration.ofMillis(1500),
            Duration.ofSeconds(2),
            Duration.ofMillis(2250),
            Duration.ofSeconds(3),
            Duration.ofMillis(3750),
            Duration.ofSeconds(4),
            Duration.ofMillis(4500),
            Duration.ofMillis(5250),
            Duration.ofSeconds(6)
        );
    }

    @Test
    public void generateMultiplesUpToLCM3() {
        List<Duration> input = List.of(
            Duration.ofSeconds(4),
            Duration.ofSeconds(4),
            Duration.ofSeconds(4)
        );
        List<Duration> output = Multiple.generateMultiplesUpToLCM(input);
        assertThat(output).containsExactly(
            Duration.ofSeconds(4)
        );
    }
    
}