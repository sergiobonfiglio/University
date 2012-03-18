package filters;
import java.io.File;

import javax.swing.filechooser.FileFilter;

// Filtro per le immagini JPG
public class JPGFilter extends FileFilter
{
  public boolean accept(File f)
  {
    String str=f.getName().toLowerCase();
    return str.endsWith(".jpg")||str.endsWith(".jpeg")||f.isDirectory();
  }

  public String getDescription()
  {
    return "IMMAGINE JPG";
  }

  public String toString()
  {
    return "JPG";
  }
}
