package grafo;

import java.awt.geom.Point2D;

/**
 * Questa classe contiene le informazioni sulla distanza di un cammino
 * 
 * @author Sergio Bonfiglio
 * 
 */
public class Path {

	Point2D source;
	Point2D destination;

	public Path(Point2D source, Point2D destination) {
		this.source = source;
		this.destination = destination;
	}

	public boolean equals(Object o) {
		if (o instanceof Path) {
			Path path = (Path) o;
			return source.equals(path.source)
					&& destination.equals(path.destination);
		} else
			return false;
	}

	public int hashCode() {
		int p1 = (int) (source.getX() * source.getY());
		int p2 = (int) (destination.getX() * destination.getY());

		return p1 ^ p2;
	}

}
