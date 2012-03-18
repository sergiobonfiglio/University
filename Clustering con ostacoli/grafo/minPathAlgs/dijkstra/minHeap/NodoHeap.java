package grafo.minPathAlgs.dijkstra.minHeap;

import java.awt.geom.Point2D;

/**
 * Le istanze di questa classe costituisco i nodi del Min-Heap
 * 
 * @author Sergio Bonfiglio
 * 
 */
public class NodoHeap implements Comparable<NodoHeap> {

	private Point2D p;
	double stimaDistanza;
	// l'indice all'interno dell'array in cui è contenuto
	int index;

	public NodoHeap(Point2D p, double stimaDistanza, int index) {
		this.p = p;
		this.stimaDistanza = stimaDistanza;
		this.index = index;
	}

	public int compareTo(NodoHeap o) {
		NodoHeap n = (NodoHeap) o;
		double differenza = this.stimaDistanza - n.stimaDistanza;
		if (differenza > 0)
			return 1;
		else if (differenza < 0)
			return -1;
		else
			return 0;
	}

	public void setP(Point2D p) {
		this.p = p;
	}

	public Point2D getP() {
		return p;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof NodoHeap)
			return this.compareTo((NodoHeap) obj) == 0;
		else
			return false;
	}

}
