import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import exeptions.DeadlineMissedException;
import resource.PriorityCeilingProtocol;
import resource.Resource;
import scheduler.Scheduler;
import scheduler.RMScheduler;
import taskSet.Chunk;
import taskSet.Task;
import taskSet.TaskSet;
import utils.sampler.ConstantSampler;

public class Main {

    public static void main(String[] args) throws DeadlineMissedException {
        // RM senza risorse
        Resource resource = new Resource();
        Task task1 = new Task(
            20,
            20,
            List.of(
                new Chunk(1, new ConstantSampler(new BigDecimal(10)), List.of(resource))));
        Task task2 = new Task(
            60,
            60,
            List.of(
                new Chunk(1, new ConstantSampler(new BigDecimal(5))),
                new Chunk(2, new ConstantSampler(new BigDecimal(4)), List.of(resource)),
                new Chunk(3, new ConstantSampler(new BigDecimal(3)))));
        TaskSet taskSet = new TaskSet(Set.of(task1, task2));
        PriorityCeilingProtocol pcp = new PriorityCeilingProtocol();
        Scheduler rm = new RMScheduler(taskSet, pcp, 60);
        rm.schedule();
    }

}