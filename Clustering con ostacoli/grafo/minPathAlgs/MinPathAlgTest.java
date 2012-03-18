package grafo.minPathAlgs;

import grafo.GrafoOstacoli;
import grafo.GrafoOstacoliTest;
import grafo.Ostacolo;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import junit.framework.TestCase;
import kMedoids.DistanceableGraphPoint;
import util.ObstacleImporter;

public class MinPathAlgTest extends TestCase {

    private final static int BELLMAN_FORD = 0;
    private final static int DIJKSTRA = 1;

    public final void testGetDistanceFromSource() throws Exception {
	int maxX = 650; // massima ordinata dei punti
	int maxY = 650;// massima ascissa dei punti
	int numPunti = 10; // numero di punti casuali da generare

	ArrayList<Ostacolo> ostacoli = null;

	File f = new File("ostacoli.txt");
	ostacoli = ObstacleImporter.importObstacle(f);

	GrafoOstacoliTest grafo = new GrafoOstacoliTest();
	grafo.addObstacles(ostacoli);

	// inizializzo la lista di punti
	ArrayList<Point2D> punti = new ArrayList<Point2D>();
	inizializzaPunti(maxX, maxY, numPunti, grafo, punti);

	double[][] bfDists = getDistances(grafo, punti, BELLMAN_FORD);
	double[][] dDists = getDistances(grafo, punti, DIJKSTRA);

	for (int i = 0; i < dDists.length; i++) {
	    for (int j = 0; j < dDists[0].length; j++) {
		System.out.print(dDists[i][j] + " ");
		assertEquals(bfDists[i][j], dDists[i][j]);

	    }
	    System.out.println();
	}

    }

    private double[][] getDistances(GrafoOstacoliTest grafo,
	    ArrayList<Point2D> punti, int algorithm) {
	double[][] dists = new double[punti.size()][punti.size()];

	MinPathAlg mpa = null;

	for (int i = 0; i < punti.size(); i++) {
	    Point2D source = punti.get(i);
	    grafo.connectToVisible(source);

	    for (int j = 0; j < punti.size(); j++) {
		Point2D destination = punti.get(j);
		grafo.connectToVisible(destination);

		if (algorithm == DIJKSTRA)
		    mpa = MinPathAlgCreator.getDijkstraAlg(grafo);
		else if (algorithm == BELLMAN_FORD)
		    mpa = MinPathAlgCreator.getBellmanFordAlg(grafo);

		mpa.singleSourceMinPath(source);
		dists[i][j] = mpa.getDistanceFromSource(destination);
		System.out.print(grafo.distance(source, destination));
		System.out.println("\t"+dists[i][j]);
		//assertEquals(dists[i][j], grafo.distance(source, destination));
		grafo.getListaAdiacenza().removeVertex(destination);
	    }
	    grafo.getListaAdiacenza().removeVertex(source);
	}
	return dists;
    }

    private static void inizializzaPunti(int maxX, int maxY, int numPunti,
	    GrafoOstacoli grafo, ArrayList<Point2D> punti) {
	Random gen = new Random();
	// inizializzo i punti casualmente
	for (int i = 0; i < numPunti; i++) {
	    DistanceableGraphPoint p = new DistanceableGraphPoint(grafo, gen
		    .nextInt(maxX), gen.nextInt(maxY));
	    if (!grafo.isInsideObstacle(p))
		punti.add(p);
	    else
		i--;
	}
    }
}
