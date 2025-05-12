package resource;

import java.util.List;

import taskSet.Task;

public final class Resource {

    private List<Task> blockedTasks;

    public Resource(List<Task> blockedTasks) {
        this.blockedTasks = blockedTasks;
    }

    public List<Task> getBlockedTasks() {
        return this.blockedTasks;
    }
    
}