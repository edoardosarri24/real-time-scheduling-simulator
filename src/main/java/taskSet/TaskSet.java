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

    public void periodAndDealineCheck() {
        for (Task task : this.tasks)
            task.periodAndDealineCheck();
    }

    public boolean hyperbolicBoundTest() {
        this.purelyPeriodicCheck();
        double hyperbolicProduct = tasks.stream()
            .mapToDouble(task -> task.utilizationFactor()+1)
            .reduce(1.0, (a,b) -> a*b);
        return hyperbolicProduct <= 2;
    }

    public double utilizationFactor() {
        return this.tasks.stream()
            .mapToDouble(Task::utilizationFactor)
            .sum();
    }

}