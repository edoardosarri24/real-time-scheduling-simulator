package taskSet;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import utils.MyClock;

import static org.assertj.core.api.Assertions.*;

public class TaskSetTest {

    @Test
    public void notPurelyPeriodic() {
        MyClock.reset();
        Chunk chunk = new Chunk(0, Duration.ofSeconds(1));
        Task task0 = new Task(
            Duration.ofSeconds(10),
            Duration.ofSeconds(10),
            List.of(chunk));
        Task task1 = new Task(
            Duration.ofSeconds(5),
            Duration.ofSeconds(1),
            List.of(chunk));
        Task task2 = new Task(
            Duration.ofSeconds(5),
            Duration.ofSeconds(5),
            List.of(chunk));
        TaskSet taskSet = new TaskSet(Set.of(task0, task1, task2));
        assertThatThrownBy(() -> taskSet.purelyPeriodicCheck())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Il task " + task1.getId() + " non è puramente periodico: ha periodo PT5S e deadline PT1S");
    }

}