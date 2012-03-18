package gui.filters;

import gui.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;

public class SizeFilter extends FileFilter {

    long size;

    public SizeFilter(long size) {
        this.size = size;
    }

    public String getDescription() {
        return "File minori di " + util.Dimensioni.rendiLeggibile(size) + "B";
    }

    public boolean accept(File f) {
        return (f.length() + 4) < size || f.isDirectory();
    }

    public String getExtension() {
        return "";
    }
}

