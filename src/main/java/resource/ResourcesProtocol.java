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
    public abstract void access(Chunk chunk) throws AccessResourceProtocolExeption;

    public abstract void progress(Chunk chunk);

    public abstract void release(Chunk chunk);

    public abstract void initStructures(TaskSet taskSet);

}