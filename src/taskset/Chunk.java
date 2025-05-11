package taskset;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import resource.Resource;
import utils.logger.LoggingConfig;

public final class Chunk {

    private final int id;
    private final Duration executionTime;
    private Duration remainingExecutionTime;
    private List<Resource> resources;
    private static final Logger logger = LoggingConfig.getLogger();

    // CONSTRUCTOR
    public Chunk(int id, Duration executionTime) {
        this.id = id;
        this.executionTime = executionTime;
        this.remainingExecutionTime = executionTime;
    }

    public Chunk(int id, Duration executionTime, List<Resource> resources) {
        this.id = id;
        this.executionTime = executionTime;
        this.remainingExecutionTime = executionTime;
        this.resources = new LinkedList<>(resources);
    }

    // GETTER AND SETTER
    Duration getRemainingExecutionTime() {
        return this.remainingExecutionTime;
    }

    public List<Resource> getResources() {
        return this.resources;
    }

    // METHOD
    void execute(Duration executionTime, Task task) {
        remainingExecutionTime = remainingExecutionTime.minus(executionTime);
        logger.info("Il chunk " + this.id + " del task " + task.getId()  + " ha eseguito per " + executionTime);
    }

    void reset() {
        this.remainingExecutionTime = this.executionTime;
    }

}