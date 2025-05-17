package scheduler;

import java.time.Duration;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import exeptions.DeadlineMissedException;
import resource.NoResourceProtocol;
import resource.ResourceProtocol;
import taskSet.Task;
import taskSet.TaskSet;
import utils.Multiple;
import utils.logger.LoggingConfig;

public final class RMScheduler {

    private final TaskSet taskSet;
    private final ResourceProtocol resProtocol;
    private List<Task> blockedTask = new LinkedList<>();
    private static final Logger logger = LoggingConfig.getLogger();

    // CONSTRUCTOR
    public RMScheduler(TaskSet taskSet) {
        this(taskSet, new NoResourceProtocol());
    }

    public RMScheduler(TaskSet taskSet, ResourceProtocol resProtocol) {
        taskSet.purelyPeriodicCheck();
        this.taskSet = taskSet;
        this.assignPriority();
        this.resProtocol = resProtocol;
    }

    // GETTER AND SETTER
    public ResourceProtocol getResProtocol() {
        return this.resProtocol;
    }

    public void blockTask(Task task) {
        this.blockedTask.add(task);
    }

    public void unblockTask(Task task) {
        this.blockedTask.remove(task);
    }

    // METHOD
    public void schedule() throws DeadlineMissedException {
        // structures
        TreeSet<Task> readyTasks = new TreeSet<>(Comparator.comparingInt(Task::getDinamicPriority));
        this.taskSet.getTasks().forEach(readyTasks::add);
        List<Duration> periods = readyTasks.stream()
            .map(Task::getPeriod)
            .collect(Collectors.toList());
        List<Duration> events = Multiple.generateMultiplesUpToLCM(periods);
        Duration currentTime = Duration.ZERO;

        // execution
        logger.info("Time: 0. Tutti i task sono stati rilascaiti");
        while (!events.isEmpty()) {
            // prossimo evento dove fare i controllli
            Duration nextEvent = events.removeFirst();
            Duration availableTime = nextEvent.minus(currentTime);

            while (availableTime.isPositive()) {
                if (readyTasks.isEmpty()) {
                    break;
                } else {
                    Task currentTask = readyTasks.pollFirst();
                    Duration executedTime = currentTask.execute(availableTime, readyTasks, this);
                    availableTime = availableTime.minus(executedTime);
                    if (!currentTask.getIsExecuted() && !this.blockedTask.contains(currentTask))
                        readyTasks.add(currentTask);
                }
            }

            // per ogni task il cui periodo è scaduto controllo se ha superato la deadline
            currentTime = nextEvent;
            logger.info("Time: " + currentTime);
            for (Task task : this.taskSet.getTasks()) {
                if (currentTime.toMillis() % task.getPeriod().toMillis() == 0) {
                    task.checkAndReset();
                    readyTasks.add(task);
                }
            }
        }
        logger.info("La generazione di tracce è avvenuta con successo!");
    }

    // HELPER
    private void assignPriority() {
        List<Task> sortedByPeriod = this.taskSet.getTasks().stream()
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