package grafo.minPathAlgs.dijkstra;

import grafo.GrafoOstacoli;
import grafo.listaAdiacenza.NodoListaAdiacenza;
import grafo.minPathAlgs.MinPathAlg;
import grafo.minPathAlgs.dijkstra.minHeap.MinHeap;
import grafo.minPathAlgs.dijkstra.minHeap.NodoHeap;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import junit.framework.Assert;

/**
 * Questa classe implementa l'algoritmo Dijkstra per la ricerca del cammino
 * minimo da sorgente sigola.
 * 
 * @author Sergio Bonfiglio
 * 
 */
public class DijkstraAlg implements MinPathAlg {

    // la tabella che conterrà la distanza di tutti i punti dalla sorgente
    private HashMap<Point2D, Double> distanze;
    // l'array dei nodi del min-heap che implementa la coda di priorità
    private NodoHeap[] arrayHeap;
    private GrafoOstacoli grafo;

    public DijkstraAlg(GrafoOstacoli grafo) {
	this.grafo = grafo;
    }

    /**
     * Calcola il cammino minimo dalla sorgente a tutti gli altri nodi
     */
    public void singleSourceMinPath(Point2D source) {

	initializeSingleSource(source);

	// Costruisco il min heap che implementa la coda di priorità con tutti i
	// vertici
	MinHeap codaPriorita = new MinHeap(arrayHeap);

	// finché la coda ha elementi itero
	while (codaPriorita.getHeapSize() > 0) {

	    // estraggo il vertice la cui stima di distanza è quella minima
	    NodoHeap nodoHeap = (NodoHeap) codaPriorita.extractMin();

	    //System.out.println(distanze.get(nodoHeap.getP()));

	    Point2D u = nodoHeap.getP();

	    ArrayList<NodoListaAdiacenza> nodiAdiacenti = grafo
		    .getListaAdiacenza().getTabella().get(u);

	    // rilasso tutti gli archi adiacenti ad u
	    for (Iterator<NodoListaAdiacenza> iter = nodiAdiacenti.iterator(); iter
		    .hasNext();) {
		NodoListaAdiacenza nodo = iter.next();

		NodoHeap nodoHeap2 = codaPriorita.getLut().get(nodo.getPoint());

		relax(codaPriorita, nodoHeap, nodoHeap2);

	    }

	}

    }

    /**
     * Inizializza la distanza di tutti i vertici a infinito e quella della
     * sorgente a 0. Inoltre viene inizializzato arrayHeap in modo che contenga
     * tutti i vertici presenti.
     */
    private void initializeSingleSource(Point2D source) {

	this.distanze = new HashMap<Point2D, Double>();

	Set<Point2D> vertici = grafo.getListaAdiacenza().getVertici();
	this.arrayHeap = new NodoHeap[vertici.size()];
	int i = 0;
	for (Iterator<Point2D> iterator = vertici.iterator(); iterator
		.hasNext();) {

	    Point2D v = iterator.next();
	    // inizializzo a infinito tutti i vertici
	    this.distanze.put(v, Double.MAX_VALUE);

	    // inizializzo i nodi del min heap con i vertici e la distanza
	    if (v.equals(source)) {
		arrayHeap[i] = new NodoHeap(v, 0d, i);
	    } else {
		arrayHeap[i] = new NodoHeap(v, Double.MAX_VALUE, i);
	    }
	    i++;
	}
	// tranne la sorgente
	this.distanze.put(source, 0d);
	Assert.assertTrue(distanze.get(source)==0d);
    }

    /**
     * Rilassa l'arco (nu,nv). Se la distanza del nodo nv diminuisce essa viene
     * aggiornata tramite la coda di priorità.
     * 
     * @param codaPriorita
     * @param nu
     * @param nv
     */
    private void relax(MinHeap codaPriorita, NodoHeap nu, NodoHeap nv) {
	Point2D u = nu.getP();
	Point2D v = nv.getP();
	double pesoArco = grafo.getListaAdiacenza().getWeight(u, v);

	if (distanze.get(v) > distanze.get(u) + pesoArco) {
	    distanze.put(v, distanze.get(u) + pesoArco);
	    codaPriorita.decreaseKey(nv, distanze.get(u) + pesoArco);
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
