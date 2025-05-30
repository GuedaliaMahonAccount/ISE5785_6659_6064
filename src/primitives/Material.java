// primitives/Material.java
package primitives;

/**
 * Material describes surface properties used in shading:
 * ambient (kA), diffuse (kD), specular (kS),
 * transparency (kT), reflection (kR) and shininess exponent.
 */
public class Material {
    private Double3 kA = Double3.ONE;   // ambient coefficient
    private Double3 kD = Double3.ZERO;  // diffuse coefficient
    private Double3 kS = Double3.ZERO;  // specular coefficient
    private Double3 kT = Double3.ZERO;  // transparency coefficient
    private Double3 kR = Double3.ZERO;  // reflection coefficient
    private int      shininess = 0;     // shininess exponent

    public Material setKA(double ka)    { this.kA = new Double3(ka); return this; }
    public Material setKA(Double3 ka)   { this.kA = ka;            return this; }
    public Material setKD(double kd)    { this.kD = new Double3(kd); return this; }
    public Material setKD(Double3 kd)   { this.kD = kd;            return this; }
    public Material setKS(double ks)    { this.kS = new Double3(ks); return this; }
    public Material setKS(Double3 ks)   { this.kS = ks;            return this; }
    public Material setKT(double kt)    { this.kT = new Double3(kt); return this; }
    public Material setKT(Double3 kt)   { this.kT = kt;            return this; }
    public Material setKR(double kr)    { this.kR = new Double3(kr); return this; }
    public Material setKR(Double3 kr)   { this.kR = kr;            return this; }
    public Material setShininess(int s) { this.shininess = s;      return this; }

    public Double3 getKA()     { return kA; }
    public Double3 getKD()     { return kD; }
    public Double3 getKS()     { return kS; }
    public Double3 getKT()     { return kT; }
    public Double3 getKR()     { return kR; }
    public int      getShininess() { return shininess; }

    @Override
    public String toString() {
        return "Material{" +
                "kA=" + kA + ", kD=" + kD + ", kS=" + kS +
                ", kT=" + kT + ", kR=" + kR +
                ", shininess=" + shininess +
                '}';
    }
}
