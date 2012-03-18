import hashForwardList.HashForwardList;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import utilities.SpazioRGB;

/**
 * Questa classe costruisce il pattern dato dal suo punto centrale, lo conserva
 * e lo preprocessa.
 * 
 * @author Sergio Bonfiglio
 * 
 */

public class Pattern {

	private PaintFrame frame = PaintFrame.getIstanza();

	/**
	 * Tabella hash dei punti indicizzata tramite il loro valore.
	 */
	private HashForwardList hashValoriPixel;

	/**
	 * Lista dei pixel che fanno parte della zona sconosciuta del pattern.
	 */
	private LinkedList listaPixelOmega;

	/**
	 * La Matrice che contiene i valori dei pixel appartenenti al pattern.
	 */
	public int[][] pixelPattern;

	/**
	 * Punto centrale del pattern.
	 */
	public Point centroPattern;

	@SuppressWarnings("unchecked")
	public Pattern(Point centroPattern, int lato) {
		this.centroPattern = centroPattern;
		// Setto la capacità iniziale in modo che non debba essere incrementata
		// dinamicamente .
		hashValoriPixel = new HashForwardList((int) (256 * 0.75));

		listaPixelOmega = new LinkedList();

		pixelPattern = new int[lato][lato];
		int path = lato / 2;

		int width = centroPattern.x + path + 1;
		int height = centroPattern.y + path + 1;

		int xP = 0, yP = 0;
		for (int y = centroPattern.y - path; y < height; y++, yP++, xP = 0) {
			for (int x = centroPattern.x - path; x < width; x++, xP++) {
				if (frame.mask[x][y] == PaintFrame.OMEGA
						|| frame.mask[x][y] == PaintFrame.DELTAOMEGA) {
					pixelPattern[xP][yP] = PaintFrame.OMEGA;
					listaPixelOmega.add(new Point(xP, yP));
				} else {
					int valorePixel = frame.image.getRGB(x, y);
					// costruisco la matrice dei valori
					pixelPattern[xP][yP] = valorePixel;

					// metto i valori del verde nella tabella hash
					int valoreVerde = SpazioRGB.getCanale(valorePixel,
							SpazioRGB.VERDE);
					hashValoriPixel.put(valoreVerde, new Point(xP, yP));

				}

			}
		}

		//costruisce la struttura dati
		hashValoriPixel.buildHashForwardList();

	}

	/**
	 * @return the hashValoriPixel
	 */
	public HashForwardList getHashValoriPixel() {
		return hashValoriPixel;
	}

	/**
	 * @return the listaPixelOmega
	 */
	public List getListaPixelOmega() {
		return listaPixelOmega;
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
