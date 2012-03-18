package filters;


import java.io.File;

import javax.swing.filechooser.FileFilter;

// Filtro per le immagini PNG
public class PNGFilter extends FileFilter
{
  public boolean accept(File f)
  {
    return f.getName().toLowerCase().endsWith(".png")||f.isDirectory();
  }

  public String getDescription()
  {
    return "IMMAGINE PNG";
  }

  public String toString()
  {
    return "PNG";
  }
}
