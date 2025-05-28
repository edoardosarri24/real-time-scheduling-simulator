package resource;

import exeptions.AccessResourceProtocolExeption;
import taskSet.Chunk;
import taskSet.TaskSet;

public class NoResourceProtocol extends ResourcesProtocol{

    @Override
    public void access(Chunk chunk) throws AccessResourceProtocolExeption {}

    @Override
    public void progress(Chunk chunk) {}

    @Override
    public void release(Chunk chunk) {}

    @Override
    public void initStructures(TaskSet taskSet) {}

}