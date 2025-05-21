package resource;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import taskSet.Task;

public final class Resource {

    private static int idCounter = 1;
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

    public boolean hasBlockedTask() {
        return !this.blockedTasks.isEmpty();
    }

    // METHOD
    public void addBlockedTask(Task task) {
        this.blockedTasks.add(task);
    }

    public void removeBlockedTask(Task task) {
        this.blockedTasks.remove(task);
    }
    
    public Optional<Task> getMaxDinamicPriorityBlockedtask() {
        return this.blockedTasks.stream()
            .min(Comparator.comparingInt(Task::getNominalPriority));
    }

    @Override
    public String toString() {
        return "Res" + this.id;
    }
    
}