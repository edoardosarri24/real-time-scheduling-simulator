package resource;

import static org.assertj.core.api.Assertions.assertThat;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import helper.ReflectionUtils;
import taskSet.Chunk;
import taskSet.Task;
import utils.sampler.ConstantSampler;

public class ResourceTest {

    @SuppressWarnings("unchecked")
    @Test
    public void getMaxDinamicPriorityBlockedtask() {
        Resource resource = new Resource();
        Chunk chunk = new Chunk(0, new ConstantSampler(new BigDecimal(10)));
        List<Task> tasks = List.of(
            new Task(new BigDecimal(10), new BigDecimal(10), List.of(chunk)),
            new Task(new BigDecimal(5), new BigDecimal(5), List.of(chunk)),
            new Task(new BigDecimal(15), new BigDecimal(15), List.of(chunk)),
            new Task(new BigDecimal(20), new BigDecimal(20), List.of(chunk))
        );
        tasks.forEach(task -> task.initPriority(5));
        List<Task> blockedTasks = (List<Task>) ReflectionUtils.getField(resource, "blockedTasks");
        blockedTasks.addAll(tasks);
        Task task = new Task(new BigDecimal(4), new BigDecimal(4), List.of(chunk));
        task.initPriority(3);
        blockedTasks.add(task);
        Optional<Task> maxTask = resource.getMaxDinamicPriorityBlockedtask();
        assertThat(maxTask)
            .isPresent()
            .hasValue(task);
    }

}