package utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.math.LongMath;

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
     * Generates a sorted list of unique {@link Duration} objects representing all positive multiples
     * of each duration in the input list, up to and including the least common multiple (LCM) of their
     * nanosecond values.
     *
     * @param durations the list of {@link Duration} objects to process; must not be null or empty
     * @return a sorted list of unique {@link Duration} objects representing all multiples up to the LCM
     * @throws RuntimeException if the input list is null or empty
     */
    public static List<Duration> generateMultiplesUpToLCM(List<Duration> durations) {
        if (durations == null || durations.isEmpty())
            throw new RuntimeException("empty list");
        List<Long> nanosList = durations.stream()
            .map(Duration::toNanos)
            .sorted()
            .collect(Collectors.toList());
        long lcm = nanosList.getFirst();
        for (int i = 1; i < nanosList.size(); i++) {
            lcm = lcm(lcm, nanosList.get(i));
        }
        List<Duration> result = new ArrayList<>();
        for (long baseNanos : nanosList) {
            long multiple = baseNanos;
            while (multiple <= lcm) {
                result.add(Duration.ofNanos(multiple));
                multiple += baseNanos;
            }
        }
        return result.stream()
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }

    // HELPER
    /**
     * Computes the Least Common Multiple (LCM) of two long integers.
     *
     * @param a the first number
     * @param b the second number
     * @return the least common multiple of a and b
     */
    private static Long lcm(long a, long b) {
        return (a / LongMath.gcd(a, b)) * b;
    }

}