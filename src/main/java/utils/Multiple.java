package utils;

import com.google.common.math.LongMath;

import java.time.Duration;
import java.util.*;

public class Multiple {

    /**
     * Generates a sorted list of all distinct multiples of the given durations
     * up to either their Least Common Multiple (LCM) or 10 times the largest duration,
     * whichever is smaller.
     *
     * @param numbers a list of {@link Duration} values representing periodic intervals
     * @return a sorted list of {@link Duration} values containing the computed multiples
     */
    public static List<Duration> generateMultiplesUpToLCM(List<Duration> numbers) {
        List<Double> doubleNumbers = new ArrayList<>();
        for (Duration number : numbers)
            doubleNumbers.add((double) number.toMillis());
        double lcm = computeLCMOfDoubleList(doubleNumbers);
        double maxNumber = Collections.max(doubleNumbers);
        double limit = Math.min(lcm, maxNumber * 10);
        Set<Double> multiples = new TreeSet<>();
        for (double number : doubleNumbers)
            for (double m = number; m <= limit + 1e-9; m += number)
                multiples.add(Math.round(m * 1e9) / 1e9);
        List<Duration> output = new ArrayList<>();
        for (Double number : multiples)
            output.add(Duration.ofMillis(Math.round(number)));
        return output;
    }

    /**
     * Computes the Least Common Multiple (LCM) of two long integers.
     *
     * @param a the first number
     * @param b the second number
     * @return the least common multiple of a and b
     */
    private static long lcm(long a, long b) {
        return (a * b) / LongMath.gcd(a, b);
    }

    /**
     * Approximates the smallest integer denominator such that the product
     * with the given floating-point number is close to an integer.
     * Used to convert doubles into rational fractions.
     *
     * @param number the floating-point number
     * @return the smallest denominator that approximates the number well
     */
    private static long findDenominator(double number) {
        final double EPSILON = 1E-9;
        long denominator = 1;
        while (Math.abs(number * denominator - Math.round(number * denominator)) > EPSILON)
            denominator++;
        return denominator;
    }

    /**
     * Computes the Least Common Multiple (LCM) of a list of double values
     * by approximating them as rational numbers and computing the LCM
     * of their numerators and the GCD of their denominators.
     *
     * @param numbers a list of double values
     * @return the LCM as a double
     */
    private static double computeLCMOfDoubleList(List<Double> numbers) {
        List<Long> numerators = new ArrayList<>();
        List<Long> denominators = new ArrayList<>();
        for (double number : numbers) {
            long denom = findDenominator(number);
            denominators.add(denom);
            numerators.add(Math.round(number * denom));
        }
        long lcmNumerators = numerators.stream().reduce(1L, (a, b) -> lcm(a, b));
        long gcdDenominators = denominators.stream().reduce(denominators.get(0), LongMath::gcd);
        return (double) lcmNumerators / gcdDenominators;
    }
    
}