package scheduler;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

import exeptions.DeadlineMissedException;
import helper.ReflectionUtils;
import resource.Resource;

import static org.assertj.core.api.Assertions.*;
import taskSet.Chunk;
import taskSet.Task;
import taskSet.TaskSet;

public class RMSchedulerTest {

    private Chunk chunk;

    @Before
    public void setUp() {
        this.chunk = new Chunk(0, Duration.ofSeconds(1));
    }

    @Test
    public void constructorKO() {
        Task task = new Task(
            Duration.ofSeconds(10),
            Duration.ofSeconds(5),
            List.of(this.chunk));
        TaskSet taskSet = new TaskSet(Set.of(task));
        assertThatThrownBy(() -> new RMScheduler(taskSet))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Il task " + task.getId() + " non è puramente periodico: ha periodo PT10S e deadline PT5S");
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

    @Test
    public void scheduleWRes1() {
        Resource res1 = new Resource();
        Resource res2 = new Resource();
        Task task0 = new Task(
            Duration.ofSeconds(20),
            Duration.ofSeconds(20),
            List.of(new Chunk(0, Duration.ofSeconds(1)),
                    new Chunk(1, Duration.ofSeconds(2), List.of(res1))));
        Task task1 = new Task(
            Duration.ofSeconds(50),
            Duration.ofSeconds(50),
            List.of(new Chunk(2, Duration.ofSeconds(2), List.of(res1)),
                    new Chunk(3, Duration.ofSeconds(1)),
                    new Chunk(4, Duration.ofSeconds(2), List.of(res2))));
        Task task2 = new Task(
            Duration.ofSeconds(100),
            Duration.ofSeconds(100),
            List.of(new Chunk(5, Duration.ofSeconds(3), List.of(res2)),
                    new Chunk(6, Duration.ofSeconds(1)),
                    new Chunk(7, Duration.ofSeconds(2))));
        TaskSet taskset = new TaskSet(Set.of(task0, task1, task2));
        RMScheduler scheduler = new RMScheduler(taskset);
        assertThatCode(() -> scheduler.schedule())
            .doesNotThrowAnyException();
    }
    
}