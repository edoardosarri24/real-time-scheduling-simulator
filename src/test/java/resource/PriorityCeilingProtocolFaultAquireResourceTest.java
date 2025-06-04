package resource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Test;

public class PriorityCeilingProtocolFaultAquireResourceTest {

    @Test
    public void contructor() {
        assertThatThrownBy(() -> new PriorityCeilingProtocolFaultAquireResource(4.2))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("La soglia con cui acquisire un semaforo deve essere compresa tra 0.0 e 1.0.");
    }

}