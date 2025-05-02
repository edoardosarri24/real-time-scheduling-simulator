package swees.scheduler;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import swees.taskset.Task;
import swees.taskset.Taskset;
import swees.utils.LoggingConfig;
import swees.utils.Multiple;

public class RMScheduler {

    private Taskset tasksSet;
    private static final Logger logger = LoggingConfig.getLogger();

    public RMScheduler(Taskset taskSet) {
        this.tasksSet = taskSet;
    }

    public void schedule() {
        // strutture necessarie
        List<Task> orderedTasks = this.tasksSet.orderByPeriod();
        List<Duration> periods = orderedTasks.stream()
            .map(task -> task.getPeriod())
            .collect(Collectors.toList());
        List<Duration> events = Multiple.generateMultiplesUpToLCM(periods);
        Map<Task, Duration> taskRemainingTime = orderedTasks.stream()
            .collect(Collectors.toMap(
                Function.identity(),
                Task::getChunkExecutionTime,
                (existing, _) -> existing,
                LinkedHashMap::new));
        Duration currentTime = Duration.ZERO;

        while (!events.isEmpty()) {
            // prossimo evento dove fare i controllli
            Duration nextEvent = events.getFirst();
            logger.info("dopo aver preso il prossimo evento " + nextEvent + " i tempi rimanenti sono : ");
            for (Map.Entry<Task, Duration> entry : taskRemainingTime.entrySet())
                logger.info("- Il task : " + entry.getKey().getId() + " ha tempo rimanente " + entry.getValue());
            nextEvent = nextEvent.minus(currentTime);
            logger.info("il valore di next event è: " + nextEvent);

            // finchè il tempo rimanente al prossimo evento è maggiore di 0
            while (nextEvent.isPositive()) {
                //prendo il task a priorità più alta che ha un qualche tempo rimanente
                Optional<Map.Entry<Task, Duration>> currentEntry = taskRemainingTime.entrySet().stream()
                    .filter(entry -> entry.getValue().isPositive())
                    .findFirst();
                // se trovo un task
                if (currentEntry.isPresent()) {
                    Task currentTask = currentEntry.get().getKey();
                    Duration currentExecTime = currentEntry.get().getValue();
                    if (currentExecTime.compareTo(nextEvent) > 0)
                        currentExecTime = nextEvent;
                    // eseguo il task
                    currentTask.execute(currentExecTime);
                    // aggiorno il tempo rimanente
                    currentEntry.get().setValue(currentEntry.get().getValue().minus(currentExecTime));
                    nextEvent = nextEvent.minus(currentExecTime);
                    logger.info("il valore di next event è: " + nextEvent);
                // se non trovo un task
                } else {
                    nextEvent = Duration.ZERO;
                }
            }

            logger.info("prima di aver rimosso il prossimo evento " + events.getFirst() + " i tempi rimanenti sono : ");
            for (Map.Entry<Task, Duration> entry : taskRemainingTime.entrySet())
                logger.info("- Il task : " + entry.getKey().getId() + " ha tempo rimanente " + entry.getValue());

            nextEvent = events.removeFirst();
            currentTime = nextEvent;
            for (Map.Entry<Task, Duration> entry : taskRemainingTime.entrySet()) {
                if (nextEvent.toMillis() % entry.getKey().getPeriod().toMillis() == 0) {
                    if (entry.getValue().isPositive()) {
                        logger.warning("Il task " + entry.getKey().getId() + " ha superato la deadline");
                        return;
                    }
                    entry.setValue(entry.getKey().getChunkExecutionTime());
                }
            }

            logger.info("dopo aver rimosso il prossimo evento " + nextEvent + " i tempi rimanenti sono : ");
            for (Map.Entry<Task, Duration> entry : taskRemainingTime.entrySet())
                logger.info("- Il task : " + entry.getKey().getId() + " ha tempo rimanente " + entry.getValue());
        }
    
    }

}