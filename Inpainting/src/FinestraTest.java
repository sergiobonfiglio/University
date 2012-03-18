import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

/**
 * Finestra che raccoglie le opzioni per far partire diverse volte l'algoritmo
 * con opzioni diverse.
 * 
 * 
 * @author Sergio Bonfiglio
 * 
 */

public class FinestraTest extends JFrame {
	private static final long serialVersionUID = 1L;

	public FinestraTest() {
		setTitle("Opzioni");
		Toolkit screen = Toolkit.getDefaultToolkit();
		Dimension size = screen.getScreenSize();
		int height = size.height;
		int width = size.width;

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Container contentPane = getContentPane();
		contentPane.add(new PannelloTest(this), BorderLayout.CENTER);
		this.pack();

		setLocation(width / 2 - this.getWidth() / 2, height / 4);

		this.pack();
		this.setVisible(true);
	}

}


class PannelloTest extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JFrame frameTest;

	private DefaultListModel listModel = new DefaultListModel();

	private JList lista = new JList(listModel);

	private JButton addButton = new JButton("Add");

	private JButton removeButton = new JButton("Remove");

	private JButton iniziaTest = new JButton("Inizia test");

	private JButton annulla = new JButton("Annulla");

	public PannelloTest(JFrame finestraTest) {
		this.frameTest = finestraTest;

		this.setLayout(new BorderLayout());

		JPanel pannelloCentrale = new JPanel();

		// pannello lista
		JPanel pannelloLista = new JPanel(new GridLayout(2, 1));
		lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		pannelloLista.add(lista, BorderLayout.CENTER);
		JPanel pannelloBottoniLista = new JPanel();
		removeButton.addActionListener(this);
		addButton.addActionListener(this);
		pannelloBottoniLista.add(removeButton);
		pannelloBottoniLista.add(addButton);
		pannelloLista.add(pannelloBottoniLista, BorderLayout.SOUTH);

		pannelloCentrale.add(pannelloLista, BorderLayout.CENTER);

		// pannello bottoni
		JPanel pannelloBottoni = new JPanel();
		annulla.addActionListener(this);
		iniziaTest.addActionListener(this);
		pannelloBottoni.add(annulla);
		pannelloBottoni.add(iniziaTest);

		this.add(pannelloCentrale, BorderLayout.CENTER);
		this.add(pannelloBottoni, BorderLayout.SOUTH);

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == addButton) {
			new FinestraSceltaOpzioniInpainting(
					lista, listModel);

		} else if (e.getSource() == annulla) {
			this.frameTest.dispose();
		} else if (e.getSource() == this.removeButton) {
			listModel.removeElementAt(listModel.getSize() - 1);
		} else if (e.getSource() == this.iniziaTest) {

			PaintFrame frame = PaintFrame.getIstanza();
			boolean ripetiMaschera = true;
			for (int i = 0; i < listModel.getSize(); i++) {
				NodoTest nodo = (NodoTest) listModel.getElementAt(i);

				if (i == listModel.getSize() - 1)
					ripetiMaschera = false;

				Thread t = new Thread(new ThreadInpainting(new OpzioniInpainting(nodo.algoritmoNaive, nodo.strategia,
						nodo.minimizzaAncheDelta, nodo.allineaBuchi, nodo.usaSubPattern),
						ripetiMaschera, frame));
				// PaintFrame.getIstanza().threadInpainting = t;
				t.start();

			}
		}

	}

}

class FinestraSceltaOpzioniInpainting extends JFrame {

	private static final long serialVersionUID = 1L;

	public FinestraSceltaOpzioniInpainting(JList lista,
			DefaultListModel listaStringhe) {

		setTitle("Opzioni");
		Toolkit screen = Toolkit.getDefaultToolkit();
		Dimension size = screen.getScreenSize();
		int height = size.height;
		int width = size.width;

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Container contentPane = getContentPane();
		contentPane.add(new PannelloSceltaOpzioni(this, lista, listaStringhe),
				BorderLayout.CENTER);
		this.pack();

		setLocation(width / 2 - this.getWidth() / 2, height / 4);

		this.setVisible(true);
	}

}

class PannelloSceltaOpzioni extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private FinestraSceltaOpzioniInpainting finestraOpzioni;

	public PannelloOpzioniAlgoritmo pannelloOpzioniAlgoritmo;

	private JButton okButton = new JButton("Conferma");

	private JButton annullaButton = new JButton("Annulla");

	private JList lista;

	private DefaultListModel listaStringhe;

	public PannelloSceltaOpzioni(
			FinestraSceltaOpzioniInpainting finestraOpzioni, JList lista,
			DefaultListModel listaStringhe) {
		this.listaStringhe = listaStringhe;
		this.lista = lista;
		this.finestraOpzioni = finestraOpzioni;

		pannelloOpzioniAlgoritmo = new PannelloOpzioniAlgoritmo();

		// costruisco pannello sud per i bottoni
		JPanel pannelloBottoni = new JPanel();
		pannelloBottoni.setAlignmentX(JPanel.RIGHT_ALIGNMENT);
		pannelloBottoni.add(annullaButton);
		pannelloBottoni.add(okButton);
		this.annullaButton.addActionListener(this);
		this.okButton.addActionListener(this);

		this.add(pannelloOpzioniAlgoritmo, BorderLayout.CENTER);
		this.add(pannelloBottoni, BorderLayout.SOUTH);
	}

	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == this.okButton) {

			NodoTest nodo = new NodoTest(
					pannelloOpzioniAlgoritmo.opzioneAlgoritmoNaive,
					pannelloOpzioniAlgoritmo.opzioneMinimizzaAncheDelta,
					pannelloOpzioniAlgoritmo.opzioneStrategia,
					pannelloOpzioniAlgoritmo.opzioneAllineaBuchi,
					pannelloOpzioniAlgoritmo.opzioneSubPattern);

			listaStringhe.addElement(nodo);
			lista = new JList(listaStringhe);
			lista.ensureIndexIsVisible(listaStringhe.getSize());
			this.finestraOpzioni.pack();

			this.finestraOpzioni.dispose();
		} else if (e.getSource() == this.annullaButton) {
			this.finestraOpzioni.dispose();
		}

	}
}

class NodoTest {

	public boolean algoritmoNaive;

	public boolean minimizzaAncheDelta;

	public byte strategia;

	public boolean allineaBuchi;

	public boolean usaSubPattern;

	public NodoTest(boolean algoritmoNaive, boolean minimizzaAncheDelta,
			byte strategia, boolean allineaBuchi, boolean usaSubPattern) {
		this.algoritmoNaive = algoritmoNaive;
		this.minimizzaAncheDelta = minimizzaAncheDelta;
		this.strategia = strategia;
		this.allineaBuchi = allineaBuchi;
		this.usaSubPattern = usaSubPattern;
	}

	public String toString() {
		String algoritmo;
		if (algoritmoNaive == true)
			algoritmo = "Naive, ";
		else
			algoritmo = "Avanzato, ";

		String minimizza;
		if (minimizzaAncheDelta == true)
			minimizza = "Deta e Gamma, ";
		else
			minimizza = "solo Gamma, ";

		String strategiaStr;
		if (strategia == RicercatorePattern.DALL_INIZIO)
			strategiaStr = "Inizio, ";
		else
			strategiaStr = "Vicino, ";

		String allineaBuchiStr;
		if (allineaBuchi == true)
			allineaBuchiStr = "Allinea Buchi,";
		else
			allineaBuchiStr = "Non allinea buchi,";

		String usaSubPatternStr;
		if (usaSubPattern)
			usaSubPatternStr = "Usa sotto pattern";
		else
			usaSubPatternStr = "Usa pattern interi";

		return algoritmo + minimizza + strategiaStr + allineaBuchiStr
				+ usaSubPatternStr;
	}

}
