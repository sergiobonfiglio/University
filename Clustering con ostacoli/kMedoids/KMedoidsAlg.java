/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kMedoids;

import java.util.ArrayList;
import java.util.List;

/**
 * Questa classe implementa l'algoritmo K-Medoid per la clusterizzazione di
 * oggetti che implementano l'interfaccia Distanceable
 * 
 * @author Sergio Bonfiglio
 * 
 */
public class KMedoidsAlg {

	/** Assegna ogni punto al cluster più vicino */
	private static Cluster[] assegnaPuntiAiCluster(List<Distanceable> punti,
			List<Distanceable> medoidi) {
		int k = medoidi.size();
		Cluster[] clusters = new Cluster[k];

		// creo i nuovi cluster
		for (int i = 0; i < medoidi.size(); i++) {
			clusters[i] = new Cluster(medoidi.get(i));
		}

		// assegna ogni oggetto non medoide ad un cluster
		for (int i = 0; i < punti.size(); i++) {
			Distanceable p = punti.get(i);
			// assegno il punto al cluster(medoide) più vicino
			int clusterVicino = indiceClusterVicino(clusters, p);
			clusters[clusterVicino].addToCluster(p);
		}

		return clusters;
	}

	/**
	 * Calcola la clusterizzazione migliore tra quelle passate come parametro
	 * sulla base della distanza media dei punti dal medoide
	 * 
	 * @param clustersProva
	 * @return il clustering migliore tra quelli passati come paramtro
	 */
	private static Cluster[] calcolaClusteringMigliore(Cluster[][] clustersProva) {
		// prendo il clustering migliore
		double min = Double.MAX_VALUE;
		int indiceClusterMigliore = -1;
		for (int i = 0; i < clustersProva.length; i++) {

			double mediaMedieDistanze = mediaMedieCluster(clustersProva[i]);

			System.out.println("media medie cluster " + i + ": "
					+ mediaMedieDistanze);
			if (min > mediaMedieDistanze) {
				min = mediaMedieDistanze;
				indiceClusterMigliore = i;
			}
		}
		System.out.println("clustering migliore: " + indiceClusterMigliore);
		return clustersProva[indiceClusterMigliore];
	}

	/**
	 * Calcola numeroClustering clusterizzazioni differenti scegliendo di volta
	 * in volta k medoidi casualmente ed assegnango ogni punto al medoide più
	 * vicino
	 * 
	 * @param punti -
	 *            i punti su cui effettuare le clusterizzazzioni
	 * @param k -
	 *            il numero di cluster da formare
	 * @param numeroClustering -
	 *            il numero di clusterizzazzioni da calcolare
	 * @return Una matrice con numeroClustering righe e k colonne il cui elemeto
	 *         in posizione ij è il j-esimo cluster della i-esima
	 *         clusterizzazzione
	 */
	private static Cluster[][] calcolaNClustering(List<Distanceable> punti,
			int k, int numeroClustering) {
		ArrayList[] medoidiProve = new ArrayList[numeroClustering];
		ArrayList[] puntiProve = new ArrayList[numeroClustering];
		Cluster[][] clustersProva = new Cluster[numeroClustering][k];

		// produco numeroClustering Clustering differenti tra cui
		// scegliere il migliore
		for (int i = 0; i < numeroClustering; i++) {
			// inizializzo la lista con i punti da considerare
			puntiProve[i] = new ArrayList();
			medoidiProve[i] = new ArrayList();
			puntiProve[i].addAll(punti);

			// scelgo k medoidi a caso
			// selectNNonMedoidRndPoints(medoidiProve[i], puntiProve[i], k);

			// scelgo k medoidi il più distante possibile tra loro
			System.out.println("Scelta dei medoidi massimizzando la distanza");
			selectNNonMedoidRndPointsMaxDistance(medoidiProve[i],
					puntiProve[i], k);

			// assegno i punti ai cluster
			clustersProva[i] = assegnaPuntiAiCluster(puntiProve[i],
					medoidiProve[i]);
			System.out.println("Clustering " + i + ":");
			printStatClusters(clustersProva[i]);
		}
		return clustersProva;
	}

	/**
	 * Restituisce l'indice del cluster il cui medoide ha la distanza minima dal
	 * punto passato come parametro
	 */
	private static int indiceClusterVicino(Cluster[] clusters, Distanceable p) {
		double min = Double.MAX_VALUE;
		int clusterVicino = -1;
		for (int j = 0; j < clusters.length; j++) {
			double d = ((Distanceable) p).distance(clusters[j].medoide);
			if (d < min) {
				min = d;
				clusterVicino = j;
			}
		}
		return clusterVicino;
	}

	/**
	 * Implementazione dell'algoritmo k-Medoid
	 * 
	 * @param punti -
	 *            la lista dei punti da clusterizzare
	 * @param k -
	 *            il numero di cluster da formare
	 * @param numeroClusteringIniziali -
	 *            il numero di clusterizzazioni iniziali. Tra queste verrà
	 *            scelta quella migliore e ulteriormente affinata
	 * @param iterazioniMinimeDopoCambiamento -
	 *            se il numero di iterazioni senza cambiamento supera il valore
	 *            di questo parametro l'algoritmo termina
	 * @return Un array di Cluster
	 */
	public static Cluster[] kMedoid(List<Distanceable> punti, int k,
			int numeroClusteringIniziali, int iterazioniMinimeDopoCambiamento) {

		Cluster[][] clustersProva = calcolaNClustering(punti, k,
				numeroClusteringIniziali);

		Cluster[] clusters = calcolaClusteringMigliore(clustersProva);

		// costruisco la lista dei medoidi del clustering migliore
		ArrayList<Distanceable> medoidi = new ArrayList<Distanceable>();
		for (int i = 0; i < clusters.length; i++) {
			medoidi.add(clusters[i].medoide);
		}

		/*
		 * Reitero finché non ci sono stati cambiamenti per un numero minimo di
		 * iterazioni
		 */
		boolean noChange = false;
		int iterazioni = 0;
		while (noChange == false
				|| iterazioni < iterazioniMinimeDopoCambiamento) {
			noChange = true;
			iterazioni++;

			/*
			 * prendo un punto a caso e lo provo come medoide del cluster che lo
			 * contiene. Il punto candidato diventa vero e proprio medoide del
			 * cluster solo se riduce la somma delle distanze
			 */
			int candidato = selectNonMedoidRndPoint(medoidi, punti);
			Distanceable puntoCandidato = (Distanceable) punti.get(candidato);

			for (int i = 0; i < clusters.length && noChange; i++) {
				// costruisco il cluster uguale all'iesimo cluster con però come
				// medoide il punto candidato
				Cluster clusterCandidato = clusters[i]
						.calcChangedMedoid(puntoCandidato);

				if (clusterCandidato != null) {
					/*
					 * Se il cluster contiene il punto calcolo il costo di
					 * cambiare medoide
					 */
					double sommaDistCandidato = clusterCandidato.sommaDistanze;
					double sommaDistCorrente = clusters[i].sommaDistanze;

					if (sommaDistCandidato < sommaDistCorrente) {
						/*
						 * Se il candidato migliora la somma delle distanze
						 * aggiorno le liste di punti e di medoidi.
						 */

						// aggiungo il vecchio medoide ai punti e lo rimuovo dai
						// medoidi
						punti.add(clusters[i].medoide);
						medoidi.remove(clusters[i].medoide);

						// aggiungo il nuovo medoide ai medoidi e lo rimuovo dai
						// punti
						punti.remove(clusterCandidato.medoide);
						medoidi.add(clusterCandidato.medoide);

						System.out
								.println("Guadagno cambio medoide: "
										+ clusters[i].medoide
										+ "-->"
										+ clusterCandidato.medoide
										+ " = "
										+ (sommaDistCorrente
												/ (double) clusters[i].numPunti - sommaDistCandidato
												/ (double) clusterCandidato.numPunti));

						clusters[i] = clusterCandidato;
						noChange = false;
						iterazioni = 0;
					}
				}

			}
			// devo riassegnare i punti solo se è cambiato un medoide
			if (noChange == false) {
				clusters = assegnaPuntiAiCluster(punti, medoidi);
				printStatClusters(clusters);
			}
		}

		return clusters;
	}

	/**
	 * Restituisce la media delle distanze medie dal medoide dei cluster
	 * 
	 * @param clusters
	 * @return La media delle distanze medie dal medoide dei cluster
	 */
	private static double mediaMedieCluster(Cluster[] clusters) {
		// calcolo media delle medie delle somme delle distanze
		double sommaMedieDistanze = 0;
		for (int j = 0; j < clusters.length; j++) {

			if (clusters[j].numPunti > 0)
				sommaMedieDistanze += clusters[j].sommaDistanze
						/ (double) clusters[j].numPunti;

		}
		double mediaMedieDistanze = sommaMedieDistanze
				/ (double) clusters.length;
		return mediaMedieDistanze;
	}

	private static void printStatClusters(Cluster[] clusters) {
		for (int i = 0; i < clusters.length; i++) {
			double media = clusters[i].sommaDistanze
					/ (double) clusters[i].numPunti;
			media = ((int) (media * 1000)) / 1000d;

			System.out.println("Cluster " + i + ": media=" + media
					+ " medoide: " + clusters[i].medoide);
		}
		System.out.println("------media medie=" + mediaMedieCluster(clusters)
				+ "-----------------");
	}

	/**
	 * Seleziona n medoidi dalla lista dei punti e li aggiunge alla lista dei
	 * medoidi
	 */
	private static void selectNNonMedoidRndPoints(List<Distanceable> medoidi,
			List<Distanceable> punti, int n) {
		for (int i = 0; i < n; i++) {
			int rnd = selectNonMedoidRndPoint(medoidi, punti);
			medoidi.add(punti.get(rnd));
			punti.remove(rnd);
		}
	}

	/**
	 * Seleziona n medoidi dalla lista dei punti il più possibile distanti tra
	 * loro e li aggiunge alla lista dei medoidi
	 */
	private static void selectNNonMedoidRndPointsMaxDistance(
			List<Distanceable> medoidi, List<Distanceable> punti, int n) {

		// scelgo il primo punto a caso
		int rnd = selectNonMedoidRndPoint(medoidi, punti);
		Distanceable p = punti.get(rnd);
		// medoidiDistanti.add(p);
		medoidi.add(p);
		punti.remove(rnd);

		// scelgo il punto più distante
		for (int i = 0; i < n - 1; i++) {
			double max = Double.MIN_VALUE;
			Distanceable p2 = null;
			for (int j = 0; j < punti.size(); j++) {
				double distanza = 0;
				for (int j2 = 0; j2 < medoidi.size(); j2++) {
					distanza += punti.get(j).distance(medoidi.get(j2));
				}
				if (distanza > max) {
					max = distanza;
					p2 = punti.get(j);
				}
			}

			medoidi.add(p2);
			punti.remove(p2);
		}

	}

	/**
	 * Prende un punto a caso che non sia già un medoide e ne restituisce
	 * l'indice all'interno della lista di punti
	 */
	private static int selectNonMedoidRndPoint(List<Distanceable> medoidi,
			List<Distanceable> punti) {
		boolean uguale = false;
		int rnd;
		do {
			rnd = (int) (Math.random() * punti.size());
			uguale = false;
			for (int j = 0; j < medoidi.size() && uguale == false; j++) {
				if (punti.get(rnd) == medoidi.get(j)) {
					uguale = true;
				}
			}
		} while (uguale);
		return rnd;
	}

}
