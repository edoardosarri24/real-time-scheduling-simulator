import java.time.Duration;
import java.util.List;
import java.util.Set;

import exeptions.DeadlineMissedException;
import scheduler.RMScheduler;
import taskSet.Chunk;
import taskSet.Task;
import taskSet.TaskSet;

public class Main {
    public static void main(String[] args) throws DeadlineMissedException {
        Task task0 = new Task(
            Duration.ofSeconds(20),
            Duration.ofSeconds(20),
            List.of(
                new Chunk(1, Duration.ofSeconds(4))));
        Task task1 = new Task(
            Duration.ofSeconds(50),
            Duration.ofSeconds(50),
            List.of(
                new Chunk(1, Duration.ofSeconds(6)),
                new Chunk(2, Duration.ofSeconds(3)),
                new Chunk(3, Duration.ofSeconds(3))));
        Task task2 = new Task(
            Duration.ofSeconds(100),
            Duration.ofSeconds(100),
            List.of(
                new Chunk(1, Duration.ofSeconds(10))));

        TaskSet taskSet = new TaskSet(Set.of(task0, task1, task2));
        RMScheduler scheduler = new RMScheduler(taskSet);
        scheduler.schedule();
    }

}