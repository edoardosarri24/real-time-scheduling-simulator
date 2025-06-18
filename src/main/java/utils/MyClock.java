package utils;

import java.time.Duration;

/**
 * Represents the global clock of the system.
 * <p>
 * This singleton class provides methods to access and manipulate the current global time,
 * allowing to advance the clock to a specific time or by a given duration.
 */
public final class MyClock {

    private static MyClock INSTANCE = new MyClock();
    private Duration currentTime = Duration.ZERO;

    private MyClock() {}

    public static MyClock getInstance() {
        return INSTANCE;
    }

    public static MyClock reset() {
        INSTANCE = new MyClock();
        return INSTANCE;
    }

    public Duration getCurrentTime() {
        return this.currentTime;
    }

    public void advanceTo(Duration newTime) {
        this.currentTime = newTime;
    }

    public void advanceBy(Duration delta) {
        if (delta.isNegative())
            throw new IllegalArgumentException("Cannot advance by negative duration");
        this.currentTime = this.currentTime.plus(delta);
    }

}