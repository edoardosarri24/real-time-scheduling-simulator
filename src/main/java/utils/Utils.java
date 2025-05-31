package utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.math.LongMath;

import taskSet.Task;

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
     * @param periods the list of {@link Duration} objects to process; must not be null or empty
     * @return a sorted list of unique {@link Duration} objects representing all multiples up to the LCM
     * @throws RuntimeException if the input list is null or empty
     */
    public static List<Duration> generatePeriodUpToLCM(List<Duration> periods) {
        if (periods == null || periods.isEmpty())
            throw new RuntimeException("empty list");
        List<Long> nanosList = periods.stream()
            .map(Duration::toNanos)
            .sorted()
            .collect(Collectors.toList());
        long lcm = LCMOfList(nanosList);
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

    /**
     * Generates a sorted list of deadlines of the form i * period + deadline for each task,
     * up to the least common multiple (LCM) of all task periods (in milliseconds).
     *
     * @param tasks the list of {@link Task} to process; must not be null or empty
     * @return a sorted list of unique {@link Duration} objects representing all generated deadlines
     * @throws RuntimeException if the input list is null or empty
     */
    public static List<Duration> generateDeadlineUpToLCM(Set<Task> tasks) {
        if (tasks == null || tasks.isEmpty())
            throw new RuntimeException("empty task list");
        List<Long> periods = tasks.stream()
            .map(task -> task.getPeriod().toNanos())
            .collect(Collectors.toList());
        long lcm = LCMOfList(periods);
        List<Duration> result = new ArrayList<>();
        for (Task task : tasks) {
            Duration period = task.getPeriod();
            Duration deadline = task.getDeadline();
            for (long i=0; i*period.toNanos()+deadline.toNanos() <= lcm; i++) {
                Duration event = period.multipliedBy(i).plus(deadline);
                result.add(event);
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

    /**
     * Computes the Least Common Multiple (LCM) of a list of long values.
     *
     * @param values list of positive long integers
     * @return the LCM of all values
     * @throws RuntimeException if the list is null or empty
     */
    private static long LCMOfList(List<Long> values) {
        if (values == null || values.isEmpty())
            throw new RuntimeException("Cannot compute LCM of empty list");
        long lcm = values.getFirst();
        for (int i = 1; i < values.size(); i++) {
            lcm = lcm(lcm, values.get(i));
        }
        return lcm;
    }

}