import java.time.Duration;
import java.util.List;
import java.util.Set;
import exeptions.DeadlineMissedException;
import scheduler.EDFScheduler;
import taskSet.Chunk;
import taskSet.Task;
import taskSet.TaskSet;


public class Main {
    public static void main(String[] args) throws DeadlineMissedException {
        Task task0 = new Task(
            Duration.ofMillis(10),
            Duration.ofMillis(5),
            List.of(new Chunk(0, Duration.ofMillis(7))));
        Task task1 = new Task(
            Duration.ofMillis(20),
            Duration.ofMillis(20),
            List.of(new Chunk(1, Duration.ofMillis(1))));
        EDFScheduler scheduler = new EDFScheduler(new TaskSet(Set.of(task0, task1)));
        scheduler.schedule();
    }
}