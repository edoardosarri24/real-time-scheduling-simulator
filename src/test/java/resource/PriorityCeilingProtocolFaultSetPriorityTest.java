package resource;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.oristool.simulator.samplers.Sampler;

import helper.ReflectionUtils;

public class PriorityCeilingProtocolFaultSetPriorityTest {

    @Test
    public void constructor() {
        PriorityCeilingProtocolFaultSetPriority faultInstance = new PriorityCeilingProtocolFaultSetPriority(-2, 3);
        Sampler delta = (Sampler) ReflectionUtils.getField(faultInstance, "delta");
        assertThat(delta.getSample().intValue())
            .isInstanceOf(Integer.class)
            .isGreaterThanOrEqualTo(-1)
            .isLessThanOrEqualTo(2);
    }

}