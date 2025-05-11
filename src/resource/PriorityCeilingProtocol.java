package resource;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;

import scheduler.RMScheduler;
import taskset.Chunk;
import taskset.Task;
import taskset.TaskSet;

public final class PriorityCeilingProtocol {

    private Map<Resource, Integer> ceiling;
    private Map<Resource, List<Task>> busyResources;

    // CONSTRUCTOR
    public PriorityCeilingProtocol (TaskSet taskSet) {
        this.ceiling = new HashMap<>();
        this.busyResources = new LinkedList<>();
        for (Task task : taskSet.getTasks())
            for (Chunk chunk : task.getChunks())
                for (Resource resource : chunk.getResources())
                    this.ceiling.merge(resource, task.getNominalPriority(), Math::max);
    }

    // GETTER AND SETTER
    public int getCeilingValue (Resource resource) {
        return this.ceiling.get(resource);
    }

    // METHOD
    public boolean access (Task task, RMScheduler scheduler) {
        Optional<Map.Entry<Resource, Integer>> maxEntry = this.busyResources.entrySet().stream()
            .filter(res -> !res.getValue().contains(task))
            .map(entry -> Map.entry(entry.getKey(), this.ceiling.get(entry.getKey())))
            .max(Comparator.comparingInt(Map.Entry::getValue));
        return maxEntry.map(
            entry -> {
                if (task.getNominalPriority() <= entry.getValue()) {
                    scheduler.getBlockedTask().add(task);
                    this.busyResources
                        .computeIfAbsent(entry.getKey(), _ -> new LinkedList<>())
                        .add(task);
                    return false;
                }
                return false;
            })
            .orElse(true);
    }

    public void progress (Chunk chunk, Task task) {
        int maxDynamicPriority = chunk.getResources().stream()
            .filter(resource -> this.busyResources.containsKey(resource))
            .flatMap(resource -> this.busyResources.get(resource).stream())
            .mapToInt(Task::getNominalPriority)
            .max()
            .orElse(task.getNominalPriority());
        task.setDinamicPriority(Math.max(
            task.getNominalPriority(),
            maxDynamicPriority));
    }

    public void release(Chunk chunk, RMScheduler scheduler, TreeSet<Task> orderedTasks) {
        for (Resource resource : chunk.getResources()) {
            List<Task> tasksBlockedOnResource = this.busyResources.get(resource);
            if (tasksBlockedOnResource == null || tasksBlockedOnResource.isEmpty())
                continue;
            Task taskMaxPriority = tasksBlockedOnResource.stream()
                .max(Comparator.comparingInt(Task::getNominalPriority))
                .get();
            scheduler.getBlockedTask().remove(taskMaxPriority);
            orderedTasks.add(taskMaxPriority);
            tasksBlockedOnResource.remove(taskMaxPriority);
            tasksBlockedOnResource.stream()
                .mapToInt(Task::getNominalPriority)
                .max()
                .ifPresentOrElse(
                    taskMaxPriority::setDinamicPriority,
                    () -> {
                        this.busyResources.remove(resource);
                        taskMaxPriority.setDinamicPriority(taskMaxPriority.getNominalPriority());
                    }
                );
        }
    }
}