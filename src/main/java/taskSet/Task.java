package taskSet;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.stream.Stream;

import exeptions.AccessResourceProtocolExecption;
import exeptions.DeadlineMissedException;
import resource.Resource;
import resource.ResourceProtocol;
import scheduler.RMScheduler;
import utils.logger.LoggingConfig;

public class Task {

    private final int id;
    private final Duration period;
    private final Duration deadline;
    private final List<Chunk> chunks;
    private int nominalPriority;
    private int dinamicPriority;

    private static int idCounter = 1;
    private List<Chunk> chunkToExecute;
    private boolean isExecuted = false;
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

    void addChunkToExecute(Chunk chunk) {
        this.chunkToExecute.addFirst(chunk);
    }

    // METHOD
    public Duration execute(Duration availableTime, TreeSet<Task> readyTasks, RMScheduler scheduler) {
        Duration remainingTime = availableTime;
        ResourceProtocol resAccProtocol = scheduler.getResProtocol();
        while (remainingTime.isPositive()) {
            if (this.chunkToExecute.isEmpty()) {
                this.isExecuted = true;
                logger.info("<" + scheduler.getClock().getCurrentTime() + ", complete " + this.toString() + ">");
                break;
            }
            Chunk currentChunk = this.chunkToExecute.removeFirst();
            try {
                resAccProtocol.access(scheduler, currentChunk);
                resAccProtocol.progress(currentChunk);
            } catch (AccessResourceProtocolExecption e) {
                this.chunkToExecute.addFirst(currentChunk);
                return Duration.ZERO;
            }
            Duration executedTime = currentChunk.execute(remainingTime, scheduler.getClock());
            remainingTime = remainingTime.minus(executedTime);
            if (!this.chunkToExecute.isEmpty() && !currentChunk.equals(this.chunkToExecute.getFirst()))
                resAccProtocol.release(currentChunk, scheduler, readyTasks);
        }
        return availableTime.minus(remainingTime);
    }

    public void checkAndReset(Duration currentTime) throws DeadlineMissedException {
        if (!this.isExecuted)
            throw new DeadlineMissedException("Il task " + this.id + " ha superato la deadline");
        this.chunkToExecute = new LinkedList<>(this.chunks);
        this.isExecuted = false;
        logger.info("<" + currentTime + ", release " + this.toString() + ">");
        this.chunkToExecute.forEach(Chunk::reset);
    }

    void purelyPeriodicCheck() {
        if (this.period.compareTo(this.deadline) != 0)
            throw new IllegalArgumentException(
                "Il task " + this.id
                + " non è puramente periodico: ha periodo " + this.period
                + " e deadline " + this.deadline);
    }

    @Override
    public String toString() {
        return "Task" + this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        Task other = (Task) obj;
        return this.id == other.id;
    }

    // HELPER
    private void initChunkPrent() {
        this.chunks.forEach(chunk -> chunk.setParent(this));
    }

}