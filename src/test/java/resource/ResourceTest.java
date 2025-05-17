package resource;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import helper.ReflectionUtils;
import taskSet.Chunk;
import taskSet.Task;

public class ResourceTest {

    @SuppressWarnings("unchecked")
    @Test
    public void getMaxDinamicPriorityBlockedtask() {
        Resource resource = new Resource();
        Chunk chunk = new Chunk(0, Duration.ofSeconds(10));
        List<Task> tasks = List.of(
            new Task(Duration.ofSeconds(10), Duration.ofSeconds(10), List.of(chunk)),
            new Task(Duration.ofSeconds(5), Duration.ofSeconds(5), List.of(chunk)),
            new Task(Duration.ofSeconds(15), Duration.ofSeconds(15), List.of(chunk)),
            new Task(Duration.ofSeconds(20), Duration.ofSeconds(20), List.of(chunk))
        );
        tasks.forEach(task -> task.initPriority(5));
        List<Task> blockedTasks = (List<Task>) ReflectionUtils.getField(resource, "blockedTasks");
        blockedTasks.addAll(tasks);
        Task task = new Task(Duration.ofSeconds(4), Duration.ofSeconds(4), List.of(chunk));
        task.initPriority(3);
        blockedTasks.add(task);
        Optional<Task> maxTask = resource.getMaxDinamicPriorityBlockedtask();
        assertThat(maxTask)
            .isPresent()
            .hasValue(task);
    }
    
}