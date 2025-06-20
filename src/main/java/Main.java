import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.oristool.simulator.samplers.UniformSampler;

import exeptions.DeadlineMissedException;
import resource.PriorityCeilingProtocol;
import resource.PriorityCeilingProtocolFaultAquireResource;
import resource.PriorityCeilingProtocolFaultSetPriority;
import resource.Resource;
import resource.ResourcesProtocol;
import scheduler.Scheduler;
import scheduler.RMScheduler;
import taskSet.Chunk;
import taskSet.Task;
import taskSet.TaskSet;
import utils.sampler.ConstantSampler;

public class Main {
    public static void main(String[] args) throws DeadlineMissedException {
        Resource resource = new Resource();
        Task task1 = new Task(
            20,
            20,
            List.of(new Chunk(
                1,
                new ConstantSampler(new BigDecimal(10)),
                List.of(resource))));
        Task task2 = new Task(
            40,
            40,
            List.of(
                new Chunk(
                    1,
                    new ConstantSampler(new BigDecimal(5)),
                    List.of(resource)),
                new Chunk(2, new ConstantSampler(new BigDecimal(4))),
                new Chunk(
                    3,
                    new ConstantSampler(new BigDecimal(3)),
                    List.of(resource))));
        TaskSet taskSet = new TaskSet(Set.of(task1, task2));
        ResourcesProtocol pcp = new PriorityCeilingProtocolFaultAquireResource(0.2);
        Scheduler rm = new RMScheduler(taskSet, pcp, 40);
        rm.schedule();
    }

}