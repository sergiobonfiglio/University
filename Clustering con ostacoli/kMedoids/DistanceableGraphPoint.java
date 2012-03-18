package kMedoids;

import grafo.GrafoOstacoli;

import java.awt.geom.Point2D;

/**
 * Questa classe rappresenta un punto all'interno dello spazio definito dal
 * grafo di visibilità e implementa la distanza come cammino minimo tra due
 * punti all'interno del grafo.
 * 
 * @author Sergio Bonfiglio
 * 
 */
public class DistanceableGraphPoint extends DistanceablePoint {

	private GrafoOstacoli grafoVisibilita;

	public DistanceableGraphPoint(GrafoOstacoli grafo, double x, double y) {
		this(x, y);
		this.grafoVisibilita = grafo;
	}

	private DistanceableGraphPoint(double x, double y) {
		super(x, y);
	}

	public double distance(Distanceable p) {
		return this.grafoVisibilita.distance(this, (Point2D) p);
	}

	public String toString() {
		return "(" + (int) this.x + "," + (int) this.y + ")";
	}
}
