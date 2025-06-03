package scheduler;

import java.time.Duration;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import exeptions.DeadlineMissedException;
import resource.NoResourceProtocol;
import resource.ResourcesProtocol;
import taskSet.Task;
import taskSet.TaskSet;
import utils.MyClock;
import utils.Utils;
import utils.logger.MyLogger;

public abstract class Scheduler {

    private final TaskSet taskSet;
    private final ResourcesProtocol resProtocol;

    private TreeSet<Task> readyTasks;
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

    protected TaskSet getTaskSet() {
        return this.taskSet;
    }

    protected TreeSet<Task> getReadyTasks() {
        return this.readyTasks;
    }

    protected void setReadyTasks(TreeSet<Task> readyTasks) {
        this.readyTasks = readyTasks;
    }

    // METHOD
    public final void schedule() throws DeadlineMissedException {
        MyClock.reset();
        List<Duration> events = initStructures();
        this.releaseAllTasks();
        this.checkFeasibility();
        while (!events.isEmpty()) {
            Duration nextEvent = events.removeFirst();
            Duration availableTime = nextEvent.minus(MyClock.getInstance().getCurrentTime());
            this.executeFor(availableTime);
            MyClock.getInstance().advanceTo(nextEvent);
            this.relasePeriodTasks();
        }
        MyLogger.log("<" + Utils.printCurrentTime() + ", end>\n");
    }

    public abstract void addReadyTask(Task task);

    protected abstract void assignPriority();

    protected abstract void checkFeasibility();

    // HELPER
    private List<Duration> initStructures() {
        this. readyTasks = new TreeSet<>(Comparator.comparingInt(Task::getDinamicPriority));
        this.taskSet.getTasks().forEach(this::addReadyTask);
        List<Duration> periods = this.taskSet.getTasks().stream()
            .map(Task::getPeriod)
            .collect(Collectors.toList());
        List<Duration> events = Utils.generatePeriodUpToLCM(periods);
        return events;
    }

    private void releaseAllTasks() {
        for (Task task : this.readyTasks)
            MyLogger.log("<" + Utils.printCurrentTime() + ", release " + task.toString() + ">");
    }

    private void executeFor(Duration availableTime) throws DeadlineMissedException {
        while (availableTime.isPositive() && !this.readyTasks.isEmpty()) {
            Task currentTask = this.readyTasks.pollFirst();
            if (this.checkLastTaskExecuted(currentTask))
                MyLogger.log("<" + Utils.printCurrentTime() + ", preempt " + this.lastTaskExecuted.toString() + ">");
            Duration executedTime;
            try {
                executedTime = currentTask.execute(availableTime, this);
            } catch (DeadlineMissedException e) {
                MyLogger.log("<" + Utils.printCurrentTime() + ", deadlineMiss " + currentTask.toString() + ">");
                throw new DeadlineMissedException(e.getMessage());
            }
            if (executedTime.isPositive())
                this.lastTaskExecuted = currentTask;
            availableTime = availableTime.minus(executedTime);
            if (!currentTask.getIsExecuted() && !this.blockedTask.contains(currentTask))
                this.addReadyTask(currentTask);
        }
    }

    private boolean checkLastTaskExecuted(Task currentTask) {
        return !(this.lastTaskExecuted==null)
                && !this.lastTaskExecuted.equals(currentTask)
                && !this.lastTaskExecuted.getIsExecuted();
    }

    private void relasePeriodTasks() throws DeadlineMissedException {
        for (Task task : this.taskSet.getTasks()) {
            if (task.toBeRelease()) {
                try {
                    task.relasePeriodTask();
                } catch (DeadlineMissedException e) {
                    MyLogger.log("<" + Utils.printCurrentTime() + ", deadlineMiss " + task.toString() + ">");
                    throw new DeadlineMissedException(e.getMessage());
                }
                this.addReadyTask(task);
            }
        }
    }

}