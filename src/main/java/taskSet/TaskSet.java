package taskSet;

import java.util.Set;

import exeptions.PurelyPeriodicException;

public final class TaskSet {

    private Set<Task> tasks;

    public TaskSet(Set<Task> tasks) {
        this.tasks = tasks;
    }

    public Set<Task> getTasks() {
        return this.tasks;
    }

    public void purelyPeriodicCheck() throws PurelyPeriodicException {
        for (Task task : this.tasks)
            task.purelyPeriodicCheck();
    }

}