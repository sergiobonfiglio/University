/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import eccezioni.MessageNotFoundException;
import java.awt.image.BufferedImage;
import javax.swing.JOptionPane;
import ssis.SSISEmbedder;
import ssis.SSISUnembedder;

/**
 *
 * @author sergio
 */
public class Controller {

    private static Controller instance;

    private Controller() {
    }

    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    public void trovaMsgSSIS(String psw, int n, int m, int iterazioni, int tipoECC) {
        FramePrincipale fp = FramePrincipale.getInstance();

        int raggio = 1;

        SSISUnembedder decoder = new SSISUnembedder(fp.getImmagine(), psw, raggio, tipoECC);

        String messaggio = "";
        try {
            messaggio = decoder.unembed(n, m, iterazioni);
        } catch (MessageNotFoundException e) {
            int risp = JOptionPane.showConfirmDialog(null, "Non è stato trovato nessun header:\nvuoi provare comunque a decodificare il messaggio?");
            if (risp == JOptionPane.OK_OPTION) {

                int lunghezzaChar = Integer.parseInt(JOptionPane.showInputDialog(
                        "Inserisci il numero di caratteri da leggere:",
                        30));

                int lungBit = (int) decoder.getEncodedCharMsgLength(lunghezzaChar);
                messaggio = decoder.unembed(lungBit);
            }
        }

        if (messaggio != "") {
            System.out.println("Messaggio trovato: " + messaggio + " length=" + messaggio.length());
            JOptionPane.showMessageDialog(null, messaggio);
        } else {
            System.out.println("Messaggio non trovato");
            JOptionPane.showMessageDialog(null, "Messaggio non trovato");
        }
    }

    public void inserisciMsgSSIS(String psw, String msg, int deviazione,int n, int m, int iterazioni,int tipoECC) {
        FramePrincipale fp = FramePrincipale.getInstance();
        int raggio = 1;

        SSISEmbedder encoder = new SSISEmbedder(fp.getImmagine(), psw, raggio, tipoECC);
        BufferedImage immagine= encoder.embed(msg, deviazione);
        fp.setImmagine(immagine);

        fp.getPannello1().setImage(immagine);
        fp.repaint();

    }
}
