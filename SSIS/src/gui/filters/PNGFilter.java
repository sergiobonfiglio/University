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
public class PNGFilter extends ExtFilter
{

  public String getDescription()
  {
    return "PNG";
  }

  public boolean accept(File f)
  {
    return f.getName().toLowerCase().endsWith(".png")||f.isDirectory();
  }

  public String getExtension()
  {
    return "png";
  }

}
