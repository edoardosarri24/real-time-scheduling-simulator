package scheduler;

import java.util.logging.Logger;

import taskset.TaskSet;
import utils.logger.LoggingConfig;

public abstract class Scheduler {

    private TaskSet taskSet;
    private static final Logger logger = LoggingConfig.getLogger();

    public Scheduler(TaskSet taskSet) {
        this.taskSet = taskSet;
    }

    protected TaskSet getTaskSet() {
        return this.taskSet;
    }

    protected static Logger getLogger() {
        return logger;
    }

    public abstract void schedule();
    
}