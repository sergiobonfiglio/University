package ssis.ecc.hamming;

import ssis.ecc.ECCDecoder;
import ssis.util.BooleanArray;

public class HammingECCDecoder extends HammingECC implements ECCDecoder{

	public HammingECCDecoder(int n, int m, int iterazioni) {
		super(n, m, iterazioni);
	}
	
	public HammingECCDecoder(int n, int m) {
		super(n, m);
	}

	public HammingECCDecoder() {
		super();
	}
	

	public String decode(boolean[] bitMsg) {
		return decode(bitMsg, this.iterazioni);
	}

	// msg.length deve essere multiplo di m+r!
	private String decode(boolean[] bitMsg, int iterazioni) {

		boolean[] msg = bitMsg;

		if (iterazioni == 0) {
			return BooleanArray.boolean2str(msg);
		} else {

			// System.out.println("decoder input:");
			// stampa(msg, m+r);

			msg = pad(bitMsg, m + r);

			boolean[] risultato = new boolean[(msg.length / (m + r)) * m];

			int[] bitControllo = generaPosizioniBitControllo(m, r);
			int[][] bitParita = generaMatriceBitControllo_bitAssociati(
					bitControllo, m, r);

			// per ogni codeword
			for (int k = 0, ris = 0; k < msg.length; k += (m + r), ris += m) {
				int bitErrato = 0;
				// per ogni bit di controllo
				for (int i = 0; i < bitControllo.length; i++) {
					// genero l'array che contiene i bit controllati
					boolean[] bitAssociati = new boolean[bitParita[i].length];
					for (int j = 0; j < bitParita[i].length; j++) {
						/*
						 * lo 0 può essere solo in prima posizione, nelle altre
						 * indica la fine dell'array
						 */
						if (bitParita[i][j] != 0 || j == 0) {
							bitAssociati[j] = msg[k + bitParita[i][j]];
						}
					}

					/*
					 * anche se l'array bitAssociati potrebbe avere degli 0 in
					 * più, questi non influenzano il calcolo di parità
					 */
					// controllo i bit associati
					if (isDispari(bitAssociati))
						bitErrato += bitControllo[i] + 1;
				}

				// correzzione
				if (bitErrato != 0 && (bitErrato - 1) < (m + r)) {

					// System.out.println("Correggo bit " + (bitErrato - 1) +
					// "/" +
					// (k + bitErrato - 1));

					msg[k + bitErrato - 1] = !msg[k + bitErrato - 1];
				}

				// selezione dei bit di messaggio
				int i = 0;
				int j = 0;// 2
				int c = 0;
				while (i < m) {
					if (c < bitControllo.length && j == bitControllo[c]) {
						j++;
						c++;
					} else {
						risultato[ris + i] = msg[k + j];
						j++;
						i++;
					}
				}

			}

			 ///System.out.println("decoder output:");
			 //test.PrintUtil.stampa(msg, m+r);

			return decode(risultato, iterazioni - 1);
		}
	}

}
