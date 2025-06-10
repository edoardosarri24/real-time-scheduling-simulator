package resource;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import exeptions.AccessResourceProtocolExeption;
import taskSet.Chunk;
import taskSet.Task;
import taskSet.TaskSet;
import utils.Utils;
import utils.logger.MyLogger;

public final class PriorityCeilingProtocolFaultAquireResource extends ResourcesProtocol {

    private final Map<Resource, Integer> ceiling = new HashMap<>();
    private List<Resource> busyResources = new LinkedList<>();
    private final double threshold;
    private List<Chunk> faultChunks = new LinkedList<>();

    // CONSTRUCTOR
    /**
     * Constructs a new PriorityCeilingProtocolFaultAquireResource with the specified threshold.
     * <p>
     * The threshold value must be between 0.0 and 1.0 (inclusive).
     *
     * @param threshold the threshold value, which must be in the range [0.0, 1.0].
     */
    public PriorityCeilingProtocolFaultAquireResource (double threshold) {
        if (threshold<0.0 || threshold>1.0)
            throw new IllegalArgumentException("La soglia con cui acquisire un semaforo deve essere compresa tra 0.0 e 1.0.");
        this.threshold = threshold;
    }

    // GETTER AND SETTER
    public int getCeilingValue(Resource resource) {
        return this.ceiling.get(resource);
    }

    // METHOD
    @Override
    public void access(Chunk chunk) throws AccessResourceProtocolExeption {
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
            MyLogger.log("<" + Utils.printCurrentTime() + ", " + chunk.toString() + "blockedOn " + resourcesId + ">");
            throw new AccessResourceProtocolExeption();
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
        if (Math.random() < this.threshold) {
            if (!resources.isEmpty()) {
                this.busyResources.addAll(resources);
                parentTask.acquireResources(resources);
                String resourcesId = resources.stream()
                    .map(Resource::toString)
                    .map(String::valueOf)
                    .collect(Collectors.joining(", ", "[", "]"));
                MyLogger.log("<" + Utils.printCurrentTime() + ", " + chunk.toString() + " lock " + resourcesId + ">");
                this.faultChunks.add(chunk);
            }
        }
    }

    @Override
    public void release(Chunk chunk) {
        List<Resource> resources = chunk.getResources();
        if (resources.isEmpty())
            return;
        Task parentTask = chunk.getParent();
        if (!this.faultChunks.contains(chunk)) {
            for (Resource resource : resources) {
                resource.getMaxDinamicPriorityBlockedtask().ifPresent(
                    t -> {
                        this.getScheduler().unblockTask(t);
                        this.getScheduler().addReadyTask(t);
                        resource.removeBlockedTask(t);
                        parentTask.releaseResource(resource);
                    });
            }
        }
        parentTask.getResourcesAcquiredStream()
            .flatMap(res -> res.getBlockedTasks().stream())
            .mapToInt(Task::getNominalPriority)
            .min()
            .ifPresentOrElse(
                parentTask::setDinamicPriority,
                () -> parentTask.setDinamicPriority(parentTask.getNominalPriority()));
        if (!this.faultChunks.contains(chunk)) {
            String resourcesId = resources.stream()
                .map(Resource::toString)
                .map(String::valueOf)
                .collect(Collectors.joining(", ", "[", "]"));
            MyLogger.log("<" + Utils.printCurrentTime() + ", " + chunk.toString() + " unlock " + resourcesId + ">");
            this.faultChunks.remove(chunk);
        }
    }

    @Override
    public void initStructures(TaskSet taskSet) {
        for (Task task : taskSet.getTasks())
            for (Chunk chunk : task.getChunks())
                for (Resource resource : chunk.getResources())
                    this.ceiling.merge(resource, task.getNominalPriority(), Math::min);
    }

}