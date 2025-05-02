package swees.taskset;

import java.time.Duration;
import java.util.logging.Logger;

import swees.utils.LoggingConfig;

public class Chunk {

    private Duration executionTime;
    private static final Logger logger = LoggingConfig.getLogger();

    public Chunk(Duration executionTime) {
        this.executionTime = executionTime;
    }

    Duration getExecutionTime() {
        return this.executionTime;
    }

    public void execute(Duration currentExecTime, Task task) {
        logger.info("Il chunk del task " + task.getId()  + " ha eseguito per " + currentExecTime + " secondi");
    }

}