package gui;

import eccezioni.MessageNotFoundException;
import gui.filters.ExtFilter;
import gui.filters.GIFFilter;
import gui.filters.JPGFilter;
import gui.filters.PNGFilter;
import gui.filters.TuttiFilter;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import ssis.SSISEmbedder;
import ssis.SSISUnembedder;
import ssis.ecc.ECC;
import ssis.noise.AlphaTrimmedFilter;
import ssis.noise.GaussianNoiseGenerator;

public class FramePrincipale extends JFrame implements ActionListener,
		ChangeListener {

	// menu
	private JMenuBar barraMenu = new JMenuBar();

	// file
	private JMenu file = new JMenu("File");
	private JMenuItem apri = new JMenuItem("Apri");
	private JMenuItem salva = new JMenuItem("Salva");
	private JMenuItem ripristinaOriginale = new JMenuItem(
			"Ripristina originale");
	private JMenuItem esci = new JMenuItem("Esci");
	private JMenuItem[] elementiMenuFile = { apri, salva, ripristinaOriginale,
			esci };

	// SSIS
	private JMenu menuSSIS = new JMenu("SSIS");
	private JMenuItem rimuoviRumore = new JMenuItem("Rimuovi Rumore (ATM)");
	private JMenuItem toGray = new JMenuItem("Scala di grigi");
	private JMenuItem mostraSpettro = new JMenuItem("Mostra Spettro");
	private JMenuItem addGaussianNoise = new JMenuItem(
			"Aggiungi Rumore Gaussiano");
	private JMenuItem inserisciMsgSSIS = new JMenuItem(
			"Inserisci Messaggio Spread Spectrum");
	private JMenuItem trovaMsgSSIS = new JMenuItem(
			"Trova Messaggio Spread Spectrum");
	private JMenuItem[] menuInizialmenteDisabilitati = { salva,
			ripristinaOriginale, toGray, rimuoviRumore, addGaussianNoise,
			inserisciMsgSSIS, trovaMsgSSIS };

	// pannello
	private ViewPanel pannello1 = new ViewPanel(null);

	public ViewPanel getPannello1() {
		return pannello1;
	}

	private ViewPanel pannello2 = new ViewPanel(null);

	// zoom
	private JSlider slider = new JSlider(JSlider.VERTICAL, 100, 3000, 100);
	// barra inferiore
	private JLabel etichettaCapienza = new JLabel("Capienza: -- ");

	// file chooser
	private FileFilter[] filtriApribili = { new JPGFilter(), new GIFFilter(),
			new TuttiFilter() };
	private FileFilter[] filtriSalvabili = { new JPGFilter(), new PNGFilter() };
	private JFileChooser fileChooser = new JFileChooser();
	private ImagePreviewPanel preview = new ImagePreviewPanel();
	private boolean modified = false;

	// immagine
	private BufferedImage originale = null;
	private BufferedImage immagine = null;
	private BufferedImage immagineVisuale = null;
	private static FramePrincipale frame;

	public void setImmagine(BufferedImage immagine) {
		this.immagine = immagine;
	}

	public BufferedImage getImmagine() {
		return immagine;
	}

	public static FramePrincipale getInstance() {
		if (frame == null) {
			frame = new FramePrincipale();
		}
		return frame;
	}

	private FramePrincipale() {
		setTitle("Ste(PN)Ganography");
		setSize(650, 550);
		Dimension schermo = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(schermo.width / 2 - getWidth() / 2, schermo.height / 2
				- getHeight() / 2);
		this.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				((FramePrincipale) (e.getWindow())).esci();
			}
		});

		Container contentPane = this.getContentPane();

		JScrollPane pannello2SP = new JScrollPane(pannello2);
		pannello2SP.setMinimumSize(new Dimension(0, 0));

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
				new JScrollPane(pannello1), pannello2SP);
		split.setOneTouchExpandable(true);
		split.setDividerLocation(getWidth());
		split.setResizeWeight(1);
		contentPane.add(split, BorderLayout.CENTER);

		contentPane.add(slider, BorderLayout.EAST);
		slider.addChangeListener(this);

		JPanel statoP = new JPanel();
		statoP.add(etichettaCapienza);
		contentPane.add(statoP, BorderLayout.SOUTH);

		// file chooser
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setAccessory(preview);
		fileChooser.addPropertyChangeListener(preview);

		for (int i = 0; i < filtriSalvabili.length; i++) {
			fileChooser.addChoosableFileFilter(filtriSalvabili[i]);
		}
		for (int i = 0; i < filtriApribili.length; i++) {
			fileChooser.addChoosableFileFilter(filtriApribili[i]);
		}

		// ================menu
		setJMenuBar(barraMenu);
		barraMenu.add(file);
		// =====menu file
		for (int i = 0; i < elementiMenuFile.length; i++) {
			file.add(elementiMenuFile[i]);
			elementiMenuFile[i].addActionListener(this);
		}
		apri.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				ActionEvent.META_MASK));
		salva.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				ActionEvent.META_MASK));
		esci.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				ActionEvent.META_MASK));
		ripristinaOriginale.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_R, ActionEvent.META_MASK));

		// =====menu SSIS
		barraMenu.add(menuSSIS);
		menuSSIS.add(toGray);
		menuSSIS.add(rimuoviRumore);
		// menuSSIS.add(mostraSpettro);
		menuSSIS.add(addGaussianNoise);
		addGaussianNoise.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				ActionEvent.META_MASK));
		menuSSIS.add(inserisciMsgSSIS);
		inserisciMsgSSIS.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,
				ActionEvent.META_MASK));
		menuSSIS.add(trovaMsgSSIS);
		trovaMsgSSIS.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
				ActionEvent.META_MASK));

		// listeners
		mostraSpettro.addActionListener(this);
		inserisciMsgSSIS.addActionListener(this);
		trovaMsgSSIS.addActionListener(this);
		addGaussianNoise.addActionListener(this);
		toGray.addActionListener(this);
		rimuoviRumore.addActionListener(this);

		// disabilito menu inutilizzabili(nessuna immagine aperta ancora!)
		for (int i = 0; i < menuInizialmenteDisabilitati.length; i++) {
			menuInizialmenteDisabilitati[i].setEnabled(false);
		}

	}

	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == slider && immagine != null) {
			int z = slider.getValue();
			if (pannello1.getImage() != null) {
				pannello1.setZoom(z / 100d,
						RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			}
			if (pannello2.getImage() != null) {
				pannello2.setZoom(z / 100d,
						RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == apri) {
			this.apri();
		} else if (e.getSource() == salva) {
			this.salva();
		} else if (e.getSource() == ripristinaOriginale) {
			this.ripristinaOriginale();
		} else if (e.getSource() == esci) {
			this.esci();
		} else if (e.getSource() == inserisciMsgSSIS) {
			EmbedFrame f = new EmbedFrame();
			int x = (this.getLocation().x + this.getWidth()) / 2;
			int y = (this.getLocation().y + this.getHeight()) / 2;
			f.setLocation(x, y);
			f.setVisible(true);
		} else if (e.getSource() == trovaMsgSSIS) {
			UnembedFrame f = new UnembedFrame();
			int x = (this.getLocation().x + this.getWidth()) / 2;
			int y = (this.getLocation().y + this.getHeight()) / 2;
			f.setLocation(x, y);
			f.setVisible(true);
		} else if (e.getSource() == addGaussianNoise) {
			this.addGaussianNoise();
		} else if (e.getSource() == toGray) {
			this.toGray();
		} else if (e.getSource() == rimuoviRumore) {
			this.rimuoviRumore();
		}
	}

	private void rimuoviRumore() {
		int raggio = Integer.parseInt(JOptionPane.showInputDialog(
				"Inserisci Raggio:", 1));
		AlphaTrimmedFilter f = new AlphaTrimmedFilter(raggio);
		immagine = f.filterAll(immagine, immagine);
		pannello1.setImage(immagine);
		repaint();
	}

	private void toGray() {
		immagine = util.ColorTransform.toGray(immagine);
		immagineVisuale = util.ColorTransform.toGray(immagineVisuale);
		pannello1.setImage(immagine);
		pannello2.setImage(immagineVisuale);
		repaint();
	}

	private void addGaussianNoise() {
		int devianza = Integer.parseInt(JOptionPane.showInputDialog(
				"Inserisci deviazione standard:", 10));
		GaussianNoiseGenerator gNoise = new GaussianNoiseGenerator();
		this.immagine = gNoise.addGaussianNoiseToGrayScale(immagine, devianza,
				0);
		/*
		 * this.immagineVisuale = gNoise.addGaussianNoiseToGrayScaleFFT(
		 * immagineVisuale, devianza, 0);
		 */

		pannello1.setImage(immagine);
		// pannello2.setImage(immagineVisuale);
		repaint();
	}

	private void addGaussianNoiseTest(int deviazione) {

		GaussianNoiseGenerator gNoise = new GaussianNoiseGenerator();
		this.immagine = gNoise.addGaussianNoiseToGrayScale(immagine,
				deviazione, 0);
	}

	private void inserisciMsgSSISTest(String psw, String msg, int deviazione,
			int tipoECC) {
		int raggio = 1;

		SSISEmbedder encoder = new SSISEmbedder(immagine, psw, raggio, tipoECC);
		this.immagine = encoder.embed(msg, deviazione);

		// pannello1.setImage(immagine);
		// repaint();
	}

	private String trovaMsgSSISTest(String psw, int lunghezzaChar, int tipoECC) {
		int raggio = 1;

		SSISUnembedder decoder = new SSISUnembedder(immagine, psw, raggio,
				tipoECC);
		// TODO:test
		String messaggio;
		try {

			messaggio = decoder.unembed();

		} catch (MessageNotFoundException e) {
			// System.out.println("header non trovato");
			int lungBit = (int) decoder.getEncodedCharMsgLength(lunghezzaChar);
			messaggio = decoder.unembed(lungBit);
		}
		return messaggio;
	}

	public static void main(String[] args) throws IOException {

		int devPartenza = 15;
		int devPasso = 5;
		int devFine = 25;// devPartenza + devPasso * 24;

		for (int d = devPartenza; d <= devFine; d += devPasso) {
			int deviazione = d;
			int numChar = 10;
			String msg = test.Generatore.generaLettere(numChar);
			int numBitMsg = 0;

			FramePrincipale fp = new FramePrincipale();

			int numCicli = 3;
			int tipoECC = ECC.ECC_COMBINED;

			String path = "/Users/sergio/Pictures/test/";
			String nomeFile = "holyrock";
			String ext = ".jpg";
			System.out.println("===deviazione: " + d);
			// for (int q = 10; q < 100; q += 10) {
			for (int rumore = 10; rumore <= 30; rumore += 5) {
				int cont = 0;
				int contParziale = 0;
				for (int c = 0; c < numCicli; c++) {

					// apro immagine
					BufferedImage immagine = ImageIO.read(new File(path
							+ nomeFile + ext));
					fp.immagine = immagine;
					String psw = test.Generatore.generaLettere(8);

					fp.inserisciMsgSSISTest(psw, msg, deviazione, tipoECC);

					// aggiungo rumore
					fp.addGaussianNoiseTest(rumore);

					/*
					 * //compressione String p2 = path + nomeFile + q + ".jpg";
					 * fp.salvaTest(p2, q); BufferedImage immagine2 =
					 * ImageIO.read(new File( p2)); fp.immagine = immagine2;
					 */

					String msgStimato;
					msgStimato = fp.trovaMsgSSISTest(psw, numChar, tipoECC);

					boolean[] bitOriginali = ssis.util.BooleanArray
							.str2boolean(msg);
					boolean[] bitStimati = ssis.util.BooleanArray
							.str2boolean(msgStimato);
					numBitMsg = bitOriginali.length;

					contParziale = 0;
					for (int i = 0; i < bitOriginali.length
							&& i < bitStimati.length; i++) {
						if (bitOriginali[i] != bitStimati[i]) {
							cont++;
							contParziale++;
						}
					}

				}

				double media = cont / (double) (numBitMsg * numCicli);

				System.out.println("0," + Double.toString(media).substring(2));
				// System.out.println("tempo Ins/Trova: "+
				// mediaIns+"/"+mediaTrova);
				// System.out.println("Bit diversi: " + cont + " su " +
				// numBitMsg*
				// numCicli);
			}
		}

	}

	private void apri() {
		for (int i = 0; i < menuInizialmenteDisabilitati.length; i++) {
			menuInizialmenteDisabilitati[i].setEnabled(true);
		}

		// aggiungo filtri lossy forse levati durante il salvataggio
		for (int i = 0; i < filtriApribili.length; i++) {
			fileChooser.addChoosableFileFilter(filtriApribili[i]);
		}

		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				BufferedImage tmp = ImageIO.read(fileChooser.getSelectedFile());
				int w = tmp.getWidth();
				int h = tmp.getHeight();
				Raster tmpRaster = tmp.getRaster();

				immagineVisuale = new BufferedImage(w, h,
						BufferedImage.TYPE_INT_ARGB);
				originale = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
				immagine = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
				WritableRaster immRaster = immagine.getRaster();
				WritableRaster immRasterOr = originale.getRaster();
				WritableRaster immRasterVi = immagineVisuale.getRaster();

				// il primo metodo di copia mi serve per aprire l'immagine con
				// pi� fedelt� nel caso fosse
				// ARGB e/o (che poi � quello che mi interessa) � stata salvata
				// con questa applicazione
				// perch� col secondo metodo i pixel non hanno ESATTAMENTE lo
				// stesso valore...non so perch�..
				if (tmpRaster.getNumBands() == immRaster.getNumBands()) {
					immRaster.setPixels(0, 0, w, h, tmpRaster.getPixels(0, 0,
							w, h, (int[]) null));
					immRasterOr.setPixels(0, 0, w, h, tmpRaster.getPixels(0, 0,
							w, h, (int[]) null));
					immRasterVi.setPixels(0, 0, w, h, tmpRaster.getPixels(0, 0,
							w, h, (int[]) null));
				} else {
					Graphics2D g2 = immagine.createGraphics();
					g2.drawImage(tmp, null, 0, 0);
					g2.dispose();
					Graphics2D gOr = originale.createGraphics();
					gOr.drawImage(tmp, null, 0, 0);
					gOr.dispose();
					Graphics2D gVi = immagineVisuale.createGraphics();
					gVi.drawImage(tmp, null, 0, 0);
					gVi.dispose();
				}

				pannello1.setImage(immagine);
				pannello1.setPreferredSize(new Dimension(w, h));
				pannello2.clear();
				pannello2.setImage(immagineVisuale);
				pannello2.setPreferredSize(new Dimension(w, h));

				setTitle("Ste(PN)Ganography - "
						+ fileChooser.getSelectedFile().getName());

				aggiornaBarra();

				repaint();
				pannello1.revalidate();
				pannello2.revalidate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void aggiornaBarra() {
		String cap = "Capienza: (Raw) "
				+ util.Dimensioni.rendiLeggibile(util.Dimensioni
						.capienzaImmagineBit(immagine)) + "B"
				+ " Capienza: (Char) "
				+ util.Dimensioni.capienzaImmagineBit(immagine) / 16 + " Char";
		etichettaCapienza.setText(cap);
	}

	private void salvaTest(String path, int compressione) {

		modified = false;

		String pathS = path;
		File f = new File(pathS);

		try {
			int qualita = compressione;

			BufferedImage tmp = util.ColorTransform.toRGB(immagine);

			Iterator<ImageWriter> iter = ImageIO
					.getImageWritersByFormatName("jpeg");

			ImageWriter writer = (ImageWriter) iter.next();
			// instantiate an ImageWriteParam object with default
			// compression options
			ImageWriteParam iwp = writer.getDefaultWriteParam();
			iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			iwp.setCompressionQuality(qualita / 100f); // an
			// integer
			// between 0
			// and 1
			// 1 specifies minimum compression and maximum quality

			FileImageOutputStream output = new FileImageOutputStream(f);
			writer.setOutput(output);
			IIOImage image = new IIOImage(tmp, null, null);
			writer.write(null, image, iwp);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void salva() {

		modified = false;
		// tolgo filtri lossy
		for (int i = 0; i < filtriApribili.length; i++) {
			fileChooser.removeChoosableFileFilter(filtriApribili[i]);
		}
		fileChooser.setFileFilter(filtriSalvabili[filtriSalvabili.length - 1]);

		if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File f = fileChooser.getSelectedFile();
			try {
				String path = f.getCanonicalPath();
				String ext = ((ExtFilter) fileChooser.getFileFilter())
						.getExtension();
				if (!path.toLowerCase().endsWith("." + ext)) {
					f = new File(path + "." + ext);
				}
			} catch (Exception e) {
			}
			int rw = JOptionPane.OK_OPTION;
			if (f.exists()) {
				rw = JOptionPane.showConfirmDialog(this,
						"Il file esiste: sovrascivere?", "Avviso",
						JOptionPane.OK_CANCEL_OPTION);
			}

			if (rw == JOptionPane.OK_OPTION) {
				try {
					if (fileChooser.getFileFilter().getDescription() == "JPG") {
						int qualita = Integer.parseInt(JOptionPane
								.showInputDialog("Inserisci la qualità:"));

						BufferedImage tmp = util.ColorTransform.toRGB(immagine);

						Iterator<ImageWriter> iter = ImageIO
								.getImageWritersByFormatName("jpeg");

						ImageWriter writer = (ImageWriter) iter.next();
						// instantiate an ImageWriteParam object with default
						// compression options
						ImageWriteParam iwp = writer.getDefaultWriteParam();
						iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
						iwp.setCompressionQuality(qualita / 100f); // an
						// integer
						// between 0
						// and 1
						// 1 specifies minimum compression and maximum quality

						File file = f;
						FileImageOutputStream output = new FileImageOutputStream(
								file);
						writer.setOutput(output);
						IIOImage image = new IIOImage(tmp, null, null);
						writer.write(null, image, iwp);

					} else {
						ImageIO.write(immagine, fileChooser.getFileFilter()
								.getDescription(), f);
					}
					JOptionPane.showMessageDialog(this,
							"Salvataggio completato");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void ripristinaOriginale() {
		modified = false;
		immagine = new BufferedImage(originale.getWidth(), originale
				.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = immagine.createGraphics();
		g2.drawImage(originale, null, 0, 0);
		g2.dispose();
		pannello1.setImage(immagine);

		immagineVisuale = new BufferedImage(originale.getWidth(), originale
				.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2Vi = immagineVisuale.createGraphics();
		g2Vi.drawImage(originale, null, 0, 0);
		g2Vi.dispose();
		pannello2.setImage(immagineVisuale);
		aggiornaBarra();
	}

	private void esci() {
		if (modified) {
			// se scelgo di salvare i cambiamenti
			if (JOptionPane.OK_OPTION == JOptionPane
					.showConfirmDialog(
							this,
							"Il file contiene dei cambiamenti non salvati. Salvare prima di uscire?",
							"Avviso", JOptionPane.OK_CANCEL_OPTION)) {
				this.salva();
			}
		}
		System.exit(0);
	}
}
