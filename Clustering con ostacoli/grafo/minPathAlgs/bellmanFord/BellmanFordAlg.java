package grafo.minPathAlgs.bellmanFord;

import grafo.GrafoOstacoli;
import grafo.minPathAlgs.MinPathAlg;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Questa classe implementa l'algoritmo Bellman-Ford per la ricerca del cammino
 * minimo da sorgente sigola.
 * 
 * @author Sergio Bonfiglio
 * 
 */
public class BellmanFordAlg implements MinPathAlg {

	// la tabella che conterrà la distanza di tutti i punti dalla sorgente
	private HashMap<Point2D, Double> distanze;
	private GrafoOstacoli grafo;

	public BellmanFordAlg(GrafoOstacoli grafo) {
		this.grafo = grafo;
	}

	/**
	 * Calcola il cammino minimo dalla sorgente a tutti gli altri nodi
	 */
	public void singleSourceMinPath(Point2D source) {

		initializeSingleSource(source);

		// rilasso tutti gli archi |V|-1 volte perché il cammino minimo non può
		// avere più di |V|-1 archi
		for (int i = 0; i < grafo.getListaAdiacenza().getSize(); i++) {

			Set<Point2D> vertici = grafo.getListaAdiacenza().getVertici();
			for (Iterator<Point2D> iterU = vertici.iterator(); iterU.hasNext();) {

				Point2D u = iterU.next();
				for (Iterator<Point2D> iterV = vertici.iterator(); iterV
						.hasNext();) {

					Point2D v = iterV.next();

					relax(u, v);
				}
			}
		}

		// il controllo della presenza di cicli negativi può essere evitato in
		// quanto tutti i pesi sono distanze positive

	}

	/**
	 * Inizializza la distanza di tutti i vertici a infinito e quella della
	 * sorgente a 0
	 */
	private void initializeSingleSource(Point2D source) {

		this.distanze = new HashMap<Point2D, Double>();

		Set<Point2D> vertici = grafo.getListaAdiacenza().getVertici();
		for (Iterator<Point2D> iterator = vertici.iterator(); iterator
				.hasNext();) {

			Point2D v = iterator.next();

			this.distanze.put(v, Double.MAX_VALUE);
		}

		this.distanze.put(source, 0d);
	}

	/**
	 * Rilassa l'arco (u,v)
	 * 
	 * @param u
	 * @param v
	 */
	private void relax(Point2D u, Point2D v) {
		double pesoArco = grafo.getListaAdiacenza().getWeight(u, v);
		if (distanze.get(v) > distanze.get(u) + pesoArco) {
			distanze.put(v, distanze.get(u) + pesoArco);
		}
	}

	/**
	 * Restituisce la distanza dalla sorgente su cui è stato eseguito
	 * l'algoritmo del vertice v o Double.MAX_VALUE se il vertice non
	 * apparteneva al grafo al momento dell'esecuzione.
	 * 
	 */
	public double getDistanceFromSource(Point2D v) {
		double distanza;
		if (distanze.get(v) == null) {
			distanza = Double.MAX_VALUE;
		} else
			distanza = distanze.get(v);

		return distanza;
	}

}
