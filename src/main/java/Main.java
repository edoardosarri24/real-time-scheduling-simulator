import java.time.Duration;
import java.util.List;
import java.util.Set;

import exeptions.DeadlineMissedException;
import resource.PriorityCeilingProtocol;
import resource.Resource;
import scheduler.RMScheduler;
import taskSet.Chunk;
import taskSet.Task;
import taskSet.TaskSet;

public class Main {
    public static void main(String[] args) throws DeadlineMissedException {
        Resource res1 = new Resource();
        Resource res2 = new Resource();
        Task task0 = new Task(
            Duration.ofSeconds(20),
            Duration.ofSeconds(20),
            List.of(new Chunk(0, Duration.ofSeconds(1)),
                    new Chunk(1, Duration.ofSeconds(2), List.of(res1, res2))));
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
        RMScheduler scheduler = new RMScheduler(taskset, new PriorityCeilingProtocol(taskset));
        scheduler.schedule();
    }

}