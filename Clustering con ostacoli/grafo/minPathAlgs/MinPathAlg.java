package grafo.minPathAlgs;

import java.awt.geom.Point2D;

/**
 * Questa interfaccia deve essere implementata da tutte le classi che
 * implementano un algoritmo per trovare il cammino minimo
 * 
 * @author Sergio Bonfiglio
 * 
 */
public interface MinPathAlg {

	public void singleSourceMinPath(Point2D source);

	public double getDistanceFromSource(Point2D v);

}
