package scheduler;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import exeptions.DeadlineMissedException;
import resource.NoResourceProtocol;
import resource.ResourceProtocol;
import taskSet.Task;
import taskSet.TaskSet;
import utils.Multiple;

public final class RMScheduler extends Scheduler {

    // CONSTRUCTOR
    public RMScheduler(TaskSet taskSet) {
        this(taskSet, new NoResourceProtocol());
    }

    public RMScheduler(TaskSet taskSet, ResourceProtocol resProtocol) {
        super(taskSet, resProtocol);
        getTaskSet().purelyPeriodicCheck();
    }

    // METHOD
    public void schedule() throws DeadlineMissedException {
        // structures
        TreeSet<Task> readyTasks = new TreeSet<>(Comparator.comparingInt(Task::getDinamicPriority));
        getTaskSet().getTasks().forEach(readyTasks::add);
        List<Duration> periods = readyTasks.stream()
            .map(Task::getPeriod)
            .collect(Collectors.toList());
        List<Duration> events = Multiple.generateMultiplesUpToLCM(periods);
        Duration currentTime = Duration.ZERO;

        // execution
        getLogger().info("Time: 0. Tutti i task sono stati rilascaiti");
        while (!events.isEmpty()) {
            // prossimo evento dove fare i controllli
            Duration nextEvent = events.removeFirst();
            Duration availableTime = nextEvent.minus(currentTime);

            while (availableTime.isPositive() && !readyTasks.isEmpty()) {
                Task currentTask = readyTasks.pollFirst();
                Duration executedTime = currentTask.execute(availableTime, readyTasks, this);
                availableTime = availableTime.minus(executedTime);
                if (!currentTask.getIsExecuted() && !blockedTasksContains(currentTask))
                    readyTasks.add(currentTask);
            }

            // per ogni task il cui periodo è scaduto controllo se ha superato la deadline
            currentTime = nextEvent;
            getLogger().info("Time: " + currentTime);
            for (Task task : getTaskSet().getTasks()) {
                if (currentTime.toMillis() % task.getPeriod().toMillis() == 0) {
                    task.checkAndReset();
                    readyTasks.add(task);
                }
            }
        }
        getLogger().info("La generazione di tracce è avvenuta con successo!");
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

}