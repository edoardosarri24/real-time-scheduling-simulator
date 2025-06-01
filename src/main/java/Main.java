import java.time.Duration;
import java.util.List;
import java.util.Set;

import exeptions.DeadlineMissedException;
import scheduler.EDFScheduler;
import scheduler.Scheduler;
import taskSet.Chunk;
import taskSet.Task;
import taskSet.TaskSet;

public class Main {

    public static void main(String[] args) throws DeadlineMissedException {
        Task task1 = new Task(
        Duration.ofMillis(4),
        Duration.ofMillis(4),
        List.of(
            new Chunk(1, Duration.ofMillis(2)),
            new Chunk(2, Duration.ofMillis(1))
        ));
        Task task2 = new Task(
            Duration.ofMillis(5),
            Duration.ofMillis(5),
            List.of(
                new Chunk(1, Duration.ofMillis(2))
            ));
        Task task3 = new Task(
            Duration.ofMillis(6),
            Duration.ofMillis(6),
            List.of(
                new Chunk(1, Duration.ofMillis(3))
            ));
        TaskSet taskset = new TaskSet(Set.of(task1, task2, task3));
        Scheduler scheduler = new EDFScheduler(taskset);
        scheduler.schedule();
    }

}