package test;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class prova {

	
	public static BufferedImage settaBanda(BufferedImage image, int banda, int valore) {
		
		WritableRaster raster = image.getRaster();
		for (int x = 0; x < raster.getWidth() ; x++) {
			for (int y = 0; y < raster.getHeight(); y++) {
				raster.setSample(x, y, banda, valore);
			}
		}
		return image;
	}
	
}
