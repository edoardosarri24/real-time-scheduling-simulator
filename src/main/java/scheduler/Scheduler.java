package scheduler;

import java.util.LinkedList;
import java.util.List;
import exeptions.DeadlineMissedException;
import resource.NoResourceProtocol;
import resource.ResourcesProtocol;
import taskSet.Task;
import taskSet.TaskSet;

public abstract class Scheduler {

    private final TaskSet taskSet;
    private final ResourcesProtocol resProtocol;

    private List<Task> blockedTask = new LinkedList<>();
    private Task lastTaskExecuted;

    // CONSTRUCTOR
    public Scheduler(TaskSet taskSet) {
        this(taskSet, new NoResourceProtocol());
    }

    public Scheduler(TaskSet taskSet, ResourcesProtocol resProtocol) {
        this.taskSet = taskSet;
        this.resProtocol = resProtocol;
        this.assignPriority();
        this.resProtocol.initStructures(this.taskSet);
        this.resProtocol.setScheduler(this);
    }

    // GETTER AND SETTER
    public ResourcesProtocol getResProtocol() {
        return this.resProtocol;
    }

    public void blockTask(Task task) {
        this.blockedTask.add(task);
    }

    public void unblockTask(Task task) {
        this.blockedTask.remove(task);
    }

    protected void setLastTaskExecuted(Task task) {
        this.lastTaskExecuted = task;
    }

    protected Task getLastTaskExecuted() {
        return this.lastTaskExecuted;
    }

    protected boolean checkLastTaskExecuted(Task currentTask) {
        return !(this.lastTaskExecuted==null)
                && !this.lastTaskExecuted.equals(currentTask)
                && !this.lastTaskExecuted.getIsExecuted();
    }

    protected boolean taskIsBlocked(Task task) {
        return this.blockedTask.contains(task);
    }

    protected TaskSet getTaskSet() {
        return this.taskSet;
    }

    // METHOD
    protected abstract void assignPriority();

    public abstract void schedule() throws DeadlineMissedException;

    public abstract void addReadyTask(Task task);

}