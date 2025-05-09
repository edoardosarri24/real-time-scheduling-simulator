package scheduler;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import resource.PriorityCeilingProtocol;
import taskset.Task;
import taskset.TaskSet;
import utils.Multiple;
import utils.logger.LoggingConfig;

public final class RMScheduler {

    private TaskSet taskSet;
    private PriorityCeilingProtocol resProtocol;
    private List<Task> blockedTask;

    private static final Logger logger = LoggingConfig.getLogger();

    public RMScheduler(TaskSet taskSet) {
        this.taskSet = taskSet;
        checkPeriocity();
    }

    public RMScheduler(TaskSet taskSet, PriorityCeilingProtocol resProtocol) {
        this.taskSet = taskSet;
        checkPeriocity();
        this.resProtocol = resProtocol;
        this.blockedTask = new ArrayList<>();
    }

    public PriorityCeilingProtocol getResProtocol() {
        return this.resProtocol;
    }

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
                    Duration exetutedTime = currentTask.execute(availableTime, this);
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

    private void checkPeriocity() {
        this.taskSet.getTasks().stream()
            .forEach(task -> {
                if (task.getPeriod().compareTo(task.getDeadline()) != 0) {
                    logger.warning("Il task " + task.getId() + " non è puramente periocico");
                    logger.warning("Il task " + task.getId() + " ha periodo " + task.getPeriod() + " e deadline " + task.getDeadline());
                    logger.warning("RM richiede task puramente periocici");
                    System.exit(1);
                }
            });
    }

    public void block(Task task) {
        this.blockedTask.addLast(task);
    }

}