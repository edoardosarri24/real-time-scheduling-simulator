package utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.Test;

import utils.sampler.ConstantSampler;

public class ConstantSamplerTest {

    @Test
    public void getSample() {
        ConstantSampler sample = new ConstantSampler(new BigDecimal(10));
        assertThat(sample.getSample())
            .isEqualTo(new BigDecimal(10));
    }
    
}