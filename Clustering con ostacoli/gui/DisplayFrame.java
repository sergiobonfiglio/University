package gui;

import grafo.GrafoOstacoli;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import kMedoids.Cluster;

public class DisplayFrame extends JFrame {
	GrafoOstacoli grafo;
	Cluster[] clusters;
	Color[] colors;

	public DisplayFrame(GrafoOstacoli grafo, Cluster[] clusters, Color[] colors) {
		this.grafo = grafo;
		this.clusters = clusters;
		this.colors = colors;
		this.init();
	}

	public DisplayFrame(GrafoOstacoli grafo, Cluster[] clusters) {
		this.grafo = grafo;
		this.clusters = clusters;
		this.init();
	}

	private void init() {
		this.setTitle("Visualizzazione clusters");
		setSize(700, 700);
		Dimension schermo = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(schermo.width / 2 - getWidth() / 2, schermo.height / 2
				- getHeight() / 2);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// this.addWindowListener(new WindowAdapter(){
		// public void windowClosing(WindowEvent e){
		// ((DisplayFrame) (e.getWindow())).esci();
		// }
		// });

		if (colors == null) {
			colors = new Color[Math.max(3, clusters.length)];

			colors[0] = Color.red;
			colors[1] = Color.green;
			colors[2] = Color.blue;

			int tolleranza = 255 / clusters.length;

			for (int i = 3; i < colors.length; i++) {
				Random generator = new Random();
				int r = generator.nextInt(256);
				int g = generator.nextInt(256);
				int b = generator.nextInt(256);
				boolean diverso = true;

				for (int j = 0; j < i - 1 && diverso; j++) {
					diverso = true;
					// almeno una componente deve avere una distanza >= alla
					// tolleranza
					if (r - colors[j].getRed() < tolleranza
							&& g - colors[j].getGreen() < tolleranza
							&& b - colors[j].getBlue() < tolleranza)
						diverso = false;
				}
				if (diverso == true)
					colors[i] = new Color(r, g, b);
				else
					i--;
			}
		}

		JPanel panel = new DisplayPanel(grafo, clusters, colors);

		this.add(panel);
		this.setVisible(true);
	}
}
