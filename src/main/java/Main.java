import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Set;

import org.oristool.simulator.samplers.ExponentialSampler;
import org.oristool.simulator.samplers.UniformSampler;
import exeptions.DeadlineMissedException;
import resource.PriorityCeilingProtocol;
import resource.PriorityCeilingProtocolFaultAquireResource;
import resource.PriorityCeilingProtocolFaultSetPriority;
import resource.Resource;
import resource.ResourcesProtocol;
import scheduler.EDFScheduler;
import scheduler.RMScheduler;
import scheduler.Scheduler;
import taskSet.Chunk;
import taskSet.Task;
import taskSet.TaskSet;
import utils.sampler.ConstantSampler;

public class Main {

    public static void main(String[] args) {

        /*
        // RM senza risorse
        Task task1 = new Task(
            new ConstantSampler(new BigDecimal(20)),
            new ConstantSampler(new BigDecimal(20)),
            List.of(
                new Chunk(1, new UniformSampler(new BigDecimal(6), new BigDecimal(17)))));
        Task task2 = new Task(
            new ConstantSampler(new BigDecimal(50)),
            new ConstantSampler(new BigDecimal(50)),
            List.of(
                new Chunk(1, new UniformSampler(new BigDecimal(3), new BigDecimal(9))),
                new Chunk(2, new UniformSampler(new BigDecimal(4), new BigDecimal(7))),
                new Chunk(3, new UniformSampler(new BigDecimal(3), new BigDecimal(7)))));
        Task task3 = new Task(
            new ConstantSampler(new BigDecimal(100)),
            new ConstantSampler(new BigDecimal(100)),
            List.of(
                new Chunk(1, new UniformSampler(new BigDecimal(6), new BigDecimal(19)))));
        TaskSet taskSet = new TaskSet(Set.of(task1, task2, task3));
        RMScheduler scheduler = new RMScheduler(taskSet, Duration.ofMillis(100));
        for (int i=0; i<10; i++)
            try {
                scheduler.schedule();
            } catch (DeadlineMissedException e) {
                continue;
            }
        */

        /*
        // RM con risorse
        Resource res1 = new Resource();
        Task task1 = new Task(
            new ConstantSampler(new BigDecimal(30)),
            new ConstantSampler(new BigDecimal(30)),
            List.of(
                new Chunk(1, new UniformSampler(new BigDecimal(14), new BigDecimal(20))),
                new Chunk(2, new UniformSampler(new BigDecimal(2), new BigDecimal(10)), List.of(res1))));
        Task task2 = new Task(
            new ConstantSampler(new BigDecimal(60)),
            new ConstantSampler(new BigDecimal(60)),
            List.of(
                new Chunk(1, new UniformSampler(new BigDecimal(10), new BigDecimal(30)), List.of(res1)),
                new Chunk(2, new UniformSampler(new BigDecimal(15), new BigDecimal(30)))));
        TaskSet taskSet = new TaskSet(Set.of(task1, task2));
        ResourcesProtocol protocol = new PriorityCeilingProtocol();
        RMScheduler scheduler = new RMScheduler(taskSet, protocol, Duration.ofMillis(60));
        for (int i=0; i<10; i++) {
            try {
                scheduler.schedule();
            } catch (DeadlineMissedException e) {
                continue;
            }
        }
        */

        /*
        // EDF
        Task task1 = new Task(
            new ConstantSampler(new BigDecimal(6)),
            new ConstantSampler(new BigDecimal(6)),
            List.of(
                new Chunk(1, new UniformSampler(new BigDecimal(1), new BigDecimal(2))),
                new Chunk(2, new UniformSampler(new BigDecimal(1), new BigDecimal(2)))
        ));
        Task task2 = new Task(
            new ConstantSampler(new BigDecimal(8)),
            new ConstantSampler(new BigDecimal(7)),
            List.of(
                new Chunk(1, new UniformSampler(new BigDecimal(1), new BigDecimal(3)))
            ));
        Task task3 = new Task(
            new ConstantSampler(new BigDecimal(12)),
            new ConstantSampler(new BigDecimal(11)),
            List.of(
                new Chunk(1, new UniformSampler(new BigDecimal(1), new BigDecimal(3))),
                new Chunk(2, new UniformSampler(new BigDecimal(1), new BigDecimal(1)))
            ));
        TaskSet taskset = new TaskSet(Set.of(task1, task2, task3));
        Scheduler scheduler = new EDFScheduler(taskset, Duration.ofMillis(100));
        for (int i=0; i<100; i++)
            try {
                scheduler.schedule();
            } catch (DeadlineMissedException e) {
                continue;
            }
        */

        /*/
        // RM senza risorse con chunk fail
        Task task1 = new Task(
            new ConstantSampler(new BigDecimal(20)),
            new ConstantSampler(new BigDecimal(20)),
            List.of(
                new Chunk(1, new UniformSampler(new BigDecimal(6), new BigDecimal(17)), new ExponentialSampler(new BigDecimal(1)))));
        Task task2 = new Task(
            new ConstantSampler(new BigDecimal(50)),
            new ConstantSampler(new BigDecimal(50)),
            List.of(
                new Chunk(1, new UniformSampler(new BigDecimal(3), new BigDecimal(6))),
                new Chunk(2, new UniformSampler(new BigDecimal(4), new BigDecimal(7)), new ExponentialSampler(new BigDecimal(0.8))),
                new Chunk(3, new UniformSampler(new BigDecimal(3), new BigDecimal(7)))));
        Task task3 = new Task(
            new ConstantSampler(new BigDecimal(100)),
            new ConstantSampler(new BigDecimal(100)),
            List.of(
                new Chunk(1, new UniformSampler(new BigDecimal(6), new BigDecimal(19)))));
        TaskSet taskSet = new TaskSet(Set.of(task1, task2, task3));
        Scheduler scheduler = new RMScheduler(taskSet, Duration.ofMillis(100));
        for (int i=0; i<100; i++)
            try {
                scheduler.schedule();
            } catch (DeadlineMissedException e) {
                continue;
            }
        */

        /*
        // RM con risorse con priority fail
        Resource res1 = new Resource();
        Task task1 = new Task(
            new ConstantSampler(new BigDecimal(30)),
            new ConstantSampler(new BigDecimal(30)),
            List.of(
                new Chunk(1, new UniformSampler(new BigDecimal(14), new BigDecimal(20))),
                new Chunk(2, new UniformSampler(new BigDecimal(2), new BigDecimal(10)), List.of(res1))));
        Task task2 = new Task(
            new ConstantSampler(new BigDecimal(60)),
            new ConstantSampler(new BigDecimal(60)),
            List.of(
                new Chunk(1, new UniformSampler(new BigDecimal(10), new BigDecimal(30)), List.of(res1)),
                new Chunk(2, new UniformSampler(new BigDecimal(15), new BigDecimal(30)))));
        TaskSet taskSet = new TaskSet(Set.of(task1, task2));
        ResourcesProtocol protocol = new PriorityCeilingProtocolFaultSetPriority(-3, 3);
        RMScheduler scheduler = new RMScheduler(taskSet, protocol, Duration.ofMillis(60));
        for (int i=0; i<100; i++)
            try {
                scheduler.schedule();
            } catch (DeadlineMissedException e) {
                continue;
            }
        */

        /*
        // RM con risorse con acquire fail
        Resource res1 = new Resource();
        Task task1 = new Task(
            new ConstantSampler(new BigDecimal(30)),
            new ConstantSampler(new BigDecimal(30)),
            List.of(
                new Chunk(1, new UniformSampler(new BigDecimal(14), new BigDecimal(20))),
                new Chunk(2, new UniformSampler(new BigDecimal(2), new BigDecimal(10)), List.of(res1))));
        Task task2 = new Task(
            new ConstantSampler(new BigDecimal(60)),
            new ConstantSampler(new BigDecimal(60)),
            List.of(
                new Chunk(1, new UniformSampler(new BigDecimal(10), new BigDecimal(30)), List.of(res1)),
                new Chunk(2, new UniformSampler(new BigDecimal(15), new BigDecimal(30)))));
        TaskSet taskSet = new TaskSet(Set.of(task1, task2));
        ResourcesProtocol protocol = new PriorityCeilingProtocolFaultAquireResource(0.2);
        RMScheduler scheduler = new RMScheduler(taskSet, protocol, Duration.ofMillis(60));
        for (int i=0; i<100; i++)
            try {
                scheduler.schedule();
            } catch (DeadlineMissedException e) {
                continue;
            }
        */

    }

}