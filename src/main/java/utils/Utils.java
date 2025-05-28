package utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

public class Utils {

    public static String durationPrinter(Duration duration) {
        long nanos = duration.toNanos();
        BigDecimal millis = new BigDecimal(nanos).divide(BigDecimal.TEN.pow(6), 3, RoundingMode.HALF_UP);
        return "" + millis;
    }

}