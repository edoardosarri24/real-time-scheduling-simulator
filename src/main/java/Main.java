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
        Chunk chunk = new Chunk(0, Duration.ofSeconds(10), Duration.ofSeconds(2));
        Task task = new Task(Duration.ofSeconds(20), Duration.ofSeconds(20), List.of(chunk));
        RMScheduler scheduler = new RMScheduler(new TaskSet(Set.of(task)));
        scheduler.schedule();
    }

}