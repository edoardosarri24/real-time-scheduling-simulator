import java.time.Duration;
import java.util.List;
import java.util.Set;

import exeptions.DeadlineMissedException;
import resource.PriorityCeilingProtocol;
import resource.Resource;
import resource.ResourceProtocol;
import scheduler.RMScheduler;
import taskSet.Chunk;
import taskSet.Task;
import taskSet.TaskSet;

public class Main {
    public static void main(String[] args) throws DeadlineMissedException {
        Resource res1 = new Resource();
        Task task0 = new Task(
            Duration.ofSeconds(30),
            Duration.ofSeconds(30),
            List.of(
                new Chunk(1, Duration.ofSeconds(4)),
                new Chunk(2, Duration.ofSeconds(2), List.of(res1))));
        Task task1 = new Task(
            Duration.ofSeconds(60),
            Duration.ofSeconds(60),
            List.of(
                new Chunk(1, Duration.ofSeconds(3), List.of(res1)),
                new Chunk(2, Duration.ofSeconds(6))));

        TaskSet taskSet = new TaskSet(Set.of(task0, task1));
        ResourceProtocol protocol = new PriorityCeilingProtocol(taskSet);
        RMScheduler scheduler = new RMScheduler(taskSet, protocol);
        scheduler.schedule();
    }

}