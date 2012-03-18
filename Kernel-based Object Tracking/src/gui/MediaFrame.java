package gui;

import gui.menu.BarraMenu;
import io.QTCamImporter;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.media.Player;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import target.Region;
import tracker.Locator;
import tracker.LocatorFactory;
import tracker.LocatorOptions;

/**
 * Implementa la finestra principale.
 */
public class MediaFrame extends JFrame {

    private BarraMenu menuBar;
    private MediaPanel mediaPanel;

    private LocatorOptions options = LocatorOptions.getInstance();

    Player player;

    Locator tracker;
    private QTCamImporter webcamThread;

    // singleton
    private static MediaFrame instance;

    private MediaFrame() {

	menuBar = new BarraMenu();
	this.setJMenuBar(menuBar);

	addWindowListener(new WindowAdapter() {

	    @Override
	    public void windowClosing(WindowEvent e) {
		MediaFrame.getInstance().close();
		super.windowClosing(e);
	    }
	});

	Container contentPane = this.getContentPane();
	mediaPanel = new MediaPanel(null, null);
	contentPane.add(mediaPanel);

	setSize(300, 300);
	Toolkit screen = Toolkit.getDefaultToolkit();
	Dimension screenSize = screen.getScreenSize();
	int height = screenSize.height;
	int width = screenSize.width;
	setLocation(width / 4, height / 4);

	setVisible(true);

    }

    public MediaPanel getMediaPanel() {
	return mediaPanel;
    }

    public static MediaFrame getInstance() {
	if (instance == null) {
	    instance = new MediaFrame();
	}
	return instance;
    }

    public void setVideo(Player processor) {

	this.player = processor;
	processor.setRate(0.8f);

	Component videoPanel = processor.getVisualComponent();
	Component controlsPanel = processor.getControlPanelComponent();

	mediaPanel = new MediaPanel(videoPanel, controlsPanel);

	JComponent cp = mediaPanel;
	cp.setOpaque(true);

	setContentPane(cp);

	this.repaint();
	this.pack();

    }

    public void liveCam() {
	JPanel p = new JPanel();
	mediaPanel = new MediaPanel(p, null);

	try {
	    webcamThread = new QTCamImporter(mediaPanel.getImgPanel());

	    webcamThread.start();

	} catch (Exception e) {
	    e.printStackTrace();
	}

	JComponent cp = mediaPanel;
	cp.setOpaque(true);
	setContentPane(cp);

	this.repaint();
	this.pack();

    }

    public void closeCam() {
	if (webcamThread != null) {
	    webcamThread.stop();
	    webcamThread.closeSession();
	    webcamThread = null;
	}
    }

    public void close() {
	try {
	    closeVideo();
	    closeCam();
	} catch (Exception e) {
	}
	this.dispose();

	System.exit(0);
    }

    public void closeVideo() {
	try {
	    player.stop();
	    player.deallocate();
	    this.player.close();
	} catch (NullPointerException e) {

	}
    }

    public void setImage(BufferedImage imm, double zoom) {
	if (mediaPanel.imgPanel != null) {
	    BufferedImage newImg = new BufferedImage(
		    (int) (imm.getWidth() * zoom),
		    (int) (imm.getHeight() * zoom), BufferedImage.TYPE_INT_RGB);

	    AffineTransform scale = new AffineTransform();
	    scale.scale(zoom, zoom);

	    newImg.createGraphics().drawImage(imm, scale, null);

	    if (tracker != null && tracker.getModello() != null) {
		tracker.locateModel(newImg);

		GlassPane glassPane = mediaPanel.getGlassPane();
		glassPane.setRegion(tracker.getModello().getRegion());
	    }
	    mediaPanel.setImage(newImg);
	    this.pack();
	}

    }

    public void setImage(BufferedImage imm) {
	setImage(imm, 1d);
    }

    public void setModello() {

	// System.out.println("Setto modello:");
	// System.out.println(this.options);

	if (mediaPanel.getGlassPane() != null) {
	    Region region = this.mediaPanel.getGlassPane().getRegion();
	    this.tracker = LocatorFactory.createLocator(
		    options.isAdattaScala(), options.isControllaCoefficiente(),
		    options.getRaggioMinimo(), options.getBandaRicerca());
	    this.tracker.setModello(mediaPanel.imgPanel.getImg(), region);
	}
    }

    public void resetModello() {
	
	this.tracker = null;
    }
}