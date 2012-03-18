package util;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

public class ColorTransform {
	
	public static BufferedImage toRGB(BufferedImage immagine){
		return toColorSpace(immagine, ColorSpace.CS_sRGB);
	}
	
	public static BufferedImage toGray(BufferedImage immagine) {
		return toColorSpace(immagine, ColorSpace.CS_GRAY);
	}
	
	public static BufferedImage toColorSpace(BufferedImage immagine, int colorSpaceType) {
		if (immagine != null && immagine.getColorModel().getColorSpace().getType() != colorSpaceType) {
			ColorSpace space = ColorSpace.getInstance(colorSpaceType);
			ColorConvertOp operation = new ColorConvertOp(space, null);
			immagine = operation.filter(immagine, null);
		}
		return immagine;
	}
	
}
