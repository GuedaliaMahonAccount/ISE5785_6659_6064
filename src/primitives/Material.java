
package primitives;

/**
 * Material class represents the material of the object
 */
public class Material {

    public double kA;
    public Double3 kD = Double3.ZERO;
    public Double3 kS = Double3.ZERO;


    /**
     * kT is the transparency factor
     */
    public Double3 kT = Double3.ZERO;

    /**
     * kR is the reflection factor
     */
    public Double3 kR = Double3.ZERO;
    public int shininess = 0;


    /**
     * set the kD parameter
     * @param kD The diffuse attenuation factor as a double parameter
     * @return the Material object
     */
    public Material setKD(double kD) {
        this.kD = new Double3(kD);
        return this;
    }

    /**
     * set the kD parameter
     * @param kD The diffuse attenuation factor as a double3 parameter
     * @return the Material object
     */
    public Material setKD(Double3 kD) {
        this.kD = kD;
        return this;
    }

    /**
     * set the kS parameter
     * @param kS The specular attenuation factor as a double parameter
     * @return the Material object
     */
    public Material setKS(double kS) {
        this.kS = new Double3(kS);
        return this;
    }


    /**
     * set the kS parameter
     * @param kS The specular attenuation factor as a double3 parameter
     * @return the Material object
     */
    public Material setKS(Double3 kS) {
        this.kS = kS;
        return this;
    }

    /**
     * set the kT parameter
     * @param kT The transparency factor as a double parameter
     * @return the Material object
     */
    public Material setKT(double kT) {
        this.kT = new Double3(kT);
        return this;
    }

    /**
     * set the kT parameter
     * @param kT The transparency factor as a double3 parameter
     * @return the Material object
     */
    public Material setKT(Double3 kT) {
        this.kT = kT;
        return this;
    }

    /**
     * set the kR parameter
     * @param kR The reflection attenuation factor as a double parameter
     * @return the Material object
     */
    public Material setKR(double kR) {
        this.kR = new Double3(kR);
        return this;
    }

    /**
     * set the kR parameter
     * @param kR The reflection attenuation factor as a double3 parameter
     * @return the Material object
     */
    public Material setKR(Double3 kR) {
        this.kR = kR;
        return this;
    }

    /**
     * set the shininess parameter
     * @param shininess The shininess of the material
     * @return the Material object
     */
    public Material setShininess(int shininess) {
        this.shininess = shininess;
        return this;
    }
}
