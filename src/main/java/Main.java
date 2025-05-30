import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import exeptions.DeadlineMissedException;
import scheduler.RMScheduler;
import scheduler.Scheduler;
import taskSet.Chunk;
import taskSet.Task;
import taskSet.TaskSet;
import utils.sampler.ConstantSampler;
import utils.sampler.SampleDuration;

public class Main {
    public static void main(String[] args) throws DeadlineMissedException {
        Task task1 = new Task(
                SampleDuration.sample(new ConstantSampler(new BigDecimal(10))),
                SampleDuration.sample(new ConstantSampler(new BigDecimal(10))),
                List.of(
                    new Chunk(1, SampleDuration.sample(new ConstantSampler(new BigDecimal(4))))));
        Task task2 = new Task(
            SampleDuration.sample(new ConstantSampler(new BigDecimal(15))),
            SampleDuration.sample(new ConstantSampler(new BigDecimal(15))),
            List.of(
                new Chunk(1, SampleDuration.sample(new ConstantSampler(new BigDecimal(6)))),
                new Chunk(2, SampleDuration.sample(new ConstantSampler(new BigDecimal(4))))));
        TaskSet taskSet = new TaskSet(Set.of(task1, task2));
        Scheduler scheduler = new RMScheduler(taskSet);
        scheduler.schedule();
    }
}