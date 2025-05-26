package scheduler;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import exeptions.DeadlineMissedException;
import resource.NoResourceProtocol;
import resource.ResourcesProtocol;
import taskSet.Task;
import taskSet.TaskSet;
import utils.Multiple;
import utils.MyClock;
import utils.logger.MyLogger;

public final class RMScheduler extends Scheduler {

    private Task lastTaskExecuted;

    // CONSTRUCTOR
    public RMScheduler(TaskSet taskSet) {
        this(taskSet, new NoResourceProtocol());
    }

    public RMScheduler(TaskSet taskSet, ResourcesProtocol resProtocol) {
        super(taskSet, resProtocol);
        getTaskSet().purelyPeriodicCheck();
    }

    // METHOD
    @Override
    public void schedule() throws DeadlineMissedException {
        List<Duration> events = initStructures();

        releaseAllTasks();
        while (!events.isEmpty()) {
            Duration nextEvent = events.removeFirst();
            MyLogger.log("");
            Duration availableTime = nextEvent.minus(MyClock.getInstance().getCurrentTime());
            this.executeFor(availableTime);
            MyClock.getInstance().advanceTo(nextEvent);
            this.relasePeriodTasks();
        }
        MyLogger.log("<" + MyClock.getInstance().getCurrentTime() + ", end>");
    }

    @Override
    protected void assignPriority() {
        List<Task> sortedByPeriod = getTaskSet().getTasks().stream()
            .sorted(Comparator.comparing(Task::getPeriod))
            .collect(Collectors.toList());
        IntStream.range(0, sortedByPeriod.size())
            .forEach(i -> {
                int priority = 5 + i * 2;
                Task task = sortedByPeriod.get(i);
                task.initPriority(priority);
            });
    }

    // HELPER
    private List<Duration> initStructures() {
        this.setReadyTasks(new TreeSet<>(Comparator.comparingInt(Task::getDinamicPriority)));
        this.getTaskSet().getTasks().forEach(this.getReadyTasks()::add);
        List<Duration> periods = this.getReadyTasks().stream()
            .map(Task::getPeriod)
            .collect(Collectors.toList());
        List<Duration> events = Multiple.generateMultiplesUpToLCM(periods);
        return events;
    }

    private void releaseAllTasks() {
        for (Task task : this.getReadyTasks())
            MyLogger.log("<" + MyClock.getInstance().getCurrentTime() + ", release " + task.toString() + ">");
    }

    private void relasePeriodTasks() throws DeadlineMissedException {
        for (Task task : getTaskSet().getTasks()) {
            if (MyClock.getInstance().getCurrentTime().toMillis() % task.getPeriod().toMillis() == 0) {
                try {
                    task.relasePeriodTasks();
                } catch (DeadlineMissedException e) {
                    MyLogger.log("<" + MyClock.getInstance().getCurrentTime() + ", deadlineMiss " + task.toString() + ">");
                    throw new DeadlineMissedException(e.getMessage());
                }
                this.addReadyTask(task);
            }
        }
    }

    private void executeFor(Duration availableTime) {
        while (availableTime.isPositive() && this.thereIsAnotherReadyTask()) {
            Task currentTask = this.removeFirstReadyTask();
            if (!(lastTaskExecuted==null)
                && !lastTaskExecuted.equals(currentTask)
                && !lastTaskExecuted.getIsExecuted())
                MyLogger.log("<" + MyClock.getInstance().getCurrentTime() + ", preempt " + lastTaskExecuted.toString() + ">");
            Duration executedTime = currentTask.execute(availableTime, this);
            if (executedTime.isPositive())
                this.lastTaskExecuted = currentTask;
            availableTime = availableTime.minus(executedTime);
            if (!currentTask.getIsExecuted() && !blockedTasksContains(currentTask))
                this.addReadyTask(currentTask);
        }
    }

}