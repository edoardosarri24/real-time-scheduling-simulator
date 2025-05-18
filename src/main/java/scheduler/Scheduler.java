package scheduler;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import exeptions.DeadlineMissedException;
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
        this.taskSet = taskSet;
        this.resProtocol = resProtocol;
        this.assignPriority();
        this.resProtocol.initStructures(this.taskSet);
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

    protected boolean blockedTasksContains(Task task) {
        return this.blockedTask.contains(task);
    }

    // METHOD
    protected abstract void assignPriority();

    public abstract void schedule() throws DeadlineMissedException;
    
}