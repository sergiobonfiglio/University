package target;

import java.awt.Point;

/**
 * Memorizza una regione con un coefficiente di similarità e fornisce i metodi
 * per confrontare i diversi coefficienti.
 */
public class RegionCoefficient extends Region {

    private float coefficient;

    public RegionCoefficient(Region regione, float coeff) {
	this(regione.centro, regione.raggioX, regione.raggioY, coeff);
    }

    public RegionCoefficient(Point centro, int raggioX, int raggioY, float coeff) {
	super(centro, raggioX, raggioY);
	this.coefficient = coeff;
    }

    public float getCoefficient() {
	return coefficient;
    }

    public static RegionCoefficient getMaxCoeffRegion(RegionCoefficient r1,
	    RegionCoefficient r2, RegionCoefficient r3) {
	float coeff1 = r1.getCoefficient();
	float coeff2 = r2.getCoefficient();
	float coeff3 = r3.getCoefficient();

	float maxCoeff = Math.max(coeff1, Math.max(coeff2, coeff3));

	if (maxCoeff == coeff1)
	    return r1;
	else if (maxCoeff == coeff2)
	    return r2;
	else if (maxCoeff == coeff3)
	    return r3;
	else
	    return null;

    }

    @Override
    public String toString() {
	String str = super.toString();
	str += "|" + coefficient;
	return str;
    }

}
