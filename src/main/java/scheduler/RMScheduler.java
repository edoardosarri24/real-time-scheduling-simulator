package scheduler;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import resource.ResourcesProtocol;
import taskSet.Task;
import taskSet.TaskSet;
import utils.logger.MyLogger;

public final class RMScheduler extends Scheduler {

    // CONSTRUCTOR
    public RMScheduler(TaskSet taskSet) {
        super(taskSet);
        this.getTaskSet().purelyPeriodicCheck();
    }

    public RMScheduler(TaskSet taskSet, ResourcesProtocol resProtocol) {
        super(taskSet, resProtocol);
        this.getTaskSet().purelyPeriodicCheck();
    }

    // METHOD
    @Override
    protected void checkFeasibility() {
        if (!this.getTaskSet().hyperbolicBoundTest())
            MyLogger.wrn("L'hyperbolic bound test non è passato: il taskset potrebbe non essere schedulabile.");
    }

    @Override
    protected void assignPriority() {
        List<Task> sortedByPeriod = getTaskSet().getTasks().stream()
            .sorted(Comparator.comparing(Task::getPeriod))
            .collect(Collectors.toList());
        IntStream.range(0, sortedByPeriod.size())
            .forEach(i -> {
                Task task = sortedByPeriod.get(i);
                task.initPriority(i+1);
            }
        );
    }

    @Override
    public void addReadyTask(Task task) {
        this.getReadyTasks().add(task);
    }

}