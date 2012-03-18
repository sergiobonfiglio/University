import gui.FramePrincipale;

import javax.swing.*;

//import com.sun.java.swing.plaf.motif.MotifLookAndFeel;
//import javax.swing.plaf.metal.MetalLookAndFeel;
//import javax.swing.UIManager.LookAndFeelInfo;


class Main{
  public static void main(String[] args) {	
  	/*	javax.swing.UIManager$LookAndFeelInfo[Metal javax.swing.plaf.metal.MetalLookAndFeel]
  	 *	javax.swing.UIManager$LookAndFeelInfo[CDE/Motif com.sun.java.swing.plaf.motif.MotifLookAndFeel]
  	 *	javax.swing.UIManager$LookAndFeelInfo[Mac OS X apple.laf.AquaLookAndFeel]
  	 * */
  	//UIManager.getDefaults()
  	//try{UIManager.setLookAndFeel(new MetalLookAndFeel());}catch(Exception e){}
  	/*LookAndFeelInfo[] a= UIManager.getInstalledLookAndFeels();
  	for (int i=0; i<a.length; i++)
  		System.out.println(a[i]);*/
  	
	  gui.FramePrincipale fp =  FramePrincipale.getInstance();
      fp.setVisible(true);
  }
}