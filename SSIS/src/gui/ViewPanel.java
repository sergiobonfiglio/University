/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 *
 * @author sergio
 */
public class ViewPanel extends JPanel {
	private Image img;
	private double zoom = 1d;
	private Object rh = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;

	public ViewPanel(Image img) {
		this.img = img;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, rh);

		g2.scale(1.0 * zoom, 1.0 * zoom);

		if (img != null)
			g2.drawImage(img, 0, 0, null);

	}

	public void setImage(Image img) {
		this.img = img;
		setPreferredSize(new Dimension((int) (img.getWidth(null) * zoom),
				(int) (img.getHeight(null) * zoom)));
		repaint();
		revalidate();
	}

	public void clear() {
		this.img = null;
		repaint();
	}

	public Image getImage() {
		return img;
	}

	public void setZoom(double zoom, Object _rh) {
		this.zoom = zoom;
		rh = _rh;
		setPreferredSize(new Dimension((int) (img.getWidth(null) * zoom),
				(int) (img.getHeight(null) * zoom)));
		repaint();
		revalidate();

	}
}
