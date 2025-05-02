package swees;

import java.time.Duration;
import java.util.Set;

import swees.scheduler.RMScheduler;
import swees.taskset.Chunk;
import swees.taskset.Task;
import swees.taskset.TaskSet;

public class Main {
    public static void main(String[] args) {
        Task task0 = new Task(
            Duration.ofSeconds(25),
            Duration.ofSeconds(25),
            new Chunk(Duration.ofSeconds(15)));
        Task task1 = new Task(
            Duration.ofSeconds(100),
            Duration.ofSeconds(100),
            new Chunk(Duration.ofSeconds(5)));
        TaskSet taskset = new TaskSet(Set.of(task0, task1));
        RMScheduler scheduler = new RMScheduler(taskset);
        scheduler.schedule();
    }

}