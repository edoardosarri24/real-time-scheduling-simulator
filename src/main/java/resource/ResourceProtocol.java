package resource;

import java.util.TreeSet;

import exeptions.AccessResourceProtocolExecption;
import exeptions.NoResourceExecption;
import scheduler.RMScheduler;
import taskSet.Chunk;
import taskSet.Task;

public interface ResourceProtocol {

    public default void access(RMScheduler scheduler, Chunk chunk) throws NoResourceExecption, AccessResourceProtocolExecption {
        throw new NoResourceExecption();
    }

    public default void progress(Chunk chunk) {}
    
    public default void release(Chunk chunk, RMScheduler scheduler, TreeSet<Task> orderedTasks) throws NoResourceExecption {
        throw new NoResourceExecption();
    }
    
}