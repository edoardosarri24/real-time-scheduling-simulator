package resource;

import java.util.TreeSet;

import exeptions.AccessResourceProtocolExecption;
import exeptions.NoResourceExecption;
import scheduler.RMScheduler;
import taskSet.Chunk;
import taskSet.Task;

public interface ResourceProtocol {

    public void access(RMScheduler scheduler, Chunk chunk) throws NoResourceExecption, AccessResourceProtocolExecption;
    public void progress(Chunk chunk);
    public void release(Chunk chunk, RMScheduler scheduler, TreeSet<Task> orderedTasks) throws NoResourceExecption;
    
}