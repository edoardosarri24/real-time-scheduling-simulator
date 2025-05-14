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

    public void purelyPeriodicCheck() {
        for (Task task : this.tasks)
            task.purelyPeriodicCheck();
    }

}