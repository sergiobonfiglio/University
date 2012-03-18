import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.List;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import filters.BMPFilter;
import filters.ImagesFilter;
import filters.JPGFilter;
import filters.PNGFilter;

public class PaintFrame extends JFrame implements ActionListener,
		MouseListener, MouseMotionListener {

	private static final int COLORE_DELTAOMEGA = 0xFF00FFFF;

	private static final int COLORE_OMEGA = 0xFFFF00FF;

	private static final long serialVersionUID = 1L;

	private static PaintFrame istanza = new PaintFrame();

	public static PaintFrame getIstanza() {
		return istanza;
	}

	// Costanti
	// pixel sano
	public static final byte SOURCE = 0;

	// buco
	public static final byte OMEGA = -3;

	// frontiera
	public static final byte DELTAOMEGA = -2;

	private JMenuItem Apri, Salva, Esci;

	private JMenuItem caricaMaschera, disegnaMaschera, selezionaMaschera,
			ripetiMaschera, avviaInpainting, fermaInpainting, inpainting,
			configuraTest;

	private JMenuItem sizeSmall, sizeMedium, sizeBig, sizeExtra;

	public JScrollPane jScrollPane = new JScrollPane();

	private JSlider sliderZoom = new JSlider(JSlider.VERTICAL, 1, 10, 1);

	private ImagePanel pannello = new ImagePanel();

	private JFileChooser fileChooser = new JFileChooser();

	private FileFilter[] filtriApribili = { new ImagesFilter() };

	private FileFilter[] filtriSalvabili = { new BMPFilter(), new PNGFilter(),
			new JPGFilter() };

	// permette l'anteprima delle immagini nel fileChooser
	private ImagePreviewPanel preview = new ImagePreviewPanel();

	protected Thread threadInpainting;

	private boolean salvato = true;

	private boolean abilitadisegno = false;

	private boolean commenti = false;

	// Finestra
	public int window = 9;

	public int path = window / 2;

	// Dimensioni immagini
	public int w, h;

	// numero pixel di Omega
	int countPixelOmega = 1;

	// Matrici
	/**
	 * Dice se il pixel x, y fa parte dell'immagine sorgente, di omega o di
	 * delta omega.
	 */
	byte[][] mask;

	byte[][] ultimaMask;

	/**
	 * Il poligono che memorizza la selezione dell'area omega da parte
	 * dell'utente.
	 */
	Polygon poligonoSelezione = new Polygon();

	Polygon ultimoPoligonoSelezione = null;

	List lista = new List();

	// matrice contenente i valori di confidence
	double[][] matrixC;

	// matrice contentente il data term
	double[][] matrixP;

	int[][] MatrixIntorno;

	// Matrici canali R,G,B
	RGBtoGray MatrixRGB;

	int[][] imageGray;

	// coordinate del rettangolo contenente la maschera
	int coordinata1x;

	int coordinata1y;

	int coordinata2x;

	int coordinata2y;

	int tmpcoordinata1x;

	int tmpcoordinata1y;

	int tmpcoordinata2x;

	int tmpcoordinata2y;

	int minx = 32976;

	int miny = 32976;

	int maxx = -1;

	int maxy = -1;

	// la variabile image contiene l'immagine da disegnare
	BufferedImage image;

	// Costruttore
	private PaintFrame() {
		setTitle("Inpanting");
		Toolkit screen = Toolkit.getDefaultToolkit();
		Dimension size = screen.getScreenSize();
		int height = size.height;
		int width = size.width;
		setSize(width / 2, height / 2);
		setLocation(width / 4, height / 4);
		this.getContentPane().add(jScrollPane, BorderLayout.CENTER);

		this.getContentPane().add(sliderZoom, BorderLayout.EAST);
		sliderZoom.addChangeListener(pannello);

		jScrollPane.getViewport().add(pannello, null);
		// Inizializzazione dei Men
		JMenuBar barraMenu = new JMenuBar();
		JMenu BarraFile = new JMenu("File");
		Apri = new JMenuItem("Apri...");
		Apri.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				ActionEvent.META_MASK));
		Apri.addActionListener(this);
		Salva = new JMenuItem("Salva...");
		Salva.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				ActionEvent.META_MASK));
		Salva.addActionListener(this);
		Esci = new JMenuItem("Esci...");
		Esci.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				ActionEvent.META_MASK));
		Esci.addActionListener(this);
		BarraFile.add(Apri);
		BarraFile.add(Salva);
		BarraFile.add(Esci);

		JMenu menuMaschera = new JMenu("Maschera");
		selezionaMaschera = new JMenuItem("Seleziona maschera...");
		selezionaMaschera.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M,
				ActionEvent.META_MASK));
		caricaMaschera = new JMenuItem("Carica maschera...");
		caricaMaschera.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
				ActionEvent.META_MASK));
		ripetiMaschera = new JMenuItem("Ripeti maschera...");
		ripetiMaschera.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
				ActionEvent.META_MASK));
		menuMaschera.add(selezionaMaschera);
		menuMaschera.add(ripetiMaschera);
		menuMaschera.add(caricaMaschera);
		caricaMaschera.addActionListener(this);
		selezionaMaschera.addActionListener(this);
		ripetiMaschera.addActionListener(this);

		JMenu menuInpainting = new JMenu("Inpainting");
		inpainting = new JMenuItem("Inpainting...");
		inpainting.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,
				ActionEvent.META_MASK));
		menuInpainting.add(inpainting);
		inpainting.addActionListener(this);

		JMenu menuProcesso = new JMenu("Processo");
		avviaInpainting = new JMenuItem("Avvia inpainting");
		fermaInpainting = new JMenuItem("Ferma inpainting");
		menuProcesso.add(avviaInpainting);
		menuProcesso.add(fermaInpainting);
		avviaInpainting.addActionListener(this);
		fermaInpainting.addActionListener(this);

		// menu Test
		JMenu menuTest = new JMenu("Test");
		this.configuraTest = new JMenuItem("Configura test...");
		menuTest.add(configuraTest);
		configuraTest.addActionListener(this);

		JMenu BarraWindow = new JMenu("Windows");
		sizeSmall = new JMenuItem("Size 3x3...");
		sizeSmall.addActionListener(this);
		sizeMedium = new JMenuItem("Size 5x5...");
		sizeMedium.addActionListener(this);
		sizeBig = new JMenuItem("Size 7x7...");
		sizeBig.addActionListener(this);
		sizeExtra = new JMenuItem("Size 9x9...");
		sizeExtra.addActionListener(this);
		BarraWindow.add(sizeSmall);
		BarraWindow.add(sizeMedium);
		BarraWindow.add(sizeBig);
		BarraWindow.add(sizeExtra);
		// Barra
		barraMenu.add(BarraFile);
		barraMenu.add(menuMaschera);
		barraMenu.add(menuInpainting);
		barraMenu.add(menuProcesso);
		barraMenu.add(menuTest);
		barraMenu.add(BarraWindow);
		setJMenuBar(barraMenu);
		// Inizializzazione JFileChooser
		fileChooser.setCurrentDirectory(new File("c:\\"));
		fileChooser.setAcceptAllFileFilterUsed(false);

		// aggiungo l'anteprima al fileChooser
		fileChooser.setAccessory(preview);
		fileChooser.addPropertyChangeListener(preview);

		// File accettati con estenzione JPG,PNG e BMP
		fileChooser.setFileFilter(new ImagesFilter());

		// Aggiungo al Pannello gli ascoltatori degli eventi del mouse
		pannello.addMouseListener(this);
		pannello.addMouseMotionListener(this);
		// Gestione Eventi Finestra
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

	}

	public void actionPerformed(ActionEvent e) {
		Object evento = e.getSource();
		if (evento == Apri)
			this.apri();
		else if (evento == ripetiMaschera)
			this.ripetiUltimaMaschera();
		else if (evento == Salva)
			this.salva();
		else if (evento == Esci)
			this.esci();
		else if (evento == caricaMaschera)
			this.mask();
		else if (evento == disegnaMaschera)
			this.riempimentoOmegaDaMaschera();
		else if (evento == selezionaMaschera)
			this.Frontiera();
		else if (evento == this.inpainting) {
			FinestraOpzioniInpainting finOpzioni = new FinestraOpzioniInpainting();
			finOpzioni.setVisible(true);
		} else if (evento == this.avviaInpainting) {
			if (this.threadInpainting != null
					&& this.threadInpainting.isAlive())
				this.threadInpainting.resume();
		} else if (evento == this.fermaInpainting) {
			if (this.threadInpainting != null
					&& this.threadInpainting.isAlive())
				this.threadInpainting.suspend();
		} else if (evento == this.configuraTest) {
			FinestraTest finTest = new FinestraTest();
			finTest.setVisible(true);
		} else if (evento == sizeSmall) {
			window = 3;
			path = window / 2;
			System.out.println("Window Size" + window + "Path" + path);
		} else if (evento == sizeMedium) {
			window = 5;
			path = window / 2;
			System.out.println("Window Size" + window + "Path" + path);
		} else if (evento == sizeBig) {
			window = 7;
			path = window / 2;
			System.out.println("Window Size" + window + "Path" + path);
		} else if (evento == sizeExtra) {
			window = 9;
			path = window / 2;
			System.out.println("Window Size" + window + "Path" + path);
		}
	}

	private void apri() {
		if (this.checkSalvato() == JOptionPane.YES_OPTION) // Verifico se
		// c' bisogno
		// di salvare
		// l'immagine
		{
			int risposta = fileChooser.showOpenDialog(this);
			if (risposta == JFileChooser.APPROVE_OPTION) // Se ho premuto il
			// tasto salva
			{
				try {
					// Carico l'immagine
					BufferedImage buff = ImageIO.read(fileChooser
							.getSelectedFile());
					// recupero le dimensioni dell'immagine
					w = buff.getWidth();
					h = buff.getHeight();
					inizializzaRettangolo();
					// creo la matrice che utilizzero come maschera
					mask = new byte[w][h];
					// Creo un'immagine nuova delle stesse dimensioni
					image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

					/*
					 * Disegno l'immagine caricata sulla nuova immagine. Questa
					 * operazione va fatta per essere sicuro che l'immagine su
					 * cui lavoro di tipo TYPE_INT_ARGB
					 */
					// creo il contesto grafico per l'immagine creata e
					// disegno l'immagine caricata
					Graphics2D g2d = image.createGraphics();
					g2d.drawImage(buff, 0, 0, null);
					g2d.dispose();

					// Imposto le dimensioni del pannello
					pannello.setPreferredSize(new Dimension(w, h));
					// Imposto l'immagine nel pannello
					pannello.setImage(image);
					// Reimposto tutto il layout del pannello
					jScrollPane.revalidate();
					salvato = true;
				} catch (Exception ex) {
				}
			}
		}
	}

	private void inizializzaRettangolo() {
		coordinata1x = path;
		coordinata1y = path;
		coordinata2x = w - path - 1;
		coordinata2y = h - path - 1;
	}

	/**
	 * Il metodo gradiente:permette di calcolare il gradiente.
	 * 
	 * @param i
	 *            la coordinata x
	 * @param j
	 *            la coordinata y
	 * @return la direzione e il modulo del gradiente
	 */
	private double[] calcolaGradiente(int i, int j) {

		int rx = 0, ry = 0;
		int tmpgradiente = 0;
		double anglegradiente = 0;
		double[] vett = new double[2];
		int x1, y1;
		int maxgradiente = -32987;

		// Applico la Finestra per determinare il gradiente nel punto di
		// frontiera
		for (int y = Math.max(j - path, 0); y < Math.min(j + path - 1, h - 1); y++)
			for (int x = Math.max(i - path, 0); x < Math.min(i + path - 1,
					w - 1); x++) {
				y1 = y + 1;
				x1 = x + 1;
				rx = 0;
				ry = 0;
				tmpgradiente = 0;
				// se il pixel è sano
				if (mask[x1][y1] == SOURCE) {
					// Applico il kernel 3x3 delle differenze separate
					// se i pixel accanto (a sinistra e a destra) sono sani
					if ((mask[(x1 - 1)][y1] == SOURCE)
							&& (mask[(x1 + 1)][y1] == SOURCE))
						rx = ((imageGray[x1 - 1][y1]) - (imageGray[x1 + 1][y1])) / 2;
					else
						rx = 0;
					// se i pixel in alto e in basso sono sani
					if ((mask[x1][(y1 - 1)] == SOURCE)
							&& (mask[x1][(y1 + 1)] == SOURCE))
						ry = ((imageGray[x1][y1 - 1]) - (imageGray[x1][y1 + 1])) / 2;
					else
						ry = 0;
					if (commenti)
						System.out.println("rx=" + rx + "ry=" + ry);
					tmpgradiente = ((rx * rx) + (ry * ry));
					if (tmpgradiente > maxgradiente) {
						maxgradiente = tmpgradiente;
						// System.out.println(" Gradiente"+maxgradiente);
						if (ry == 0)
							anglegradiente = 90;
						else {
							anglegradiente = Math.atan((double)rx / (1 * ry));
							anglegradiente = Math.toDegrees(anglegradiente);
							if (commenti)
								System.out.println("Angolo Gradiente"
										+ anglegradiente);
						}
					}
				}
			}

		if (anglegradiente < 0)
			anglegradiente = 180 + anglegradiente;

		vett[0] = anglegradiente;
		vett[1] = maxgradiente;

		return vett;
	}

	/**
	 * Il metodo calcolaNp:permette di calcolare la direzione dei punti del
	 * contorno.
	 * 
	 * @param i
	 *            la coordinata x
	 * @param j
	 *            la coordinata y
	 * @return l'angolo della normale alla retta passante per i punti precedente
	 *         e successivo al punto dato in input
	 */
	private double calcolaNormale(int i, int j) {
		Point prec = new Point(0, 0);
		Point succ = new Point(0, 0);
		double anglenp = 0, m = 0;
		boolean flagp = false, flags = false;

		// Vengono determinati il succ e il prec punto
		if (commenti)
			System.out.println("Coordinate Punto Frontiera" + i + "," + j);
		// Finestra applicata al punto (i,j) di dimensione 3x3
		for (int x = Math.max(i - 1, 0); x < Math.min(i + 2, w - 1); x++)
			for (int y = Math.max(j - 1, 0); y < Math.min(j + 2, h - 1); y++) {
				// non considero il punto (i,j) in esame
				if ((x != i) || (y != j)) {
					if (mask[x][y] == DELTAOMEGA)
						if (!flagp) {
							prec = new Point(x, y);
							// System.out.println("Precedente"+prec.x+","+prec.y+"---");
							flagp = true;
							// break;
						} else {
							succ = new Point(x, y);
							// System.out.println("Successivo"+succ.x+","+succ.y);
							flags = true;
						}
				}
			}
		// se stato determinato solo un punto,utilizzo il punto (i,j)come
		// secondo
		if (!flags)
			succ = new Point(i, j);

		if ((flagp) && (flags)) {
			if (prec.x - succ.x == 0) {
				anglenp = 0;
				return anglenp;
				// System.out.println("Angolo "+(p.x) +"," +(p.y)+"="+anglenp);
			} else if (prec.y - succ.y == 0) {
				anglenp = 90;
				return anglenp;
				// System.out.println("Angolo "+(p.x) +"," +(p.y)+"="+anglenp);
			} else {
				m = (double)(succ.y - prec.y) / (succ.x - prec.x);
				if (commenti)
					// System.out.println("Coefficiente angolare "+m);
					anglenp = Math.atan(-1 * (1 / m));
				// System.out.println("np "+(p.x) +","
				// +(p.y)+"="+Math.toDegrees(anglenp));
				if (anglenp < 0)
					anglenp = 180 + anglenp;
				if (commenti)
					System.out.println("np " + "=" + Math.toDegrees(anglenp));
				return Math.toDegrees(anglenp);

			}
		}
		// se il prec e succ non sono stati trovati,vuol dire che il punto di
		// frontiera isolato
		else
			return 0;
	}

	/**
	 * Il metodo calcolaPriorit:permette di calcolare la priorit di un punto
	 * (i,j) della frontiera.
	 * 
	 * @param i
	 *            la coordinata x
	 * @param j
	 *            la coordinata y
	 */
	private void calcolaPriorita(int i, int j) {
		double normale;
		double gradiente[];
		double dataterm;
		double teta;

		normale = calcolaNormale(i, j);
		gradiente = calcolaGradiente(i, j);
		// Teta=Angolo compreso tra Np e Perpendicolare al gradiente
		teta = Math.toRadians(normale - gradiente[0]);
		dataterm = (Math.abs(gradiente[1] * Math.cos(teta)) / 255);
		matrixP[i][j] = matrixC[i][j] * (dataterm);

	}

	private int checkSalvato() {
		if (!salvato) // Se devo salvare
		{
			int risposta = JOptionPane.showConfirmDialog(this,
					"Il disegno non  stato salvato\nVuoi salvarlo?",
					"Salvataggio", JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (risposta == JOptionPane.YES_OPTION)
				return this.salva();
			else if (risposta == JOptionPane.NO_OPTION)
				return JOptionPane.YES_OPTION;
			else
				return JOptionPane.NO_OPTION;
		} else
			return JOptionPane.YES_OPTION;
	}

	/**
	 * Metodo computeRectangle:determina il rettangolo che contiene la selezione
	 * "domega" in questo modo sar possibile limitare la ricerca solamente alla
	 * zona interessata. In questo modo possibile ottimizzare l'inpainting.
	 * 
	 * @param coordinata
	 *            x del punto in esame
	 * @param coordinata
	 *            y del punto in esame
	 */
	public void computeRectangle(int x, int y) {
		if (x < minx) {
			tmpcoordinata1x = x;
			minx = x;
		}
		if (x > maxx) {
			tmpcoordinata2x = x;
			maxx = x;
		}
		if (y < miny) {
			tmpcoordinata1y = y;
			miny = y;
		}
		if (y > maxy) {
			tmpcoordinata2y = y;
			maxy = y;
		}
	}

	/**
	 * Metodo copy:copia la porzione d'immagine dall'intorno del puntobest a
	 * quello del puntomax
	 * 
	 * @param puntoMax
	 *            coordinate punto max
	 * @param puntoBest
	 *            coordinate punto best
	 */

	private void copy(Point puntoMax, Point puntoBest) {
		int x, y, x1, y1;
		int colore;
		// Determino i nuovi pixel del contorno
		// Analizzo il contorno della finestra window x window del punto di
		// priorit max per determinare il profilo del nuovo contorno
		y = (puntoMax.y) - path;
		x = (puntoMax.x) - path;
		x--;
		// lato sinistro
		while (y < Math.min(puntoMax.y + path + 2, h)) {
			if (mask[x][y] == OMEGA) {
				mask[x][y] = DELTAOMEGA;
				image.setRGB(x, y, COLORE_DELTAOMEGA);
				// Aggiorno il valore di Cp
				matrixC[x][y] = matrixC[puntoMax.x][puntoMax.y];
			}
			y++;
		}
		y--;
		x++;
		// lato inferiore
		while (x < Math.min(puntoMax.x + path + 2, w)) {
			if (mask[x][y] == OMEGA) {
				mask[x][y] = DELTAOMEGA;
				image.setRGB(x, y, COLORE_DELTAOMEGA);
				// Aggiorno il valore di Cp
				matrixC[x][y] = matrixC[puntoMax.x][puntoMax.y];
			}
			x++;
		}
		x--;
		y--;
		// lato destro
		while (y > Math.max(puntoMax.y - path - 2, -1)) {
			if (mask[x][y] == OMEGA) {
				mask[x][y] = DELTAOMEGA;
				image.setRGB(x, y, COLORE_DELTAOMEGA);
				// Aggiorno il valore di Cp
				matrixC[x][y] = matrixC[puntoMax.x][puntoMax.y];
			}
			y--;
		}
		y++;
		x--;
		// lato superiore
		while (x > Math.max(puntoMax.x - path - 2, -1)) {
			if (mask[x][y] == OMEGA) {
				mask[x][y] = DELTAOMEGA;
				image.setRGB(x, y, COLORE_DELTAOMEGA);
				// Aggiorno il valore di Cp
				matrixC[x][y] = matrixC[puntoMax.x][puntoMax.y];
			}
			x--;
		}

		// L'intorno del PuntoMax viene esplorato tramite gli indici x,y
		// L'intorno del PuntoBest viene esplorato tramite gli indici w,z
		// (x,y) e (w,z) sono i corrispondenti punti nei due intorni

		for (y = puntoMax.y - path, y1 = puntoBest.y - path; y < puntoMax.y
				+ path + 1; y++, y1++)
			for (x = puntoMax.x - path, x1 = puntoBest.x - path; x < puntoMax.x
					+ path + 1; x++, x1++) {
				if (mask[x][y] != SOURCE)// solo per i punti di omega
				{
					// if (mask[x][y]==DELTAOMEGA)
					countPixelOmega--;
					// prelevo il colore dal punto best match e lo utilizzo
					// per riempire il corrispondente punto (x,y) nella zona
					// di priorit massima
					colore = image.getRGB(x1, y1);
					image.setRGB(x, y, colore);
					// aggiorno la matrice Gray dell'immagine a toni di
					// grigio
					MatrixRGB.setMatriceGray(x, y, colore);
					// Aggiorno Frontiera,il pixel stato riempito quindi
					// il valore settato a zero
					mask[x][y] = SOURCE;
					// Aggiorno il valore di Cp
					matrixC[x][y] = matrixC[puntoMax.x][puntoMax.y];
				}
			}

		pannello.repaint();
		// image.setRGB(puntoBest.x, puntoBest.y, 0xFF0000FF);
		// image.setRGB(puntoMax.x, puntoMax.y, 0xFFFF0000);

	}

	private void esci() {
		// Verifico se c' bisogno di salvare l'immagine e poi esco
		if (this.checkSalvato() == JOptionPane.YES_OPTION)
			System.exit(0);
	}

	private Point findBestPatch2(Point puntoMaxPriorita, byte strategia,
			boolean minimizzaAncheDeltaDistanza,
			boolean allineaBuchiSoloSuMatch, boolean usaSubPattern) {

		if (usaSubPattern == true) {
			SubPattern subPattern = new SubPattern(puntoMaxPriorita,
					this.window);
			RicercatoreSubPattern ricercatore = new RicercatoreSubPattern(
					subPattern);

			return ricercatore.ricercaMiglioreCorrispondenza(strategia,
					minimizzaAncheDeltaDistanza);
		} else {
			Pattern pattern = new Pattern(puntoMaxPriorita, this.window);
			RicercatorePattern ricercatore = new RicercatorePattern(pattern);

			return ricercatore.ricercaMiglioreCorrispondenza(strategia,
					minimizzaAncheDeltaDistanza, allineaBuchiSoloSuMatch);
		}

	}

	/**
	 * Il metodo findBestPatch:determina il punto il cui intorno match meglio
	 * con quello del punto di priorit max
	 * 
	 * @return le coordinate del punto il cui intorno a SSD:SUM SQUARE DISTANT
	 *         minima
	 */
	private Point findBestPatch(Point puntomax) {
		int a, b, c, d;
		int x, y;
		int r0, g0, b0, r1, g1, b1;
		int colore;
		double min = 2147483647;
		double ssd = 0;
		boolean contieneOmega = false;
		MatrixIntorno = new int[window][window];
		int width, height;
		// Punto centrale dell'intorno
		Point p = new Point(0, 0);
		// Determino l'intorno del punto di deltaomega avente priorit max

		a = puntomax.x;
		b = puntomax.y;
		width = Math.min(a + path + 1, w - 1);
		height = Math.min(b + path + 1, h - 1);

		int i = 0, j = 0;
		for (y = Math.max(b - path, 0); y < height; y++, j++) {
			for (x = Math.max(a - path, 0), i = 0; x < width; x++, i++) {
				if (mask[x][y] == OMEGA || mask[x][y] == DELTAOMEGA)
					MatrixIntorno[i][j] = OMEGA;
				else {
					MatrixIntorno[i][j] = image.getRGB(x, y);
				}
			}
		}

		// Esploro l'immagine,applicando la window al fine di determinare quella
		// che Match meglio con il punto di priorit max
		// A partire dalla posizione path,path
		for (j = path; j < h - path; j++)
			for (i = path; i < w - path; i++) {
				if (mask[i][j] == SOURCE) {
					colore = 0;
					c = 0;
					d = 0;
					contieneOmega = false;
					for (y = j - path; y < j + path + 1 && !contieneOmega; y++) {
						for (x = i - path; x < i + path + 1 && !contieneOmega; x++) {
							/*
							 * controllo se il punto fa parte di Omega. Se e'
							 * cosi aumento il valore di ssd in modo tale che
							 * l'intorno non venga scelto come BestMatch.
							 * Infatti in questo caso vuol dire che una parte
							 * dell'allineamento in esame cade all'interno di
							 * Omega.
							 */
							if ((mask[x][y] == OMEGA)
									|| (mask[x][y] == DELTAOMEGA)) {
								ssd = Integer.MAX_VALUE;
								contieneOmega = true;
							}
							if (MatrixIntorno[c][d] != OMEGA && !contieneOmega) {
								colore = image.getRGB(x, y);
								r0 = (colore & 0x00FF0000) >> 16;
								g0 = (colore & 0x0000FF00) >> 8;
								b0 = (colore & 0x000000FF);
								r1 = (MatrixIntorno[c][d] & 0x00FF0000) >> 16;
								g1 = (MatrixIntorno[c][d] & 0x0000FF00) >> 8;
								b1 = (MatrixIntorno[c][d] & 0x000000FF);
								// ssd += Math.sqrt(Math.pow((r0 - r1), 2)
								// + Math.pow((g0 - g1), 2)
								// + Math.pow((b0 - b1), 2));
								ssd += (int) Math.sqrt(Math.pow((r0 - r1), 2)
										* 0.3 + Math.pow((g0 - g1), 2) * 0.59
										+ Math.pow((b0 - b1), 2) * 0.11);
							}
							c++;
						}
						c = 0;
						d++;
						// se il valore di ssd nell'intorno e' gia' maggiore di
						// min,si interrompe la ricerca
						if (ssd > min)
							break;
					}

					if (ssd < min && !contieneOmega) {
						min = ssd;
						p.x = i;
						p.y = j;
					}
					// se la somma=0 restitusco il punto,non ha senso
					// continuare la ricerca
					if (ssd == 0)
						return p;
					ssd = 0;
				}
			}

		return p;
	}

	private void Frontiera() {
		abilitadisegno = true;
	}

	/**
	 * Il metodo getMaxPriorit:determina il pixel di massima priorit
	 * 
	 * @return le coordinate del punto di max priorit
	 */

	private Point getMaxPriorita() {
		double max = -32768;
		double tmp;
		Point puntomax = null;

		// Determino il punto con priorit Max
		for (int j = coordinata1y; j < coordinata2y + 1; j++)
			for (int i = coordinata1x; i < coordinata2x + 1; i++)
				if (mask[i][j] == DELTAOMEGA)// Solo per i punti di frontiera
				{
					tmp = matrixP[i][j];
					// System.out.println("Pp"+i+","+j+"= "+tmp);
					if (tmp > max) {
						max = tmp;
						puntomax = new Point(i, j);
					}
					tmp = 0;
				}
		return puntomax;
	}

	// Metodo Inpainting
	protected void inpainting(OpzioniInpainting opzioniInpainting,
			boolean ripetiMaschera) {
		Point pointmax = new Point(0, 0);
		Point pointbest = new Point(0, 0);
		long startInpainting = System.currentTimeMillis();
		long totalFindPattern = 0;
		// dichiararla nel costruttore
		matrixP = new double[w][h];

		// Inizializzo la Matrice Confidence
		Confidence C = new Confidence(mask, w, h, window);
		// matrixC=C.getMatriceC();

		MatrixRGB = new RGBtoGray(image);
		// ImageGray la matrice a toni di grigio
		imageGray = MatrixRGB.getMatriceGray();

		long numCicli = 0;

		countPixelOmega = 1;
		while (countPixelOmega != 0) {
			countPixelOmega = 0;
			// Calcolo il valore di Priorità per i pixel della frontiera
			for (int j = coordinata1y; j < coordinata2y + 1; j++)
				for (int i = coordinata1x; i < coordinata2x + 1; i++)
					if (mask[i][j] == DELTAOMEGA) {
						countPixelOmega++;
						computeRectangle(i, j);
						C.UpdateC(i, j);
						matrixC = C.getMatriceC();
						calcolaPriorita(i, j);
					}

			// aggiorno le coordinate del rettangolo
			coordinata1x = tmpcoordinata1x;
			coordinata2x = tmpcoordinata2x;
			coordinata1y = tmpcoordinata1y;
			coordinata2y = tmpcoordinata2y;
			tmpcoordinata1x = 0;
			tmpcoordinata2x = 0;
			tmpcoordinata1y = 0;
			tmpcoordinata2y = 0;
			minx = 32976;
			maxx = -1;
			miny = 32976;
			maxy = -1;

			// Determino il pixel con priorità max
			pointmax = this.getMaxPriorita();
			if (pointmax != null) {
				long timeStartFindPattern = System.currentTimeMillis();
				// determino l'intorno che Match meglio con quello del punto di
				// priorit max
				if (opzioniInpainting.algoritmoNaive)
					pointbest = findBestPatch(pointmax);
				else {
					pointbest = findBestPatch2(pointmax, opzioniInpainting.strategia,
							opzioniInpainting.minimizzaAncheDelta,
							opzioniInpainting.allineaBuchi, opzioniInpainting.usaSubPattern);
				}
				long timeStopFindPattern = System.currentTimeMillis();

				totalFindPattern += timeStopFindPattern - timeStartFindPattern;
				numCicli++;

				copy(pointmax, pointbest);
			}

		}
		long stopInpainting = System.currentTimeMillis();

		String string = "Opzioni: " + "\nNaive: " + opzioniInpainting.algoritmoNaive;
		if (!opzioniInpainting.algoritmoNaive)
			string += "\nStrategia(vicino=0): " + opzioniInpainting.strategia
					+ "\nMinimizza anche delta: " + opzioniInpainting.minimizzaAncheDelta
					+ "\nAllinea buchi solo su match: "
					+ opzioniInpainting.allineaBuchi + "\nSub-pattern: "
					+ opzioniInpainting.usaSubPattern;
		string += "\nTempi: " + "\nTotale: "
				+ (stopInpainting - startInpainting) / 1000 + " s\n"
				+ "findBestPatch() totale: " + totalFindPattern / 1000 + "s\n"
				+ "findBestPatch() medio: " + (totalFindPattern / numCicli)
				+ "ms\n";

		System.out.println(string);

		reset();

		if (ripetiMaschera)
			ripetiUltimaMaschera();
	}

	private void reset() {
		inizializzaRettangolo();
	}

	protected void ripetiUltimaMaschera() {
		/*
		 * if (ultimoPoligonoSelezione != null) { poligonoSelezione =
		 * ultimoPoligonoSelezione; aggiornaMascheraDaPoligono(); } else {
		 */
		mask = ultimaMask;
		this.riempimentoOmegaDaMaschera();
		// }
	}

	/**
	 * Carica un file mashera
	 * 
	 */
	private void mask() {

		int count = 0;
		int width = 0;
		int height = 0;
		BufferedImage buff = null;

		int risposta = fileChooser.showOpenDialog(this);
		if (risposta == JFileChooser.APPROVE_OPTION) {
			try {

				// Carico l'immagine
				buff = ImageIO.read(fileChooser.getSelectedFile());
				// recupero le dimensioni dell'immagine
				width = buff.getWidth();
				height = buff.getHeight();
			} catch (Exception ex) {
			}
			for (int j = 0; j < height; j++)
				for (int i = 0; i < width; i++) {
					// se il pixel è nero
					if ((buff.getRGB(i, j)) == 0xFF000000) {
						count = 0;
						// Finestra applicata al punto (i,j) di dimensione 3x3
						for (int x = Math.max(i - 1, 0); x < Math.min(i + 2,
								w - 1); x++)
							for (int y = Math.max(j - 1, 0); y < Math.min(
									j + 2, h - 1); y++) {
								// non considero il punto (i,j) in esame
								if ((x != i) || (y != j))
									if ((buff.getRGB(x, y)) == 0xFF000000)
										count++;
							}
						// se il pixel ha attorno 8 pixel neri allora è interno
						if (count == 8) {
							mask[i][j] = OMEGA;
						} else {
							mask[i][j] = DELTAOMEGA;
						}
					} else {
						// se il pixel è bianco lo setto a source
						mask[i][j] = SOURCE;
					}
				}
			this.riempimentoOmegaDaMaschera();
		}
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		if (abilitadisegno && image != null) {

			// Recupero le posizioni del punto
			Point p = e.getPoint();
			// aggiungo il punto al poligono
			this.poligonoSelezione.addPoint(p.x, p.y);

		}
	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}

	public void mouseMoved(MouseEvent e) {
	}

	// Gestione Eventi Mouse
	public void mousePressed(MouseEvent e) {
		if (abilitadisegno && image != null) {

			// Recupero le posizioni del punto
			Point p = e.getPoint();
			// aggiungo il punto al poligono
			this.poligonoSelezione.addPoint(p.x, p.y);
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (abilitadisegno && image != null) {

			// Recupero le posizioni del punto
			Point p = e.getPoint();
			// aggiungo il punto al poligono
			this.poligonoSelezione.addPoint(p.x, p.y);

			aggiornaMascheraDaPoligono();
		}
	}

	private void aggiornaMascheraDaPoligono() {
		// recupero tutti i punti appartenenti al poligono e li marco come
		// appartenenti al delta omega
		PathIterator iter = poligonoSelezione.getPathIterator(null);
		Point puntoPrecedente = new Point();
		float[] coords = new float[6];
		while (!iter.isDone()) {
			iter.currentSegment(coords);

			Point puntoCorrente = new Point((int) coords[0], (int) coords[1]);
			// se NON è uguale al punto precedente
			if (!puntoPrecedente.equals(puntoCorrente)) {
				puntoPrecedente = puntoCorrente;
				mask[(int) coords[0]][(int) coords[1]] = DELTAOMEGA;
				image.setRGB((int) coords[0], (int) coords[1], COLORE_OMEGA);

			}

			iter.next();

		}
		this.riempimentoOmegaDaPoligono();

		copyMask();

	}

	/**
	 * Copia la maschera corrente: conserva una copia dell'ultima maschera
	 * usata.
	 */
	private void copyMask() {
		ultimaMask = new byte[mask.length][mask[0].length];
		for (int c = 0; c < mask[0].length; c++) {
			for (int r = 0; r < mask.length; r++) {
				ultimaMask[r][c] = mask[r][c];
			}
		}
	}

	/**
	 * Il metodo riempimentoOmega:permette di colorare con il colore prescelto
	 * la regione Omega.
	 */
	private void riempimentoOmegaDaPoligono() {

		Rectangle boundsSelezione = poligonoSelezione.getBounds();
		Point topLeftCorner = boundsSelezione.getLocation();
		int area = 0;

		for (int y = topLeftCorner.y; y < topLeftCorner.y
				+ boundsSelezione.height + 1; y++)
			for (int x = topLeftCorner.x; x < topLeftCorner.x
					+ boundsSelezione.width + 1; x++) {
				if (mask[x][y] == SOURCE) {
					if (poligonoSelezione.contains(x, y)) {
						mask[x][y] = OMEGA;
						image.setRGB(x, y, COLORE_OMEGA);
						area++;
					}
				}
			}

		// resetto il poligono per nuove selezioni
		poligonoSelezione = new Polygon();
		pannello.repaint();
		System.out.println("AREA OMEGA: " + area);
	}

	private void riempimentoOmegaDaMaschera() {

		copyMask();

		int area = 0;

		for (int y = 0; y < h; y++)
			for (int x = 0; x < w; x++) {
				if (mask[x][y] == OMEGA) {
					image.setRGB(x, y, COLORE_OMEGA);
					area++;
				} else if (mask[x][y] == DELTAOMEGA) {
					image.setRGB(x, y, COLORE_DELTAOMEGA);
					area++;
				}
			}

		pannello.repaint();
		System.out.println("AREA OMEGA: " + area);
	}

	private int salva() {
		int risposta2 = JOptionPane.NO_OPTION;
		File f = null;
		String ext = null;

		for (int i = 0; i < filtriApribili.length; i++) {
			fileChooser.removeChoosableFileFilter(this.filtriApribili[i]);
		}
		for (int i = 0; i < filtriSalvabili.length; i++) {
			fileChooser.addChoosableFileFilter(this.filtriSalvabili[i]);
		}

		while (risposta2 == JOptionPane.NO_OPTION) // Finch non decido di
		// salvare o di
		// rinunciare
		{

			// Visualizzo la finestra di dialogo
			int risposta = fileChooser.showSaveDialog(this);
			if (risposta == JFileChooser.APPROVE_OPTION) // Se ho premuto
			// il tasto
			// salva
			{
				try {
					// Recupero il file selezionato
					f = fileChooser.getSelectedFile();
					// Recupero l'estensione del file
					ext = fileChooser.getFileFilter().toString();
					// Recupero il path del file
					String str = f.getCanonicalPath();
					// Se il nome del file non contiene l'estensione, la
					// aggiungo io a mano
					if (!str.toLowerCase().endsWith("." + ext))
						f = new File(str + "." + ext);
					// Se il file esiste chiedo se lo voglio sovrascrivere
					if (f.exists())
						risposta2 = JOptionPane.showConfirmDialog(this,
								"Il file esiste gia'\nlo vuoi sovrascrivere?",
								"Sovrascrittura",
								JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE);
					else
						risposta2 = JOptionPane.YES_OPTION;
				} catch (IOException ex) {
				}
			} else
				risposta2 = JOptionPane.CANCEL_OPTION;
		}
		if (risposta2 == JOptionPane.YES_OPTION) {
			try { // creo una nuova immagine RGB
				BufferedImage image1 = new BufferedImage(w, h,
						BufferedImage.TYPE_INT_RGB);
				// creo il contesto grafico per l'immagine creata e disegno
				// l'immagine
				Graphics2D g2 = image1.createGraphics();
				g2.drawImage(image, 0, 0, null);
				g2.dispose();
				// Provo a salvare l'immagine
				ImageIO.write(image1, ext, f);
				salvato = true;
			} catch (Exception ex) {
			}
		}
		return risposta2;
	}

}
