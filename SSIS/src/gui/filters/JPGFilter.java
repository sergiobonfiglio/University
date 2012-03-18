/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui.filters;

import java.io.File;

/**
 *
 * @author sergio
 */
public class JPGFilter extends ExtFilter
{
  public String getDescription()
  {
    return "JPG";
  }

  public boolean accept(File f)
  {
    return f.getName().toLowerCase().endsWith(".jpg")||
        f.getName().toLowerCase().endsWith(".jpeg")||f.isDirectory();
  }

  public String getExtension()
  {
    return "jpg";
  }
}