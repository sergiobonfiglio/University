import hashForwardList.HashForwardList;
import hashForwardList.Nodo;

import java.awt.Point;
import java.util.Iterator;

import utilities.SpazioRGB;

/**
 * Questa classe prende in input un istanza della classe SubPattern e ricerca la
 * migliore corrispondenza all'interno dell'immagine selezionata. I pixel del
 * pattern che non fanno parte del sotto-pattern vengono controllati solo nel
 * caso in cui vi sia un allineamento valido del sotto-pattern.
 * 
 * @author Sergio Bonfiglio
 * 
 */
public class RicercatoreSubPattern {

	private SubPattern patternRicercato;

	private Point puntoMigliore;

	private int deltaDistanzaMassimaConsentitaRGB;

	private int gammaDistanzaMassimaConsentitaPattern;

	private int gammaDistanzaMassimaConsentitaSubPattern;

	public static final byte DA_VICINO = 0;

	public static final byte DALL_INIZIO = 1;

	public RicercatoreSubPattern(SubPattern patternRicercato) {
		this.patternRicercato = patternRicercato;

		puntoMigliore = null;

		deltaDistanzaMassimaConsentitaRGB = 256;
		gammaDistanzaMassimaConsentitaPattern = Integer.MAX_VALUE;
		gammaDistanzaMassimaConsentitaSubPattern = Integer.MAX_VALUE;

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
	 * @return Il Point al centro dell'allineamento migliore.
	 */

	public Point ricercaMiglioreCorrispondenza(byte strategia,
			boolean minimizzaAncheDeltaDistanza) {
		if (strategia == DA_VICINO) {
			return ricercaMiglioreCorrispondenzaDaVicino(minimizzaAncheDeltaDistanza);
		} else if (strategia == DALL_INIZIO) {
			return ricercaMiglioreCorrispondenzaDallInizio(minimizzaAncheDeltaDistanza);
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
	 * @return Il Point al centro dell'allineamento migliore.
	 */
	private Point ricercaMiglioreCorrispondenzaDaVicino(
			boolean minimizzaAncheDeltaDistanza) {

		PaintFrame frame = PaintFrame.getIstanza();

		puntoMigliore = null;

		Point puntoMaxPriorita = patternRicercato.centroPattern;

		int dimensioneFinestra = patternRicercato.widthSubPattern;

		/*
		 * Prendo un punto ogni m (dimensione del pattern ricercato) sia in
		 * orizzontale che in verticale. Inizio dalla riga del punto con più
		 * alta priorità (vicinanza delle zone simili) e ciclo fino alla fine
		 * dell'immagine. Poi riprendo dall'inizio.
		 */
		int yIniziale = Math.max(puntoMaxPriorita.y, frame.window);
		yIniziale = Math.min(yIniziale, frame.h - frame.window);

		for (int y = yIniziale; y < frame.h - frame.window + 1
				&& gammaDistanzaMassimaConsentitaPattern > 0; y += dimensioneFinestra) {
			for (int x = frame.window; x < frame.w - frame.window + 1
					&& gammaDistanzaMassimaConsentitaPattern > 0; x += dimensioneFinestra) {
				ricercaMiglioreCorrispondenzaPuntuale(frame, x, y,
						minimizzaAncheDeltaDistanza);

			}// fine ciclo sulle x dell'immagine
		}// fine ciclo sulle y dell'immagine

		/*
		 * Poi ricomincio dall'inizio fino al punto da cui sono partito
		 */
		for (int y = frame.window; y < yIniziale
				&& gammaDistanzaMassimaConsentitaPattern > 0; y += dimensioneFinestra) {
			for (int x = frame.window; x < frame.w - frame.window + 1
					&& gammaDistanzaMassimaConsentitaPattern > 0; x += dimensioneFinestra) {
				ricercaMiglioreCorrispondenzaPuntuale(frame, x, y,
						minimizzaAncheDeltaDistanza);

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
	 * @return Il Point al centro dell'allineamento migliore.
	 */
	private Point ricercaMiglioreCorrispondenzaDallInizio(
			boolean minimizzaAncheDeltaDistanza) {

		PaintFrame frame = PaintFrame.getIstanza();

		puntoMigliore = null;

		/*
		 * Prendo un punto ogni m (dimensione del pattern ricercato) sia in
		 * orizzontale che in verticale
		 */

		int dimensioneFinestra = frame.window;

		for (int y = frame.window; y < frame.h - frame.window + 1
				&& gammaDistanzaMassimaConsentitaPattern > 0; y += dimensioneFinestra) {
			for (int x = dimensioneFinestra; x < frame.w - frame.window + 1
					&& gammaDistanzaMassimaConsentitaPattern > 0; x += dimensioneFinestra) {

				ricercaMiglioreCorrispondenzaPuntuale(frame, x, y,
						minimizzaAncheDeltaDistanza);

			}// fine ciclo sulle x dell'immagine
		}// fine ciclo sulle y dell'immagine

		return puntoMigliore;
	}

	/**
	 * Ricerca la migliore corrispondenza tra tutti gli allineamenti col punto
	 * dell'immagine (x, y) che rientrano nella delta distanza massima.
	 * 
	 * @param frame
	 * @param x -
	 *            la x del punto dell'immagine di cui testare gli allineamenti.
	 * @param y -
	 *            la y del punto dell'immagine di cui testare gli allineamenti.
	 * @param minimizzaAncheDeltaDistanza -
	 *            Se true minimizzerà sia la delta che la gamma distanza. Se
	 *            false minimizzerà esclusivamente la gamma distanza.
	 * @return true se è stato trovato almeno un allineamento migliore.
	 */
	private boolean ricercaMiglioreCorrispondenzaPuntuale(PaintFrame frame,
			int x, int y, boolean minimizzaAncheDeltaDistanza) {

		boolean almenoUnAllineamentoMigliore = false;
		/*
		 * Se il punto fa parte della zona sconosciuta lo posso saltare subito
		 * perché gli allineamenti con esso non mi restituirebbero un pattern
		 * incompleto e non potrei quindi sostituirlo nella zona selezionata.
		 */
		if (frame.mask[x][y] == PaintFrame.SOURCE) {

			int valorePixelImmagine = frame.image.getRGB(x, y);

			int deltaDistanzaMassimaCanale = SpazioRGB.getDeltaDistanzaCanale(
					deltaDistanzaMassimaConsentitaRGB, SpazioRGB.VERDE);

			int valoreCanale = SpazioRGB.getCanale(valorePixelImmagine,
					SpazioRGB.VERDE);
			// considero la distanza come provocata interamente dal canale verde
			int limiteSup = Math.min(valoreCanale + deltaDistanzaMassimaCanale,
					255);
			int limiteInf = Math.max(valoreCanale - deltaDistanzaMassimaCanale,
					0);

			HashForwardList hashValoriLista = patternRicercato
					.getHashValoriPixelSubPattern();
			/*
			 * controllo solo il verde: dal pixel con tutto l'errore sul verde
			 * in negativo a quello con tutto l'errore sul verde in positivo.
			 */
			for (int i = limiteInf; i < limiteSup + 1; i++) {
				Nodo nodo = (Nodo) hashValoriLista.get(i);

				// se ci sono pixel con valore i controllo gli allineamenti da
				// essi generati
				if (nodo.lista != null) {
					Iterator iter = nodo.lista.iterator();
					almenoUnAllineamentoMigliore = almenoUnAllineamentoMigliore
							|| calcolaDistanzeAllineamentiSubPattern(
									patternRicercato, new Point(x, y), iter,
									minimizzaAncheDeltaDistanza);
				} else {
					// salto alla prossima lista
					i = nodo.nearestList - 1;
				}
			}// fine ciclo punti che soddisfano la
			// delta-distanza

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
	private boolean calcolaDistanzeAllineamentiSubPattern(SubPattern pattern,
			Point puntoSorgente, Iterator iter,
			boolean minimizzaAncheDeltaDistanza) {

		PaintFrame frame = PaintFrame.getIstanza();

		boolean almenoUnAllineamentoMigliore = false;

		// ciclo sui punti del sub pattern che soddisfano la delta distanza
		while (iter.hasNext()) {
			Point puntoSubPattern = (Point) iter.next();

			int valorePuntoSorgente = frame.image.getRGB(puntoSorgente.x,
					puntoSorgente.y);
			int valorePuntoPattern = pattern.pixelSubPattern[puntoSubPattern.x][puntoSubPattern.y];

			/*
			 * il punto potrebbe non soddisfare la condizione sulla delta
			 * distanza ed è quindi inutile controllare l'intero allineamento se
			 * voglio minimizzare anche la delta distanza.
			 */
			if (!minimizzaAncheDeltaDistanza
					|| SpazioRGB.getDistanza(valorePuntoSorgente,
							valorePuntoPattern) <= this.deltaDistanzaMassimaConsentitaRGB) {

				AllineamentoSubPattern allineamentoCorrente = new AllineamentoSubPattern(
						this.patternRicercato, puntoSubPattern, puntoSorgente);

				boolean allineamentoMigliore = allineamentoCorrente
						.calcolaDistanzeAllineamento(
								gammaDistanzaMassimaConsentitaSubPattern,
								gammaDistanzaMassimaConsentitaPattern,
								this.deltaDistanzaMassimaConsentitaRGB,
								minimizzaAncheDeltaDistanza);

				if (allineamentoMigliore) {
					/*
					 * Ho trovato un allineamento migliore: aggiorno le
					 * distanze.
					 */
					almenoUnAllineamentoMigliore = true;
					this.deltaDistanzaMassimaConsentitaRGB = allineamentoCorrente.deltaDistanza;

					this.gammaDistanzaMassimaConsentitaPattern = allineamentoCorrente.gammaDistanzaPattern;
					this.gammaDistanzaMassimaConsentitaSubPattern = allineamentoCorrente.gammaDistanzaSubPattern;

					puntoMigliore = allineamentoCorrente
							.getPuntoCentralePattern();

				}
			}
		}
		return almenoUnAllineamentoMigliore;
	}
}

/**
 * 
 * La classe AllineamentoSubPattern controlla l'allineamento generato dal punto
 * dell'immagine e da quello del sotto-pattern passati al costruttore. Inoltre
 * aggiorna le distanze minime trovate.
 * 
 * @author Sergio Bonfiglio
 * 
 */
class AllineamentoSubPattern {

	public int deltaDistanza;

	public int gammaDistanzaPattern;

	public int gammaDistanzaSubPattern;

	public Point topLeftCornerPattern;

	public Point topLeftCornerAllineamentoSubPattern;

	private int[][] pixelPattern;

	private int[][] pixelSubPattern;

	SubPattern pattern;

	public AllineamentoSubPattern(SubPattern pattern, Point puntoSubPattern,
			Point puntoSorgente) {

		deltaDistanza = -1;
		gammaDistanzaPattern = 0;
		gammaDistanzaSubPattern = 0;

		this.pattern = pattern;

		this.pixelPattern = pattern.pixelPattern;
		this.pixelSubPattern = pattern.pixelSubPattern;

		topLeftCornerAllineamentoSubPattern = new Point(puntoSorgente.x
				- puntoSubPattern.x, puntoSorgente.y - puntoSubPattern.y);
		// la posizione del punto iniziale del pattern grande in questo
		// allineamento
		topLeftCornerPattern = new Point(topLeftCornerAllineamentoSubPattern.x
				- pattern.topLeftCornerSubPattern.x,
				topLeftCornerAllineamentoSubPattern.y
						- pattern.topLeftCornerSubPattern.y);

	}

	/**
	 * Restituisce il punto centrale dell'allineamento.
	 * 
	 * @return Restituisce il punto centrale dell'allineamento migliore.
	 */
	public Point getPuntoCentralePattern() {

		if (topLeftCornerPattern != null) {
			int x = topLeftCornerPattern.x + pixelPattern.length / 2;
			int y = topLeftCornerPattern.y + pixelPattern.length / 2;

			return new Point(x, y);
		} else
			return null;
	}

	/**
	 * 
	 * Calcola la distanza tra il pattern e il testo. Prima viene controllata la
	 * distanza del sotto-pattern: se essa è al di sotto delle soglie minime
	 * trovate fin ora viene controllato anche il resto del pattern.
	 * 
	 * @param gammaDistanzaMinima -
	 *            La gamma-distanza minore trovata fin ora.
	 * @param deltaDistanzaMinima -
	 *            La delta-distanza minore trovata fin ora.
	 * 
	 * @return true se ha trovato un allineamento con gammaDistanza minore di
	 *         quella passata come parametro, false altrimenti. Inoltre gli
	 *         allineamenti che si sovrappongono a zone contenenti omega nella
	 *         sorgente vengono considerati non validi e verrà restituito false.
	 */
	public boolean calcolaDistanzeAllineamento(
			int gammaDistanzaMinimaSubPattern, int gammaDistanzaMinimaPattern,
			int deltaDistanzaMinima, boolean minimizzaAncheDeltaDistanza) {
		this.gammaDistanzaPattern = 0;
		this.gammaDistanzaSubPattern = 0;

		boolean allineamentoValido = true;
		PaintFrame frame = PaintFrame.getIstanza();
		int dimensioneFinestra = pixelSubPattern.length;

		/*
		 * ciclo su ogni pixel del sub pattern finché l'allineamento rimane
		 * valido.
		 */
		for (int y = topLeftCornerAllineamentoSubPattern.y, yP = 0; y < topLeftCornerAllineamentoSubPattern.y
				+ dimensioneFinestra
				&& allineamentoValido; y++, yP++) {
			for (int x = topLeftCornerAllineamentoSubPattern.x, xP = 0; x < topLeftCornerAllineamentoSubPattern.x
					+ dimensioneFinestra
					&& allineamentoValido; x++, xP++) {

				int valPixelSorgente = frame.image.getRGB(x, y);
				int valPixelSubPattern = pixelSubPattern[xP][yP];

				if (frame.mask[x][y] == PaintFrame.OMEGA
						|| frame.mask[x][y] == PaintFrame.DELTAOMEGA) {
					/*
					 * Se in questo allineamento la sorgente contiene zone che
					 * fanno parte di omega l'allineamento deve essere
					 * eliminato.
					 */
					allineamentoValido = false;

				} else {

					int distanza = SpazioRGB.getDistanza(valPixelSubPattern,
							valPixelSorgente);

					this.gammaDistanzaSubPattern += distanza;

					if (gammaDistanzaSubPattern >= gammaDistanzaMinimaSubPattern) {
						allineamentoValido = false;
					}

					if (distanza > deltaDistanza) {
						deltaDistanza = distanza;
					}

					// se la delta distanza massima trovata è maggiore di quella
					// minima scarto l'allineamento ma solo se sto minimizzando
					// anche la delta distanza
					if (this.deltaDistanza >= deltaDistanzaMinima
							&& minimizzaAncheDeltaDistanza) {
						allineamentoValido = false;
					}
				}
			}// fine ciclo x
		}// fine ciclo y

		if (allineamentoValido == true) {
			/*
			 * se l'allineamento del sub pattern è valido allora controllo tutto
			 * il resto del pattern.
			 */
			dimensioneFinestra = pixelPattern.length;
			/*
			 * la gamma distanza del pattern intero è uguale a quello del
			 * sotto-pattern più la somma delle distanze dei pixel che
			 * appartengono esclusivamente al pattern intero. Quindi viene
			 * inizializzata con il valore di gamma distanza del sotto-pattern e
			 * nel ciclo vengono sommate le distanze trovate.
			 */
			this.gammaDistanzaPattern = this.gammaDistanzaSubPattern;
			for (int y = topLeftCornerPattern.y, yP = 0; y < topLeftCornerPattern.y
					+ dimensioneFinestra
					&& allineamentoValido; y++, yP++) {
				for (int x = topLeftCornerPattern.x, xP = 0; x < topLeftCornerPattern.x
						+ dimensioneFinestra
						&& allineamentoValido; x++, xP++) {
					if (!pattern.areaSubPattern.contains(xP, yP)) {
						int valPixelSorgente = frame.image.getRGB(x, y);
						int valPixelPattern = pixelPattern[xP][yP];

						if (frame.mask[x][y] != PaintFrame.SOURCE) {
							allineamentoValido = false;
						} else if (valPixelPattern != PaintFrame.OMEGA) {
							int distanza = SpazioRGB.getDistanza(
									valPixelPattern, valPixelSorgente);
							this.gammaDistanzaPattern += distanza;

							if (gammaDistanzaPattern >= gammaDistanzaMinimaPattern) {
								allineamentoValido = false;
							}
							if (distanza > deltaDistanza) {
								deltaDistanza = distanza;
							}
							// se la delta distanza massima trovata è maggiore
							// di quella minima scarto l'allineamento ma solo se
							// sto minimizzando anche la delta distanza
							if (this.deltaDistanza >= deltaDistanzaMinima
									&& minimizzaAncheDeltaDistanza) {
								allineamentoValido = false;
							}

						}
					}
				}// fine ciclo x
			}// fine ciclo y

		}// fine if(allineamentoValido==true)

		return allineamentoValido;
	}
}
