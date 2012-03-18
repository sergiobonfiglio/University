package ssis.noise;

import java.awt.image.BufferedImage;
import ssis.util.*;

public class NoiseEstimator {

	/**
	 * Effettua un restauro dell'immagine e sottrae quest'ultima a quella di
	 * partenza al fine di ottenere una stima del rumore presente in
	 * quest'ultima.
	 * 
	 * @param immagine -
	 *            immagine di partenza rumorosa.
	 * @param raggio -
	 *            il raggio che determina la dimensione del kernel di
	 *            convoluzione.
	 * @return La matrice di double che nel punto (x, y) contiene il rumore
	 *         stimato nell'immagine di partenza nel punto (x, y).
	 */
	public static double[][] estimateNoise(BufferedImage immagine, int raggio,
			long interleaverSeed, int lunghezzaMsg) {

		AlphaTrimmedFilter f = new AlphaTrimmedFilter(raggio, interleaverSeed,
				lunghezzaMsg);
		BufferedImage imgFiltrata = f.filter(immagine, null);

		double[][] rumore = ImageTransformer.img2matrix(immagine);
		double[][] stimaOriginale = ImageTransformer.img2matrix(imgFiltrata);

		for (int r = 0; r < rumore.length; r++) {
			for (int c = 0; c < rumore[0].length; c++) {
				rumore[r][c] = rumore[r][c] - stimaOriginale[r][c];
			}
		}

		return rumore;
	}

	// TODO: test
	public static double[][] estimateNoise(BufferedImage immagine,
			BufferedImage immOriginale) {

		immOriginale = util.ColorTransform.toGray(immOriginale);

		double[][] rumore = ImageTransformer.img2matrix(immagine);

		double[][] stimaOriginale = ImageTransformer.img2matrix(immOriginale);

		for (int r = 0; r < rumore.length; r++) {
			for (int c = 0; c < rumore[0].length; c++) {
				rumore[r][c] = rumore[r][c] - stimaOriginale[r][c];
			}
		}

		// System.out.println("Differenza:");
		// stampa(rumore);

		return rumore;
	}

	private static void stampa(double[][] mat) {
		for (int c = 0; c < mat[0].length; c++) {
			for (int r = 0; r < mat.length; r++) {
				System.out.print(mat[r][c] + "\t");
			}
			System.out.println();
		}
		System.out.println("\n\n");
	}

}
