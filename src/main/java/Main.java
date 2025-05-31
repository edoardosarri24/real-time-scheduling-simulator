import java.math.BigDecimal;
import java.time.Duration;
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
        Duration period = Duration.ofNanos(10);
        Duration currentTime = Duration.ofNanos(18);
        System.out.println(currentTime.toNanos() / period.toNanos());
    }
}