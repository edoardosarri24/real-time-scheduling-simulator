package resource;

import java.util.TreeSet;

import exeptions.AccessResourceProtocolExecption;
import scheduler.RMScheduler;
import taskSet.Chunk;
import taskSet.Task;
import taskSet.TaskSet;

public interface ResourceProtocol {

    public default void access(Chunk chunk) throws AccessResourceProtocolExecption {return;}

    public default void progress(Chunk chunk) {}
    
    public default void release(Chunk chunk, RMScheduler scheduler, TreeSet<Task> orderedTasks) {return;}

    public default void initStructures(TaskSet taskSet) {}
    
}