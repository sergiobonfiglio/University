import hashForwardList.HashForwardList;
import hashForwardList.Nodo;

import java.awt.Point;
import java.util.Iterator;
import java.util.List;

import utilities.SpazioRGB;

/**
 * Questa classe prende in input un istanza della classe Pattern e ricerca la
 * migliore corrispondenza all'interno dell'immagine selezionata.
 * 
 * @author Sergio Bonfiglio
 * 
 */

public class RicercatorePattern {

	private Pattern patternRicercato;

	private Point puntoMigliore;

	private int deltaDistanzaMassimaConsentitaGreen;

	private int gammaDistanzaMassimaConsentitaPattern;

	public static final byte DA_VICINO = 0;

	public static final byte DALL_INIZIO = 1;

	public RicercatorePattern(Pattern patternRicercato) {
		this.patternRicercato = patternRicercato;

		puntoMigliore = null;

		deltaDistanzaMassimaConsentitaGreen = 256;
		gammaDistanzaMassimaConsentitaPattern = Integer.MAX_VALUE;

	}

	/**
	 * L'unico metodo esposto della classe. Seleziona le opzioni e richiama i
	 * metodi interni per eseguire l'algoritmo.
	 * 
	 * @param strategia -
	 *            La strategia di ricerca da usare.
	 * @param minimizzaAncheDeltaDistanza -
	 *            Se true minimizzerà sia la delta che la gamma distanza. Se
	 *            false minimizzerà esclusivamente la gamma distanza.
	 * @param allineaBuchiSoloSuMatch -
	 *            Se true prova gli allineamenti generati dalla lista dei buchi
	 *            solo nel caso in cui gli allineamenti selezionati dalla delta
	 *            distanza abbiano generato almeno un allineamento migliorativo.
	 *            Questa opzione non ha molto senso probabilmente verrà rimossa.
	 * @return Il Point al centro dell'allineamento migliore.
	 */

	public Point ricercaMiglioreCorrispondenza(byte strategia,
			boolean minimizzaAncheDeltaDistanza, boolean allineaBuchiSoloSuMatch) {
		if (strategia == DA_VICINO) {
			return ricercaMiglioreCorrispondenzaDaVicino(
					minimizzaAncheDeltaDistanza, allineaBuchiSoloSuMatch);
		} else if (strategia == DALL_INIZIO) {
			return ricercaMiglioreCorrispondenzaDallInizio(
					minimizzaAncheDeltaDistanza, allineaBuchiSoloSuMatch);
		} else
			return null;
	}

	/**
	 * Implementa la strategia di ricerca da vicino. In questa modalità la
	 * ricerca della patch migliore inizia dalla medesima riga in cui si trova
	 * la patch da sostituire. Quando ha raggiunto la fine dell'immagine
	 * riprende la ricerca dalla prima riga (della griglia). Quest'opzione
	 * ipotizza che la foto sia orizzontale. Immagini orientate verticalmente
	 * dovrebbero essere prima ruotate per ottenere migliori prestazioni.
	 * 
	 * @param minimizzaAncheDeltaDistanza -
	 *            Se true minimizzerà sia la delta che la gamma distanza. Se
	 *            false minimizzerà esclusivamente la gamma distanza.
	 * @param allineaBuchiSoloSuMatch -
	 *            Se true prova gli allineamenti generati dalla lista dei buchi
	 *            solo nel caso in cui gli allineamenti selezionati dalla delta
	 *            distanza abbiano generato almeno un allineamento migliorativo.
	 *            Questa opzione non ha molto senso probabilmente verrà rimossa.
	 * @return Il Point al centro dell'allineamento migliore.
	 */
	private Point ricercaMiglioreCorrispondenzaDaVicino(
			boolean minimizzaAncheDeltaDistanza, boolean allineaBuchiSoloSuMatch) {

		PaintFrame frame = PaintFrame.getIstanza();

		puntoMigliore = null;

		Point puntoMaxPriorita = patternRicercato.centroPattern;

		int dimensioneFinestra = frame.window;

		/*
		 * Prendo un punto ogni m (dimensione del pattern ricercato) sia in
		 * orizzontale che in verticale. Inizio dalla riga del punto con più
		 * alta priorità (vicinanza delle zone simili) e ciclo fino alla fine
		 * dell'immagine. Poi riprendo dall'inizio.
		 */
		int yIniziale = Math.max(puntoMaxPriorita.y, dimensioneFinestra);
		yIniziale = Math.min(yIniziale, frame.h - dimensioneFinestra);

		for (int y = yIniziale; y < frame.h - dimensioneFinestra + 1
				&& gammaDistanzaMassimaConsentitaPattern > 0; y += dimensioneFinestra) {
			for (int x = dimensioneFinestra; x < frame.w - dimensioneFinestra
					+ 1
					&& gammaDistanzaMassimaConsentitaPattern > 0; x += dimensioneFinestra) {

				ricercaMiglioreCorrispondenzaPuntuale(frame, x, y,
						minimizzaAncheDeltaDistanza, allineaBuchiSoloSuMatch);

			}// fine ciclo sulle x dell'immagine
		}// fine ciclo sulle y dell'immagine

		/*
		 * Poi ricomincio dall'inizio fino al punto da cui sono partito
		 */
		for (int y = dimensioneFinestra; y < yIniziale
				&& gammaDistanzaMassimaConsentitaPattern > 0; y += dimensioneFinestra) {
			for (int x = dimensioneFinestra; x < frame.w - dimensioneFinestra
					+ 1
					&& gammaDistanzaMassimaConsentitaPattern > 0; x += dimensioneFinestra) {

				ricercaMiglioreCorrispondenzaPuntuale(frame, x, y,
						minimizzaAncheDeltaDistanza, allineaBuchiSoloSuMatch);

			}// fine ciclo sulle x dell'immagine
		}// fine ciclo sulle y dell'immagine

		return puntoMigliore;
	}

	/**
	 * Implementa la strategia di ricerca dall'inizio. In questa modalità la
	 * ricerca della patch migliore inizia dalla prima riga della griglia.
	 * 
	 * @param minimizzaAncheDeltaDistanza -
	 *            Se true minimizzerà sia la delta che la gamma distanza. Se
	 *            false minimizzerà esclusivamente la gamma distanza.
	 * @param allineaBuchiSoloSuMatch -
	 *            Se true prova gli allineamenti generati dalla lista dei buchi
	 *            solo nel caso in cui gli allineamenti selezionati dalla delta
	 *            distanza abbiano generato almeno un allineamento migliorativo.
	 *            Questa opzione non ha molto senso probabilmente verrà rimossa.
	 * @return Il Point al centro dell'allineamento migliore.
	 */
	private Point ricercaMiglioreCorrispondenzaDallInizio(
			boolean minimizzaAncheDeltaDistanza, boolean allineaBuchiSoloSuMatch) {

		PaintFrame frame = PaintFrame.getIstanza();

		puntoMigliore = null;

		/*
		 * Prendo un punto ogni m (dimensione del pattern ricercato) sia in
		 * orizzontale che in verticale
		 */
		int dimensioneFinestra = frame.window;

		for (int y = dimensioneFinestra; y < frame.h - dimensioneFinestra + 1
				&& gammaDistanzaMassimaConsentitaPattern > 0; y += dimensioneFinestra) {
			for (int x = dimensioneFinestra; x < frame.w - dimensioneFinestra
					+ 1
					&& gammaDistanzaMassimaConsentitaPattern > 0; x += dimensioneFinestra) {

				ricercaMiglioreCorrispondenzaPuntuale(frame, x, y,
						minimizzaAncheDeltaDistanza, allineaBuchiSoloSuMatch);

			}// fine ciclo sulle x dell'immagine
		}// fine ciclo sulle y dell'immagine

		return puntoMigliore;
	}

	/**
	 * Ricerca la migliore corrispondenza tra tutti gli allineamenti col punto
	 * dell'immagine (x, y) che rientrano nella delta distanza massima e poi con
	 * quelli generati dalla lista dei buchi.
	 * 
	 * @param frame
	 * @param x -
	 *            la x del punto dell'immagine di cui testare gli allineamenti.
	 * @param y -
	 *            la y del punto dell'immagine di cui testare gli allineamenti.
	 * @param minimizzaAncheDeltaDistanza -
	 *            Se true minimizzerà sia la delta che la gamma distanza. Se
	 *            false minimizzerà esclusivamente la gamma distanza.
	 * @param allineaBuchiSoloSuMatch -
	 *            Se true prova gli allineamenti generati dalla lista dei buchi
	 *            solo nel caso in cui gli allineamenti selezionati dalla delta
	 *            distanza abbiano generato almeno un allineamento migliorativo.
	 *            Questa opzione non ha molto senso probabilmente verrà rimossa.
	 * @return true se è stato trovato almeno un allineamento migliore.
	 */
	private boolean ricercaMiglioreCorrispondenzaPuntuale(PaintFrame frame,
			int x, int y, boolean minimizzaAncheDeltaDistanza,
			boolean allineaBuchiSoloSuMatch) {

		boolean almenoUnAllineamentoMigliore = false;

		/*
		 * Se il punto fa parte della zona sconosciuta lo posso saltare subito
		 * perché gli allineamenti con esso non mi restituirebbero un pattern
		 * incompleto e non potrei quindi sostituirlo nella zona selezionata.
		 */
		if (frame.mask[x][y] == PaintFrame.SOURCE) {

			int valorePixelImmagine = frame.image.getRGB(x, y);

			// considero la distanza come provocata interamente dal canale verde
			int deltaDistanzaMassimaCanale = SpazioRGB.getDeltaDistanzaCanale(
					deltaDistanzaMassimaConsentitaGreen, SpazioRGB.VERDE);

			int valoreCanale = SpazioRGB.getCanale(valorePixelImmagine,
					SpazioRGB.VERDE);

			// limiti consentiti dalla delta distanza corrente. Gli altri
			// allineamenti, probabilmente, sono troppo distanti.
			int limiteSup = Math.min(valoreCanale + deltaDistanzaMassimaCanale,
					255);
			int limiteInf = Math.max(valoreCanale - deltaDistanzaMassimaCanale,
					0);

			HashForwardList hashValoriLista = patternRicercato
					.getHashValoriPixel();
			/*
			 * controllo solo il verde: dal pixel con tutto l'errore sul verde
			 * in negativo a quello con tutto l'errore sul verde in positivo.
			 */
			for (int i = limiteInf; i < limiteSup + 1; i++) {
				Nodo nodo = (Nodo) hashValoriLista.get(i);
				// se ci sono pixel con valore i
				if (nodo.lista != null) {
					Iterator iter = nodo.lista.iterator();
					almenoUnAllineamentoMigliore = almenoUnAllineamentoMigliore
							|| calcolaDistanzeAllineamenti(
									patternRicercato.pixelPattern, new Point(x,
											y), iter,
									minimizzaAncheDeltaDistanza,
									gammaDistanzaMassimaConsentitaPattern,
									false);
				} else {
					// salto alla prossima lista
					i = nodo.nearestList - 1;
				}
			}// fine ciclo punti che soddisfano la
			// delta-distanza

			/*
			 * Controllati i punti del pattern che soddisfano la delta distanza
			 * devo allineare con tutti i punti del pattern che fanno parte di
			 * omega e quindi sconosciuti. Infatti essi vengono considerati
			 * pixel jolly e possono essere allineati con qualsiasi altro pixel.
			 */
			List listaBuchiPattern = patternRicercato.getListaPixelOmega();
			Iterator iter = listaBuchiPattern.iterator();

			// se allineaBuchiSoloSuMatch è true li seleziono solo se ho trovato
			// un buon allineamento tra quelli generati dai punti precedenti.
			if (almenoUnAllineamentoMigliore || !allineaBuchiSoloSuMatch) {
				calcolaDistanzeAllineamenti(patternRicercato.pixelPattern,
						new Point(x, y), iter, minimizzaAncheDeltaDistanza,
						gammaDistanzaMassimaConsentitaPattern, false);
			}

		}// fine if(frame.mask[x][y] == PaintFrame.SOURCE)
		return almenoUnAllineamentoMigliore;
	}

	/**
	 * Calcola le distanze di tutti gli allineamenti generati dall'iteratore
	 * passato come parametro col punto dell'immagine puntoSorgente.
	 * 
	 * @param puntoSorgente -
	 *            punto dell'immagine in esame.
	 * @param iter -
	 *            iteratore dei punti che generano gli allineamenti da
	 *            controllare.
	 * @param minimizzaAncheDeltaDistanza -
	 *            Se true minimizzerà sia la delta che la gamma distanza. Se
	 *            false minimizzerà esclusivamente la gamma distanza.
	 * @return true se è stato trovato almeno un allineamento migliore rispetto
	 *         a quelli precedenti, false altrimenti.
	 */
	private boolean calcolaDistanzeAllineamenti(int[][] pixelPattern,
			Point puntoSorgente, Iterator<Point> iter,
			boolean minimizzaAncheDeltaDistanza,
			int gammaDistanzaMassimaConsentita, boolean gammaDistanzaSubPattern) {

		PaintFrame frame = PaintFrame.getIstanza();

		boolean almenoUnAllineamentoMigliore = false;

		while (iter.hasNext()) {
			Point puntoPattern = iter.next();

			int valorePuntoSorgente = frame.image.getRGB(puntoSorgente.x,
					puntoSorgente.y);
			int valorePuntoPattern = pixelPattern[puntoPattern.x][puntoPattern.y];

			/*
			 * il punto potrebbe non soddisfare la condizione sulla delta
			 * distanza ed è quindi inutile controllare l'intero allineamento se
			 * voglio minimizzare anche la delta distanza.
			 */
			if (!minimizzaAncheDeltaDistanza
					|| SpazioRGB.getDistanza(valorePuntoSorgente,
							valorePuntoPattern) <= this.deltaDistanzaMassimaConsentitaGreen) {

				Allineamento allineamentoCorrente = new Allineamento(
						pixelPattern, puntoPattern, puntoSorgente);

				boolean allineamentoMigliore = allineamentoCorrente
						.isAllineamentoMigliore(gammaDistanzaMassimaConsentita,
								this.deltaDistanzaMassimaConsentitaGreen,
								minimizzaAncheDeltaDistanza);

				if (allineamentoMigliore) {
					aggiornaAllineamento(allineamentoCorrente);

					almenoUnAllineamentoMigliore = true;

				}
				/* altrimenti passo al prossimo allineamento */
			}
		}
		return almenoUnAllineamentoMigliore;
	}

	/**
	 * Aggiorna le distanze
	 * 
	 * @param allineamentoCorrente
	 * @return
	 */
	private void aggiornaAllineamento(Allineamento allineamentoCorrente) {
		/*
		 * aggiorno le distanze.
		 */
		this.deltaDistanzaMassimaConsentitaGreen = allineamentoCorrente.deltaDistanzaMaxTrovata;

		gammaDistanzaMassimaConsentitaPattern = allineamentoCorrente.gammaDistanza;

		puntoMigliore = allineamentoCorrente.getPuntoCentrale();
	}
}

/**
 * 
 * La classe Allineamento controlla l'allineamento generato dal punto
 * dell'immagine e da quello del pattern passati al costruttore. Inoltre
 * aggiorna le distanze minime trovate.
 * 
 * @author Sergio Bonfiglio
 * 
 */

class Allineamento {

	public int deltaDistanzaMaxTrovata;

	public int gammaDistanza;

	public Point topLeftCorner;

	private int[][] pixelPattern;

	public Allineamento(int[][] pixelPattern, Point puntoPattern,
			Point puntoSorgente) {

		deltaDistanzaMaxTrovata = -1;
		gammaDistanza = 0;

		this.pixelPattern = pixelPattern;

		topLeftCorner = new Point(puntoSorgente.x - puntoPattern.x,
				puntoSorgente.y - puntoPattern.y);

	}

	/**
	 * Restituisce il punto centrale dell'allineamento.
	 * 
	 * @return Restituisce il punto centrale dell'allineamento migliore.
	 */
	public Point getPuntoCentrale() {

		if (topLeftCorner != null) {
			int x = topLeftCorner.x + pixelPattern.length / 2;
			int y = topLeftCorner.y + pixelPattern.length / 2;

			return new Point(x, y);
		} else
			return null;
	}

	private boolean isOmega(int x, int y) {
		/*
		 * Se in questo allineamento la sorgente contiene zone che fanno parte
		 * di omega l'allineamento deve essere eliminato.
		 */

		PaintFrame frame = PaintFrame.getIstanza();

		if (frame.mask[x][y] == PaintFrame.OMEGA
				|| frame.mask[x][y] == PaintFrame.DELTAOMEGA) {

			return true;
		}
		return false;
	}

	/**
	 * 
	 * Calcola la distanza tra il pattern e il testo.
	 * 
	 * @param gammaDistanzaMinima -
	 *            La gamma-distanza minore trovata fin ora.
	 * @param deltaDistanzaMassimaConsentita -
	 *            La delta-distanza minore trovata fin ora.
	 * 
	 * @return true se ha trovato un allineamento con gammaDistanza minore di
	 *         quella passata come parametro, false altrimenti. Inoltre gli
	 *         allineamenti che si sovrappongono a zone contenenti omega nella
	 *         sorgente vengono considerati non validi e verrà restituito false.
	 */
	public boolean isAllineamentoMigliore(int gammaDistanzaMinima,
			int deltaDistanzaMassimaConsentita,
			boolean minimizzaAncheDeltaDistanza) {

		this.gammaDistanza = 0;
		boolean allineamentoValido = true;
		PaintFrame frame = PaintFrame.getIstanza();
		int dimensioneFinestra = pixelPattern.length;

		/*
		 * ciclo su ogni pixel dell'allineamento fino a che l'allineamento
		 * risulta valido
		 */
		for (int y = topLeftCorner.y, yP = 0; y < topLeftCorner.y
				+ dimensioneFinestra
				&& yP < dimensioneFinestra && allineamentoValido; y++, yP++) {
			for (int x = topLeftCorner.x, xP = 0; x < topLeftCorner.x
					+ dimensioneFinestra
					&& xP < dimensioneFinestra && allineamentoValido; x++, xP++) {

				int valPixelSorgente = frame.image.getRGB(x, y);
				int valPixelPattern = pixelPattern[xP][yP];

				if (isOmega(x, y))
					return false;

				/*
				 * I buchi del pattern non contribuiscono al conteggio della
				 * distanza
				 */
				if (valPixelPattern != PaintFrame.OMEGA) {

					int distanza = SpazioRGB.getDistanza(valPixelPattern,
							valPixelSorgente);

					this.gammaDistanza += distanza;

					allineamentoValido = isGammaDistanzaValida(gammaDistanzaMinima);

					aggiornaDeltaDistanzaMax(distanza);

					allineamentoValido = controllaLimiteDeltaDistanza(
							deltaDistanzaMassimaConsentita,
							minimizzaAncheDeltaDistanza);

				}// fine if (valPixelPattern != PaintFrame.OMEGA)
			}// fine ciclo x
		}// fine ciclo y

		return allineamentoValido;
	}

	/**
	 * @param deltaDistanzaMassimaConsentita
	 * @param minimizzaAncheDeltaDistanza
	 * @param allineamentoValido
	 * @return
	 */
	private boolean controllaLimiteDeltaDistanza(
			int deltaDistanzaMassimaConsentita,
			boolean minimizzaAncheDeltaDistanza) {
		// se la delta distanza massima trovata è maggiore di quella
		// minima scarto l'allineamento ma solo se sto minimizzando
		// anche la delta distanza
		if (this.deltaDistanzaMaxTrovata >= deltaDistanzaMassimaConsentita
				&& minimizzaAncheDeltaDistanza) {
			return false;
		}
		return true;
	}

	/**
	 * @param distanza
	 */
	private void aggiornaDeltaDistanzaMax(int distanza) {
		if (distanza > deltaDistanzaMaxTrovata) {
			deltaDistanzaMaxTrovata = distanza;
		}
	}

	/**
	 * @param gammaDistanzaMinima
	 * @param allineamentoValido
	 * @return
	 */
	private boolean isGammaDistanzaValida(int gammaDistanzaMinima) {
		if (this.gammaDistanza >= gammaDistanzaMinima) {
			return false;
		}
		return true;
	}
}
