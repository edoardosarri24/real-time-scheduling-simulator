package scheduler;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

import taskSet.Chunk;
import taskSet.Task;
import taskSet.TaskSet;

public class RMSchedulerTest {

    @Test
    public void constructorKO() {
        Task task = new Task(
            Duration.ofSeconds(10),
            Duration.ofSeconds(5),
            List.of(new Chunk(0, Duration.ofSeconds(1))));
        TaskSet taskSet = new TaskSet(Set.of(task));
        assertThatThrownBy(() -> new RMScheduler(taskSet))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Il task " + task.getId() + " non è puramente periocico: ha periodo PT10S e deadline PT5S");
    }
    
}