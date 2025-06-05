package taskSet;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;

import org.oristool.simulator.samplers.Sampler;

import resource.Resource;
import utils.MyClock;
import utils.Utils;
import utils.logger.MyLogger;
import utils.sampler.ConstantSampler;
import utils.sampler.SampleDuration;

public class Chunk {

    private final int id;
    private Duration expectedExecutionTime;
    private Duration executionTime;
    private final List<Resource> resources;
    private final Sampler exectutionTimeSampler;
    private final Sampler overheadExectutionTimeSampler;
    private Duration remainingExecutionTime;
    private Task parent;

    // CONSTRUCTOR
    public Chunk(int id, Sampler exectutionTimeSampler) {
        this(id, exectutionTimeSampler, new ConstantSampler(new BigDecimal(0)), List.of());
    }

    public Chunk(int id, Sampler exectutionTimeSampler, Sampler overheadExectutionTimeSampler) {
        this(id, exectutionTimeSampler, overheadExectutionTimeSampler, List.of());
    }

    public Chunk(int id, Sampler exectutionTimeSampler, List<Resource> resources) {
        this(id, exectutionTimeSampler, new ConstantSampler(new BigDecimal(0)), resources);
    }

    public Chunk(int id, Sampler exectutionTimeSampler, Sampler overheadExectutionTimeSampler, List<Resource> resources) {
        this.id = id;
        this.exectutionTimeSampler = exectutionTimeSampler;
        this.overheadExectutionTimeSampler = overheadExectutionTimeSampler;
        this.resources = new LinkedList<>(resources);
        this.expectedExecutionTime = SampleDuration.sample(exectutionTimeSampler);
        this.executionTime = this.expectedExecutionTime.plus(SampleDuration.sample(overheadExectutionTimeSampler));
        this.remainingExecutionTime = this.executionTime;
    }

    // GETTER AND SETTER
    public List<Resource> getResources() {
        return this.resources;
    }

    public int getId() {
        return this.id;
    }

    public void setParent(Task task) {
        this.parent = task;
    }

    public Task getParent() {
        return this.parent;
    }

    public boolean hasResources() {
        return !this.resources.isEmpty();
    }

    Duration getExecutionTime() {
        return this.executionTime;
    }

    // METHOD controlla
    public Duration execute(Duration availableTime) {
        if (availableTime.compareTo(this.remainingExecutionTime) < 0) {
            this.remainingExecutionTime = this.remainingExecutionTime.minus(availableTime);
            this.parent.addChunkToExecute(this);
            MyLogger.log("<" + Utils.printCurrentTime() + ", execute " + this.toString() + ">");
            MyClock.getInstance().advanceBy(availableTime);
            return availableTime;
        } else {
            MyLogger.log("<" + Utils.printCurrentTime() + ", execute " + this.toString() + ">");
            MyClock.getInstance().advanceBy(this.remainingExecutionTime);
            MyLogger.log("<" + Utils.printCurrentTime() + ", finish " + this.toString() + ">");
            if (!this.executionTime.equals(this.expectedExecutionTime))
                MyLogger.wrn("Il chunk " + this.id
                    + " ha eseguito per " + this.executionTime
                    + ", ma il suo expected execution time era " + this.expectedExecutionTime);
            return this.remainingExecutionTime;
        }
    }

    public void reset() {
        this.expectedExecutionTime = SampleDuration.sample(exectutionTimeSampler);
        this.executionTime = this.expectedExecutionTime.plus(SampleDuration.sample(overheadExectutionTimeSampler));
        this.remainingExecutionTime = this.executionTime;
    }

    @Override
    public String toString() {
        return ("Chunk" + this.parent.getId() + "." + this.id);
    }

}