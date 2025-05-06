package utils;

import java.math.BigDecimal;

import org.oristool.simulator.samplers.UniformSampler;

public class ConstantSampler extends UniformSampler {

    public ConstantSampler(BigDecimal value) {
        super(value, value);
    }
    
}