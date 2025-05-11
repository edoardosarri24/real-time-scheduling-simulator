package scheduler;

import java.time.Duration;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import exeptions.PurelyPeriodicException;
import resource.PriorityCeilingProtocol;
import taskSet.Task;
import taskSet.TaskSet;
import utils.Multiple;
import utils.logger.LoggingConfig;

public final class RMScheduler {

    private TaskSet taskSet;
    private PriorityCeilingProtocol resProtocol;
    private List<Task> blockedTask = new LinkedList<>();;

    private static final Logger logger = LoggingConfig.getLogger();

    // CONSTRUCTOR
    public RMScheduler(TaskSet taskSet) {
        this.taskSet = taskSet;
        checkPeriocity();
    }

    public RMScheduler(TaskSet taskSet, PriorityCeilingProtocol resProtocol) {
        this.taskSet = taskSet;
        checkPeriocity();
        this.resProtocol = resProtocol;
    }

    // GETTER AND SETTER
    public PriorityCeilingProtocol getResProtocol() {
        return this.resProtocol;
    }

    public List<Task> getBlockedTask() {
        return this.blockedTask;
    }

    // METHOD
    public void schedule() {
        // strutture
        TreeSet<Task> orderedTasks = new TreeSet<>(Comparator.comparing(Task::getPeriod));
        this.taskSet.getTasks().forEach(orderedTasks::add);
        List<Duration> periods = orderedTasks.stream()
            .map(Task::getPeriod)
            .collect(Collectors.toList());
        List<Duration> events = Multiple.generateMultiplesUpToLCM(periods);
        Duration currentTime = Duration.ZERO;

        while (!events.isEmpty()) {
            // prossimo evento dove fare i controllli
            Duration nextEvent = events.removeFirst();
            Duration availableTime = nextEvent.minus(currentTime);
            logger.info("- Il tempo disponibile è: " + availableTime);

            while (availableTime.isPositive()) {
                if (orderedTasks.isEmpty()) {
                    availableTime = Duration.ZERO;
                } else {
                    // prendo il task a priorità più alta
                    Task currentTask = orderedTasks.pollFirst();
                    // lo eseguo per al più il tempo rimanente
                    Duration exetutedTime = currentTask.execute(availableTime, orderedTasks, this);
                    // aggiorno il tempo rimanente
                    availableTime = availableTime.minus(exetutedTime);
                    // se il task non è finito lo rimetto in coda
                    if (!currentTask.isExecuted() && !this.blockedTask.contains(currentTask))
                        orderedTasks.add(currentTask);
                }
            }

            // per ogni task il cui periodo è scaduto controllo se ha superato la deadline
            currentTime = nextEvent;
            for (Task task : this.taskSet.getTasks()) {
                if (currentTime.toMillis() % task.getPeriod().toMillis() == 0) {
                    logger.info("- Al tempo "  + currentTime + " il task controllato e resettato: " + task.getId());
                    task.checkAndReset(currentTime);
                    orderedTasks.add(task);
                    logger.info("I task nella coda sono: ");
                    for (Task t : orderedTasks)
                        logger.info(""+ t.getId());
                }
            }
        }
    }

    // HELPER
    private void checkPeriocity() {
        this.taskSet.getTasks().stream()
            .forEach(task -> {
                if (task.getPeriod().compareTo(task.getDeadline()) != 0) {
                    logger.warning("Il task " + task.getId() + " non è puramente periocico");
                    logger.warning("Il task " + task.getId() + " ha periodo " + task.getPeriod() + " e deadline " + task.getDeadline());
                    logger.warning("RM richiede task puramente periocici");
                    throw new PurelyPeriodicException("Il task " + task.getId() + " non è puramente periocico");
                }
            });
    }

}