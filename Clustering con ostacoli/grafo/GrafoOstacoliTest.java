package grafo;

import java.awt.geom.Point2D;

//usata solo per i test
public class GrafoOstacoliTest extends GrafoOstacoli {

    public void connectToVisible(Point2D p) {
	this.connectToVisible(p, null);
    }

    @Override
    public double distance(Point2D p1, Point2D p2) {
	// TODO Auto-generated method stub
	return super.distance(p1, p2);
    }

}
