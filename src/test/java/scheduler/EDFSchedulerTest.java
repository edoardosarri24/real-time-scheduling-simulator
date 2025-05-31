package scheduler;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import taskSet.Chunk;
import taskSet.Task;
import taskSet.TaskSet;
import utils.MyClock;
import static org.assertj.core.api.Assertions.*;

public class EDFSchedulerTest {

    private Chunk chunk;

    @Before
    public void setup() {
        this.chunk = new Chunk(0, Duration.ofSeconds(1));
        MyClock.reset();
    }

    @Test
    public void assignPriority() {
        Task task0 = new Task(
            Duration.ofSeconds(10),
            Duration.ofSeconds(8),
            List.of(this.chunk));
        Task task1 = new Task(
            Duration.ofSeconds(5),
            Duration.ofSeconds(4),
            List.of(this.chunk));
        Task task2 = new Task(
            Duration.ofSeconds(15),
            Duration.ofSeconds(11),
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
        new EDFScheduler(new TaskSet(Set.of(task0, task1, task2)));
        assertThat(task1.getNominalPriority())
            .isEqualTo(task1.getNominalPriority())
            .isEqualTo(5);
        assertThat(task0.getNominalPriority())
            .isEqualTo(task0.getNominalPriority())
            .isEqualTo(7);
        assertThat(task2.getNominalPriority())
            .isEqualTo(task2.getNominalPriority())
            .isEqualTo(9);
    }

}