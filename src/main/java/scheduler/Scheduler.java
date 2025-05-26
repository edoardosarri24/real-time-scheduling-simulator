package scheduler;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import exeptions.DeadlineMissedException;
import resource.ResourcesProtocol;
import taskSet.Task;
import taskSet.TaskSet;

public abstract class Scheduler {

    private final TaskSet taskSet;
    private final ResourcesProtocol resProtocol;
    private List<Task> blockedTask = new LinkedList<>();
    private TreeSet<Task> readyTasks;

    // CONSTRUCTOR
    public Scheduler(TaskSet taskSet, ResourcesProtocol resProtocol) {
        this.taskSet = taskSet;
        this.resProtocol = resProtocol;
        this.assignPriority();
        this.resProtocol.initStructures(this.taskSet);
        this.resProtocol.setScheduler(this);
    }

    // GETTER AND SETTER
    protected TaskSet getTaskSet() {
        return this.taskSet;
    }

    protected void setReadyTasks(TreeSet<Task> readyTasks) {
        this.readyTasks = readyTasks;
    }

    public TreeSet<Task> getReadyTasks() {
        return this.readyTasks;
    }

    public void addReadyTask(Task task) {
        this.readyTasks.add(task);
    }

    public Task removeFirstReadyTask() {
        return this.readyTasks.pollFirst();
    }

    public boolean thereIsAnotherReadyTask() {
        return !this.readyTasks.isEmpty();
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

    protected boolean blockedTasksContains(Task task) {
        return this.blockedTask.contains(task);
    }

    // METHOD
    protected abstract void assignPriority();

    public abstract void schedule() throws DeadlineMissedException;

}