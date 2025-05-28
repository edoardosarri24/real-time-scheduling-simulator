package taskSet;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import exeptions.AccessResourceProtocolExeption;
import exeptions.DeadlineMissedException;
import resource.Resource;
import resource.ResourcesProtocol;
import scheduler.Scheduler;
import utils.MyClock;
import utils.Utils;
import utils.logger.MyLogger;

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

    // CONSTRUCTOR
    public Task(Duration period, Duration deadline, List<Chunk> chunks) {
        this.id = idCounter++;
        this.period = period;
        this.deadline = deadline;
        this.chunks = chunks;
        this.chunkToExecute = new LinkedList<>(chunks);
        this.initChunkParent();
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

    public void addChunkToExecute(Chunk chunk) {
        this.chunkToExecute.addFirst(chunk);
    }

    // METHOD
    public Duration execute(Duration availableTime, Scheduler scheduler) {
        ResourcesProtocol resAccProtocol = scheduler.getResProtocol();
        Duration remainingTime = availableTime;
        while (remainingTime.isPositive()) {
            if (this.chunkToExecute.isEmpty()) {
                this.isExecuted = true;
                MyLogger.log("<" + Utils.durationPrinter(MyClock.getInstance().getCurrentTime()) + ", complete " + this.toString() + ">");
                break;
            }
            Chunk currentChunk = this.chunkToExecute.removeFirst();
            try {
                resAccProtocol.access(currentChunk);
                resAccProtocol.progress(currentChunk);
            } catch (AccessResourceProtocolExeption e) {
                return Duration.ZERO;
            }
            Duration executedTime = currentChunk.execute(remainingTime);
            remainingTime = remainingTime.minus(executedTime);
            if(!this.chunkToExecute.stream()
                .filter(chunk -> chunk.equals(currentChunk))
                .findFirst()
                .isPresent())
                resAccProtocol.release(currentChunk);
        }
        return availableTime.minus(remainingTime);
    }

    void purelyPeriodicCheck() {
        if (this.period.compareTo(this.deadline) != 0)
            throw new IllegalArgumentException(
                "Il task " + this.id
                + " non è puramente periodico: ha periodo " + this.period
                + " e deadline " + this.deadline);
    }

    public void relasePeriodTasks() throws DeadlineMissedException {
        if (!this.isExecuted)
            throw new DeadlineMissedException("Il task " + this.id + " ha superato la deadline");
        this.chunkToExecute = new LinkedList<>(this.chunks);
        this.isExecuted = false;
        MyLogger.log("<" + Utils.durationPrinter(MyClock.getInstance().getCurrentTime()) + ", release " + this.toString() + ">");
        this.chunkToExecute.forEach(Chunk::reset);
    }

    // OBJECT METHODS
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

    @Override
    public int hashCode() {
        return this.id;
    }

    // HELPER
    private void initChunkParent() {
        this.chunks.forEach(chunk -> chunk.setParent(this));
    }

}