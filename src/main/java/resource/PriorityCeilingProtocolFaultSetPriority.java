package resource;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.oristool.simulator.samplers.Sampler;
import org.oristool.simulator.samplers.UniformSampler;

import exeptions.AccessResourceProtocolExeption;
import taskSet.Chunk;
import taskSet.Task;
import taskSet.TaskSet;
import utils.Utils;
import utils.logger.MyLogger;

/**
 * Implementation of the Priority Ceiling Protocol (PCP) with Faulty Priority Setting.
 * <p>
 * In this implementation, when a task acquires a resource, its dynamic priority is set to the correct value (as per PCP)
 * plus a sampled delta value. The delta is drawn from a uniform distribution between the specified {@code min} and {@code max}
 * bounds (exclusive).
 */
public final class PriorityCeilingProtocolFaultSetPriority extends ResourcesProtocol {

    private final Map<Resource, Integer> ceiling = new HashMap<>();
    private List<Resource> busyResources = new LinkedList<>();
    private final Sampler delta;

    // CONSTRUCTOR
    /**
     * The {@code delta} value, which is added to the dynamic priority set during the progress phase,
     * will be sampled from a uniform distribution and will be strictly between {@code min} and {@code max} (exclusive).
     * @param min the lower bound (exclusive) for sampling the delta value
     * @param max the upper bound (exclusive) for sampling the delta value
     */
    public PriorityCeilingProtocolFaultSetPriority (double min, double max) {
        this.delta = new UniformSampler(BigDecimal.valueOf(min), BigDecimal.valueOf(max));
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
        int deltaValue = this.delta.getSample().intValue();
        int faultPriority = Math.min(
            parentTask.getNominalPriority(),
            MaxDinamicPriorityBlockedtask)
            + deltaValue;
        parentTask.setDinamicPriority(faultPriority);
        MyLogger.wrn("PCP ha inalzato la priorità del task in modo errato. Alla priorità corretta è stato aggiunto " + deltaValue);
        if (!resources.isEmpty()) {
            this.busyResources.addAll(resources);
            parentTask.acquireResources(resources);
            String resourcesId = resources.stream()
                .map(Resource::toString)
                .map(String::valueOf)
                .collect(Collectors.joining(", ", "[", "]"));
            MyLogger.log("<" + Utils.printCurrentTime() + ", " + chunk.toString() + " lock " + resourcesId + ">");
        }
    }

    @Override
    public void release(Chunk chunk) {
        List<Resource> resources = chunk.getResources();
        if (resources.isEmpty())
            return;
        Task parentTask = chunk.getParent();
        for (Resource resource : resources) {
            resource.getMaxDinamicPriorityBlockedtask().ifPresent(
                t -> {
                    this.getScheduler().unblockTask(t);
                    this.getScheduler().addReadyTask(t);
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
        MyLogger.log("<" + Utils.printCurrentTime() + ", " + chunk.toString() + " unlock " + resourcesId + ">");
    }

    @Override
    public void initStructures(TaskSet taskSet) {
        for (Task task : taskSet.getTasks())
            for (Chunk chunk : task.getChunks())
                for (Resource resource : chunk.getResources())
                    this.ceiling.merge(resource, task.getNominalPriority(), Math::min);
    }

}