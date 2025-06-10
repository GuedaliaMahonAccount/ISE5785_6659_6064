// SamplingConfig.java
package renderer.sampling;

/**
 * Holds all anti-aliasing / super-sampling parameters.
 */
public class SamplingConfig {
    private int sampleCount = 1;
    private TargetShape targetShape = TargetShape.RECTANGLE;
    private SamplingPattern samplingPattern = SamplingPattern.GRID;

    public SamplingConfig() { }

    public SamplingConfig(int sampleCount, TargetShape targetShape, SamplingPattern samplingPattern) {
        this.sampleCount = sampleCount;
        this.targetShape = targetShape;
        this.samplingPattern = samplingPattern;
    }

    public int getSampleCount() {
        return sampleCount;
    }
    public void setSampleCount(int sampleCount) {
        this.sampleCount = sampleCount;
    }

    public TargetShape getTargetShape() {
        return targetShape;
    }
    public void setTargetShape(TargetShape targetShape) {
        this.targetShape = targetShape;
    }

    public SamplingPattern getSamplingPattern() {
        return samplingPattern;
    }
    public void setSamplingPattern(SamplingPattern samplingPattern) {
        this.samplingPattern = samplingPattern;
    }
}
