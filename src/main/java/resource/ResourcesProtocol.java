package resource;

import exeptions.AccessResourceProtocolExeption;
import scheduler.Scheduler;
import taskSet.Chunk;
import taskSet.TaskSet;

public abstract class ResourcesProtocol {

    private Scheduler scheduler;

    // GETTER AND SETTER
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    protected Scheduler getScheduler() {
        return this.scheduler;
    }

    // METHOD
    public void access(Chunk chunk) throws AccessResourceProtocolExeption {}

    public void progress(Chunk chunk) {};

    public void release(Chunk chunk) {}

    public void initStructures(TaskSet taskSet) {};

}