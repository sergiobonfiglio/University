package grafo.minPathAlgs.dijkstra.minHeap;

import java.awt.geom.Point2D;
import java.util.HashMap;

/**
 * La classe MinHeap implementa un Min-Heap e una coda di priorità
 * 
 * @author Sergio Bonfiglio
 * 
 */
public class MinHeap {
	/**
	 * Restituisce il figlio sinistro del nodo di indice i
	 */
	private static int left(int i) {
		return i * 2 + 1;
	}
	/**
	 * Restituisce il padre del nodo di indice i
	 */
	private static int parent(int i) {
		return (i - 1) / 2;
	}
	/**
	 * Restituisce il figlio destro del nodo di indice i
	 */
	private static int right(int i) {
		return i * 2 + 2;
	}

	private NodoHeap[] heap;

	private int heapsize;

	// lookup table: è necessaria per l'aggiornamento efficiente dei nodi quando
	// viene invocato il metodo per decrementarne la chiave
	private HashMap<Point2D, NodoHeap> lut;


	public MinHeap(NodoHeap[] array) {
		heap = array;
		heapsize = heap.length;
		// inizializzo la lut con le corrispondenze tra punti e nodi dell'heap
		setLut(new HashMap<Point2D, NodoHeap>());
		for (int i = 0; i < array.length; i++) {
			getLut().put(array[i].getP(), array[i]);
		}

		buildMinHeap();
	}

	private void buildMinHeap() {
		for (int i = parent(heapsize - 1); i >= 0; i--) {
			minHeapify(i);
		}
	}

	public void decreaseKey(int indexNodo, double priority) {
		NodoHeap nodoVecchio = (NodoHeap) this.heap[indexNodo];

		if (nodoVecchio.stimaDistanza > priority) {
			nodoVecchio.stimaDistanza = priority;
			while (indexNodo > 0
					&& heap[parent(indexNodo)].compareTo(nodoVecchio) > 0) {
				swap(parent(indexNodo), indexNodo);
				indexNodo = parent(indexNodo);
			}

		} else {
			System.out.println("errore priorità");
		}
	}

	/**
	 * Decrementa la priorità del nodo n
	 * 
	 * @param n
	 * @param priority
	 */
	public void decreaseKey(NodoHeap n, double priority) {
		decreaseKey(n.index, priority);
	}

	public NodoHeap extractMin() {
		NodoHeap min = heap[0];
		heap[0] = heap[heapsize - 1];
		heap[0].index = 0;
		this.heapsize = heapsize - 1;
		minHeapify(0);

		return min;
	}

	public NodoHeap[] getHeap() {
		return heap;
	}

	public int getHeapSize() {
		return heapsize;
	}

	public HashMap<Point2D, NodoHeap> getLut() {
		return lut;
	}

	public NodoHeap min() {
		return this.heap[0];
	}

	/**
	 * Sposta il nodo i nella sua posizione naturale all'interno dell'heap
	 */
	private void minHeapify(int i) {
		int l = left(i);
		int r = right(i);
		int min = i;

		if (l <= heapsize - 1 && heap[min].compareTo(heap[l]) > 0) {
			min = l;
		}
		if (r <= heapsize - 1 && heap[min].compareTo(heap[r]) > 0) {
			min = r;
		}

		if (min != i) {
			swap(i, min);
			minHeapify(min);
		}

	}

	public void setHeap(NodoHeap[] a) {
		heap = a;
	}

	public void setLut(HashMap<Point2D, NodoHeap> lut) {
		this.lut = lut;
	}

	private void swap(int i, int j) {
		heap[i].index = j;
		heap[j].index = i;

		NodoHeap tmp = heap[i];
		heap[i] = heap[j];
		heap[j] = tmp;

	}
}
