package taskSet;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import resource.Resource;
import utils.logger.LoggingConfig;

public class Chunk {

    private final int id;
    private final Duration executionTime;
    private Duration remainingExecutionTime;
    private final List<Resource> resources;
    private Task parent;
    private static final Logger logger = LoggingConfig.getLogger();

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

    // METHOD
    public Duration execute(Duration availableTime) {
        if (availableTime.compareTo(this.remainingExecutionTime) < 0) {
            this.remainingExecutionTime = this.remainingExecutionTime.minus(availableTime);
            logger.info("Il chunk " + this.id
                + " del task " + this.getParent().getId() 
                + " ha eseguito per "+ availableTime);
            this.parent.addChunkToExecute(this);
            return availableTime;
        } else {
            logger.info("Il chunk " + this.id
                + " del task " + this.getParent().getId() 
                + " ha eseguito per " + this.remainingExecutionTime);
            return this.remainingExecutionTime;
        }
    }

    public void reset() {
        this.remainingExecutionTime = this.executionTime;
    }

}