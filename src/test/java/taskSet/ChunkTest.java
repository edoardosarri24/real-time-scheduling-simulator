package taskSet;

import org.junit.Before;
import org.junit.Test;
import helper.ReflectionUtils;
import java.time.Duration;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

public class ChunkTest {

    private Chunk chunk;
    private Task task;

    @Before
    public void setUp() {
        this.chunk = new Chunk(0, Duration.ofSeconds(10));
        this.task = new Task(
            Duration.ofSeconds(10),
            Duration.ofSeconds(10),
            List.of(this.chunk));
    }

    @Test
    public void execute() {
        assertThat(chunk.getRemainingExecutionTime())
            .isEqualTo(Duration.ofSeconds(10));
        chunk.execute(Duration.ofSeconds(4), this.task);
        assertThat(chunk.getRemainingExecutionTime())
            .isEqualTo(Duration.ofSeconds(6));
        chunk.execute(Duration.ofSeconds(6), this.task);
        assertThat(chunk.getRemainingExecutionTime())
            .isEqualTo(Duration.ZERO);
    }

    @Test
    public void reset() {
        assertThat(this.chunk.getRemainingExecutionTime())
            .isEqualTo(Duration.ofSeconds(10));
        ReflectionUtils.setField(
            this.chunk,
            "remainingExecutionTime",
            Duration.ofSeconds(3));
        this.chunk.reset();
        assertThat(this.chunk.getRemainingExecutionTime())
            .isEqualTo(Duration.ofSeconds(10));
    }
    
}