package grafo.listaAdiacenza;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Questa classe implementa una lista di adiacenza attraverso l'uso di una
 * tabella hash.
 * 
 * @author Sergio Bonfiglio
 * 
 */
public class ListaDiAdiacenzaHash {

	private HashMap<Point2D, ArrayList<NodoListaAdiacenza>> tabella;

	public ListaDiAdiacenzaHash() {
		this.tabella = new HashMap<Point2D, ArrayList<NodoListaAdiacenza>>();
	}

	public Set<Point2D> getVertici() {
		return tabella.keySet();
	}

	public int getSize() {
		return tabella.size();
	}

	public HashMap<Point2D, ArrayList<NodoListaAdiacenza>> getTabella() {
		return tabella;
	}

	/**
	 * Restituisce il peso dell'arco (u,v) o infinito se l'arco non esiste
	 * 
	 * @param u
	 * @param v
	 * @return restituisce il peso dell'arco (u,v), Double.MAX_VALUE altrimenti
	 */
	public double getWeight(Point2D u, Point2D v) {
		ArrayList<NodoListaAdiacenza> lista = this.tabella.get(u);
		for (Iterator<NodoListaAdiacenza> iterator = lista.iterator(); iterator
				.hasNext();) {
			NodoListaAdiacenza nodo = iterator.next();
			if (nodo.getPoint().equals(v)) {
				return nodo.getWeight();
			}
		}
		return Double.MAX_VALUE;
	}

	/**
	 * aggiunge l'arco tra il punto p1 e il punto p2 di peso weight
	 */
	public void addEdge(Point2D p1, Point2D p2, double weight) {
		add(p1, new NodoPuntoPeso(p2, weight));
	}

	/** aggiunge il nodo alla lista di adiacenza del punto p1 */
	private void add(Point2D p1, NodoListaAdiacenza nodo) {
		ArrayList<NodoListaAdiacenza> lista = this.tabella.get(p1);
		if (lista == null) {
			lista = new ArrayList<NodoListaAdiacenza>();
		}
		lista.add(nodo);
		this.tabella.put(p1, lista);
	}

	/** rimuove l'arco tra il punto p1 e p2 */
	public boolean removeEdge(Point2D p1, Point2D p2) {
		ArrayList<NodoListaAdiacenza> lista = this.tabella.get(p1);
		if (lista == null) {
			return false;
		}

		/* cerco il punto p2 nella lista indirizzata dal punto p1 */
		for (Iterator<NodoListaAdiacenza> iterator = lista.iterator(); iterator
				.hasNext();) {
			NodoListaAdiacenza nodo = iterator.next();
			if (nodo.getPoint().equals(p2)) {
				lista.remove(nodo);
				return true;
			}
		}

		return false;
	}

	/**
	 * Rimuove un vertice dalla lista di adiacenza e tutti gli archi collegati
	 * ad esso
	 * 
	 * @param v
	 */
	public void removeVertex(Point2D v) {
		// rimuovo lista di adiacenza di v
		tabella.remove(v);

		// rimuovo v da tutte le liste di adiacenza
		for (Iterator<ArrayList<NodoListaAdiacenza>> iterator = tabella
				.values().iterator(); iterator.hasNext();) {

			ArrayList<NodoListaAdiacenza> lista = iterator.next();
			
			NodoListaAdiacenza nodo = new NodoPuntoPeso(v, 0d);
			ArrayList<NodoListaAdiacenza> elem = new ArrayList<NodoListaAdiacenza>();
			elem.add(nodo);
			
			lista.removeAll(elem);

		}

	}

}
