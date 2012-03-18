package utilities;

/**
 * Questa classe implementa alcuni metodi relativi al recupero del valore di un
 * particolare canale o al calcolo della distanza tra due punti.
 * 
 * @author Sergio Bonfiglio
 * 
 */
public class SpazioRGB {

	public static final byte ROSSO = 0;

	public static final byte VERDE = 1;

	public static final byte BLU = 2;

	/**
	 * Calcola la distanza tra due punti come distanza geometrica pesata.
	 * 
	 * @param valoreRGB1
	 * @param valoreRGB2
	 * @return la radice quadrata della somma delle differenze di valore tra i
	 *         vari canali al quadrato moltiplicate per il loro peso nel sistema
	 *         visivo umano.
	 */
	public static int getDistanza(int valoreRGB1, int valoreRGB2) {
		int rosso1 = (valoreRGB1 & 0x00FF0000) >> 16;
		int verde1 = (valoreRGB1 & 0x0000FF00) >> 8;
		int blu1 = (valoreRGB1 & 0x000000FF);
		int rosso2 = (valoreRGB2 & 0x00FF0000) >> 16;
		int verde2 = (valoreRGB2 & 0x0000FF00) >> 8;
		int blu2 = (valoreRGB2 & 0x000000FF);
		// graycolor=(int) ((0.3*r)+(0.59*g)+(0.11*b));
		int distanza = (int) (Math.sqrt(Math.pow((rosso1 - rosso2), 2) * 0.3
				+ Math.pow((verde1 - verde2), 2) * 0.59
				+ Math.pow((blu1 - blu2), 2) * 0.11));

		return distanza;
	}

	/**
	 * Calcola il valore di un singolo canale.
	 * 
	 * @param valoreRGB
	 * @param canale
	 * @return il valore di un singolo canale.
	 */
	public static int getCanale(int valoreRGB, byte canale) {
		if (canale == ROSSO) {
			return (valoreRGB & 0x00FF0000) >> 16;
		} else if (canale == VERDE) {
			return (valoreRGB & 0x0000FF00) >> 8;
		} else if (canale == BLU) {
			return (valoreRGB & 0x000000FF);
		} else
			return -1;
	}

	/**
	 * Calcola la formula inversa della distanza assumendo che tutta la distanza
	 * sia dovuta ad un particolare canale.
	 * 
	 * @param deltaDistanza
	 * @param canale
	 * @return la radice quadrata della distanza al quadrato divisa per il peso
	 *         del canale.
	 */
	public static int getDeltaDistanzaCanale(int deltaDistanza, byte canale) {
		if (canale == SpazioRGB.ROSSO) {
			return (int) Math.sqrt(Math.pow(deltaDistanza, 2) / 0.3);
		} else if (canale == SpazioRGB.VERDE) {
			return (int) Math.sqrt(Math.pow(deltaDistanza, 2) / 0.59);
		} else if (canale == SpazioRGB.BLU) {
			return (int) Math.sqrt(Math.pow(deltaDistanza, 2) / 0.11);
		} else
			return -1;
	}

}
