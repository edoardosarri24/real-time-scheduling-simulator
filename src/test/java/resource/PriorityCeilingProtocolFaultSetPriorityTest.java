package resource;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import helper.ReflectionUtils;

public class PriorityCeilingProtocolFaultSetPriorityTest {

    @Test
    public void constructor() {
        PriorityCeilingProtocolFaultSetPriority faultInstance = new PriorityCeilingProtocolFaultSetPriority(-2, 3);
        int delta = (int) ReflectionUtils.getField(faultInstance, "delta");
        assertThat(delta)
            .isInstanceOf(Integer.class)
            .isGreaterThanOrEqualTo(-1)
            .isLessThanOrEqualTo(2);
    }

}