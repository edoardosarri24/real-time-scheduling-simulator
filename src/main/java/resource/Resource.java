package resource;

import java.util.LinkedList;
import java.util.List;

import taskSet.Task;

public final class Resource {

    private static int idCounter = 0;
    private final int id;
    private List<Task> blockedTasks = new LinkedList<>();

    // CONSTRUCTOR
    public Resource() {
        this.id = idCounter++;
    }

    // GETTER AND SETTER
    public List<Task> getBlockedTasks() {
        return this.blockedTasks;
    }

    public int getId() {
        return this.id;
    }
    
}