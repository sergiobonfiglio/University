import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * JPanel per la scelta delle opzioni con cui richiamare l'algoritmo di pattern
 * matching
 * 
 * @author Sergio Bonfiglio
 * 
 */
public class PannelloOpzioniAlgoritmo extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	public JRadioButton algoritmoNaive = new JRadioButton("Naive"),
			algoritmoAvanzato = new JRadioButton("Avanzato"),
			minimizzaGamma = new JRadioButton("Gamma distanza"),
			minimizzaDeltaEGamma = new JRadioButton("Delta e Gamma distanza"),
			strategiaDallInizio = new JRadioButton("Dall'inizio"),
			strategiaDaVicino = new JRadioButton("Da Vicino");

	public JCheckBox allineamentoSubPattern = new JCheckBox(
			"Usa i sotto-pattern"), allineamentoBuchi = new JCheckBox(
			"Allinea buchi solo quando trovi un allineamento valido");

	public boolean opzioneAlgoritmoNaive = false;

	public boolean opzioneMinimizzaAncheDelta = true;

	public byte opzioneStrategia;

	public boolean opzioneAllineaBuchi = false;

	public boolean opzioneSubPattern = false;

	public PannelloOpzioniAlgoritmo() {

		this.setLayout(new BorderLayout());

		JPanel pannelloCentrale = initPannelloCentrale();

		JPanel pannelloAlgoritmo = initPannelloSceltaAlgoritmo();

		JPanel pannelloMinimizzazione = initPannelloParametriDaMinimizzare();

		JPanel pannelloStrategia = initPannelloSceltaStrategiaRicerca();

		JPanel pannelloAllineamento = initPannelloSceltaAllineamentoBuchi();

		// assemblo pannello centrale
		pannelloCentrale.add(pannelloAlgoritmo);
		pannelloCentrale.add(pannelloMinimizzazione);
		pannelloCentrale.add(pannelloStrategia);
		pannelloCentrale.add(pannelloAllineamento);

		this.add(pannelloCentrale, BorderLayout.CENTER);

	}

	/**
	 * @return
	 */
	private JPanel initPannelloCentrale() {
		JPanel pannelloCentrale = new JPanel();
		pannelloCentrale.setLayout(new GridLayout(4, 1));
		pannelloCentrale.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		return pannelloCentrale;
	}

	/**
	 * @return
	 */
	private JPanel initPannelloSceltaAllineamentoBuchi() {
		JPanel pannelloAllineamento = new JPanel(new GridLayout(3, 1));
		pannelloAllineamento.add(new JLabel(" Allineamento:"));
		pannelloAllineamento.add(allineamentoSubPattern);
		pannelloAllineamento.add(allineamentoBuchi);
		this.allineamentoBuchi.addActionListener(this);
		this.allineamentoSubPattern.addActionListener(this);
		return pannelloAllineamento;
	}

	/**
	 * @return
	 */
	private JPanel initPannelloSceltaStrategiaRicerca() {
		JPanel pannelloStrategia = new JPanel();
		pannelloStrategia.setLayout(new GridLayout(3, 1));
		ButtonGroup gruppoStrategia = new ButtonGroup();
		gruppoStrategia.add(strategiaDallInizio);
		gruppoStrategia.add(strategiaDaVicino);
		this.strategiaDaVicino.setSelected(true);
		this.strategiaDallInizio.addActionListener(this);
		this.strategiaDaVicino.addActionListener(this);
		pannelloStrategia.add(new JLabel(" Strategia:"));
		pannelloStrategia.add(strategiaDallInizio);
		pannelloStrategia.add(strategiaDaVicino);
		return pannelloStrategia;
	}

	/**
	 * @return
	 */
	private JPanel initPannelloParametriDaMinimizzare() {
		JPanel pannelloMinimizzazione = new JPanel();
		pannelloMinimizzazione.setLayout(new GridLayout(3, 1));
		ButtonGroup gruppoMinimizzazione = new ButtonGroup();
		gruppoMinimizzazione.add(minimizzaGamma);
		gruppoMinimizzazione.add(minimizzaDeltaEGamma);
		this.minimizzaGamma.addActionListener(this);
		this.minimizzaDeltaEGamma.addActionListener(this);
		this.minimizzaDeltaEGamma.setSelected(true);
		pannelloMinimizzazione.add(new JLabel(" Minimizzazione:"));
		pannelloMinimizzazione.add(minimizzaGamma);
		pannelloMinimizzazione.add(minimizzaDeltaEGamma);
		return pannelloMinimizzazione;
	}

	/**
	 * @return
	 */
	private JPanel initPannelloSceltaAlgoritmo() {
		JPanel pannelloAlgoritmo = new JPanel();
		pannelloAlgoritmo.setLayout(new GridLayout(3, 1));
		ButtonGroup gruppoAlgoritmo = new ButtonGroup();
		gruppoAlgoritmo.add(algoritmoNaive);
		gruppoAlgoritmo.add(algoritmoAvanzato);
		this.algoritmoAvanzato.setSelected(true);
		this.algoritmoNaive.addActionListener(this);
		this.algoritmoAvanzato.addActionListener(this);
		pannelloAlgoritmo.add(new JLabel(" Algoritmo:"));
		pannelloAlgoritmo.add(algoritmoNaive);
		pannelloAlgoritmo.add(algoritmoAvanzato);
		return pannelloAlgoritmo;
	}

	/**
	 * Imposta le variabili booleane pubbliche che indicano le opzioni scelte.
	 * Le altre classi accederanno a queste variabili per stabilire quali
	 * opzioni sono state selezionate dall'utente. Inoltre si occupa di
	 * disabilitare le opzioni non compatibili tra di loro.
	 */
	public void actionPerformed(ActionEvent e) {

		this.opzioneAlgoritmoNaive = this.algoritmoNaive.isSelected();

		this.opzioneMinimizzaAncheDelta = this.minimizzaDeltaEGamma
				.isSelected();

		if (strategiaDallInizio.isSelected())
			this.opzioneStrategia = RicercatorePattern.DALL_INIZIO;
		else
			this.opzioneStrategia = RicercatorePattern.DA_VICINO;

		this.opzioneAllineaBuchi = this.allineamentoBuchi.isSelected();

		this.opzioneSubPattern = this.allineamentoSubPattern.isSelected();

		if (opzioneAlgoritmoNaive == true) {
			setEnabledAvanzate(false);
		} else {
			setEnabledAvanzate(true);
		}

		if (allineamentoSubPattern.isSelected()) {
			this.allineamentoBuchi.setSelected(false);
			this.allineamentoBuchi.setEnabled(false);
		}

	}

	private void setEnabledAvanzate(boolean stato) {
		this.minimizzaGamma.setEnabled(stato);
		this.minimizzaDeltaEGamma.setEnabled(stato);
		this.allineamentoSubPattern.setEnabled(stato);
		this.allineamentoBuchi.setEnabled(stato);
		this.strategiaDallInizio.setEnabled(stato);
		this.strategiaDaVicino.setEnabled(stato);

	}

}
