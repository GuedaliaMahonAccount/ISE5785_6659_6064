// SamplingPattern.java
package renderer.sampling;

/**
 * Sampling layouts for super-sampling.
 */
public enum SamplingPattern {
    /** Uniform √N×√N grid */
    GRID,
    /** Grid + random jitter within each cell */
    JITTERED,
    /** Pure random within the pixel area */
    RANDOM
}
