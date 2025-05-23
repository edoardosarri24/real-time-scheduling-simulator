package taskSet;

import java.util.Set;

public class TaskSet {

    private final Set<Task> tasks;

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