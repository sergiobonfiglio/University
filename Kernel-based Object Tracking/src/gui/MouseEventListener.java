package gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Intercetta gli eventi generati dal mouse e imposta la regione selezionata di
 * conseguenza.
 */
public class MouseEventListener implements MouseListener, MouseMotionListener {

    GlassPane panel;

    public MouseEventListener(GlassPane panel) {
	this.panel = panel;
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
	panel.region.setCentro(e.getPoint());
	panel.repaint();
    }

    public void mouseReleased(MouseEvent e) {
	System.out.println("region: " + panel.region.getRaggioX() * 2 + "x"
		+ panel.region.getRy() * 2 + "==" + panel.region.getRaggioX()
		* 2 * panel.region.getRy() * 2);

    }

    public void mouseDragged(MouseEvent e) {
	if (panel.region.getCentro() != null) {
	    panel.region.setRaggioX(Math.abs(panel.region.getCentro().x
		    - e.getX()));
	    panel.region.setRaggioY(Math.abs(panel.region.getCentro().y
		    - e.getY()));
	    panel.repaint();

	}
    }

    public void mouseMoved(MouseEvent e) {
    }
}
