package ssis.ecc.hamming;

import ssis.ecc.ECCEncoder;

public class HammingECCEncoder extends HammingECC implements ECCEncoder{

	public HammingECCEncoder(int n, int m, int iterazioni) {
		super(n, m, iterazioni);
	}

	public HammingECCEncoder(int n, int m) {
		super(n, m);
	}
	
	public HammingECCEncoder() {
		super();
	}
	
	public boolean[] encode(String msg) {
		return encode(ssis.util.BooleanArray.str2boolean(msg));
	}

	public boolean[] encode(boolean[] bitMsg) {
		return encode(bitMsg, this.iterazioni);
	}
	
	private boolean[] encode(boolean[] bitMsg, int iterazioni) {

		boolean[] msg = bitMsg;

		if (iterazioni == 0) {
			return msg;
		} else {
			// TODO: controllare che r sia >= 2
			/*
			 * il messaggio deve avere un numero di bit multiplo di m. Se non è
			 * multiplo effettuo padding
			 */
			msg = pad(bitMsg, m);

			int dim = (msg.length / m) * m + (msg.length / m) * r;
			boolean[] risultato = new boolean[dim];

			int[] bitControllo = generaPosizioniBitControllo(m, r);

			int[][] bitParita = generaMatriceBitControllo_bitAssociati(
					bitControllo, m, r);

			// per ogni codeword
			for (int i = 0, j = 0; i < risultato.length; i += (m + r), j += m) {

				// prima scrivo in risultato[] i bit di messaggio
				for (int bm = 0, c = 0, d = 0; bm < m + r; bm++) {
					if (c < bitControllo.length && bm == bitControllo[c])
						c++;
					else {
						risultato[i + bm] = msg[j + d];
						d++;
					}

				}

				// per tutti i bit della codeword corrente
				for (int j2 = 0, c = 0; j2 < (m + r); j2++) {
					// inserisco solo i bit di controllo: i bit di messaggio li
					// ho
					// già scritti
					if (c < bitControllo.length && j2 == bitControllo[c]) {
						boolean[] array = generaArrayValoriBitAssociati(
								bitParita, c, risultato, i);

						/*
						 * forzo il bit di controllo a essere pari con i suoi
						 * bit associati
						 */
						risultato[i + j2] = isDispari(array);
						c++;
					}

				}

			}

			// System.out.println("encoded: ");
			// stampa(risultato, (m + r));

			return encode(risultato, iterazioni - 1);
		}

	}

	/**
	 * costruisce l'array dei bit da considerare per il calcolo di parità.
	 * 
	 * @param bitParita -
	 *            La tabella che nella posizione ij contiene la posizione
	 *            (relativa alla codeword quindi [0,m+r-1]) del j-esimo bit
	 *            associato all'i-esimo bit di controllo.
	 * @param iesimoBitControllo -
	 *            L'iesimo bit di controllo
	 * @param arrayCodeword -
	 *            L'array che contiene tutte le codeword. I bit del messaggio
	 *            devono essere già stati scritti nelle posizioni adeguate.
	 * @param iesimaCodeword -
	 *            La posizione in cui inizia la codeword corrente
	 *            nell'arrayCodeword
	 * @return L'array dei valori che insieme all'iesimo bit di controllo devono
	 *         essere pari.
	 */
	private static boolean[] generaArrayValoriBitAssociati(int[][] bitParita,
			int iesimoBitControllo, boolean[] arrayCodeword, int iesimaCodeword) {
		/*
		 * costruisco array dei bit da considerare per il calcolo di parità
		 */
		boolean[] array = new boolean[bitParita[iesimoBitControllo].length - 1];
		for (int j3 = 0; j3 < array.length; j3++) {
			/*
			 * il numero di bit da considerare realmente potrebbe essere più
			 * corto dell'array allocato. In questo caso ci saranno degli zeri
			 * che non devo considerare
			 */
			if (bitParita[iesimoBitControllo][j3 + 1] != 0) {
				array[j3] = arrayCodeword[iesimaCodeword
						+ bitParita[iesimoBitControllo][j3 + 1]];
			}
		}
		return array;
	}

}
