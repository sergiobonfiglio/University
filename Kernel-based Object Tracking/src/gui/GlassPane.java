package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import target.Region;

/**
 * Il pannello trasparente che viene sovrapposto al video per evidenziare la
 * regione di interesse.
 */
public class GlassPane extends JPanel {

    MouseEventListener listener;

    Region region;

    BufferedImage overlay;

    public GlassPane() {

	listener = new MouseEventListener(this);
	addMouseListener(listener);
	addMouseMotionListener(listener);

	region = new Region();
	this.setOpaque(false);

    }

    @Override
    public void setSize(Dimension d) {
	super.setSize(d);
    }

    public void setRegion(Region region) {
	this.region = region;
    }

    public void setCentro(Point centro) {

	region.setCentro(centro);
    }

    public void setRx(int rx) {
	region.setRaggioX(rx);
    }

    public void setRy(int ry) {
	region.setRaggioY(ry);
    }

    public Region getRegion() {
	return region;
    }

    @Override
    public void paint(Graphics g) {
	super.paint(g);

	if (region != null && region.getCentro() != null) {

	    GraphicsConfiguration gc = getGraphicsConfiguration();
	    overlay = gc.createCompatibleImage(this.getSize().width, this
		    .getSize().height, Transparency.TRANSLUCENT);
	    Graphics graphics = overlay.getGraphics();
	    graphics.setColor(Color.magenta);
	    graphics.drawOval(region.getCentro().x - region.getRaggioX(),
		    region.getCentro().y - region.getRy(),
		    region.getRaggioX() * 2, region.getRy() * 2);

	    Graphics2D g12 = (Graphics2D) g;
	    g12.drawImage(overlay, 0, 0, null);

	}

    }

}
