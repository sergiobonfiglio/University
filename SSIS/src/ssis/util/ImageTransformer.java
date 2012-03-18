package ssis.util;

import java.awt.image.BufferedImage;

public class ImageTransformer {

	
	public static double[][] img2matrix(BufferedImage immagine) {
		
		immagine = util.ColorTransform.toGray(immagine);
		
		double[][] matriceImmagine = new double[immagine.getWidth()][immagine.getHeight()];

		// trasformo l'immagine in una matrice
		for (int r = 0; r < immagine.getWidth(); r++) {
			for (int c = 0; c < immagine.getHeight(); c++) {
				matriceImmagine[r][c] = immagine.getRaster().getSample(r, c, 0);
			}
		}
		return matriceImmagine;
	}
	
}
