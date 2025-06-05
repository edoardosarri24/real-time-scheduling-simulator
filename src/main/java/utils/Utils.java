package utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Utils {

    // METHODS
    /**
     * Returns the current time as a string representing milliseconds with three decimal places.
     * The current time is obtained from the {@link MyClock} singleton instance.
     *
     * @return A string representation of the current time in milliseconds, rounded to three decimal places
     */
    public static String printCurrentTime() {
        Duration currentTime = MyClock.getInstance().getCurrentTime();
        long nanos = currentTime.toNanos();
        BigDecimal millis = new BigDecimal(nanos).divide(BigDecimal.TEN.pow(6), 3, RoundingMode.HALF_UP);
        return "" + millis;
    }

    /**
     * Generates a sorted list of unique {@link Duration} objects representing all multiples
     * of the given periods up to the specified simulation duration.
     *
     * <p>For each period in the input list, this method computes all its multiples (starting from the period itself)
     * that do not exceed the simulation duration. The resulting durations are collected, deduplicated, and sorted
     * in natural order.</p>
     *
     * @param periods the list of periods as {@link Duration} objects; must not be null or empty
     * @param simulationDuration the maximum duration up to which multiples are generated; must not be null
     * @return a sorted {@link List} of unique {@link Duration} objects representing all multiples of the input periods up to the simulation duration
     * @throws IllegalArgumentException if the periods list is null or empty
     */
    public static List<Duration> generatePeriodUpToMax(List<Duration> periods, Duration simulationDuration) {
        if (periods == null || periods.isEmpty())
            throw new IllegalArgumentException("La lista dei periodi è vuota o nulla.");

        List<Long> nanosList = periods.stream()
            .map(Duration::toNanos)
            .sorted()
            .collect(Collectors.toList());

        long maxNanos = simulationDuration.toNanos();
        Set<Duration> resultSet = new TreeSet<>(Comparator.naturalOrder());

        for (long baseNanos : nanosList) {
            long multiple = baseNanos;
            while (multiple <= maxNanos) {
                resultSet.add(Duration.ofNanos(multiple));
                multiple += baseNanos;
            }
        }

        return new ArrayList<>(resultSet);
    }

}