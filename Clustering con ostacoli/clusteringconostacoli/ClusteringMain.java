/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package clusteringconostacoli;

import grafo.GrafoOstacoli;
import grafo.Ostacolo;
import gui.DisplayFrame;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import kMedoids.Cluster;
import kMedoids.Distanceable;
import kMedoids.DistanceableGraphPoint;
import kMedoids.KMedoidsAlg;
import util.ObstacleImporter;

/**
 * Classe principale. Richiama l'algoritmo di k-medoid sul grafo di visibilità,
 * costruito a partire dal file contentente i punti degli ostacoli, con punti
 * casuali.
 * 
 * @author sergio
 */
public class ClusteringMain {

	public static void main(String[] args) throws Exception {

		int maxX = 650; // massima ordinata dei punti
		int maxY = 650;// massima ascissa dei punti
		int numPunti = 1000; // numero di punti casuali da generare

		ArrayList<Ostacolo> ostacoli = null;

		File f = new File("ostacoli.txt");
		if (f != null) {
			ostacoli = ObstacleImporter.importObstacle(f);
		} else {
			ostacoli = ostacoliDefault(maxX, maxY, ostacoli);
		}

		GrafoOstacoli grafo = new GrafoOstacoli();
		grafo.addObstacles(ostacoli);

		// inizializzo la lista di punti
		ArrayList<Distanceable> punti = new ArrayList<Distanceable>();
		inizializzaPunti(maxX, maxY, numPunti, grafo, punti);

		// numero di cluster da formare
		int numCluster = 6;
		// numero di scelte dei k medoidi iniziali
		int numClusteringIniziali = 10;
		int numIterazioniMinime = 50;

		Cluster[] clusters = KMedoidsAlg.kMedoid(punti, numCluster,
				numClusteringIniziali, numIterazioniMinime);

		new DisplayFrame(grafo, clusters);

	}

	/**
	 * Inizializza la lista di punti con punti casuali
	 * 
	 * @param maxX
	 *            - massima ordinata dei punti
	 * @param maxY
	 *            - massima ascissa dei punti
	 * @param numPunti
	 *            - numero di punti da generare
	 * @param grafo
	 *            - grafo di visibilità
	 * @param punti
	 *            - lista in cui inserire i punti
	 */
	private static void inizializzaPunti(int maxX, int maxY, int numPunti,
			GrafoOstacoli grafo, ArrayList<Distanceable> punti) {
		Random gen = new Random();
		// inizializzo i punti casualmente
		for (int i = 0; i < numPunti; i++) {
			DistanceableGraphPoint p = new DistanceableGraphPoint(grafo, gen
					.nextInt(maxX), gen.nextInt(maxY));
			if (!grafo.isInsideObstacle(p)) {
				punti.add(p);
				//grafo.connectToVisible(p, null);
			}
			else
				i--;
		}
	}

	/**
	 * Costruisce gli ostacoli predefiniti nel caso non venga trovato il file
	 * ostacoli
	 * 
	 * @param maxX
	 * @param maxY
	 * @param ostacoli
	 * @return
	 */
	private static ArrayList<Ostacolo> ostacoliDefault(int maxX, int maxY,
			ArrayList<Ostacolo> ostacoli) {
		double[] xOstacoloTmp = { 50, maxX - 50, maxX - 50, 50 };
		double[] yOstacoloTmp = { maxY / 2.0, maxY / 2.0, maxY / 2.0 + 20,
				maxY / 2.0 + 20 };

		double[] xOstacolo2Tmp = { maxX / 2.0, maxX / 2.0 - 100, maxX - 150 };
		double[] yOstacolo2Tmp = { maxY / 2.0 + 80, maxY - 50, maxY - 50 };

		double[] xOstacolo3Tmp = { maxX / 2d, maxX / 2d + 25, maxX / 2d + 25,
				maxX / 2d };
		double[] yOstacolo3Tmp = { -100, -100, maxY / 2d - 50, maxY / 2d - 50 };

		if (ostacoli == null)
			ostacoli = new ArrayList<Ostacolo>();
		ostacoli.add(new Ostacolo(xOstacoloTmp, yOstacoloTmp));
		ostacoli.add(new Ostacolo(xOstacolo2Tmp, yOstacolo2Tmp));
		ostacoli.add(new Ostacolo(xOstacolo3Tmp, yOstacolo3Tmp));
		return ostacoli;
	}

}
