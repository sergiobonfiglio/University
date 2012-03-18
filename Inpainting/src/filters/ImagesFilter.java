package filters;

import java.io.File;

import javax.swing.filechooser.FileFilter;


public class ImagesFilter extends FileFilter {

	private JPGFilter jpgFilter = new JPGFilter();

	private PNGFilter pngFilter = new PNGFilter();

	private BMPFilter bmpFilter = new BMPFilter();

	public boolean accept(File f) {

		return jpgFilter.accept(f) || pngFilter.accept(f)
				|| bmpFilter.accept(f);
	}

	public String getDescription() {
		return "Immagini " + jpgFilter + ", " + pngFilter + ", " + bmpFilter;
	}

	public String toString() {
		return jpgFilter + ", " + pngFilter + ", " + bmpFilter;
	}
}
