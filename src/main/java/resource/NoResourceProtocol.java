package resource;

import exeptions.AccessResourceProtocolExeption;
import taskSet.Chunk;
import taskSet.TaskSet;

/**
 * The {@code NoResourceProtocol} class implements a resource access protocol that should not be instantiated directly by the user.
 * <p>
 * This protocol is required internally when no shared resources are specified in the scheduling scenario.
 * It provides empty implementations for all resource management methods, effectively disabling resource management.
 */
public final class NoResourceProtocol extends ResourcesProtocol{

    @Override
    public void access(Chunk chunk) throws AccessResourceProtocolExeption {}

    @Override
    public void progress(Chunk chunk) {}

    @Override
    public void release(Chunk chunk) {}

    @Override
    public void initStructures(TaskSet taskSet) {}

}