package target;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Iterator;

import tracker.Locator;

/**
 * Estende TargetModel e fornisce i metodi per la ricerca, all'interno del
 * corrente ImageModel, della regione più simile ad un dato TargetModel.<br>
 * Fornisce i metodi per lo spostamento della regione di interesse che evitano
 * di ricalcolare alcuni dati grazie al riciclo dell'ImageModel.
 */
public class TargetCandidate extends TargetModel {

    public TargetCandidate(BufferedImage frame, Point centro, int raggioX,
	    int raggioY) {
	super(frame, centro, (int) (raggioX * Locator.BANDWIDTH_RICERCA),
		(int) (raggioY * Locator.BANDWIDTH_RICERCA));

    }

    /**
     * Calcola il peso di un bin dell'istogramma che viene usato nel calcolo del
     * nuovo centro della regione.
     * 
     * @param prevModel
     *            - modello con cui confrontarsi.
     * @param u
     *            - il bin dell'istogramma.
     * @return il peso che avrà il bin u nella determinazione del nuovo centro.
     */
    private float getWi(TargetModel prevModel, int u) {
	float q1U = prevModel.getQ()[u];
	float pU = q[u];
	if (pU == 0) {
	    return 0;
	}
	float rapporto = q1U / pU;
	float Wi = (float) Math.sqrt(rapporto);
	return Wi;
    }

    /**
     * Calcola un coefficiente di similarità di questo oggetto rispetto ad un
     * dato TargetModel.
     * 
     * @param prevModel
     *            - modello con cui confrontarsi.
     * @return il coefficiente di similarità con prevModel.
     */
    public float bhattacharyyaCoeff(TargetModel prevModel) {
	float somma = 0;
	for (Iterator<Integer> itK = frameModel.getInvBMap().keySet()
		.iterator(); itK.hasNext();) {
	    Integer u = itK.next();

	    float Pu = q[u];
	    somma += Math.sqrt(Pu * prevModel.getQ()[u]);
	}

	return somma;
    }

    public void setRegion(Point centro, int raggioX, int raggioY) {
	Region region = new Region(centro, raggioX, raggioY);
	setRegion(region);
    }

    public void setRegion(Region region) {
	this.region = region;

	frameModel.updateInvBMap(region);

	calculateQ();

    }

    /**
     * Calcola la posizione del centro in modo da massimizzare il coefficiente
     * di similarità (i raggi della regione rimangono inalterati).
     * 
     * @param prevModel
     *            - il modello rispetto al quale viene calcolata la similarità.
     * @return - il centro della regione, entro la banda di ricerca, più simile
     *         al prevModel.
     */
    public Point nextLocation(TargetModel prevModel) {
	Point2D.Float tmp = nextNomalizedLocation(prevModel);

	int x = region.getDenormalizedX(tmp.x);
	int y = region.getDenormalizedY(tmp.y);

	return new Point(x, y);

    }

    private Point2D.Float nextNomalizedLocation(TargetModel prevModel) {

	float sommaW = 0;
	float sommaX = 0;
	float sommaY = 0;

	float Wi = 0;

	for (Iterator<Integer> itK = frameModel.getInvBMap().keySet()
		.iterator(); itK.hasNext();) {
	    Integer u = itK.next();
	    /* lista dei punti tali che b(x,y)==u */
	    HashSet<Point> setU = frameModel.getInvBMap().get(u);
	    for (Iterator<Point> it = setU.iterator(); it.hasNext();) {
		Point p = it.next();
		int x = p.x;
		int y = p.y;

		Wi = getWi(prevModel, u);

		sommaW += Wi;
		sommaX += Wi * region.getNormalizedX(x);
		sommaY += Wi * region.getNormalizedY(y);

	    }

	}
	float wX = sommaX / sommaW;
	float wY = sommaY / sommaW;

	return new Point2D.Float(wX, wY);
    }

    public void spostaCentro(Point nuovoCentro) {
	Region newRegion = new Region(nuovoCentro, this.region.getRaggioX(),
		this.region.getRy());

	setRegion(newRegion);
    }

    /**
     * Trasforma questo TargetCandidate in un TargetModel eliminando la porzione
     * di regione che serve ai fini della ricerca.
     * 
     * @return il TargetModel calcolato a partire da questo TargetCandidate.
     */
    public TargetModel toTargetModel() {

	Region newRegion = new Region(region.getCentro(), (int) (region
		.getRaggioX() / Locator.BANDWIDTH_RICERCA), (int) (region
		.getRy() / Locator.BANDWIDTH_RICERCA));
	this.setRegion(newRegion);

	return this;
    }
}
