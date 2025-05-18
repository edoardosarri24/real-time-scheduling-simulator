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
    
}