package taskSet;

import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class TaskTest {

    private Task task;

    @Before
    public void setUp() {
        this.task = new Task(
            Duration.ofSeconds(10),
            Duration.ofSeconds(10),
            List.of(new Chunk(0, Duration.ofSeconds(10))));
    }
    
}