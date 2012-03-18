import javax.swing.*;
import java.awt.*;
import java.beans.*;
import java.io.File;

public class ImagePreviewPanel extends JPanel implements PropertyChangeListener {

	private int width, height;
	private ImageIcon icon;
	private Image image;
	private static final int ACCESSORY_SIZE = 150;
	private Color background;

	public ImagePreviewPanel() {
		setPreferredSize(new Dimension(ACCESSORY_SIZE, ACCESSORY_SIZE));
		background = getBackground();
	}

	public void propertyChange(PropertyChangeEvent e) {
		String propertyName = e.getPropertyName();

		// Selezione dell'evento a cui rispondere
		if (propertyName.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
			File selection = (File) e.getNewValue();
			String name;

			if (selection == null)
				return;
			else
				name = selection.getAbsolutePath();

			// facciamo l'anteprima soltanto dei file immagine
			if ((name != null) && name.toLowerCase().endsWith(".jpg")
					|| name.toLowerCase().endsWith(".jpeg")
					|| name.toLowerCase().endsWith(".gif")
					|| name.toLowerCase().endsWith(".png")) {
				icon = new ImageIcon(name);
				image = icon.getImage();
				scaleImage();
				repaint();
			}
		}
	}

	private void scaleImage() {
		width = image.getWidth(this);
		height = image.getHeight(this);
		double zoom = 1.0;

		// Determiniamo come scalare l'immagine assicurandoci di non superare
		// ACCESSORY_SIZE

		if (width >= height) {
			zoom = (double) (ACCESSORY_SIZE) / width;
			width = ACCESSORY_SIZE;
			height = (int) (height * zoom);
		} else {
			if (getHeight() > ACCESSORY_SIZE) {
				zoom = (double) (ACCESSORY_SIZE) / height;
				height = ACCESSORY_SIZE;
				width = (int) (width * zoom);
			} else {
				zoom = (double) getHeight() / height;
				height = getHeight();
				width = (int) (width * zoom);
			}
		}

		image = image.getScaledInstance(width, height, Image.SCALE_DEFAULT);
	}

	public void paintComponent(Graphics g) {
		g.setColor(background);

		//ripulisco il pannello
		g.fillRect(0, 0, ACCESSORY_SIZE, getHeight());
		
		//disegno la nuova miniatura
		g.drawImage(image, getWidth() / 2 - width / 2 , getHeight() / 2
				- height / 2, this);
	}

}