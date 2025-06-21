package resource;

import java.util.List;
import java.util.stream.Collectors;

import taskSet.Chunk;
import taskSet.Task;
import taskSet.TaskSet;
import utils.Utils;
import utils.logger.MyLogger;

public final class PriorityCeilingProtocol extends PCP {

    // METHOD
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
            this.addAllBusyResources(resources);
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
        this.resetStructures();
        for (Task task : taskSet.getTasks())
            for (Chunk chunk : task.getChunks())
                for (Resource resource : chunk.getResources())
                    this.mergeCeiling(resource, task.getNominalPriority());
    }

}