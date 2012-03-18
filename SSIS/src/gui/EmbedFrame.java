/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author sergio
 */
public class EmbedFrame extends JFrame{


    public EmbedFrame() {
        
        this.add(new EmbedPanel(this));
        this.setTitle("Inserisci messaggio");
        this.pack();
                
        
    }
    
    
   
}
