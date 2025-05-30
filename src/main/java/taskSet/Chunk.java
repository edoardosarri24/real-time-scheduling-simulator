package taskSet;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import resource.Resource;
import utils.MyClock;
import utils.Utils;
import utils.logger.MyLogger;

public class Chunk {

    private final int id;
    private final Duration expectedExecutionTime;
    private Duration executionTime;
    private final List<Resource> resources;

    private Duration remainingExecutionTime;
    private Task parent;

    // CONSTRUCTOR
    public Chunk(int id, Duration executionTime) {
        this(id, executionTime, Duration.ZERO, List.of());
    }

    public Chunk(int id, Duration executionTime, Duration overheadExectutionTime) {
        this(id, executionTime, overheadExectutionTime, List.of());
    }

    public Chunk(int id, Duration executionTime, List<Resource> resources) {
        this(id, executionTime, Duration.ZERO, resources);
    }

    public Chunk(int id, Duration executionTime, Duration overheadExectutionTime, List<Resource> resources) {
        this.id = id;
        this.executionTime = executionTime.plus(overheadExectutionTime);
        this.expectedExecutionTime = executionTime;
        this.remainingExecutionTime = executionTime;
        this.resources = new LinkedList<>(resources);
    }

    // GETTER AND SETTER
    public List<Resource> getResources() {
        return this.resources;
    }

    public int getId() {
        return this.id;
    }

    public void setParent(Task task) {
        this.parent = task;
    }

    public Task getParent() {
        return this.parent;
    }

    public boolean hasResources() {
        return !this.resources.isEmpty();
    }

    // METHOD controlla
    public Duration execute(Duration availableTime) {
        if (availableTime.compareTo(this.remainingExecutionTime) < 0) {
            this.remainingExecutionTime = this.remainingExecutionTime.minus(availableTime);
            this.parent.addChunkToExecute(this);
            MyLogger.log("<" + Utils.printCurrentTime() + ", execute " + this.toString() + ">");
            MyClock.getInstance().advanceBy(availableTime);
            return availableTime;
        } else {
            MyLogger.log("<" + Utils.printCurrentTime() + ", execute " + this.toString() + ">");
            MyClock.getInstance().advanceBy(this.remainingExecutionTime);
            MyLogger.log("<" + Utils.printCurrentTime() + ", finish " + this.toString() + ">");
            if (!this.executionTime.equals(this.expectedExecutionTime))
                MyLogger.wrn("Il chunk " + this.id
                    + " ha eseguito per " + this.executionTime
                    + ", ma il suo expected execution time era " + this.expectedExecutionTime);
            return this.remainingExecutionTime;
        }
    }

    public void reset() {
        this.remainingExecutionTime = this.executionTime;
    }

    @Override
    public String toString() {
        return ("Chunk" + this.parent.getId() + "." + this.id);
    }

}