package scheduler;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import resource.ResourceProtocol;
import taskSet.Task;
import taskSet.TaskSet;
import utils.logger.LoggingConfig;

public abstract class Scheduler {

    private final TaskSet taskSet;
    private final ResourceProtocol resProtocol;
    private List<Task> blockedTask = new LinkedList<>();
    private static final Logger logger = LoggingConfig.getLogger();

    // CONSTRUCTOR
    public Scheduler(TaskSet taskSet, ResourceProtocol resProtocol) {
        taskSet.purelyPeriodicCheck();
        this.taskSet = taskSet;
        this.assignPriority();
        this.resProtocol = resProtocol;
    }

    // GETTER AND SETTER

    protected Logger getLogger() {
        return logger;
    }

    protected TaskSet getTaskSet() {
        return this.taskSet;
    }

    public ResourceProtocol getResProtocol() {
        return this.resProtocol;
    }

    public void blockTask(Task task) {
        this.blockedTask.add(task);
    }

    public void unblockTask(Task task) {
        this.blockedTask.remove(task);
    }

    protected boolean blockeTasksContainTask(Task task) {
        return this.blockedTask.contains(task);
    }

    // HELPER
    private void assignPriority() {
        List<Task> sortedByPeriod = this.taskSet.getTasks().stream()
            .sorted(Comparator.comparing(Task::getPeriod))
            .collect(Collectors.toList());
        IntStream.range(0, sortedByPeriod.size())
            .forEach(i -> {
                int priority = 5 + i * 2;
                Task task = sortedByPeriod.get(i);
                task.initPriority(priority);
            });
    }
    
}