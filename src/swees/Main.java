package swees;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import swees.scheduler.RMScheduler;
import swees.taskset.Chunk;
import swees.taskset.Task;
import swees.taskset.TaskSet;

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
                    new Chunk(1, Duration.ofSeconds(6))));
        Task task2 = new Task(
            Duration.ofSeconds(100),
            Duration.ofSeconds(100),
            List.of(new Chunk(0, Duration.ofSeconds(4))));
        TaskSet taskset = new TaskSet(Set.of(task0, task1, task2));
        RMScheduler scheduler = new RMScheduler(taskset);
        scheduler.schedule();
    }

}