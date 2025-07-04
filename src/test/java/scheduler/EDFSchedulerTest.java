package scheduler;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.junit.Before;
import org.junit.Test;
import exeptions.DeadlineMissedException;
import helper.ReflectionUtils;
import taskSet.Chunk;
import taskSet.Task;
import taskSet.TaskSet;
import utils.MyClock;
import utils.sampler.ConstantSampler;
import static org.assertj.core.api.Assertions.*;

public class EDFSchedulerTest {

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
            8,
            List.of(this.chunk));
        Task task1 = new Task(
            5,
            4,
            List.of(this.chunk));
        Task task2 = new Task(
            15,
            11,
            List.of(this.chunk));
        assertThat(task0.getNominalPriority())
            .isEqualTo(task0.getDinamicPriority())
            .isZero();
        assertThat(task1.getNominalPriority())
            .isEqualTo(task1.getDinamicPriority())
            .isZero();
        assertThat(task2.getNominalPriority())
            .isEqualTo(task2.getDinamicPriority())
            .isZero();
        new EDFScheduler(new TaskSet(Set.of(task0, task1, task2)), 30);
        assertThat(task1.getNominalPriority())
            .isEqualTo(task1.getNominalPriority())
            .isEqualTo(1);
        assertThat(task0.getNominalPriority())
            .isEqualTo(task0.getNominalPriority())
            .isEqualTo(2);
        assertThat(task2.getNominalPriority())
            .isEqualTo(task2.getNominalPriority())
            .isEqualTo(3);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void addReadyTask() {
        Task task1 = new Task(
            10,
            8,
            List.of(this.chunk));
        Task task2 = new Task(
            5,
            4,
            List.of(this.chunk));
        Task task3 = new Task(
            15,
            11,
            List.of(this.chunk));
        TaskSet taskset = new TaskSet(Set.of(task1, task2, task3));
        Scheduler scheduler = new EDFScheduler(taskset, 30);
        TreeSet<Task> readyTasks = new TreeSet<>(Comparator.comparingInt(Task::getDinamicPriority));
        readyTasks.addAll(List.of(task1, task3));
        ReflectionUtils.setField(scheduler, "readyTasks", readyTasks);
        scheduler.addReadyTask(task2);
        readyTasks = (TreeSet<Task>) ReflectionUtils.getField(scheduler, "readyTasks");
        assertThat(readyTasks)
            .containsExactly(task2, task1, task3);
    }

    // tested also with the trace generated by the scheduler
    @Test
    public void scheduleWOResourceOK() {
        Task task1 = new Task(
            6,
            6,
        List.of(
            new Chunk(1, new ConstantSampler(new BigDecimal(1))),
            new Chunk(2, new ConstantSampler(new BigDecimal(1)))
        ));
        Task task2 = new Task(
            8,
            7,
            List.of(
                new Chunk(1, new ConstantSampler(new BigDecimal(1)))
            ));
        Task task3 = new Task(
            12,
            11,
            List.of(
                new Chunk(1, new ConstantSampler(new BigDecimal(1))),
                new Chunk(2, new ConstantSampler(new BigDecimal(1)))
            ));
        TaskSet taskset = new TaskSet(Set.of(task1, task2, task3));
        assertThat(taskset.utilizationFactor())
            .isLessThan(1);
        Scheduler scheduler = new EDFScheduler(taskset, 24);
        assertThatCode(() -> scheduler.schedule())
            .doesNotThrowAnyException();
    }

    // tested also with the trace generated by the scheduler
    @Test
    public void scheduleWOResourceKO() {
        Task task1 = new Task(
            4,
            4,
        List.of(
            new Chunk(1, new ConstantSampler(new BigDecimal(2))),
            new Chunk(2, new ConstantSampler(new BigDecimal(1)))
        ));
        Task task2 = new Task(
            5,
            5,
            List.of(
                new Chunk(1, new ConstantSampler(new BigDecimal(2)))
            ));
        Task task3 = new Task(
            6,
            6,
            List.of(
                new Chunk(1, new ConstantSampler(new BigDecimal(3)))
            ));
        TaskSet taskset = new TaskSet(Set.of(task1, task2, task3));
        assertThat(taskset.utilizationFactor())
            .isGreaterThan(1);
        Scheduler scheduler = new EDFScheduler(taskset, 120);
        assertThatThrownBy(() -> scheduler.schedule())
            .isInstanceOf(DeadlineMissedException.class)
            .hasMessage("Il task " + task3.getId() + " ha superato la deadline");
    }

}