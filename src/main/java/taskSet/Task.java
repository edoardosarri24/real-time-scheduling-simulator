package taskSet;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.stream.Stream;

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
        this.initChunkPrent();
    }

    // GETTER AND SETTER
    public Duration getPeriod() {
        return this.period;
    }

    public int getNominalPriority() {
        return this.nominalPriority;
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

    public Stream<Resource> getResourcesAcquiredStream () {
        return this.resourcesAcquired.stream();
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

    public void initPriority(int priority) {
        this.nominalPriority = priority;
        this.dinamicPriority = priority;
    }

    public boolean hasAquiredThatResource(Resource resource) {
        return this.resourcesAcquired.contains(resource);
    }

    public void releaseResource(Resource resource) {
        this.resourcesAcquired.remove(resource);
    }

    public void acquireResources(List<Resource> resources) {
        this.resourcesAcquired.addAll(resources);
    }

    // METHOD
    public Duration execute(Duration availableTime, TreeSet<Task> readyTasks, RMScheduler scheduler) {
        Duration remainingTime = availableTime;
        ResourceProtocol resAccProtocol = scheduler.getResProtocol();
        while (remainingTime.isPositive()) {
            if (this.chunkToExecute.isEmpty()) {
                this.isExecuted = true;
                break;
            } else {
                Chunk currentChucnk = this.chunkToExecute.removeFirst();
                try {
                    resAccProtocol.access(scheduler, currentChucnk);
                    resAccProtocol.progress(currentChucnk);
                } catch (NoResourceExecption e) {
                } catch (AccessResourceProtocolExecption e) {
                    this.chunkToExecute.addFirst(currentChucnk);
                    return Duration.ZERO;
                }
                Duration chunkExecutionTime = currentChucnk.getRemainingExecutionTime();
                logger.info("il tempo rimanente è " + remainingTime);
                if (remainingTime.compareTo(chunkExecutionTime) < 0) {
                    currentChucnk.execute(remainingTime);
                    remainingTime = Duration.ZERO;
                    this.chunkToExecute.addFirst(currentChucnk);
                } else {
                    currentChucnk.execute(chunkExecutionTime);
                    remainingTime = remainingTime.minus(chunkExecutionTime);
                    try {
                        resAccProtocol.release(currentChucnk, scheduler, readyTasks);
                    } catch (NoResourceExecption e) {}
                }
                logger.info("il tempo rimanente è " + remainingTime);
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

    // HELPER
    private void initChunkPrent() {
        for (Chunk chunk : this.chunks)
            chunk.setParent(this);
    }

}