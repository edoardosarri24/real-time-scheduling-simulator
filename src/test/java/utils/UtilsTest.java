package utils;

import java.time.Duration;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

public class UtilsTest {

    @Test
    public void durationPrinter() {
        MyClock.reset();
        MyClock.getInstance().advanceTo(Duration.ofMillis(5));
        assertThat(Utils.printCurrentTime())
            .isEqualTo("5.000");
    }

}