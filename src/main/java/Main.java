import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import exeptions.DeadlineMissedException;
import scheduler.Scheduler;
import scheduler.RMScheduler;
import taskSet.Chunk;
import taskSet.Task;
import taskSet.TaskSet;
import utils.sampler.ConstantSampler;

public class Main {

    public static void main(String[] args) throws DeadlineMissedException {
        // RM senza risorse
        Task task1 = new Task(
            new BigDecimal(20),
            new BigDecimal(20),
            List.of(
                new Chunk(1, new ConstantSampler(new BigDecimal(10)))));
        Task task2 = new Task(
            new BigDecimal(60),
            new BigDecimal(60),
            List.of(
                new Chunk(1, new ConstantSampler(new BigDecimal(5))),
                new Chunk(2, new ConstantSampler(new BigDecimal(4))),
                new Chunk(3, new ConstantSampler(new BigDecimal(3)))));
        TaskSet taskSet = new TaskSet(Set.of(task1, task2));
        Scheduler rm = new RMScheduler(taskSet, 60);
        rm.schedule();
    }

}