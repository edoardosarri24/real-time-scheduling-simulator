package scheduler;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import exeptions.DeadlineMissedException;
import resource.NoResourceProtocol;
import resource.ResourcesProtocol;
import taskSet.Task;
import taskSet.TaskSet;
import utils.Utils;
import utils.logger.MyLogger;

public abstract class Scheduler {

    private final TaskSet taskSet;
    private final ResourcesProtocol resProtocol;

    private List<Task> blockedTask = new LinkedList<>();
    private TreeSet<Task> readyTasks;
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

    public TreeSet<Task> getReadyTasks() {
        return this.readyTasks;
    }

    public void addReadyTask(Task task) {
        this.readyTasks.add(task);
    }

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

    protected boolean blockedTasksContains(Task task) {
        return this.blockedTask.contains(task);
    }

    protected Task removeFirstReadyTask() {
        return this.readyTasks.pollFirst();
    }

    protected boolean thereIsAnotherReadyTask() {
        return !this.readyTasks.isEmpty();
    }
    protected TaskSet getTaskSet() {
        return this.taskSet;
    }

    protected void setReadyTasks(TreeSet<Task> readyTasks) {
        this.readyTasks = readyTasks;
    }

    // METHOD
    protected abstract void assignPriority();

    public abstract void schedule() throws DeadlineMissedException;

    // HELPER
    protected void releaseAllTasks() {
        for (Task task : this.readyTasks)
            MyLogger.log("<" + Utils.printCurrentTime() + ", release " + task.toString() + ">");
    }

}