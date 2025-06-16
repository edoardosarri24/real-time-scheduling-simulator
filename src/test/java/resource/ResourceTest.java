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
            new Task(10, 10, List.of(chunk)),
            new Task(5, 5, List.of(chunk)),
            new Task(15, 15, List.of(chunk)),
            new Task(20, 20, List.of(chunk))
        );
        tasks.forEach(task -> task.initPriority(5));
        List<Task> blockedTasks = (List<Task>) ReflectionUtils.getField(resource, "blockedTasks");
        blockedTasks.addAll(tasks);
        Task task = new Task(4, 4, List.of(chunk));
        task.initPriority(3);
        blockedTasks.add(task);
        Optional<Task> maxTask = resource.getMaxDinamicPriorityBlockedtask();
        assertThat(maxTask)
            .isPresent()
            .hasValue(task);
    }

}