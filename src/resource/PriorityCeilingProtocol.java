package resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scheduler.RMScheduler;
import taskset.Chunk;
import taskset.Task;
import taskset.TaskSet;

public final class PriorityCeilingProtocol {

    private Map<Resource, Integer> ceiling;
    private Map<Resource, List<Task>> busyResources;

    public PriorityCeilingProtocol (TaskSet taskSet) {
        this.busyResources = new HashMap<>();
        for (Task task : taskSet.getTasks())
            for (Chunk chunk : task.getChunks())
                for (Resource resource : chunk.getResources())
                    this.ceiling.merge(resource, task.getNominalPriority(), Math::max);
    }

    public int getCeilingValue (Resource resource) {
        return this.ceiling.get(resource);
    }

    public boolean access (Task task, RMScheduler scheduler) {
        for (Resource busyResource : this.busyResources.keySet()) {
            if (this.ceiling.get(busyResource) >= task.getNominalPriority()) {
                scheduler.block(task);
                this.busyResources
                    .computeIfAbsent(busyResource, _ -> new ArrayList<>())
                    .add(task);
                return false;
            }
        }
        return true;
    }

    public void progress (Chunk chunk, Task task) {
        for (Resource resource : chunk.getResources()) {
            task.setDinamicPriority(this.busyResources.get(resource)
                .stream()
                .mapToInt(Task::getNominalPriority)
                .max()
                .getAsInt());
        }
    }
    
}