package scheduler;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;

import java.util.TreeSet;
import java.util.stream.Collectors;

import taskset.Task;
import taskset.TaskSet;
import utils.Multiple;

public final class RMScheduler extends Scheduler {

    public RMScheduler(TaskSet taskSet) {
        super(taskSet);
        checkPeriocity();
    }

    @Override
    public void schedule() {
        // strutture
        TreeSet<Task> orderedTasks = new TreeSet<>(Comparator.comparing(Task::getPeriod));
        getTaskSet().getTasks().forEach(orderedTasks::add);
        List<Duration> periods = orderedTasks.stream()
            .map(Task::getPeriod)
            .collect(Collectors.toList());
        List<Duration> events = Multiple.generateMultiplesUpToLCM(periods);
        Duration currentTime = Duration.ZERO;

        while (!events.isEmpty()) {
            // prossimo evento dove fare i controllli
            Duration nextEvent = events.removeFirst();
            Duration availableTime = nextEvent.minus(currentTime);
            getLogger().info("- Il tempo disponibile è: " + availableTime);

            while (availableTime.isPositive()) {
                if (orderedTasks.isEmpty()) {
                    availableTime = Duration.ZERO;
                } else {
                    // prendo il task a priorità più alta
                    Task currentTask = orderedTasks.pollFirst();
                    // lo eseguo per al più il tempo rimanente
                    Duration exetutedTime = currentTask.execute(availableTime);
                    // aggiorno il tempo rimanente
                    availableTime = availableTime.minus(exetutedTime);
                    // se il task non è finito lo rimetto in coda
                    if (!currentTask.isExecuted()) {
                        orderedTasks.add(currentTask);
                    }
                }
            }

            // per ogni task il cui periodo è scaduto controllo se ha superato la deadline
            currentTime = nextEvent;
            for (Task task : getTaskSet().getTasks()) {
                if (currentTime.toMillis() % task.getPeriod().toMillis() == 0) {
                    getLogger().info("- Al tempo "  + currentTime + " il task controllato e resettato: " + task.getId());
                    task.checkAndReset(currentTime);
                    orderedTasks.add(task);
                    getLogger().info("I task nella coda sono: ");
                    for (Task t : orderedTasks)
                        getLogger().info(""+ t.getId());
                }
            }
        }
    }

    private void checkPeriocity() {
        getTaskSet().getTasks().stream()
            .forEach(task -> {
                if (task.getPeriod().compareTo(task.getDeadline()) != 0) {
                    getLogger().warning("Il task " + task.getId() + " non è puramente periocico");
                    getLogger().warning("Il task " + task.getId() + " ha periodo " + task.getPeriod() + " e deadline " + task.getDeadline());
                    getLogger().warning("RM richiede task puramente periocici");
                    System.exit(1);
                }
            });
    }

}