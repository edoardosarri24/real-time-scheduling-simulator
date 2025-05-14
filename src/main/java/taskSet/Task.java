package taskSet;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Logger;

import exeptions.AccessResourceProtocolExecption;
import exeptions.DeadlineMissedException;
import exeptions.NoResourceExecption;
import resource.Resource;
import resource.ResourceProtocol;
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
    private List<Resource> resourcesAcquired = new LinkedList<>();
    private static final Logger logger = LoggingConfig.getLogger();

    // CONSTRUCTOR
    public Task(Duration period, Duration deadline, List<Chunk> chunks) {
        this.id = idCounter++;
        this.period = period;
        this.deadline = deadline;
        this.chunks = chunks;
        this.chunkToExecute = new LinkedList<>(chunks);
    }

    // GETTER AND SETTER
    public Duration getPeriod() {
        return this.period;
    }

    public int getNominalPriority() {
        return this.nominalPriority;
    }

    public void setNominalPriority(int nominalPriority) {
        this.nominalPriority = nominalPriority;
    }

    public void setDinamicPriority(int dinamicPriority) {
        this.dinamicPriority = dinamicPriority;
    }

    public int getDinamicPriority() {
        return this.dinamicPriority;
    }

    List<Chunk> getChunkToExecute() {
        return this.chunkToExecute;
    }

    public List<Resource> getResourcesAcquired () {
        return this.resourcesAcquired;
    }

    public boolean getIsExecuted() {
        return this.isExecuted;
    }

    public int getId() {
        return this.id;
    }

    public List<Chunk> getChunks() {
        return this.chunks;
    }

    // METHOD
    public Duration execute(Duration availableTime, TreeSet<Task> orderedTasks, RMScheduler scheduler) {
        Duration remainingTime = availableTime;
        ResourceProtocol resAccProtocol = scheduler.getResProtocol();
        while (remainingTime.isPositive()) {
            if (this.chunkToExecute.isEmpty()) {
                this.isExecuted = true;
                break;
            } else {
                Chunk currentChucnk = this.chunkToExecute.removeFirst();
                try {
                    resAccProtocol.access(this, scheduler, currentChucnk);
                    resAccProtocol.progress(currentChucnk, this);
                } catch (NoResourceExecption e) {
                } catch (AccessResourceProtocolExecption e) {
                    this.chunkToExecute.addFirst(currentChucnk);
                    return Duration.ZERO;
                }
                Duration executionTime = currentChucnk.getRemainingExecutionTime();
                if (remainingTime.compareTo(executionTime) < 0) {
                    currentChucnk.execute(remainingTime, this);
                    this.chunkToExecute.addFirst(currentChucnk);
                } else {
                    currentChucnk.execute(executionTime, this);
                    try {
                        resAccProtocol.release(currentChucnk, scheduler, orderedTasks, this);
                    } catch (NoResourceExecption e) {}                        
                }
                remainingTime = remainingTime.minus(executionTime);
            }
        }
        return availableTime.minus(remainingTime);
    }

    public void checkAndReset() throws DeadlineMissedException {
        if (!this.isExecuted) {
            throw new DeadlineMissedException("Il task " + this.id + " ha superato la deadline");
        } else {
            this.chunkToExecute = new LinkedList<>(chunks);
            this.isExecuted = false;
            logger.info("Il task " + this.id + " è stato rilasciato");
            for (Chunk chunk : this.chunkToExecute)
                chunk.reset();
        }
    }

    void purelyPeriodicCheck() {
        if (this.period.compareTo(this.deadline) != 0)
            throw new IllegalArgumentException(
                "Il task " + this.id + " non è puramente periodico: ha periodo " + this.period + " e deadline " + this.deadline);
    }

}