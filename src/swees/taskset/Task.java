package swees.taskset;

import java.time.Duration;

public class Task {

    private static int idCounter = 0;
    private final int id;
    private Duration period;
    private Duration deadline;
    private Chunk chunk;

    public Task(Duration period, Duration deadline, Chunk chunk) {
        this.id = idCounter++;
        this.period = period;
        this.deadline = deadline;
        this.chunk = chunk;
    }

    public Duration getPeriod() {
        return this.period;
    }

    public Duration getDeadline() {
        return this.deadline;
    }

    public Duration getChunkExecutionTime() {
        return this.chunk.getExecutionTime();
    }

    public int getId() {
        return this.id;
    }

    public void execute(Duration currentExecTime) {
        this.chunk.execute(currentExecTime, this);
    }

}