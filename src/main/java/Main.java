import java.time.Duration;
import java.util.List;
import java.util.Set;

import exeptions.DeadlineMissedException;
import scheduler.RMScheduler;
import taskSet.Chunk;
import taskSet.Task;
import taskSet.TaskSet;

public class Main {
    public static void main(String[] args) {
        Task task0 = new Task(
            Duration.ofSeconds(20),
            Duration.ofSeconds(20),
            List.of(new Chunk(0, Duration.ofSeconds(10)),
                    new Chunk(1, Duration.ofSeconds(3))));
        Task task1 = new Task(
            Duration.ofSeconds(50),
            Duration.ofSeconds(50),
            List.of(new Chunk(0, Duration.ofSeconds(5)),
                    new Chunk(1, Duration.ofSeconds(2))));
        Task task2 = new Task(
            Duration.ofSeconds(100),
            Duration.ofSeconds(100),
            List.of(new Chunk(0, Duration.ofSeconds(4))));
        TaskSet taskset = new TaskSet(Set.of(task0, task1, task2));
        RMScheduler scheduler = new RMScheduler(taskset);
        try {
            scheduler.schedule();
        } catch (DeadlineMissedException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

}