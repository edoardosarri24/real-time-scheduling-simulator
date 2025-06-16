package resource;

import static org.assertj.core.api.Assertions.assertThat;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import helper.ReflectionUtils;
import scheduler.RMScheduler;
import taskSet.Chunk;
import taskSet.Task;
import taskSet.TaskSet;
import utils.MyClock;
import utils.sampler.ConstantSampler;

public class PriorityCeilingProtocolTest {

    @SuppressWarnings("unchecked")
    @Test
    public void initStructures() {
        MyClock.reset();
        Resource res0 = new Resource();
        Resource res1 = new Resource();
        Resource res2 = new Resource();
        Chunk chunk0 = new Chunk(0, new ConstantSampler(new BigDecimal(1)), List.of(res0));
        Chunk chunk1 = new Chunk(1, new ConstantSampler(new BigDecimal(2)));
        Chunk chunk2 = new Chunk(2, new ConstantSampler(new BigDecimal(2)), List.of(res0, res1, res2));
        Chunk chunk3 = new Chunk(3, new ConstantSampler(new BigDecimal(1)));
        Chunk chunk4 = new Chunk(4, new ConstantSampler(new BigDecimal(2)), List.of(res1));
        Task task0 = new Task(
            new BigDecimal(5),
            new BigDecimal(5),
            List.of(chunk0, chunk1));
        Task task1 = new Task(
            new BigDecimal(8),
            new BigDecimal(8),
            List.of(chunk2));
        Task task2 = new Task(
            new BigDecimal(10),
            new BigDecimal(10),
            List.of(chunk3, chunk4));
        TaskSet taskSet = new TaskSet(Set.of(task0, task1, task2));
        PriorityCeilingProtocol protocol = new PriorityCeilingProtocol();
        new RMScheduler(taskSet, protocol, 0);
        Map<Resource, Integer> ceiling = (Map<Resource, Integer>) ReflectionUtils.getField(protocol, "ceiling");
        assertThat(ceiling.keySet())
            .containsExactlyInAnyOrder(res0, res1, res2);
        assertThat(ceiling.get(res0))
            .isEqualTo(1);
        assertThat(ceiling.get(res1))
            .isEqualTo(2);
        assertThat(ceiling.get(res2))
            .isEqualTo(2);
    }

}