package scheduler;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import helper.ReflectionUtils;
import taskSet.Chunk;
import taskSet.Task;
import taskSet.TaskSet;
import utils.MyClock;
import utils.sampler.ConstantSampler;

import static org.assertj.core.api.Assertions.*;

public class SchedulerTest {

    @SuppressWarnings("unchecked")
    @Test
    public void initStructures1() {
        MyClock.reset();
        Chunk chunk = new Chunk(1, new ConstantSampler(new BigDecimal(1)));
        Task task1 = new Task(
        new BigDecimal(4),
        new BigDecimal(4),
        List.of(chunk));
        Task task2 = new Task(
            new BigDecimal(5),
            new BigDecimal(5),
            List.of(chunk));
        EDFScheduler scheduler = new EDFScheduler(new TaskSet(Set.of(task1, task2)), Duration.ofMillis(43));
        List<Duration> events = (List<Duration>) ReflectionUtils.invokeMethod(scheduler, "initStructures");
        assertThat(events.getLast())
            .isEqualTo(Duration.ofMillis(40));
    }

}