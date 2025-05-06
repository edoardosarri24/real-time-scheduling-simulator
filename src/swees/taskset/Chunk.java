package swees.taskset;

import java.time.Duration;
import java.util.logging.Logger;

import swees.utils.LoggingConfig;

public class Chunk {

    private final int id;
    private final Duration executionTime;
    private Duration remainingExecutionTime;
    private static final Logger logger = LoggingConfig.getLogger();

    public Chunk(int id, Duration executionTime) {
        this.id = id;
        this.executionTime = executionTime;
        this.remainingExecutionTime = executionTime;
    }

    Duration getRemainingExecutionTime() {
        return this.remainingExecutionTime;
    }

    void execute(Duration executionTime, Task task) {
        remainingExecutionTime = remainingExecutionTime.minus(executionTime);
        logger.info("Il chunk " + this.id + " del task " + task.getId()  + " ha eseguito per " + executionTime);
    }

    void reset() {
        this.remainingExecutionTime = this.executionTime;
    }

}