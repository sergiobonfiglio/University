package gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * La Classe ImagePanel estende la classe JPanel e ridefinisce il metodo
 * paintComponent per il disegno dell'immagine
 */
public class ImagePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private BufferedImage img;

    private double zoom = 1;

    public void paintComponent(Graphics g) {
	super.paintComponent(g);

	if (img != null)
	    g.drawImage(img, 0, 0, null);

    }

    public void setImage(BufferedImage imm) {
	this.img = imm;
	this.setSize((imm.getWidth()), (imm.getHeight()));
	this.repaint();
    }

    public BufferedImage getImg() {
	return img;
    }

}