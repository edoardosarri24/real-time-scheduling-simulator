package resource;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import helper.ReflectionUtils;
import taskSet.Chunk;
import taskSet.Task;

public class ResourceTest {

    private Resource resource;

    @Before
    public void setUp() {
        this.resource = new Resource();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getMaxDinamicPriorityBlockedtask() {
        List<Chunk> chunk = List.of(new Chunk(0, Duration.ofSeconds(10)));
        List<Task> blockedTasks = (List<Task>) ReflectionUtils.getField(this.resource, "blockedTasks");
        blockedTasks.addAll(
            List.of(
                new Task(Duration.ofSeconds(10), Duration.ofSeconds(10), chunk),
                new Task(Duration.ofSeconds(5), Duration.ofSeconds(5), chunk),
                new Task(Duration.ofSeconds(15), Duration.ofSeconds(15), chunk),
                new Task(Duration.ofSeconds(20), Duration.ofSeconds(20), chunk)
            )
        );
        blockedTasks.forEach(t -> t.initPriority(5));
        Task task = new Task(Duration.ofSeconds(4), Duration.ofSeconds(4), chunk);
        task.initPriority(3);
        blockedTasks.add(task);
        Optional<Task> maxTask = this.resource.getMaxDinamicPriorityBlockedtask();
        assertThat(maxTask)
            .isPresent()
            .hasValue(task);
    }
    
}