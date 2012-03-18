/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kMedoids;

import java.awt.geom.Point2D;

/**
 * Punto euclideo
 * 
 * @author sergio
 */
public class DistanceablePoint extends Point2D.Double implements Distanceable {

	public DistanceablePoint(double x, double y) {
		super(x, y);
	}

	public double distance(Distanceable p) {
		return this.distance((Point2D) p);
	}

	public String toString() {
		return "(" + (int) this.x + "," + (int) this.y + ")";
	}
}
