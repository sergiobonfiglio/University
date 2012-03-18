package grafo;

import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import sun.tools.tree.SuperExpression;

/**
 * Questa classe contiene la rappresentazione di un ostacolo. I vertici vengono
 * connessi nello stesso ordine con cui vengono passati
 * 
 * @author Sergio Bonfiglio
 * 
 */
public class Ostacolo {

	// le ascisse e le ordinate dei vertici dell'ostacolo
	public double[] x;
	public double[] y;

	Polygon poligono;

	public Ostacolo(double[] xpoints, double[] ypoints) {
		this.x = xpoints;
		this.y = ypoints;

		// costruisco il poligono che rappresenta l'ostacolo
		poligono = new Polygon();
		for (int i = 0; i < ypoints.length; i++) {
			poligono.addPoint((int) xpoints[i], (int) ypoints[i]);
		}

	}

	/**
	 * Controlla se il punto p è contenuto all'interno dell'ostacolo
	 * 
	 * @param p
	 * @return true se il punto è contenuto all'interno dell'ostacolo, false
	 *         altrimenti
	 */
	public boolean contains(Point2D p) {
		/*
		 * Un punto appartenente al bordo dell'ostacolo viene considerato come
		 * interno. A questo scopo viene considerata l'intersezione tra il
		 * rettangolo costruito espandendo il punto di un unità in tutte le
		 * direzioni, e il poligono
		 */
		int raggio = 1;
		int diametro = 2 * raggio + 1;
		Rectangle2D.Double rect = new Rectangle2D.Double(p.getX() - raggio, p
				.getY()
				- raggio, diametro, diametro);
		return this.poligono.intersects(rect);
	}

	public boolean obstruct(Line2D linea) {
		return this.obstruct(linea.getP1(), linea.getP2());
	}

	/**
	 * Controlla se questo ostacolo ostruisce la linea di visibilità tra i due
	 * punti passati come parametro
	 * 
	 * @param p1
	 * @param p2
	 * @return true se l'ostacolo ostruisce la linea di visibilità tra i due
	 *         punti, false altrimenti
	 */
	public boolean obstruct(Point2D p1, Point2D p2) {
		Line2D lineaVisibilita = new Line2D.Double(p1, p2);

		// poiché il metodo lavora con numeri double è necessaria una certa
		// tolleranza numerica
		double tolleranzaNumerica = 0.000001;

		/*
		 * controllo l'intersezione con tutti i segmenti che formano il
		 * perimetro dell'ostacolo
		 */
		for (int i = 0; i + 1 < this.x.length; i++) {
			Point2D p3 = new Point2D.Double(x[i], y[i]);
			Point2D p4 = new Point2D.Double(x[i + 1], y[i + 1]);

			Line2D lineaPol = new Line2D.Double(p3, p4);

			Point2D puntoIntersezione = getLineIntersectionPoint(lineaPol,
					lineaVisibilita);
			/*
			 * se le linee si intersecano allora controllo se il punto di
			 * intersezione avviene tra i segmenti(e nn tra le rette
			 * corrispondenti); inoltre controllo che il punto di intersezione
			 * non coincida con un vertice dell'ostacolo (i vertici
			 * dell'ostacolo sono percorribili)
			 */
			if (puntoIntersezione != null
					&& lineaPol.intersectsLine(lineaVisibilita)
					&& puntoIntersezione.distance(p3) > tolleranzaNumerica
					&& puntoIntersezione.distance(p4) > tolleranzaNumerica) {
				return true;
			}
		}
		// controllo ultimo segmento
		Point2D p3 = new Point2D.Double(x[0], y[0]);
		Point2D p4 = new Point2D.Double(x[x.length - 1], y[y.length - 1]);
		Line2D lineaPol = new Line2D.Double(p3, p4);
		Point2D puntoIntersezione = getLineIntersectionPoint(lineaPol,
				lineaVisibilita);

		if (puntoIntersezione != null
				&& lineaPol.intersectsLine(lineaVisibilita)
				&& puntoIntersezione.distance(p3) > tolleranzaNumerica
				&& puntoIntersezione.distance(p4) > tolleranzaNumerica) {
			return true;
		}

		return false;
	}

	/**
	 * Calcola il punto di intersezione tra le rette determinate dai rispettivi
	 * segmenti
	 * 
	 * @param linea1
	 * @param linea2
	 * @return il punto di intersezione tra le rette passate come parametro
	 *         sotto forma di linee, null se le rette sono parallele
	 */
	private static Point2D getLineIntersectionPoint(Line2D linea1, Line2D linea2) {

		Point2D.Double intersezione = null;
		double a1, b1, c1, a2, b2, c2, denom;
		a1 = linea1.getY2() - linea1.getY1();
		b1 = linea1.getX1() - linea1.getX2();
		c1 = linea1.getX2() * linea1.getY1() - linea1.getX1() * linea1.getY2();
		// a1x + b1y + c1 = 0 linea1 eq
		a2 = linea2.getY2() - linea2.getY1();
		b2 = linea2.getX1() - linea2.getX2();
		c2 = linea2.getX2() * linea2.getY1() - linea2.getX1() * linea2.getY2();
		// a2x + b2y + c2 = 0 linea2 eq
		denom = a1 * b2 - a2 * b1;
		if (denom != 0)
			intersezione = new Point2D.Double((b1 * c2 - b2 * c1) / denom, (a2
					* c1 - a1 * c2)
					/ denom);
		else {
			/*
			 * se le rette sono parallele il punto di intersezione rimane
			 * inizializzato a null
			 */
		}
		return intersezione;

	}

}
