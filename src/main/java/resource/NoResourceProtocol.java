package resource;

import java.util.TreeSet;

import exeptions.NoResourceExecption;
import scheduler.RMScheduler;
import taskSet.Chunk;
import taskSet.Task;

public class NoResourceProtocol implements ResourceProtocol{

    public void access (RMScheduler scheduler, Chunk chunk) throws NoResourceExecption {
        throw new NoResourceExecption();
    }

    public void release(Chunk chunk, RMScheduler scheduler, TreeSet<Task> orderedTasks) throws NoResourceExecption {
        throw new NoResourceExecption();
    }

    @Override
    public void progress(Chunk chunk) {
    
    }
    
}