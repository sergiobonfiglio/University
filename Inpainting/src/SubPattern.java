import hashForwardList.HashForwardList;

import java.awt.Point;
import java.awt.Rectangle;

import utilities.SpazioRGB;

/**
 * Questa classe costruisce il sotto-pattern dato dal punto centrale del
 * pattern. Il sotto-pattern è il più grande quadrato senza buchi presente
 * all'interno del pattern.
 * 
 * @author Sergio Bonfiglio
 * 
 */
public class SubPattern {

	private PaintFrame frame = PaintFrame.getIstanza();

	/**
	 * Tabella hash dei punti del sotto-pattern indicizzata tramite il loro
	 * valore.
	 */
	private HashForwardList hashValoriPixelSubPattern;

	/**
	 * La matrice che contiene i valori dei pixel appartenenti al pattern.
	 */
	public int[][] pixelPattern;

	/**
	 * La matrice che contiene i valori dei pixel appartenenti al sotto-pattern.
	 */
	public int[][] pixelSubPattern;

	/**
	 * Il punto più in alto a sinistra del sotto-pattern. Le coordinate sono
	 * relative al punto più in alto a sinistra del pattern intero.
	 */
	public Point topLeftCornerSubPattern;

	/**
	 * Larghezza del sotto-pattern.
	 */
	public int widthSubPattern;

	/**
	 * Quadrato rappresentante il sotto-pattern.
	 */
	public Rectangle areaSubPattern;

	/**
	 * Il punto centrale del pattern intero.
	 */
	public Point centroPattern;

	public SubPattern(Point centroPattern, int lato) {
		this.centroPattern = centroPattern;
		// Setto la capacità iniziale in modo che non debba essere incrementata
		// dinamicamente.
		hashValoriPixelSubPattern = new HashForwardList((int) (256 * 0.75));

		pixelPattern = new int[lato][lato];
		int path = lato / 2;

		int width = centroPattern.x + path + 1;
		int height = centroPattern.y + path + 1;

		// costruisco pattern intero
		int xP = 0, yP = 0;
		for (int y = centroPattern.y - path; y < height; y++, yP++, xP = 0) {
			for (int x = centroPattern.x - path; x < width; x++, xP++) {
				if (frame.mask[x][y] == PaintFrame.OMEGA
						|| frame.mask[x][y] == PaintFrame.DELTAOMEGA) {
					pixelPattern[xP][yP] = PaintFrame.OMEGA;
				} else {
					int valorePixel = frame.image.getRGB(x, y);
					pixelPattern[xP][yP] = valorePixel;
				}

			}
		}

		// costruisco pattern piccolo
		Rectangle quadrato = utilities.BiggestRect.biggestRect(pixelPattern,
				PaintFrame.OMEGA);
		this.topLeftCornerSubPattern = quadrato.getLocation();
		this.widthSubPattern = (int) quadrato.getWidth();

		this.pixelSubPattern = new int[widthSubPattern][widthSubPattern];
		this.areaSubPattern = quadrato;

		/*
		 * Solo i valori del sotto-pattern vengono inseriti nella tabella hash
		 * poiché sono gli unici ad essere allineati.
		 */
		xP = 0;
		yP = 0;
		for (int y = topLeftCornerSubPattern.y; y < topLeftCornerSubPattern.y
				+ widthSubPattern; y++, yP++, xP = 0) {
			for (int x = topLeftCornerSubPattern.x; x < topLeftCornerSubPattern.x
					+ widthSubPattern; x++, xP++) {
				pixelSubPattern[xP][yP] = pixelPattern[x][y];

				// metto i valori del verde nella tabella hash
				int valoreVerde = SpazioRGB.getCanale(pixelSubPattern[xP][yP],
						SpazioRGB.VERDE);
				hashValoriPixelSubPattern.put(valoreVerde, new Point(xP, yP));
			}
		}

		this.hashValoriPixelSubPattern.buildHashForwardList();

	}

	/**
	 * @return the hashValoriPixelPatternRidotto
	 */
	public HashForwardList getHashValoriPixelSubPattern() {
		return hashValoriPixelSubPattern;
	}

	public String toString() {
		String str = "";

		for (int y = 0; y < pixelPattern.length; y++) {
			for (int x = 0; x < pixelPattern.length; x++) {
				str += pixelPattern[x][y] + "\t";
			}
			str += "\n";
		}

		return str;
	}
}
