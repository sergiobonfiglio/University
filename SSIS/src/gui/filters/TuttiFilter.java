/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui.filters;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author sergio
 */
public class TuttiFilter extends FileFilter
{
  public String getDescription()
  {
    return "Tutti i formati permessi";
  }

  public boolean accept(File f)
  {
    return f.getName().toLowerCase().endsWith(".png")||
        f.getName().toLowerCase().endsWith(".gif")||
        f.getName().toLowerCase().endsWith(".jpg")||
        f.getName().toLowerCase().endsWith(".jpeg")||
        f.isDirectory();
  }

}
