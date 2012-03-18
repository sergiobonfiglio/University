package gui.menu;

import gui.MediaFrame;
import io.JMFCamImporter;
import io.VideoFileImporter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.media.Player;
import javax.media.Processor;
import javax.media.protocol.DataSource;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import video.processor.ProcessorFactory;

/**
 * Implementa la voce di menù "File" che contiene i comandi per aprire un file
 * video, aprire lo stream proveniente da una webcam e per uscire
 * dall'applicazione.
 */
public class MenuFile extends JMenu implements ActionListener {

    private JMenuItem apriVideo;
    private JMenuItem apriCam;
    private JMenuItem esci;

    public MenuFile() {
	super("File");

	initMenuItems();

	addListenerToMenuItems();

	addMenuItemsToMenu();

    }

    private void addMenuItemsToMenu() {
	super.add(apriVideo);
	super.add(apriCam);
	super.add(esci);
    }

    private void addListenerToMenuItems() {
	apriVideo.addActionListener(this);
	apriCam.addActionListener(this);
	esci.addActionListener(this);
    }

    private void initMenuItems() {
	apriVideo = new JMenuItem("Apri video...");
	apriVideo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
		ActionEvent.META_MASK));
	apriCam = new JMenuItem("Apri WebCam");
	apriCam.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
		ActionEvent.META_MASK));
	esci = new JMenuItem("Esci");
	esci.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
		ActionEvent.META_MASK));
    }

    public void actionPerformed(ActionEvent e) {

	JMenuItem source = (JMenuItem) e.getSource();

	if (source == apriVideo) {

	    MediaFrame.getInstance().closeCam();

	    JFileChooser fileChooser = new JFileChooser();

	    int ret = fileChooser.showOpenDialog(MediaFrame.getInstance());
	    if (ret == JFileChooser.APPROVE_OPTION) {

		try {
		    URL mediaURL = fileChooser.getSelectedFile().toURL();
		    DataSource dataSource = VideoFileImporter
			    .getDataSource(mediaURL);

		    ProcessorFactory factory = new ProcessorFactory(dataSource,
			    true);
		    Processor pl = factory.createPrefetchedProcessor();

		    MediaFrame.getInstance().setVideo(pl);

		} catch (MalformedURLException e1) {
		    e1.printStackTrace();
		}

	    }

	} else if (source == apriCam) {

	    try {
		MediaFrame.getInstance().closeVideo();

		Player player = JMFCamImporter.getJMFWebCam();
		if (player != null) {
		    MediaFrame.getInstance().setVideo(player);
		} else {

		    MediaFrame.getInstance().liveCam();
		}
	    } catch (Exception e1) {
		e1.printStackTrace();
	    }

	} else if (source == esci) {

	    MediaFrame.getInstance().close();

	}

    }
}
