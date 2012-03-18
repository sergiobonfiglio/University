package filters;
import java.io.File;

import javax.swing.filechooser.FileFilter;

public class BMPFilter extends FileFilter {

    public boolean accept(File f) {
        return f.getName().toLowerCase().endsWith(".bmp")||f.isDirectory();
    }

    public String getDescription() {
        return "bitmap 24 bit (*.bmp)";
    }

    public String toString() {
        return "BMP";
    }

}
