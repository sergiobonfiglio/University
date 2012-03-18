package gui;

import grafo.GrafoOstacoli;
import grafo.Ostacolo;
import grafo.listaAdiacenza.NodoListaAdiacenza;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JPanel;

import kMedoids.Cluster;
import kMedoids.DistanceablePoint;

public class DisplayPanel extends JPanel {

    GrafoOstacoli grafo;
    Cluster[] clusters;
    Color[] colors;

    public DisplayPanel(GrafoOstacoli grafo, Cluster[] clusters) {
	this.grafo = grafo;
	this.clusters = clusters;
	this.setBackground(Color.black);
    }

    public DisplayPanel(GrafoOstacoli grafo, Cluster[] clusters, Color[] colors) {
	this(grafo, clusters);
	this.colors = colors;

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
	super.paintComponent(g);
	Graphics2D g2 = (Graphics2D) g;

	drawObstacles(g2);
	drawClusters(g2);

    }

    /**
     * @param g2
     */
    private void drawClusters(Graphics2D g2) {
	// per ogni cluster
	for (int i = 0; i < clusters.length; i++) {
	    g2.setPaintMode();
	    g2.setColor(colors[i]);
	    // i punti del cluster
	    for (Iterator iterator = clusters[i].punti.iterator(); iterator
		    .hasNext();) {
		Point2D.Double p2 = (Point2D.Double) iterator.next();

		int raggio = 2;
		g2.fillOval((int) p2.x - raggio, (int) p2.y - raggio,
			2 * raggio, 2 * raggio);
		// g2.drawLine((int) p2.x, (int) p2.y, (int) p2.x, (int) p2.y);

	    }
	    // disegno il medoide
	    int raggio = 7;

	    DistanceablePoint p = (DistanceablePoint) (clusters[i].medoide);
	    g2.setXORMode(Color.white);
	    g2.fillOval((int) (p.x - raggio), (int) (p.y - raggio), 2 * raggio,
		    2 * raggio);
	}
    }

    /**
     * @param g2
     */
    private void drawObstacles(Graphics2D g2) {
	// disegno archi
	// drawVisibilityEdges(g2);

	// disegno gli ostacoli
	for (Iterator<Ostacolo> iterator = grafo.getOstacoli().iterator(); iterator
		.hasNext();) {

	    Ostacolo ost = iterator.next();

	    g2.setPaintMode();
	    g2.setColor(new Color(255, 255, 255));

	    // disegno i vertici
	    for (int i = 0; i + 1 < ost.x.length; i++) {

		g2.drawLine((int) ost.x[i], (int) ost.y[i], (int) ost.x[i + 1],
			(int) ost.y[i + 1]);

		// drawGraphEdge(g2, ost, i);
	    }

	    g2.drawLine((int) ost.x[0], (int) ost.y[0],
		    (int) ost.x[ost.x.length - 1],
		    (int) ost.y[ost.y.length - 1]);
	}

    }

    private void drawVisibilityEdges(Graphics2D g2) {
	g2.setPaintMode();
	g2.setColor(Color.ORANGE);

	Set<Point2D> vertici = grafo.getListaAdiacenza().getVertici();

	for (Iterator iterator = vertici.iterator(); iterator.hasNext();) {
	    Point2D u = (Point2D) iterator.next();

	    ArrayList<NodoListaAdiacenza> confinanti = grafo
		    .getListaAdiacenza().getTabella().get(u);

	    for (int i = 0; i < confinanti.size(); i++) {
		Point2D p2 = confinanti.get(i).getPoint();

		g2.drawLine((int) u.getX(), (int) u.getY(), (int) p2.getX(),
			(int) p2.getY());
	    }

	}
    }

    /**
     * @param g2
     * @param ost
     * @param i
     */
    private void drawGraphEdge(Graphics2D g2, Ostacolo ost, int i) {

	g2.setPaintMode();
	g2.setColor(Color.gray);
	// // disegno gli archi
	HashMap<Point2D, ArrayList<NodoListaAdiacenza>> lista = grafo
		.getListaAdiacenza().getTabella();

	Point2D.Double v = new Point2D.Double(ost.x[i], ost.y[i]);
	if (lista.containsKey(v)) {
	    for (Iterator<NodoListaAdiacenza> iterator2 = lista.get(v)
		    .iterator(); iterator2.hasNext();) {
		NodoListaAdiacenza nodo = iterator2.next();

		Point2D v2 = nodo.getPoint();
		g2.drawLine((int) v.x, (int) v.y, (int) v2.getX(), (int) v2
			.getY());

	    }
	}
    }

}
