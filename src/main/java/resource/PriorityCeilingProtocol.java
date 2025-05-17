package resource;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import exeptions.AccessResourceProtocolExecption;
import exeptions.NoResourceExecption;
import scheduler.RMScheduler;
import taskSet.Chunk;
import taskSet.Task;
import taskSet.TaskSet;
import utils.logger.LoggingConfig;

public final class PriorityCeilingProtocol implements ResourceProtocol {

    private Map<Resource, Integer> ceiling;
    private List<Resource> busyResources;
    private static final Logger logger = LoggingConfig.getLogger();

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
    public void access(RMScheduler scheduler, Chunk chunk) throws NoResourceExecption, AccessResourceProtocolExecption {
        Task parentTask = chunk.getParent();
        if (chunk.getResources().isEmpty())
            throw new NoResourceExecption();
        int maxCeiling = this.busyResources.stream()
            .filter(res -> !parentTask.hasAquiredThatResource(res))
            .mapToInt(res -> this.ceiling.get(res))
            .max()
            .orElse(Integer.MIN_VALUE);
        if (parentTask.getNominalPriority() <= maxCeiling) {
            scheduler.blockTask(parentTask);
            chunk.getResources().forEach(res -> res.addBlockedTask(parentTask));
            logger.info("Il chunk " + chunk.getId() + " del task " + parentTask.getId() + " si è bloccato sulle risorse " + chunk.getResources());
            throw new AccessResourceProtocolExecption();
        }
    }

    public void progress(Chunk chunk) {
        Task parentTask = chunk.getParent();
        List<Resource> resources = chunk.getResources();
        int MaxDinamicPriorityBlockedtask = resources.stream()
            .flatMap(res -> res.getBlockedTasks().stream())
            .mapToInt(Task::getNominalPriority)
            .max()
            .orElse(Integer.MIN_VALUE);
        parentTask.setDinamicPriority(Math.max(
            parentTask.getNominalPriority(),
            MaxDinamicPriorityBlockedtask));
        this.busyResources.addAll(resources);
        parentTask.acquireResources(resources);
        String resourcesId = resources.stream()
                              .map(Resource::getId)
                              .map(String::valueOf)
                              .collect(Collectors.joining(", ", "[", "]"));
        logger.info("Il chunk " + chunk.getId() + " del task " + parentTask.getId() + " ha acquisito le risorse " + resourcesId + ". La priorità dinamica del task ora è " + parentTask.getDinamicPriority());
    }

    public void release(Chunk chunk, RMScheduler scheduler, TreeSet<Task> readyTasks) throws NoResourceExecption {
        Task parentTask = chunk.getParent();
        List<Resource> resources = chunk.getResources();
        if (resources.isEmpty())
            throw new NoResourceExecption();
        for (Resource resource : resources) {
            resource.getMaxDinamicPriorityBlockedtask().ifPresentOrElse(
                t -> {
                    scheduler.unblockTask(t);
                    readyTasks.add(t);
                    resource.removeBlockedTask(t);
                    parentTask.releaseResource(resource);
                },
                () -> {});
        }
        parentTask.getResourcesAcquiredStream()
            .flatMap(res -> res.getBlockedTasks().stream())
            .mapToInt(Task::getNominalPriority)
            .max()
            .ifPresentOrElse(
                parentTask::setDinamicPriority,
                () -> parentTask.setDinamicPriority(parentTask.getNominalPriority()));
        String resourcesId = resources.stream()
                              .map(Resource::getId)
                              .map(String::valueOf)
                              .collect(Collectors.joining(", ", "[", "]"));
        logger.info("Il chunk " + chunk.getId() + " del task " + parentTask.getId() + " ha rilasciato le risorse " + resourcesId + ". La priorità dinamica del task ora è " + parentTask.getDinamicPriority());
    }

}