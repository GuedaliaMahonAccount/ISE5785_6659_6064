package primitives;

/**
 * Material class represents the physical properties of a geometric object,
 * including its ambient, diffuse, specular, transparency, reflection properties,
 * and shininess. These properties are used in shading calculations for realistic rendering.
 */
public class Material {

    private Double3 kA = Double3.ONE; // Ambient attenuation (default full)
    private Double3 kD = Double3.ZERO; // Diffuse attenuation
    private Double3 kS = Double3.ZERO; // Specular attenuation
    private Double3 kT = Double3.ZERO; // Transparency factor
    private Double3 kR = Double3.ZERO; // Reflection factor
    private int shininess = 0; // Shininess factor

    /**
     * Sets the ambient attenuation factor (kA) using a scalar value.
     * @param kA the ambient attenuation factor as a double
     * @return the current Material object (for method chaining)
     */
    public Material setKA(double kA) {
        this.kA = new Double3(kA);
        return this;
    }

    /**
     * Sets the ambient attenuation factor (kA) using a Double3 vector.
     * @param kA the ambient attenuation factor as a Double3
     * @return the current Material object (for method chaining)
     */
    public Material setKA(Double3 kA) {
        this.kA = kA;
        return this;
    }

    /**
     * Sets the diffuse attenuation factor (kD) using a scalar value.
     * @param kD the diffuse attenuation factor as a double
     * @return the current Material object (for method chaining)
     */
    public Material setKD(double kD) {
        this.kD = new Double3(kD);
        return this;
    }

    /**
     * Sets the diffuse attenuation factor (kD) using a Double3 vector.
     * @param kD the diffuse attenuation factor as a Double3
     * @return the current Material object (for method chaining)
     */
    public Material setKD(Double3 kD) {
        this.kD = kD;
        return this;
    }

    /**
     * Sets the specular attenuation factor (kS) using a scalar value.
     * @param kS the specular attenuation factor as a double
     * @return the current Material object (for method chaining)
     */
    public Material setKS(double kS) {
        this.kS = new Double3(kS);
        return this;
    }

    /**
     * Sets the specular attenuation factor (kS) using a Double3 vector.
     * @param kS the specular attenuation factor as a Double3
     * @return the current Material object (for method chaining)
     */
    public Material setKS(Double3 kS) {
        this.kS = kS;
        return this;
    }

    /**
     * Sets the transparency factor (kT) using a scalar value.
     * @param kT the transparency factor as a double
     * @return the current Material object (for method chaining)
     */
    public Material setKT(double kT) {
        this.kT = new Double3(kT);
        return this;
    }

    /**
     * Sets the transparency factor (kT) using a Double3 vector.
     * @param kT the transparency factor as a Double3
     * @return the current Material object (for method chaining)
     */
    public Material setKT(Double3 kT) {
        this.kT = kT;
        return this;
    }

    /**
     * Sets the reflection attenuation factor (kR) using a scalar value.
     * @param kR the reflection attenuation factor as a double
     * @return the current Material object (for method chaining)
     */
    public Material setKR(double kR) {
        this.kR = new Double3(kR);
        return this;
    }

    /**
     * Sets the reflection attenuation factor (kR) using a Double3 vector.
     * @param kR the reflection attenuation factor as a Double3
     * @return the current Material object (for method chaining)
     */
    public Material setKR(Double3 kR) {
        this.kR = kR;
        return this;
    }

    /**
     * Sets the shininess factor of the material.
     * This factor controls the sharpness of specular highlights.
     * @param shininess the shininess of the material
     * @return the current Material object (for method chaining)
     */
    public Material setShininess(int shininess) {
        this.shininess = shininess;
        return this;
    }

    /**
     * Returns the ambient attenuation factor (kA).
     * @return the ambient attenuation factor as a Double3
     */
    public Double3 getKA() {
        return kA;
    }

    /**
     * Returns the diffuse attenuation factor (kD).
     * @return the diffuse attenuation factor as a Double3
     */
    public Double3 getKD() {
        return kD;
    }

    /**
     * Returns the specular attenuation factor (kS).
     * @return the specular attenuation factor as a Double3
     */
    public Double3 getKS() {
        return kS;
    }

    /**
     * Returns the transparency factor (kT).
     * @return the transparency factor as a Double3
     */
    public Double3 getKT() {
        return kT;
    }

    /**
     * Returns the reflection attenuation factor (kR).
     * @return the reflection attenuation factor as a Double3
     */
    public Double3 getKR() {
        return kR;
    }

    /**
     * Returns the shininess factor of the material.
     * @return the shininess as an int
     */
    public int getShininess() {
        return shininess;
    }

    @Override
    public String toString() {
        return "Material{" +
                "kA=" + kA +
                ", kD=" + kD +
                ", kS=" + kS +
                ", kT=" + kT +
                ", kR=" + kR +
                ", shininess=" + shininess +
                '}';
    }
}
