package taskset;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import utils.Logger.LoggingConfig;

public class Task {

    private static int idCounter = 0;
    private final int id;
    private final Duration period;
    private final Duration deadline;
    private final List<Chunk> chunks;
    private List<Chunk> chunkToExecute;
    private boolean isExecuted = false;
    private static final Logger logger = LoggingConfig.getLogger();

    public Task(Duration period, Duration deadline, List<Chunk> chunks) {
        this.id = idCounter++;
        this.period = period;
        this.deadline = deadline;
        this.chunks = chunks;
        this.chunkToExecute = new LinkedList<>(chunks);
    }

    public Duration getPeriod() {
        return this.period;
    }

    public Duration getDeadline() {
        return this.deadline;
    }

    public boolean isExecuted() {
        return this.isExecuted;
    }

    public int getId() {
        return this.id;
    }

    public Duration execute(Duration availableTime) {
        Duration remainingTime = availableTime;
        while (remainingTime.isPositive()) {
            if (chunkToExecute.isEmpty()) {
                this.isExecuted = true;
                break;
            } else {
                Chunk chunk = chunkToExecute.removeFirst();
                Duration executionTime = chunk.getRemainingExecutionTime();
                if (remainingTime.compareTo(executionTime) < 0) {
                    chunk.execute(remainingTime, this);
                    this.chunkToExecute.addFirst(chunk);
                } else {
                    chunk.execute(executionTime, this);
                }
                remainingTime = remainingTime.minus(executionTime);
            }
        }
        return availableTime.minus(remainingTime);
    }

    public void checkAndReset(Duration currentTime) {
        if (!this.isExecuted) {
            logger.warning("Il task " + this.id + " ha superato la deadline");  
            System.exit(1);
        } else {
            this.chunkToExecute = new LinkedList<>(chunks);
            this.isExecuted = false;
            for (Chunk chunk : this.chunkToExecute) {
                chunk.reset();
            }
        }
    }

}