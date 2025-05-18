package scheduler;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import helper.ReflectionUtils;
import taskSet.Chunk;
import taskSet.Task;
import taskSet.TaskSet;

import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

public class RMSchedulerTest {

    private Chunk chunk;

    @Before
    public void setup() {
        this.chunk = new Chunk(0, Duration.ofSeconds(1));
    }

    @Test
    public void assignPriority() {
        Task task0 = new Task(
            Duration.ofSeconds(10),
            Duration.ofSeconds(10),
            List.of(this.chunk));
        Task task1 = new Task(
            Duration.ofSeconds(5),
            Duration.ofSeconds(5),
            List.of(this.chunk));
        Task task2 = new Task(
            Duration.ofSeconds(15),
            Duration.ofSeconds(15),
            List.of(this.chunk));
        assertThat(task0.getNominalPriority())
            .isEqualTo((int) ReflectionUtils.getField(task0, "dinamicPriority"))
            .isZero();
        assertThat(task1.getNominalPriority())
            .isEqualTo((int) ReflectionUtils.getField(task1, "dinamicPriority"))
            .isZero();
        assertThat(task2.getNominalPriority())
            .isEqualTo((int) ReflectionUtils.getField(task2, "dinamicPriority"))
            .isZero();
        new RMScheduler(new TaskSet(Set.of(task0, task1, task2)));
        assertThat(task1.getNominalPriority())
            .isEqualTo((int) ReflectionUtils.getField(task1, "dinamicPriority"))
            .isEqualTo(5);
        assertThat(task0.getNominalPriority())
            .isEqualTo((int) ReflectionUtils.getField(task0, "dinamicPriority"))
            .isEqualTo(7);
        assertThat(task2.getNominalPriority())
            .isEqualTo((int) ReflectionUtils.getField(task2, "dinamicPriority"))
            .isEqualTo(9);
    }

    @Test
    public void relasePeriodTasks() throws Exception {
        Task task0 = new Task(
            Duration.ofSeconds(7),
            Duration.ofSeconds(7),
            List.of(this.chunk));
        Task task1 = new Task(
            Duration.ofSeconds(5),
            Duration.ofSeconds(5),
            List.of(this.chunk));
        ReflectionUtils.setField(task1, "isExecuted", true);
        Task task2 = new Task(
            Duration.ofSeconds(15),
            Duration.ofSeconds(15),
            List.of(this.chunk));
        TaskSet taskSet = new TaskSet(Set.of(task0, task1, task2));
        RMScheduler scheduler = new RMScheduler(taskSet);
        TreeSet<Task> readyTasks = new TreeSet<>(Comparator.comparingInt(Task::getDinamicPriority));
        Duration currentTime = Duration.ofSeconds(10);
        readyTasks.add(task0);
        readyTasks.add(task2);

        ReflectionUtils.invokeMethod(
            scheduler,
            "relasePeriodTasks",
            new Class<?>[]{TreeSet.class, Duration.class},
            new Object[]{readyTasks, currentTime});
        assertThat(readyTasks)
            .containsExactly(task1, task0, task2);
    }

    @Test
    public void schedule() {
        Chunk chunk0 = new Chunk(0, Duration.ofSeconds(2));
        Chunk chunk1 = new Chunk(1, Duration.ofSeconds(4));
        Chunk chunk2 = new Chunk(2, Duration.ofSeconds(2));
        Chunk chunk3 = new Chunk(3, Duration.ofSeconds(3));
        Chunk chunk4 = new Chunk(4, Duration.ofSeconds(2));
        Chunk chunk5 = new Chunk(5, Duration.ofSeconds(1));
        Chunk chunk6 = new Chunk(6, Duration.ofSeconds(5));
        Chunk chunk7  = new Chunk(7, Duration.ofSeconds(4));
        Task task0 = new Task(
            Duration.ofSeconds(20),
            Duration.ofSeconds(20),
            List.of(chunk0));
        Task task1 = new Task(
            Duration.ofSeconds(50),
            Duration.ofSeconds(50),
            List.of(chunk1, chunk2));
        Task task2 = new Task(
            Duration.ofSeconds(100),
            Duration.ofSeconds(100),
            List.of(chunk3, chunk4, chunk5));
        Task task3 = new Task(
            Duration.ofSeconds(200),
            Duration.ofSeconds(200),
            List.of(chunk6, chunk7));

        TaskSet taskSet = new TaskSet(Set.of(task0, task1, task2, task3));
        RMScheduler scheduler = new RMScheduler(taskSet);
        assertThatCode(() -> scheduler.schedule())
            .doesNotThrowAnyException();
    }
    
}