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
import scheduler.EDFScheduler;
import scheduler.RMScheduler;
import taskSet.Chunk;
import taskSet.Task;
import taskSet.TaskSet;
import utils.sampler.ConstantSampler;

public class Main {
    public static void main(String[] args) throws DeadlineMissedException {
        Resource res1 = new Resource();
        Resource res2 = new Resource();
        Task task1 = new Task(
            35,
            35,
            List.of(
                new Chunk(
                    1,
                    new UniformSampler(new BigDecimal(2), new BigDecimal(2))),
                new Chunk(
                    2,
                    new UniformSampler(new BigDecimal(1), new BigDecimal(1.5)),
                    List.of(res1)),
                new Chunk(
                    3,
                    new UniformSampler(new BigDecimal(0.5), new BigDecimal(1)))));
        Task task2 = new Task(
            50,
            50,
            List.of(
                new Chunk(
                    1,
                    new UniformSampler(new BigDecimal(3), new BigDecimal(4))),
                new Chunk(
                    2,
                    new UniformSampler(new BigDecimal(3), new BigDecimal(3.5)),
                    List.of(res2)),
                new Chunk(
                    3,
                    new UniformSampler(new BigDecimal(3), new BigDecimal(3.5)))));
        Task task3 = new Task(
            80,
            80,
            List.of(
                new Chunk(
                    1,
                    new UniformSampler(new BigDecimal(4), new BigDecimal(5))),
                new Chunk(
                    2,
                    new UniformSampler(new BigDecimal(4), new BigDecimal(4.5)))));
        Task task4 = new Task(
            110,
            110,
            List.of(
                new Chunk(
                    1,
                    new UniformSampler(new BigDecimal(5), new BigDecimal(5.5))),
                new Chunk(
                    2,
                    new UniformSampler(new BigDecimal(2), new BigDecimal(3)),
                    List.of(res2))));
        Task task5 = new Task(
            160,
            160,
            List.of(
                new Chunk(
                    2,
                    new UniformSampler(new BigDecimal(3.5), new BigDecimal(4)),
                    List.of(res1))));
        TaskSet taskSet = new TaskSet(Set.of(task1, task2, task3, task4, task5));
        ResourcesProtocol pcp = new PriorityCeilingProtocolFaultAquireResource(0.2);
        Scheduler rm = new RMScheduler(taskSet, pcp, 1500);
        rm.scheduleDataset(10);
    }

}