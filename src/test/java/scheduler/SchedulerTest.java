package scheduler;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import helper.ReflectionUtils;
import taskSet.Chunk;
import taskSet.Task;
import taskSet.TaskSet;
import static org.assertj.core.api.Assertions.*;

public class SchedulerTest {

    @SuppressWarnings("unchecked")
    @Test
    public void initStructures1() {
        Chunk chunk = new Chunk(1, Duration.ofMillis(1));
        Task task1 = new Task(
        Duration.ofMillis(4),
        Duration.ofMillis(4),
        List.of(chunk));
        Task task2 = new Task(
            Duration.ofMillis(5),
            Duration.ofMillis(5),
            List.of(chunk));
        EDFScheduler scheduler = new EDFScheduler(new TaskSet(Set.of(task1, task2)), Duration.ofMillis(43));
        List<Duration> events = (List<Duration>) ReflectionUtils.invokeMethod(scheduler, "initStructures");
        assertThat(events.getLast())
            .isEqualTo(Duration.ofMillis(40));
    }

}