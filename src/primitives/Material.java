package primitives;

/**
 * Material class represents the material of the object, including its
 * diffuse, specular, transparency, reflection properties, and shininess.
 */
public class Material {

    private Double3 kA = Double3.ONE; // Ambient attenuation (default full)
    private Double3 kD = Double3.ZERO; // Diffuse attenuation
    private Double3 kS = Double3.ZERO; // Specular attenuation
    private Double3 kT = Double3.ZERO; // Transparency factor
    private Double3 kR = Double3.ZERO; // Reflection factor
    private int shininess = 0; // Shininess factor

    /**
     * Sets the ambient attenuation factor (kA).
     * @param kA The ambient attenuation factor as a double parameter
     * @return the Material object
     */
    public Material setKA(double kA) {
        this.kA = new Double3(kA);
        return this;
    }

    /**
     * Sets the ambient attenuation factor (kA).
     * @param kA The ambient attenuation factor as a Double3 parameter
     * @return the Material object
     */
    public Material setKA(Double3 kA) {
        this.kA = kA;
        return this;
    }

    /**
     * Sets the diffuse attenuation factor (kD).
     * @param kD The diffuse attenuation factor as a double parameter
     * @return the Material object
     */
    public Material setKD(double kD) {
        this.kD = new Double3(kD);
        return this;
    }

    /**
     * Sets the diffuse attenuation factor (kD).
     * @param kD The diffuse attenuation factor as a Double3 parameter
     * @return the Material object
     */
    public Material setKD(Double3 kD) {
        this.kD = kD;
        return this;
    }

    /**
     * Sets the specular attenuation factor (kS).
     * @param kS The specular attenuation factor as a double parameter
     * @return the Material object
     */
    public Material setKS(double kS) {
        this.kS = new Double3(kS);
        return this;
    }

    /**
     * Sets the specular attenuation factor (kS).
     * @param kS The specular attenuation factor as a Double3 parameter
     * @return the Material object
     */
    public Material setKS(Double3 kS) {
        this.kS = kS;
        return this;
    }

    /**
     * Sets the transparency factor (kT).
     * @param kT The transparency factor as a double parameter
     * @return the Material object
     */
    public Material setKT(double kT) {
        this.kT = new Double3(kT);
        return this;
    }

    /**
     * Sets the transparency factor (kT).
     * @param kT The transparency factor as a Double3 parameter
     * @return the Material object
     */
    public Material setKT(Double3 kT) {
        this.kT = kT;
        return this;
    }

    /**
     * Sets the reflection attenuation factor (kR).
     * @param kR The reflection attenuation factor as a double parameter
     * @return the Material object
     */
    public Material setKR(double kR) {
        this.kR = new Double3(kR);
        return this;
    }

    /**
     * Sets the reflection attenuation factor (kR).
     * @param kR The reflection attenuation factor as a Double3 parameter
     * @return the Material object
     */
    public Material setKR(Double3 kR) {
        this.kR = kR;
        return this;
    }

    /**
     * Sets the shininess parameter (nSh).
     * @param shininess The shininess of the material (nSh)
     * @return the Material object
     */
    public Material setShininess(int shininess) {
        this.shininess = shininess;
        return this;
    }

    /**
     * Returns a string representation of the material.
     * @return the string representation
     */
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
