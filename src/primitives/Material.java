// primitives/Material.java
package primitives;

/**
 * Material encapsulates the surface optical properties used in shading:
 * ambient (kA), diffuse (kD), specular (kS), transparency (kT),
 * reflection (kR), and shininess exponent (Phong model).
 */
public class Material {
    /** Ambient reflection coefficient. */
    private Double3 kA = Double3.ONE;
    /** Diffuse reflection coefficient. */
    private Double3 kD = Double3.ZERO;
    /** Specular reflection coefficient. */
    private Double3 kS = Double3.ZERO;
    /** Transparency coefficient for refraction. */
    private Double3 kT = Double3.ZERO;
    /** Reflection coefficient for mirror-like reflection. */
    private Double3 kR = Double3.ZERO;
    /** Phong shininess exponent controlling specular highlight size. */
    private int shininess = 0;

    /**
     * Sets the ambient reflection coefficient (scalar).
     * @param ka ambient coefficient value
     * @return this Material for chaining
     */
    public Material setKA(double ka)    { this.kA = new Double3(ka); return this; }
    /**
     * Sets the ambient reflection coefficient (vector).
     * @param ka ambient coefficient vector
     * @return this Material for chaining
     */
    public Material setKA(Double3 ka)   { this.kA = ka;            return this; }

    /**
     * Sets the diffuse reflection coefficient (scalar).
     * @param kd diffuse coefficient value
     * @return this Material for chaining
     */
    public Material setKD(double kd)    { this.kD = new Double3(kd); return this; }
    /**
     * Sets the diffuse reflection coefficient (vector).
     * @param kd diffuse coefficient vector
     * @return this Material for chaining
     */
    public Material setKD(Double3 kd)   { this.kD = kd;            return this; }

    /**
     * Sets the specular reflection coefficient (scalar).
     * @param ks specular coefficient value
     * @return this Material for chaining
     */
    public Material setKS(double ks)    { this.kS = new Double3(ks); return this; }
    /**
     * Sets the specular reflection coefficient (vector).
     * @param ks specular coefficient vector
     * @return this Material for chaining
     */
    public Material setKS(Double3 ks)   { this.kS = ks;            return this; }

    /**
     * Sets the transparency coefficient (scalar).
     * @param kt transparency coefficient value
     * @return this Material for chaining
     */
    public Material setKT(double kt)    { this.kT = new Double3(kt); return this; }
    /**
     * Sets the transparency coefficient (vector).
     * @param kt transparency coefficient vector
     * @return this Material for chaining
     */
    public Material setKT(Double3 kt)   { this.kT = kt;            return this; }

    /**
     * Sets the reflection coefficient (scalar).
     * @param kr reflection coefficient value
     * @return this Material for chaining
     */
    public Material setKR(double kr)    { this.kR = new Double3(kr); return this; }
    /**
     * Sets the reflection coefficient (vector).
     * @param kr reflection coefficient vector
     * @return this Material for chaining
     */
    public Material setKR(Double3 kr)   { this.kR = kr;            return this; }

    /**
     * Sets the shininess exponent for specular highlights.
     * @param s shininess exponent (higher values produce smaller highlights)
     * @return this Material for chaining
     */
    public Material setShininess(int s) { this.shininess = s;      return this; }

    /**
     * Retrieves the ambient coefficient vector.
     * @return ambient coefficient (kA)
     */
    public Double3 getKA()     { return kA; }
    /**
     * Retrieves the diffuse coefficient vector.
     * @return diffuse coefficient (kD)
     */
    public Double3 getKD()     { return kD; }
    /**
     * Retrieves the specular coefficient vector.
     * @return specular coefficient (kS)
     */
    public Double3 getKS()     { return kS; }
    /**
     * Retrieves the transparency coefficient vector.
     * @return transparency coefficient (kT)
     */
    public Double3 getKT()     { return kT; }
    /**
     * Retrieves the reflection coefficient vector.
     * @return reflection coefficient (kR)
     */
    public Double3 getKR()     { return kR; }
    /**
     * Retrieves the shininess exponent.
     * @return shininess exponent
     */
    public int getShininess() { return shininess; }

    /**
     * Returns a string representation of the material's properties.
     * @return formatted Material field values
     */
    @Override
    public String toString() {
        return "Material{" +
                "kA=" + kA + ", kD=" + kD + ", kS=" + kS +
                ", kT=" + kT + ", kR=" + kR +
                ", shininess=" + shininess +
                '}';
    }
}
