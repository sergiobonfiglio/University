package utilities;

import java.awt.Point;
import java.awt.Rectangle;

public class BiggestRect {

	//metodo di test
	private static void stampaMatrice(int[][] matrice) {
		for (int y = 0; y < matrice.length; y++) {
			for (int x = 0; x < matrice[0].length; x++) {
				char simbolo;
				if (matrice[x][y] == 1)
					simbolo = '1';
				else
					simbolo = '0';
				System.out.print(simbolo + " ");
			}
			System.out.println();
		}
	}

	/**
	 * Restituisce il più grande quadrato senza buchi nella matrice.
	 * 
	 * @param matrice
	 * @param valoreBuco
	 * @return
	 */
	public static Rectangle biggestRect(int[][] matrice, int valoreBuco) {
		/*
		 * Inizialmente vede se esiste un quadrato matrice.length - 1 x
		 * matrice.length -1 senza buchi e man mano diminuisce le dimensioni del
		 * quadrato cercato fino al ritrovamento del quadrato.
		 */
		int areaMassima = 0;
		int xIniziale = 0, yIniziale = 0, larghezzaQuadrato = 0;
		// dimensione pattern
		for (int i = matrice.length - 1; i > 0 && areaMassima < i * i; i--) {
			// allineamenti orizzontali
			for (int allX = 0; allX <= matrice.length - i; allX++) {
				// allineamenti verticali
				for (int allY = 0; allY <= matrice[0].length - i; allY++) {
					int areaCorrente = isConsentito(matrice, valoreBuco, allX,
							allX + i - 1, allY, allY + i - 1);
					if (areaCorrente > areaMassima) {
						areaMassima = areaCorrente;
						xIniziale = allX;
						yIniziale = allY;
						larghezzaQuadrato = i;
					}
				}// fine allineamento verticale

			}// fine allineamento orizzontale

		}

		Point puntoIniziale = new Point(xIniziale, yIniziale);

		Rectangle rect = new Rectangle(puntoIniziale.x, puntoIniziale.y,
				larghezzaQuadrato - 1, larghezzaQuadrato - 1);

		return rect;
	}

	/**
	 * Controlla che non ci siano buchi nella sotto matrice indicata dalle
	 * coordinate passate come parametro.
	 * 
	 * @param matrice
	 * @param valoreBuco
	 * @param daX -
	 *            la x di partenza.
	 * @param aX -
	 *            la prima x al di fuori della sotto matrice.
	 * @param daY -
	 *            la y di partenza.
	 * @param aY -
	 *            la prima y al di fuori della sotto matrice
	 * @return l'area in punti del quadrato più grande. -1 se nel quadrato sono
	 *         presenti buchi.
	 */
	private static int isConsentito(int[][] matrice, int valoreBuco, int daX,
			int aX, int daY, int aY) {

		int area = (aX - daX) * (aY - daY);
		for (int y = daY; y < aY; y++) {
			for (int x = daX; x < aX; x++) {
				if (matrice[x][y] == valoreBuco)
					return -1;
			}
		}
		return area;
	}

	public static void main(String[] args) {

		int valoreBuco = 1;

		int[][] matrice = new int[9][9];
		for (int c = 0; c < matrice[0].length; c++) {
			for (int r = 0; r < matrice.length; r++) {
				matrice[r][c] = (Math.random() > .80) ? valoreBuco : 0;
				System.out.print(matrice[r][c] + " ");
			}
			System.out.println();
		}
		System.out.println();

		System.out.println(biggestRect(matrice, valoreBuco));

	}

}
