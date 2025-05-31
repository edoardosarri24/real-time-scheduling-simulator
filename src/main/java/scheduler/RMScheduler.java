package scheduler;

import java.time.Duration;
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

public final class RMScheduler extends Scheduler {

    // CONSTRUCTOR
    public RMScheduler(TaskSet taskSet) {
        super(taskSet);
        this.getTaskSet().purelyPeriodicCheck();
    }

    public RMScheduler(TaskSet taskSet, ResourcesProtocol resProtocol) {
        super(taskSet, resProtocol);
        this.getTaskSet().purelyPeriodicCheck();
    }

    // METHOD
    @Override
    public void schedule() throws DeadlineMissedException {
        List<Duration> events = initStructures();
        this.releaseAllTasks();
        if (!this.getTaskSet().hyperbolicBoundTest())
            MyLogger.wrn("L'hyperbolic bound test non è passato: il taskset potrebbe non essere schedulabile.");
        while (!events.isEmpty()) {
            Duration nextEvent = events.removeFirst();
            Duration availableTime = nextEvent.minus(MyClock.getInstance().getCurrentTime());
            this.executeFor(availableTime);
            MyClock.getInstance().advanceTo(nextEvent);
            this.relasePeriodTasks();
        }
        MyLogger.log("<" + Utils.printCurrentTime() + ", end>");
    }

    // HELPER
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
            }
        );
    }

    private List<Duration> initStructures() {
        this.setReadyTasks(new TreeSet<>(Comparator.comparingInt(Task::getDinamicPriority)));
        this.getTaskSet().getTasks().forEach(this::addReadyTask);
        List<Duration> periods = this.getTaskSet().getTasks().stream()
            .map(Task::getPeriod)
            .collect(Collectors.toList());
        List<Duration> events = Utils.generatePeriodUpToLCM(periods);
        return events;
    }

    private void relasePeriodTasks() throws DeadlineMissedException {
        for (Task task : getTaskSet().getTasks()) {
            if (task.toBeRelease()) {
                try {
                    task.relasePeriodTasks();
                } catch (DeadlineMissedException e) {
                    MyLogger.log("<" + Utils.printCurrentTime() + ", deadlineMiss " + task.toString() + ">");
                    throw new DeadlineMissedException(e.getMessage());
                }
                this.addReadyTask(task);
            }
        }
    }

    private void executeFor(Duration availableTime) throws DeadlineMissedException {
        while (availableTime.isPositive() && this.thereIsAnotherReadyTask()) {
            Task currentTask = this.removeFirstReadyTask();
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

}