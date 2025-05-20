package utils;

import java.time.Duration;

public class VirtualClock {

    private Duration currentTime = Duration.ZERO;

    public Duration getCurrentTime() {
        return this.currentTime;
    }

    public void advanceTo(Duration newTime) {
        if (newTime.compareTo(this.currentTime) < 0) {
            throw new IllegalArgumentException("Cannot go back in time");
        }
        this.currentTime = newTime;
    }

    public void advanceBy(Duration delta) {
        if (delta.isNegative())
            throw new IllegalArgumentException("Cannot advance by negative duration");
        this.currentTime = this.currentTime.plus(delta);
    }
    
}