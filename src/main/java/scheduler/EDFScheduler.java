package scheduler;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
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

    private TreeSet<Task> readyTasks;

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
        if (this.getTaskSet().utilizationFactor() > 1)
            MyLogger.wrn("Il fattore di utilizzo del taskset è maggiore di 1: il taskset non è schedulabile");
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
        List<Task> temp = new ArrayList<>(this.readyTasks);
        temp.addAll(Arrays.asList(task));
        temp.sort(Comparator.comparing(Task::nextDeadline));
        int priority = 1;
        for (Task t : temp)
            t.setDinamicPriority(priority++);
        this.readyTasks = new TreeSet<>(Comparator.comparingInt(Task::getDinamicPriority));
        this.readyTasks.addAll(temp);
    }

    @Override
    protected void assignPriority() {
        List<Task> sortedByDeadline = getTaskSet().getTasks().stream()
            .sorted(Comparator.comparing(Task::getDeadline))
            .collect(Collectors.toList());
        IntStream.range(0, sortedByDeadline.size())
            .forEach(i -> {
                Task task = sortedByDeadline.get(i);
                task.initPriority(i+1);
            }
        );
    }

    // HELPER
    private List<Duration> initStructures() {
        this.readyTasks = new TreeSet<>(Comparator.comparingInt(Task::getDinamicPriority));
        this.getTaskSet().getTasks().forEach(this::addReadyTask);
        List<Duration> periods = this.getTaskSet().getTasks().stream()
            .map(Task::getPeriod)
            .collect(Collectors.toList());
        List<Duration> events = Utils.generatePeriodUpToLCM(periods);
        return events;
    }

    private void executeFor(Duration availableTime) throws DeadlineMissedException {
        while (availableTime.isPositive() && !this.readyTasks.isEmpty()) {
            Task currentTask = this.readyTasks.pollFirst();
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
        for (Task task : getTaskSet().getTasks()) {
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

    private void releaseAllTasks() {
        for (Task task : this.readyTasks)
            MyLogger.log("<" + Utils.printCurrentTime() + ", release " + task.toString() + ">");
    }

}