import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


import java.awt.*;
import java.awt.geom.AffineTransform;

// La Classe ImagePanel estende la classe JPanel e ridefinisce 
// il metodo paintComponent per il disegno dell'immagine

class ImagePanel extends JPanel implements ChangeListener {

	private static final long serialVersionUID = 1L;

	private Image img;

	private double zoom = 1;

	public void stateChanged(ChangeEvent e) {

		JSlider source = (JSlider) e.getSource();
		zoom = source.getValue();
		//PaintFrame.getIstanza().jScrollPane.setPreferredSize();
		this.setPreferredSize(new Dimension((int)(img.getWidth(null)*zoom),(int)( img.getHeight(null)*zoom)));
		//PaintFrame.getIstanza().jScrollPane.setPreferredSize(new Dimension((int)(img.getWidth(null)*zoom),(int)( img.getHeight(null)*zoom)));
		PaintFrame.getIstanza().jScrollPane.getViewport().revalidate();
		PaintFrame.getIstanza().repaint();
		
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g; 
		AffineTransform oldTransform = g2.getTransform();
		g2.scale(zoom, zoom);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		
		if (img != null)
			g.drawImage(img, 0, 0, null);
		
		g2.setTransform(oldTransform);


	}

	public void setImage(Image imm) {
		this.img = imm;
		this.repaint();
	}

	/**
	 * @return the zoom
	 */
	public double getZoom() {
		return zoom;
	}

}