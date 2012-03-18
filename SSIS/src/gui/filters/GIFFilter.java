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
public class GIFFilter extends ExtFilter
{
  public String getDescription()
  {
    return "GIF";
  }

  public boolean accept(File f)
  {
    return f.getName().toLowerCase().endsWith(".gif")||f.isDirectory();
  }

  public String getExtension()
  {
    return "gif";
  }
}
