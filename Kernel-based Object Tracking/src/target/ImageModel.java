package target;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Astrazione di un frame. Fornisce i metodi necessari al calcolo del valore di
 * un pixel e una HashMap con i punti indicizzati per valore.<br>
 * Per aumentare le performance, quando viene cambiata la regione di interesse i
 * valori dei punti già calcolati vengono riciclati e solo la mappa inversa
 * viene aggiornata.
 * 
 */
public class ImageModel {

    /**
     * L'intera immagine.
     */
    private BufferedImage img;

    /**
     * Map dei valori indicizzati per posizione.
     */
    private HashMap<Point, Integer> bMap;

    /**
     * Map dei punti indicizzati per valore.
     */
    private HashMap<Integer, HashSet<Point>> invBMap;

    public ImageModel(BufferedImage img) {
	this.img = img;

    }

    public HashMap<Integer, HashSet<Point>> getInvBMap() {
	return invBMap;
    }

    /**
     * Calcola il valore di un pixel.
     * 
     * @param x
     *            - l'ascissa del pixel
     * @param y
     *            - l'ordinata del pixel
     * @return il valore di un pixel
     */
    public int b(int x, int y) {
	if (bMap == null) {
	    bMap = new HashMap<Point, Integer>();
	}

	if (bMap.get(new Point(x, y)) == null) {
	    int red = img.getRaster().getSample(x, y, 0);
	    int green = img.getRaster().getSample(x, y, 1);
	    int blue = img.getRaster().getSample(x, y, 2);

	    int lum = (int) (((0.30 * red) + (0.59 * green) + (0.11 * blue)));
	    // int gray = (int) ((red + green + blue) / 3d);

	    bMap.put(new Point(x, y), Integer.valueOf(lum));
	}
	return bMap.get(new Point(x, y)).intValue();
    }

    /**
     * Calcola la mappa dei valori inversa per una nuova regione. Poiché la
     * regione spesso si sposta di poco è conveniente conservare la mappa dei
     * valori dei punti.
     * 
     * @param region
     *            - la nuova regione da considerare.
     */
    public void updateInvBMap(Region region) {
	this.invBMap = new HashMap<Integer, HashSet<Point>>();

	int minX = getMinImgX(region);
	int maxX = getMaxImgX(region);
	int minY = getMinImgY(region);
	int maxY = getMaxImgY(region);

	for (int x = minX; x < maxX; x++) {
	    for (int y = minY; y < maxY; y++) {
		double distQuad = region.distanzaQuadDalCentroNorm(x, y);
		if (distQuad <= 1) {
		    int u = b(x, y);
		    HashSet<Point> setU = invBMap.get(Integer.valueOf(u));
		    if (setU == null) {
			setU = new HashSet<Point>();
			invBMap.put(Integer.valueOf(u), setU);
		    }
		    setU.add(new Point(x, y));
		}
	    }
	}
    }

    private int getMaxImgX(Region r) {
	return Math.min(img.getWidth(), r.getCentro().x + r.getRaggioX());
    }

    private int getMaxImgY(Region r) {
	return Math.min(img.getHeight(), r.getCentro().y + r.getRy());
    }

    private int getMinImgX(Region r) {
	return Math.max(0, r.getCentro().x - r.getRaggioX());
    }

    private int getMinImgY(Region r) {
	return Math.max(0, r.getCentro().y - r.getRy());
    }

}
