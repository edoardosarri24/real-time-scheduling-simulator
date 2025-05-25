package taskSet;

import org.junit.Before;
import org.junit.Test;
import helper.ReflectionUtils;
import utils.MyClock;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.*;

public class ChunkTest {

    private Chunk chunk;

    @Before
    public void setUp() {
        this.chunk = new Chunk(0, Duration.ofSeconds(10));
        new Task(
            Duration.ofSeconds(10),
            Duration.ofSeconds(10),
            List.of(this.chunk));
        MyClock.reset();
    }

    @Test
    public void reset() {
        Supplier<Duration> remainingExecutionTime = (() -> (Duration) ReflectionUtils.getField(this.chunk, "remainingExecutionTime"));
        assertThat(remainingExecutionTime.get())
            .isEqualTo(Duration.ofSeconds(10));
        ReflectionUtils.setField(
            this.chunk,
            "remainingExecutionTime",
            Duration.ofSeconds(3));
        this.chunk.reset();
        assertThat(remainingExecutionTime.get())
            .isEqualTo(Duration.ofSeconds(10));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void exectuteRemainingExecutionTime() {
        Duration availableTime = Duration.ofSeconds(4);
        Duration result = this.chunk.execute(availableTime);
        assertThat(ReflectionUtils.getField(this.chunk, "remainingExecutionTime"))
            .isEqualTo(Duration.ofSeconds(6));
        List<Chunk> chunkToExecute = (List<Chunk>) ReflectionUtils.getField(this.chunk.getParent(), "chunkToExecute");
        assertThat(chunkToExecute.getFirst())
            .isSameAs(this.chunk);
        assertThat(result)
            .isEqualTo(Duration.ofSeconds(4));
        assertThat(MyClock.getInstance().getCurrentTime())
            .isEqualTo(Duration.ofSeconds(4));
    }

    @Test
    public void executeAvailableTime() {
        Duration availableTime = Duration.ofSeconds(12);
        Duration result = this.chunk.execute(availableTime);
        assertThat(result)
            .isEqualTo(Duration.ofSeconds(10));
        assertThat(MyClock.getInstance().getCurrentTime())
            .isEqualTo(Duration.ofSeconds(10));
    }

}