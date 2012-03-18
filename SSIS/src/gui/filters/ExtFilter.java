/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui.filters;

import javax.swing.filechooser.FileFilter;

/**
 *
 * @author sergio
 */
public abstract class ExtFilter extends FileFilter
{

  abstract public String getExtension();
}
