package gui.menu;

import gui.MediaFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import tracker.LocatorOptions;

/**
 * Implementa la voce di menù "Tracker" che contiene le opzioni per l'algoritmo
 * di tracking e i comandi per impostare il modello da tracciare.
 */
public class MenuTracker extends JMenu implements ActionListener, ItemListener {

    JMenuItem impostaModello;
    JMenuItem resettaModello;
    JCheckBoxMenuItem adattaScala;
    JCheckBoxMenuItem controllaSimilarita;
    SliderMenuItem sliderRaggioMinimo;
    SliderMenuItem sliderBandaRicerca;

    public MenuTracker() {
	super("Tracker");

	impostaModello = new JMenuItem("Imposta modello");
	impostaModello.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,
		ActionEvent.META_MASK));
	super.add(impostaModello);
	impostaModello.addActionListener(this);

	resettaModello = new JMenuItem("Resetta modello");
	resettaModello.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
		ActionEvent.META_MASK));
	super.add(resettaModello);
	resettaModello.addActionListener(this);

	super.add(new JSeparator());

	adattaScala = new JCheckBoxMenuItem("Adatta scala");
	super.add(adattaScala);
	adattaScala.addItemListener(this);

	controllaSimilarita = new JCheckBoxMenuItem("Controlla similarita'");
	super.add(controllaSimilarita);
	controllaSimilarita.addItemListener(this);

	super.add(new JSeparator());

	sliderRaggioMinimo = new SliderMenuItem("Raggio minimo", 15, 10, 30,
		null);
	super.add(sliderRaggioMinimo);
	sliderRaggioMinimo.setEnabled(false);

	super.add(new JSeparator());
	sliderBandaRicerca = new SliderDoubleMenuItem("Banda di ricerca", 2.0,
		1.0, 3.0, null);
	super.add(sliderBandaRicerca);

	LocatorOptions.getInstance().registerAdattaScala(adattaScala);
	LocatorOptions.getInstance().registerControllaCoefficiente(
		controllaSimilarita);
	LocatorOptions.getInstance().registerRaggioMinimo(sliderRaggioMinimo);
	LocatorOptions.getInstance().registerBandaRicerca(sliderBandaRicerca);

    }

    public void actionPerformed(ActionEvent e) {
	if (e.getSource() == impostaModello) {

	    MediaFrame.getInstance().setModello();

	} else if (e.getSource() == resettaModello) {
	    MediaFrame.getInstance().resetModello();
	}
    }

    public void itemStateChanged(ItemEvent e) {

	if (e.getSource() == this.adattaScala) {
	    this.sliderRaggioMinimo.setEnabled(adattaScala.isSelected());
	}

    }

}
