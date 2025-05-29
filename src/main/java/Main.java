import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import utils.Utils;

public class Main {

    public static void main(String[] args) {
        ArrayList<Duration> list = new ArrayList<>(List.of(
            Duration.ofMillis(5),
            Duration.ofMillis(3),
            Duration.ofMillis(7),
            Duration.ofMillis(11)));
        List<Duration> multiples = Utils.generateMultiplesUpToLCM(list);
        multiples.forEach(m -> System.out.println(m));
    }

}