package resource;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import exeptions.AccessResourceProtocolExecption;
import taskSet.Chunk;
import taskSet.Task;
import taskSet.TaskSet;
import utils.logger.LoggingConfig;

public final class PriorityCeilingProtocol extends ResourcesProtocol {

    private final Map<Resource, Integer> ceiling;
    private List<Resource> busyResources;
    private static final Logger logger = LoggingConfig.getLogger();

    // CONSTRUCTOR
    public PriorityCeilingProtocol (TaskSet taskSet) {
        this.ceiling = new HashMap<>();
        this.busyResources = new LinkedList<>();
    }

    // GETTER AND SETTER
    public int getCeilingValue(Resource resource) {
        return this.ceiling.get(resource);
    }

    // METHOD
    @Override
    public void access(Chunk chunk) throws AccessResourceProtocolExecption {
        if (!chunk.hasResources())
            return;
        Task parentTask = chunk.getParent();
        int maxCeiling = this.busyResources.stream()
            .filter(res -> !parentTask.hasAquiredThatResource(res))
            .mapToInt(res -> this.ceiling.get(res))
            .min()
            .orElse(Integer.MIN_VALUE);
        if (parentTask.getNominalPriority() <= maxCeiling) {
            chunk.getResources().forEach(res -> res.addBlockedTask(parentTask));
            getScheduler().blockTask(parentTask);
            parentTask.addChunkToExecute(chunk);
            String resourcesId = chunk.getResources().stream()
                              .map(Resource::toString)
                              .map(String::valueOf)
                              .collect(Collectors.joining(", ", "[", "]"));
            logger.info("<" + getScheduler().getClock().getCurrentTime() + ", " + chunk.toString() + "blockedOn " + resourcesId + ">");
            throw new AccessResourceProtocolExecption();
        }
    }

    @Override
    public void progress(Chunk chunk) {
        Task parentTask = chunk.getParent();
        List<Resource> resources = chunk.getResources();
        int MaxDinamicPriorityBlockedtask = resources.stream()
            .flatMap(res -> res.getBlockedTasks().stream())
            .mapToInt(Task::getNominalPriority)
            .min()
            .orElse(Integer.MAX_VALUE);
        parentTask.setDinamicPriority(Math.min(
            parentTask.getNominalPriority(),
            MaxDinamicPriorityBlockedtask));
        if (!resources.isEmpty()) {
            this.busyResources.addAll(resources);
            parentTask.acquireResources(resources);
            String resourcesId = resources.stream()
                                  .map(Resource::toString)
                                  .map(String::valueOf)
                                  .collect(Collectors.joining(", ", "[", "]"));
            logger.info("<" + getScheduler().getClock().getCurrentTime() + ", " + chunk.toString() + " lock " + resourcesId + ">");
        }
    }

    @Override
    public void release(Chunk chunk, TreeSet<Task> readyTasks) {
        List<Resource> resources = chunk.getResources();
        if (resources.isEmpty())
            return;
        Task parentTask = chunk.getParent();
        for (Resource resource : resources) {
            resource.getMaxDinamicPriorityBlockedtask().ifPresent(
                t -> {
                    getScheduler().unblockTask(t);
                    readyTasks.add(t);
                    resource.removeBlockedTask(t);
                    parentTask.releaseResource(resource);
                });
        }
        parentTask.getResourcesAcquiredStream()
            .flatMap(res -> res.getBlockedTasks().stream())
            .mapToInt(Task::getNominalPriority)
            .min()
            .ifPresentOrElse(
                parentTask::setDinamicPriority,
                () -> parentTask.setDinamicPriority(parentTask.getNominalPriority()));
        String resourcesId = resources.stream()
                              .map(Resource::toString)
                              .map(String::valueOf)
                              .collect(Collectors.joining(", ", "[", "]"));
        logger.info("<" + getScheduler().getClock().getCurrentTime() + ", " + chunk.toString() + " unlock " + resourcesId + ">");
    }

    @Override
    public void initStructures(TaskSet taskSet) {
        for (Task task : taskSet.getTasks())
            for (Chunk chunk : task.getChunks())
                for (Resource resource : chunk.getResources())
                    this.ceiling.merge(resource, task.getNominalPriority(), Math::min);
    }

}