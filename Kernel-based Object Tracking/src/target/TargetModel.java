package target;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Iterator;

/**
 * E' un modello matematico di una porzione di immagine definita dall'attributo
 * Region. Si basa su un'istogramma del colore che da però maggiore importanza
 * ai pixel più vicini al centro.
 */
public class TargetModel {

    protected Region region;

    protected float[] q;

    protected ImageModel frameModel;

    public TargetModel(BufferedImage frame, Point centro, int raggioX,
	    int raggioY) {

	this.region = new Region(centro, raggioX, raggioY);

	this.frameModel = new ImageModel(frame);
	this.frameModel.updateInvBMap(region);

	calculateQ();
    }

    /**
     * Calcola il vettore q istogramma pesato del colore.
     */
    protected void calculateQ() {
	q = new float[256];

	float C1 = 0;
	for (Iterator<Integer> itK = frameModel.getInvBMap().keySet()
		.iterator(); itK.hasNext();) {
	    Integer u = itK.next();
	    float kPointSum = 0;
	    /* lista dei punti tali che b(x,y)==u */
	    HashSet<Point> setU = frameModel.getInvBMap().get(u);
	    for (Iterator<Point> it = setU.iterator(); it.hasNext();) {
		Point p = it.next();
		float distQuad = region.distanzaQuadDalCentroNorm(p.x, p.y);

		float tmp = kernel(distQuad);
		C1 += tmp;
		kPointSum += tmp;

	    }
	    q[u.intValue()] = kPointSum;
	}

	// normalizza q;
	for (int i = 0; i < q.length; i++) {
	    q[i] = q[i] / C1;
	}

    }

    public Point getCentro() {
	return region.getCentro();
    }

    public float[] getQ() {
	return q;
    }

    public int getRaggioX() {
	return region.getRaggioX();
    }

    public int getRaggioY() {
	return region.getRy();
    }

    public Region getRegion() {
	return region;
    }

    /**
     * Attribuisce un maggiore valore ai pixel più vicini al centro della
     * regione.
     * 
     * @param x
     *            distanza quadratica normalizzata dal centro.
     * @return il peso del punto.
     */
    protected float kernel(float x) {
	if (x > 1) {
	    return 0;
	} else {
	    return 0.75f * (1 - x);
	}
    }

}
