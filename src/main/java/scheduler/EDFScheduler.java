package scheduler;

import java.time.Duration;
import java.util.AbstractMap;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import exeptions.DeadlineMissedException;
import resource.ResourcesProtocol;
import taskSet.Task;
import taskSet.TaskSet;
import utils.MyClock;
import utils.Utils;
import utils.logger.MyLogger;

public final class EDFScheduler extends Scheduler {

    List<Map.Entry<Task, Duration>> readyTasks;

    // CONSTRUCTOR
    public EDFScheduler(TaskSet taskSet) {
        super(taskSet);
        this.getTaskSet().periodAndDealineCheck();
    }

    public EDFScheduler(TaskSet taskSet, ResourcesProtocol resProtocol) {
        super(taskSet, resProtocol);
        this.getTaskSet().periodAndDealineCheck();
    }

    // METHODS
    @Override
    public void schedule() throws DeadlineMissedException {
        List<Duration> events = initStructures();
        this.releaseAllTasks();

        while (!events.isEmpty()) {
            Duration nextEvent = events.removeFirst();
            Duration availableTime = nextEvent.minus(MyClock.getInstance().getCurrentTime());
            this.executeFor(availableTime);
            MyClock.getInstance().advanceTo(nextEvent);
            this.relasePeriodTasks();
        }
        MyLogger.log("<" + Utils.printCurrentTime() + ", end>");
    }

    @Override
	public void addReadyTask(Task task) {
        List<Task> ready = new LinkedList<>(this.getReadyTasks());
        this.readyTasks = new LinkedList<>();
        this.readyTasks.add(new AbstractMap.SimpleEntry<>(task, task.nextDeadline()));
        ready.forEach(t -> this.readyTasks.add(new AbstractMap.SimpleEntry<>(t, t.nextDeadline())));
        this.readyTasks.sort(Comparator.comparing(Map.Entry::getValue));
	}

    // HELPER
    @Override
    protected void assignPriority() {
        List<Task> sortedByDeadline = getTaskSet().getTasks().stream()
            .sorted(Comparator.comparing(Task::getDeadline))
            .collect(Collectors.toList());
        IntStream.range(0, sortedByDeadline.size())
            .forEach(i -> {
                int priority = 5 + i * 2;
                Task task = sortedByDeadline.get(i);
                task.initPriority(priority);
            }
        );
    }

    private List<Duration> initStructures() {
        this.readyTasks = new LinkedList<>();
        this.getTaskSet().getTasks().forEach(
            task -> this.readyTasks.add(new AbstractMap.SimpleEntry<>(task, task.getDeadline())));
        this.readyTasks.sort(Comparator.comparing(Map.Entry::getValue));
        List<Duration> periods = this.getTaskSet().getTasks().stream()
            .map(Task::getPeriod)
            .collect(Collectors.toList());
        List<Duration> events = Utils.generatePeriodUpToLCM(periods);
        return events;
    }

    private void executeFor(Duration availableTime) throws DeadlineMissedException {
        while (availableTime.isPositive() && this.readyTasks.isEmpty()) {
            Task currentTask = this.readyTasks.removeFirst().getKey();
            if (this.checkLastTaskExecuted(currentTask))
                MyLogger.log("<" + Utils.printCurrentTime() + ", preempt " + this.getLastTaskExecuted().toString() + ">");
            Duration executedTime;
            try {
                executedTime = currentTask.execute(availableTime, this);
            } catch (DeadlineMissedException e) {
                MyLogger.log("<" + Utils.printCurrentTime() + ", deadlineMiss " + currentTask.toString() + ">");
                throw new DeadlineMissedException(e.getMessage());
            }
            if (executedTime.isPositive())
                this.setLastTaskExecuted(currentTask);
            availableTime = availableTime.minus(executedTime);
            if (!currentTask.getIsExecuted() && !taskIsBlocked(currentTask))
                this.addReadyTask(currentTask);
        }
    }

    private void relasePeriodTasks() throws DeadlineMissedException {
        List<Task> ready = new LinkedList<>(this.getReadyTasks());
        List<Map.Entry<Task, Duration>> released = new LinkedList<>();
        for (Task task : getTaskSet().getTasks()) {
            if (task.toBeRelease()) {
                try {
                    task.relasePeriodTasks();
                } catch (DeadlineMissedException e) {
                    MyLogger.log("<" + Utils.printCurrentTime() + ", deadlineMiss " + task.toString() + ">");
                    throw new DeadlineMissedException(e.getMessage());
                }
                released.add(new AbstractMap.SimpleEntry<>(
                    task,
                    MyClock.getInstance().getCurrentTime().plus(task.getDeadline())));
            }
        }
        this.readyTasks = new LinkedList<>(released);
        ready.forEach(task -> this.readyTasks.add(new AbstractMap.SimpleEntry<>(task, task.nextDeadline())));
        this.readyTasks.sort(Comparator.comparing(Map.Entry::getValue));
    }

    private void releaseAllTasks() {
        for (Task task : this.getReadyTasks())
            MyLogger.log("<" + Utils.printCurrentTime() + ", release " + task.toString() + ">");
    }

    private List<Task> getReadyTasks() {
        return this.readyTasks.stream()
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

}