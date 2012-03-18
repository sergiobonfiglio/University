package grafo;

import grafo.listaAdiacenza.ListaDiAdiacenzaHash;
import grafo.minPathAlgs.MinPathAlg;
import grafo.minPathAlgs.MinPathAlgCreator;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import junit.framework.Assert;

/**
 * Questa classe contiene la rappresentazione del grafo di visibilità generato a
 * partire dagli ostacoli aggiunti. Inoltre fornisce i metodi necessari al
 * calcolo delle distanze tra punti anche non appartenenti al grafo.
 * 
 * @author Sergio Bonfiglio
 * 
 */

public class GrafoOstacoli {

    // questa tabella memorizza tutte le distanze calcolate
    private HashMap<Path, Double> distanze;
    private ListaDiAdiacenzaHash listaAdiacenza;
    private ArrayList<Ostacolo> ostacoli;

    public GrafoOstacoli() {
	this.listaAdiacenza = new ListaDiAdiacenzaHash();
    }

    /**
     * Aggiunge un ostacolo al grafo e collega i suoi vertici a quelli visibili
     * già presenti.
     * 
     * @param x
     *            - array contenente le ascisse dei punti
     * @param y
     *            - array contenente le ordinate dei punti
     */
    public void addObstacle(double[] x, double[] y) {

	if (this.ostacoli == null)
	    this.ostacoli = new ArrayList<Ostacolo>();

	// aggiungo i nodi al grafo
	for (int i = 0; i + 1 < x.length; i++) {
	    Point2D p1 = new Point2D.Double(x[i], y[i]);
	    Point2D p2 = new Point2D.Double(x[i + 1], y[i + 1]);

	    // il grafo non è orientato quindi aggiungo anche l'arco con
	    // direzione opposta
	    listaAdiacenza.addEdge(p1, p2, p1.distance(p2));
	    listaAdiacenza.addEdge(p2, p1, p1.distance(p2));
	}
	// aggiungo l'ultimo arco
	Point2D p1 = new Point2D.Double(x[0], y[0]);
	Point2D p2 = new Point2D.Double(x[x.length - 1], y[y.length - 1]);
	listaAdiacenza.addEdge(p1, p2, p1.distance(p2));
	listaAdiacenza.addEdge(p2, p1, p1.distance(p2));

	// aggiungo l'ostacolo alla lista degli ostacoli
	Ostacolo ostacoloAggiunto = new Ostacolo(x, y);
	this.ostacoli.add(ostacoloAggiunto);

	// devo collegare i vertici mutuamente visibili degli ostacoli presenti
	// per ogni vertice dell'ostacolo
	for (int i = 0; i < x.length; i++) {
	    Point2D p = new Point2D.Double(x[i], y[i]);
	    // non aggiungo gli archi tra il punto e i vertici dell'ostacolo
	    // appena aggiunto in quanto già presenti
	    connectToVisible(p, ostacoloAggiunto);
	}

    }

    public void addObstacle(Ostacolo o) {
	this.addObstacle(o.x, o.y);
    }

    public void addObstacles(Collection<Ostacolo> ostacoli) {
	for (Iterator<Ostacolo> iterator = ostacoli.iterator(); iterator
		.hasNext();) {
	    Ostacolo o = iterator.next();
	    this.addObstacle(o);
	}
    }

    /**
     * Determina se due punti sono mutuamente visibili.
     * 
     * @param p1
     * @param p2
     * @return true se il segmento che collega i due punti non interseca nessun
     *         lato degli ostacoli presenti, false altrimenti.
     */
    public boolean areVisible(Point2D p1, Point2D p2) {
	for (Iterator<Ostacolo> iterator = ostacoli.iterator(); iterator
		.hasNext();) {
	    Ostacolo ostacolo = iterator.next();
	    if (ostacolo.obstruct(p1, p2)) {
		return false;
	    }
	}
	return true;
    }

    /**
     * Aggiunge gli archi che collegano p ai vertici degli ostacoli visibili ad
     * eccezzione dell'ostacoloDaNonConsiderare. Cioè non vengono aggiunti gli
     * archi dal punto ai vertici dell'ostacolo passato come parametro.
     * 
     * @param p
     * @param ostacoloDaNonConsiderare
     * @return
     */
    public void connectToVisible(Point2D p, Ostacolo ostacoloDaNonConsiderare) {

	// per ogni vertice di ogni ostacolo
	for (Iterator<Ostacolo> iterator = ostacoli.iterator(); iterator
		.hasNext();) {
	    Ostacolo ostacolo = iterator.next();
	    // tranne quello passato come parametro
	    if (ostacoloDaNonConsiderare != ostacolo) {
		// prendo i vertici
		for (int j = 0; j < ostacolo.x.length; j++) {
		    Point2D vertice = new Point2D.Double(ostacolo.x[j],
			    ostacolo.y[j]);
		    // se i vertici presi in considerazione sono visibil
		    // aggiungo un arco che li collega
		    if (areVisible(p, vertice)) {
			double w = p.distance(vertice);
			listaAdiacenza.addEdge(p, vertice, w);
			listaAdiacenza.addEdge(vertice, p, w);
		    }
		}
	    }
	}
    }

    /**
     * Calcola la distanza tra due punti considerando anche gli ostacoli
     * 
     * @param p1
     * @param p2
     * @return la distanza tra due punti calcolata come cammino minimo tra
     *         quest'ultimi all'interno del grafo di visibilità
     */
    public double distance(Point2D p1, Point2D p2) {

	if (areVisible(p1, p2)) {
	    // se i due punti sono visibili la distanza è quella euclidea
	    return p1.distance(p2);
	} else {

	    if (this.distanze == null)
		distanze = new HashMap<Path, Double>();
	    Path path = new Path(p1, p2);

	    if (this.distanze.containsKey(path)) {
		// se ho già calcolato la distanza la restituisco
		return this.distanze.get(path);
	    } else {
		// altrimenti calcolo la distanza su grafo

		// aggiungo i punti e li collego ai vertici degli ostacoli
		// visibili
		this.connectToVisible(p1, null);
		this.connectToVisible(p2, null);

		// eseguo algoritmo per trovare il cammino minimo sul grafo di
		// visibilità
		MinPathAlg minPathAlg = MinPathAlgCreator
			.getAlgorithm(this);
		minPathAlg.singleSourceMinPath(p1);
		double distanza = minPathAlg.getDistanceFromSource(p2);

		Assert.assertTrue(distanza<Double.MAX_VALUE);
		
		// rimuovo i punti dal grafo di visibilità
		listaAdiacenza.removeVertex(p1);
		listaAdiacenza.removeVertex(p2);

		// memorizzo la distanza trovata per usi futuri
		this.distanze.put(path, distanza);

		// restituisco il peso del cammino minimo trovato
		return distanza;
	    }
	}

    }

    public ListaDiAdiacenzaHash getListaAdiacenza() {
	return listaAdiacenza;
    }

    public ArrayList<Ostacolo> getOstacoli() {
	return ostacoli;
    }

    /**
     * Controlla che il punto passato come parametro non sia contenuto
     * all'interno di un ostacolo
     * 
     * @param p
     * @return true se il punto p si trova all'interno di un ostacolo, false
     *         altrimenti
     */
    public boolean isInsideObstacle(Point2D p) {
	for (Iterator<Ostacolo> iterator = ostacoli.iterator(); iterator
		.hasNext();) {
	    Ostacolo ostacolo = iterator.next();
	    if (ostacolo.contains(p)) {
		return true;
	    }
	}
	return false;
    }

}
