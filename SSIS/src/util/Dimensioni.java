package util;

import java.awt.image.BufferedImage;

public class Dimensioni {
	
	public static long capienzaImmagineChar(BufferedImage immagine) {
		return (immagine.getWidth() * immagine.getHeight())/16;
	}
	
	public static long capienzaImmagineBit(BufferedImage immagine) {
		return immagine.getWidth() * immagine.getHeight();
	}
	

	public static String rendiLeggibile(long cap) {
		return rendiLeggibile(cap, 1024);
	}

	// pubblico:chiamato anche dalla classe steganografo
	public static String rendiLeggibile(long cap, int div) {
		double capf = cap;
		int cont = 0;
		while (capf >= div && cont < 4) {
			capf /= div;
			cont++;
		}
		String m = "";
		if (cont == 0)
			m = "";
		else if (cont == 1)
			m = " K";
		else if (cont == 2)
			m = " M";
		else if (cont == 3)
			m = " G";

		capf = tronca(capf, 2);
		String str = new Double(capf).toString() + "" + m;
		return str;
	}

	private static double tronca(double i, int n) {
		int j = (int) (i * Math.pow(10, n));
		return j / Math.pow(10, n);
	}
}
