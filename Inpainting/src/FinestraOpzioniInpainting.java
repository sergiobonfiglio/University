import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class FinestraOpzioniInpainting extends JFrame {

	private static final long serialVersionUID = 1L;

	public FinestraOpzioniInpainting() {
		setTitle("Opzioni");
		Toolkit screen = Toolkit.getDefaultToolkit();
		Dimension size = screen.getScreenSize();
		int height = size.height;
		int width = size.width;

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Container contentPane = getContentPane();
		contentPane.add(new PannelloOpzioniInpainting(this),
				BorderLayout.CENTER);
		this.pack();

		setLocation(width / 2 - this.getWidth() / 2, height / 4);

		this.setVisible(true);
	}

}

class PannelloOpzioniInpainting extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JFrame finestraOpzioni;

	private PannelloOpzioniAlgoritmo pannelloOpzioni;

	private JButton okButton = new JButton("Conferma");

	private JButton annullaButton = new JButton("Annulla");

	public PannelloOpzioniInpainting(JFrame finestraOpzioni) {
		this.setLayout(new BorderLayout());
		this.finestraOpzioni = finestraOpzioni;

		pannelloOpzioni = new PannelloOpzioniAlgoritmo();

		// costruisco pannello sud per i bottoni
		JPanel pannelloBottoni = initSouthPanel();

		this.add(pannelloOpzioni, BorderLayout.CENTER);
		this.add(pannelloBottoni, BorderLayout.SOUTH);
	}

	/**
	 * @return
	 */
	private JPanel initSouthPanel() {
		JPanel pannelloBottoni = new JPanel();
		pannelloBottoni.setAlignmentX(JPanel.RIGHT_ALIGNMENT);
		pannelloBottoni.add(annullaButton);
		pannelloBottoni.add(okButton);
		this.annullaButton.addActionListener(this);
		this.okButton.addActionListener(this);
		return pannelloBottoni;
	}

	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == this.okButton) {
			Thread t = new Thread(new ThreadInpainting(
					new OpzioniInpainting(pannelloOpzioni.opzioneAlgoritmoNaive, pannelloOpzioni.opzioneStrategia,
							pannelloOpzioni.opzioneMinimizzaAncheDelta, pannelloOpzioni.opzioneAllineaBuchi, pannelloOpzioni.opzioneSubPattern),
					false,
					null));
			PaintFrame.getIstanza().threadInpainting = t;
			t.start();
			this.finestraOpzioni.dispose();
		} else if (e.getSource() == this.annullaButton) {
			this.finestraOpzioni.dispose();
		}

	}

}