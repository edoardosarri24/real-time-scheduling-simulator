package scheduler;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import helper.ReflectionUtils;
import resource.PriorityCeilingProtocol;
import resource.Resource;
import resource.ResourcesProtocol;
import taskSet.Chunk;
import taskSet.Task;
import taskSet.TaskSet;
import utils.MyClock;
import utils.sampler.ConstantSampler;
import org.junit.Before;
import org.junit.Test;
import exeptions.DeadlineMissedException;
import static org.assertj.core.api.Assertions.*;

public class RMSchedulerTest {

    private Chunk chunk;

    @Before
    public void setup() {
        this.chunk = new Chunk(0, new ConstantSampler(new BigDecimal(1)));
        MyClock.reset();
    }

    @Test
    public void assignPriority() {
        Task task0 = new Task(
            10,
            10,
            List.of(this.chunk));
        Task task1 = new Task(
            5,
            5,
            List.of(this.chunk));
        Task task2 = new Task(
            15,
            15,
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
        new RMScheduler(new TaskSet(Set.of(task0, task1, task2)), 30);
        assertThat(task1.getNominalPriority())
            .isEqualTo((int) ReflectionUtils.getField(task1, "dinamicPriority"))
            .isEqualTo(1);
        assertThat(task0.getNominalPriority())
            .isEqualTo((int) ReflectionUtils.getField(task0, "dinamicPriority"))
            .isEqualTo(2);
        assertThat(task2.getNominalPriority())
            .isEqualTo((int) ReflectionUtils.getField(task2, "dinamicPriority"))
            .isEqualTo(3);
    }

    @Test
    public void relasePeriodTasks() throws Exception {
        Task task0 = new Task(
            7,
            7,
            List.of(this.chunk));
        Task task1 = new Task(
            5,
            5,
            List.of(this.chunk));
        ReflectionUtils.setField(task1, "isExecuted", true);
        Task task2 = new Task(
            15,
            15,
            List.of(this.chunk));
        TaskSet taskSet = new TaskSet(Set.of(task0, task1, task2));
        RMScheduler scheduler = new RMScheduler(taskSet, 105);
        TreeSet<Task> readyTasks = new TreeSet<>(Comparator.comparingInt(Task::getDinamicPriority));
        ReflectionUtils.setField(scheduler, "readyTasks", readyTasks);
        readyTasks.add(task0);
        readyTasks.add(task2);
        MyClock.getInstance().advanceBy(Duration.ofMillis(5));
        ReflectionUtils.invokeMethod(scheduler, "relasePeriodTasks");
        assertThat(readyTasks)
            .contains(task1);
    }

    // tested also with the trace generated by the scheduler
    @Test
    public void scheduleWOResourceOK() {
        Task task1 = new Task(
            20,
            20,
            List.of(
                new Chunk(1, new ConstantSampler(new BigDecimal(4)))));
        Task task2 = new Task(
            50,
            50,
            List.of(
                new Chunk(1, new ConstantSampler(new BigDecimal(6))),
                new Chunk(2, new ConstantSampler(new BigDecimal(3))),
                new Chunk(3, new ConstantSampler(new BigDecimal(3)))));
        Task task3 = new Task(
            100,
            100,
            List.of(
                new Chunk(1, new ConstantSampler(new BigDecimal(10)))));
        TaskSet taskSet = new TaskSet(Set.of(task1, task2, task3));
        assertThat(taskSet.hyperbolicBoundTest()).isTrue();
        RMScheduler scheduler = new RMScheduler(taskSet, 100);
        assertThatCode(() -> scheduler.schedule())
            .doesNotThrowAnyException();
    }

    // tested also with the trace generated by the scheduler
    @Test
    public void scheduleWOResourceKO() {
        Task task1 = new Task(
            10,
            10,
            List.of(
                new Chunk(1, new ConstantSampler(new BigDecimal(4)))));
        Task task2 = new Task(
            15,
            15,
            List.of(
                new Chunk(1, new ConstantSampler(new BigDecimal(6))),
                new Chunk(2, new ConstantSampler(new BigDecimal(4)))));
        TaskSet taskSet = new TaskSet(Set.of(task1, task2));
        assertThat(taskSet.utilizationFactor()).isGreaterThan(1);
        assertThat(taskSet.hyperbolicBoundTest()).isFalse();
        RMScheduler scheduler = new RMScheduler(taskSet, 30);
        assertThatThrownBy(() -> scheduler.schedule())
            .isInstanceOf(DeadlineMissedException.class)
            .hasMessageContaining("Il task " + task2.getId() + " ha superato la deadline");
    }

    @Test
    public void scheduleWResourceOK() {
        Resource res1 = new Resource();
        Task task1 = new Task(
            30,
            30,
            List.of(
                new Chunk(1, new ConstantSampler(new BigDecimal(4))),
                new Chunk(2, new ConstantSampler(new BigDecimal(2)), List.of(res1))));
        Task task2 = new Task(
            60,
            60,
            List.of(
                new Chunk(1, new ConstantSampler(new BigDecimal(3)), List.of(res1)),
                new Chunk(2, new ConstantSampler(new BigDecimal(6)))));
        TaskSet taskSet = new TaskSet(Set.of(task1, task2));
        ResourcesProtocol protocol = new PriorityCeilingProtocol();
        RMScheduler scheduler = new RMScheduler(taskSet, protocol, 60);
        assertThatCode(() -> scheduler.schedule())
            .doesNotThrowAnyException();
    }

    @Test
    public void scheduleWResourceKO() {
        Resource res1 = new Resource();
        Resource res2 = new Resource();
        Task task1 = new Task(
            20,
            20,
            List.of(
                new Chunk(1, new ConstantSampler(new BigDecimal(8)), List.of(res1, res2)),
                new Chunk(2, new ConstantSampler(new BigDecimal(2)))));
        Task task2 = new Task(
            30,
            30,
            List.of(
                new Chunk(1, new ConstantSampler(new BigDecimal(5)), List.of(res1)),
                new Chunk(2, new ConstantSampler(new BigDecimal(10)))));
        Task task3 = new Task(
            50,
            50,
            List.of(
                new Chunk(1, new ConstantSampler(new BigDecimal(5)), List.of(res2)),
                new Chunk(2, new ConstantSampler(new BigDecimal(10)))));
        TaskSet taskSet = new TaskSet(Set.of(task1, task2, task3));
        ResourcesProtocol protocol = new PriorityCeilingProtocol();
        RMScheduler scheduler = new RMScheduler(taskSet, protocol, 300);
        assertThatThrownBy(() -> scheduler.schedule())
            .isInstanceOf(DeadlineMissedException.class)
            .hasMessageContaining("Il task " + task2.getId() + " ha superato la deadline");
    }

    @Test
    public void twoConsecutiveSchedules() {
        Task task1 = new Task(
            20,
            20,
            List.of(
                new Chunk(1, new ConstantSampler(new BigDecimal(4)))));
        Task task2 = new Task(
            50,
            50,
            List.of(
                new Chunk(1, new ConstantSampler(new BigDecimal(6))),
                new Chunk(2, new ConstantSampler(new BigDecimal(3))),
                new Chunk(3, new ConstantSampler(new BigDecimal(3)))));
        Task task3 = new Task(
            100,
            100,
            List.of(
                new Chunk(1, new ConstantSampler(new BigDecimal(10)))));
        TaskSet taskSet = new TaskSet(Set.of(task1, task2, task3));
        RMScheduler scheduler = new RMScheduler(taskSet, 100);
        assertThatCode(() -> scheduler.schedule())
            .doesNotThrowAnyException();
        assertThatCode(() -> scheduler.schedule())
            .doesNotThrowAnyException();
    }

}