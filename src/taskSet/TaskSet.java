package taskSet;

import java.util.Set;

public final class TaskSet {

    private Set<Task> tasks;

    public TaskSet(Set<Task> tasks) {
        this.tasks = tasks;
    }

    public Set<Task> getTasks() {
        return this.tasks;
    }

}