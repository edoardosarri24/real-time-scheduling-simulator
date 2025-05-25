package scheduler;

import java.time.Duration;
import java.util.Comparator;
import java.util.LinkedList;
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
        // structures
        TreeSet<Task> readyTasks = new TreeSet<>(Comparator.comparingInt(Task::getDinamicPriority));
        getTaskSet().getTasks().forEach(readyTasks::add);
        List<Duration> periods = readyTasks.stream()
            .map(Task::getPeriod)
            .collect(Collectors.toList());
        List<Duration> events = Multiple.generateMultiplesUpToLCM(periods);

        // execution
        for (Task task : readyTasks)
            MyLogger.log("<" + this.getClock().getCurrentTime() + ", release " + task.toString() + ">");
        while (!events.isEmpty()) {
            Duration nextEvent = events.removeFirst();
            MyLogger.log("");
            Duration availableTime = nextEvent.minus(this.getClock().getCurrentTime());
            this.executeUntil(readyTasks, availableTime);
            this.getClock().advanceTo(nextEvent);
            List<Task> releasedTasks = this.relasePeriodTasks(this.getClock().getCurrentTime());
            readyTasks.addAll(releasedTasks);
        }
        MyLogger.log("<" + this.getClock().getCurrentTime() + ", end>");
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
    private List<Task> relasePeriodTasks(Duration currentTime) throws DeadlineMissedException {
        List<Task> taskToRelease = new LinkedList<>();
        for (Task task : getTaskSet().getTasks()) {
            if (currentTime.toMillis() % task.getPeriod().toMillis() == 0) {
                try {
                    task.relasePeriodTasks(currentTime);
                } catch (DeadlineMissedException e) {
                    MyLogger.log("<" + this.getClock().getCurrentTime() + ", deadlineMiss " + task.toString() + ">");
                    throw new DeadlineMissedException(e.getMessage());
                }
                taskToRelease.add(task);
            }
        }
        return taskToRelease;
    }

    private void executeUntil(TreeSet<Task> readyTasks, Duration availableTime) {
        while (availableTime.isPositive() && !readyTasks.isEmpty()) {
            Task currentTask = readyTasks.pollFirst();
            if (!(lastTaskExecuted==null)
                && !lastTaskExecuted.equals(currentTask)
                && !lastTaskExecuted.getIsExecuted())
                MyLogger.log("<" + this.getClock().getCurrentTime() + ", preempt " + lastTaskExecuted.toString() + ">");
            Duration executedTime = currentTask.execute(availableTime, readyTasks, this);
            if (executedTime.isPositive())
                this.lastTaskExecuted = currentTask;
            availableTime = availableTime.minus(executedTime);
            if (!currentTask.getIsExecuted() && !blockedTasksContains(currentTask))
                readyTasks.add(currentTask);
        }
    }

}