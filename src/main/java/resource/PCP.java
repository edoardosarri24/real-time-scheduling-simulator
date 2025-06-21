package resource;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import exeptions.AccessResourceProtocolExeption;
import taskSet.Chunk;
import taskSet.Task;
import utils.Utils;
import utils.logger.MyLogger;

public abstract class PCP extends ResourcesProtocol{

    private Map<Resource, Integer> ceiling = new HashMap<>();
    private List<Resource> busyResources = new LinkedList<>();

    //GETTER AND SETTER
    protected Integer getCeilingValue(Resource resource) {
        return this.ceiling.get(resource);
    }

    protected void mergeCeiling(Resource resource, int nominalPriority) {
        this.ceiling.merge(resource, nominalPriority, Math::min);
    }

    protected void resetStructures() {
        this.ceiling = new HashMap<>();
        this.busyResources = new LinkedList<>();
    }

    protected List<Resource> getBusyResources() {
        return this.busyResources;
    }

    protected void addAllBusyResources(List<Resource> resources) {
        this.busyResources.addAll(resources);
    }

    //METHODS
    @Override
    public void access(Chunk chunk) throws AccessResourceProtocolExeption {
        if (!chunk.hasResources())
            return;
        Task parentTask = chunk.getParent();
        int maxCeiling = this.getBusyResources().stream()
            .filter(res -> !parentTask.hasAquiredThatResource(res))
            .mapToInt(res -> this.getCeilingValue(res))
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

}