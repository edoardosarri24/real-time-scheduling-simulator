package taskSet;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

public class ChunkTest {

    @Test
    public void testSomma() {
        int risultato = 2 + 3;
        assertThat(risultato).isEqualTo(4);
    }
}