package taskSet;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;

import resource.Resource;
import utils.VirtualClock;
import utils.logger.MyLogger;

public class Chunk {

    private final int id;
    private final Duration executionTime;
    private final List<Resource> resources;

    private Duration remainingExecutionTime;
    private Task parent;

    // CONSTRUCTOR
    public Chunk(int id, Duration executionTime) {
        this(id, executionTime, List.of());
    }

    public Chunk(int id, Duration executionTime, List<Resource> resources) {
        this.id = id;
        this.executionTime = executionTime;
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
    public Duration execute(Duration availableTime, VirtualClock clock) {
        if (availableTime.compareTo(this.remainingExecutionTime) < 0) {
            this.remainingExecutionTime = this.remainingExecutionTime.minus(availableTime);
            this.parent.addChunkToExecute(this);
            MyLogger.log("<" + clock.getCurrentTime() + ", execute " + this.toString() + ">");
            clock.advanceBy(availableTime);
            return availableTime;
        } else {
            MyLogger.log("<" + clock.getCurrentTime() + ", execute " + this.toString() + ">");
            clock.advanceBy(this.remainingExecutionTime);
            MyLogger.log("<" + clock.getCurrentTime() + ", finish " + this.toString() + ">");
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