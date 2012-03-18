package util;

import grafo.Ostacolo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Questa classe serve alla lettura da file dei vertici degli ostacoli. Il primo
 * vertice di un ostacolo deve essere preceduto dalla stringa
 * "INIZIO_NUOVO_OSTACOLO SEPARATORE numero di vertici dell'ostacolo \n".
 * Inoltre su ogni riga deve essere presente un solo vertice le cui ascisse
 * siano separate dalle ordinate dal carattere SEPARATORE.
 * 
 * Un esempio: 
 * punti 4 
 * 50 300
 * 650 300
 * 650 320
 * 50 320 
 * punti 3 
 * 300 380 
 * 200 560 
 * 450 560 
 * punti 6 
 * 300 10 
 * 580 10 
 * 580 30 
 * 320 30 
 * 320 250 
 * 300 250
 * 
 * @author Sergio Bonfiglio
 * 
 */

public class ObstacleImporter {

	private static final String INIZIO_NUOVO_OSTACOLO = "punti";
	static final char SEPARATORE = '\t';

	public static ArrayList<Ostacolo> importObstacle(File f) throws Exception {

		FileInputStream fis = new FileInputStream(f);
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(isr);
		String linea = br.readLine();
		double[] xOstacolo = null;
		double[] yOstacolo = null;
		int pIndex = 0;
		ArrayList<Ostacolo> ostacoli = new ArrayList<Ostacolo>();
		while (linea != null) {

			// se trovo il marcatore di inizio nuovo ostacolo
			if (linea.startsWith(INIZIO_NUOVO_OSTACOLO)) {
				int i = linea.lastIndexOf(SEPARATORE);
				int numVertici = Integer.parseInt("" + linea.charAt(i + 1));

				// resetto gli array che contengono i vertici
				xOstacolo = new double[numVertici];
				yOstacolo = new double[numVertici];
				pIndex = 0;
			} else {
				// aggiungo il punto letto nella linea corrente
				int i = linea.lastIndexOf(SEPARATORE);
				double x = Double.parseDouble(linea.substring(0, i));
				double y = Double.parseDouble(linea.substring(i + 1, linea
						.length()));

				xOstacolo[pIndex] = x;
				yOstacolo[pIndex] = y;
				pIndex++;

				// se ho letto tutti i vertici dell'ostacolo aggiungo il nuovo
				// ostacolo alla lista di quelli importati
				if (pIndex == xOstacolo.length)
					ostacoli.add(new Ostacolo(xOstacolo, yOstacolo));
			}

			linea = br.readLine();
		}

		br.close();
		
		return ostacoli;
	}

}
