package utils.sampler;

import org.oristool.simulator.samplers.Sampler;

public class SamplerAdapter {

    private final Sampler sampler;

    public SamplerAdapter(Sampler sampler) {
        this.sampler = sampler;
    }

    public long getSample() {
        return this.sampler.getSample().longValue();
    }

}