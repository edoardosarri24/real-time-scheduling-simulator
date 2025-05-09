package taskset;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import resource.PriorityCeilingProtocol;
import resource.Resource;
import scheduler.RMScheduler;
import utils.logger.LoggingConfig;

public final class Task {

    private static int idCounter = 0;
    private final int id;
    private final Duration period;
    private final Duration deadline;
    private final List<Chunk> chunks;
    private List<Chunk> chunkToExecute;
    private boolean isExecuted = false;
    private int nominalPriority;
    private int dinamicPriority;
    private static final Logger logger = LoggingConfig.getLogger();

    // CONSTRUCTOR
    public Task(Duration period, Duration deadline, List<Chunk> chunks) {
        this.id = idCounter++;
        this.period = period;
        this.deadline = deadline;
        this.chunks = chunks;
        this.chunkToExecute = new LinkedList<>(chunks);
    }

    public Task(Duration period, Duration deadline, List<Chunk> chunks, int priority) {
        this.id = idCounter++;
        this.period = period;
        this.deadline = deadline;
        this.chunks = chunks;
        this.chunkToExecute = new LinkedList<>(chunks);
        this.nominalPriority = priority;
        this.dinamicPriority = priority;
    }

    // GETTER AND SETTER
    public Duration getPeriod() {
        return this.period;
    }

    public int getNominalPriority() {
        return this.nominalPriority;
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

    public List<Chunk> getChunks() {
        return this.chunks;
    }

    public void setDinamicPriority (int priority) {
        this.dinamicPriority = Math.max(this.dinamicPriority, priority);
    }

    // METHOD
    public Duration execute(Duration availableTime, RMScheduler scheduler) {
        Duration remainingTime = availableTime;
        PriorityCeilingProtocol resAccProtocol = scheduler.getResProtocol();
        while (remainingTime.isPositive()) {
            if (chunkToExecute.isEmpty()) {
                this.isExecuted = true;
                break;
            } else {
                Chunk chunk = chunkToExecute.removeFirst();
                if (!(chunk.getResources()==null)) {
                    boolean accessOk = resAccProtocol.access(this, scheduler);
                    if (!accessOk)
                        return Duration.ZERO;
                    resAccProtocol.progress(chunk, this);
                }
                // SONO AL CHUNK CHE DEVE ESEGUIRE HO FATTO ACCESSO E PROGRESSO
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