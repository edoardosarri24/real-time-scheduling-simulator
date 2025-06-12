package taskSet;

import org.junit.Before;
import org.junit.Test;
import org.oristool.simulator.samplers.UniformSampler;

import helper.ReflectionUtils;
import scheduler.RMScheduler;
import utils.MyClock;
import utils.sampler.ConstantSampler;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.*;

public class ChunkTest {

    private Chunk chunk;

    @Before
    public void setUp() {
        this.chunk = new Chunk(0, new ConstantSampler(new BigDecimal(10)));
        new Task(
            new BigDecimal(10),
            new BigDecimal(10),
            List.of(this.chunk));
        MyClock.reset();
    }

    @Test
    public void reset() {
        Supplier<Duration> remainingExecutionTime = (() -> (Duration) ReflectionUtils.getField(this.chunk, "remainingExecutionTime"));
        assertThat(remainingExecutionTime.get())
            .isEqualTo(Duration.ofMillis(10));
        ReflectionUtils.setField(
            this.chunk,
            "remainingExecutionTime",
            Duration.ofMillis(3));
        this.chunk.reset();
        assertThat(remainingExecutionTime.get())
            .isEqualTo(Duration.ofMillis(10));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void exectuteRemainingExecutionTime() {
        Duration availableTime = Duration.ofMillis(4);
        Duration result = this.chunk.execute(availableTime);
        assertThat(ReflectionUtils.getField(this.chunk, "remainingExecutionTime"))
            .isEqualTo(Duration.ofMillis(6));
        List<Chunk> chunkToExecute = (List<Chunk>) ReflectionUtils.getField(this.chunk.getParent(), "chunkToExecute");
        assertThat(chunkToExecute.getFirst())
            .isSameAs(this.chunk);
        assertThat(result)
            .isEqualTo(Duration.ofMillis(4));
        assertThat(MyClock.getInstance().getCurrentTime())
            .isEqualTo(Duration.ofMillis(4));
    }

    @Test
    public void executeAvailableTime() {
        Duration availableTime = Duration.ofMillis(12);
        Duration result = this.chunk.execute(availableTime);
        assertThat(result)
            .isEqualTo(Duration.ofMillis(10));
        assertThat(MyClock.getInstance().getCurrentTime())
            .isEqualTo(Duration.ofMillis(10));
    }

    // tested with the log in trace.log
    @Test
    public void executeWOverhead() {
        Chunk chunk = new Chunk(0, new ConstantSampler(new BigDecimal(10)), new ConstantSampler(new BigDecimal(2)));
        Task task = new Task(
            new BigDecimal(20),
            new BigDecimal(20),
            List.of(chunk));
        RMScheduler scheduler = new RMScheduler(new TaskSet(Set.of(task)), Duration.ofMillis(20));
        assertThatCode(() -> scheduler.schedule())
            .doesNotThrowAnyException();
    }

    @Test
    public void samplingDiffentTime() {
        Chunk chunk = new Chunk(0, new UniformSampler(new BigDecimal(1), new BigDecimal(100)));
        Duration executionTime1 = (Duration) ReflectionUtils.getField(chunk, "executionTime");
        chunk.reset();
        Duration executionTime2 = (Duration) ReflectionUtils.getField(chunk, "executionTime");
        assertThat(executionTime1)
            .isNotEqualTo(executionTime2);
    }

}