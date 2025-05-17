package scheduler;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import org.junit.Test;

import exeptions.DeadlineMissedException;
import resource.PriorityCeilingProtocol;
import resource.Resource;

import static org.assertj.core.api.Assertions.*;
import taskSet.Chunk;
import taskSet.Task;
import taskSet.TaskSet;

public class RMSchedulerTest {

    @Test
    public void schedule1() {
        Task task0 = new Task(
            Duration.ofSeconds(20),
            Duration.ofSeconds(20),
            List.of(new Chunk(0, Duration.ofSeconds(10)),
                    new Chunk(1, Duration.ofSeconds(3))));
        Task task1 = new Task(
            Duration.ofSeconds(50),
            Duration.ofSeconds(50),
            List.of(new Chunk(2, Duration.ofSeconds(5)),
                    new Chunk(3, Duration.ofSeconds(2))));
        Task task2 = new Task(
            Duration.ofSeconds(100),
            Duration.ofSeconds(100),
            List.of(new Chunk(4, Duration.ofSeconds(4))));
        TaskSet taskset = new TaskSet(Set.of(task0, task1, task2));
        RMScheduler scheduler = new RMScheduler(taskset);
        assertThatCode(() -> scheduler.schedule())
            .doesNotThrowAnyException();
    }

    @Test
    public void schedule2() {
        Task task0 = new Task(
            Duration.ofSeconds(20),
            Duration.ofSeconds(20),
            List.of(new Chunk(0, Duration.ofSeconds(10)),
                    new Chunk(1, Duration.ofSeconds(3))));
        Task task1 = new Task(
            Duration.ofSeconds(50),
            Duration.ofSeconds(50),
            List.of(new Chunk(2, Duration.ofSeconds(5)),
                    new Chunk(3, Duration.ofSeconds(22))));
        Task task2 = new Task(
            Duration.ofSeconds(100),
            Duration.ofSeconds(100),
            List.of(new Chunk(4, Duration.ofSeconds(4))));
        TaskSet taskset = new TaskSet(Set.of(task0, task1, task2));
        RMScheduler scheduler = new RMScheduler(taskset);
        assertThatThrownBy(() -> scheduler.schedule())
            .isInstanceOf(DeadlineMissedException.class)
            .hasMessage("Il task " + task1.getId() + " ha superato la deadline");
    }

    @Test
    public void schedule3() {
        Task task0 = new Task(
            Duration.ofSeconds(20),
            Duration.ofSeconds(20),
            List.of(new Chunk(0, Duration.ofSeconds(10)),
                    new Chunk(1, Duration.ofSeconds(3))));
        Task task1 = new Task(
            Duration.ofSeconds(50),
            Duration.ofSeconds(50),
            List.of(new Chunk(2, Duration.ofSeconds(5)),
                    new Chunk(3, Duration.ofSeconds(2))));
        Task task2 = new Task(
            Duration.ofSeconds(100),
            Duration.ofSeconds(100),
            List.of(new Chunk(4, Duration.ofSeconds(42))));
        TaskSet taskset = new TaskSet(Set.of(task0, task1, task2));
        RMScheduler scheduler = new RMScheduler(taskset);
        assertThatThrownBy(() -> scheduler.schedule())
            .isInstanceOf(DeadlineMissedException.class)
            .hasMessage("Il task " + task2.getId() + " ha superato la deadline");
    }

    @Test
    public void schedule4() {
        Task task0 = new Task(
            Duration.ofSeconds(20),
            Duration.ofSeconds(20),
            List.of(new Chunk(0, Duration.ofSeconds(10)),
                    new Chunk(1, Duration.ofSeconds(11))));
        Task task1 = new Task(
            Duration.ofSeconds(50),
            Duration.ofSeconds(50),
            List.of(new Chunk(2, Duration.ofSeconds(5)),
                    new Chunk(3, Duration.ofSeconds(2))));
        Task task2 = new Task(
            Duration.ofSeconds(100),
            Duration.ofSeconds(100),
            List.of(new Chunk(4, Duration.ofSeconds(42))));
        TaskSet taskset = new TaskSet(Set.of(task0, task1, task2));
        RMScheduler scheduler = new RMScheduler(taskset);
        assertThatThrownBy(() -> scheduler.schedule())
            .isInstanceOf(DeadlineMissedException.class)
            .hasMessage("Il task " + task0.getId() + " ha superato la deadline");
    }
    
}