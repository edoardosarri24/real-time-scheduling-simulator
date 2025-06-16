package scheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import taskSet.Task;
import taskSet.TaskSet;

public final class EDFScheduler extends Scheduler {

    // CONSTRUCTOR
    public EDFScheduler(TaskSet taskSet, double simulationDuration) {
        super(taskSet, simulationDuration);
        this.getTaskSet().periodAndDealineCheck();
    }

    // METHODS
    @Override
	public void addReadyTask(Task task) {
        List<Task> temp = new ArrayList<>(this.getReadyTasks());
        temp.addAll(Arrays.asList(task));
        temp.sort(Comparator.comparing(Task::nextDeadline));
        int priority = 1;
        for (Task t : temp)
            t.setDinamicPriority(priority++);
        this.setReadyTasks(new TreeSet<>(Comparator.comparingInt(Task::getDinamicPriority)));
        this.getReadyTasks().addAll(temp);
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

    @Override
    public boolean checkFeasibility() {
        return this.getTaskSet().utilizationFactor() <= 1;
    }

}