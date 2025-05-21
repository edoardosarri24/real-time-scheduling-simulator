package resource;

import java.util.TreeSet;

import exeptions.AccessResourceProtocolExecption;
import scheduler.RMScheduler;
import scheduler.Scheduler;
import taskSet.Chunk;
import taskSet.Task;
import taskSet.TaskSet;

public abstract class ResourceProtocol {

    private Scheduler scheduler;

    // GETTER AND SETTER
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    protected Scheduler getScheduler() {
        return this.scheduler;
    }

    // METHOD
    public void access(Chunk chunk) throws AccessResourceProtocolExecption {}

    public void progress(Chunk chunk) {};
    
    public void release(Chunk chunk, RMScheduler scheduler, TreeSet<Task> orderedTasks) {}

    public void initStructures(TaskSet taskSet) {};
    
}