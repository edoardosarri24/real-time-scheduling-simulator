package swees.taskset;

import java.time.Duration;

public class Chunk {

    private Duration executionTime;

    public Chunk(Duration executionTime) {
        this.executionTime = executionTime;
    }

    Duration getExecutionTime() {
        return this.executionTime;
    }

}