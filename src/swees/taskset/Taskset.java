package swees.taskset;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Taskset {

    private Set<Task> tasks;

    public Taskset(Set<Task> tasks) {
        this.tasks = tasks;
    }

    public List<Task> orderByPeriod() {
        return tasks.stream()
                .sorted(Comparator.comparing(Task::getPeriod))
                .collect(Collectors.toList());
    }

}