package ssis.ecc.reedSolomon;

import ssis.ecc.ECCEncoder;
import ssis.util.BooleanArray;

public class RsBinaryEncode extends RS_ECC implements ECCEncoder {

	public RsBinaryEncode() {
	}

	public RsBinaryEncode(int n, int m) {
		super(n, m);
	}

	public RsBinaryEncode(int n, int m, int iterazioni) {
		super(n, m, iterazioni);
	}

	public boolean[] encode(String msg) {
		return encode(BooleanArray.str2boolean(msg));
	}

	public boolean[] encode(boolean[] data) {
		return encode(data, this.iterazioni);
	}
	
	public boolean[] encode(boolean[] data, int iterazioni) {

		if (iterazioni == 0) {
			return data;
		} else {

			data = pad(data, m * 8);

			// data deve essere multiplo di 8*m! altrimenti padding...
			// ho data.length/m*8 blocchi
			boolean[] encData = new boolean[n * 8 * (data.length / (m * 8))];
			// per ogni blocco di m*8 bit
			// costruisco gli m int con i bit
			int en = 0;
			for (int a = 0; a < (data.length / (m * 8)); a++) {

				// costruisco il blocco di m interi
				int[] blocco = new int[m];
				for (int i = 0; i < blocco.length; i++) {
					int tmpInt = 0;
					for (int j = 0; j < 8; j++) {
						int bit = (data[(i * 8) + j + (a * m * 8)]) ? 1 : 0;
						tmpInt = ssis.util.BooleanArray.inserisciBit(tmpInt,
								bit, j);
					}
					blocco[i] = tmpInt;
				}

				// codifica il blocco con n interi (n*8 bit)
				int[] parity = new int[n - m];
				RsEncode enc = new RsEncode(n - m);
				enc.encode(blocco, parity);

				// System.out.println("a=" + a);
				// System.out.println(java.util.Arrays.toString(blocco));
				// System.out.println(java.util.Arrays.toString(parity));
				/*
				 * trasforma il blocco di n int in array di n*8 bit e lo metto
				 * in encData.
				 */
				for (int j = 0; j < blocco.length; j++) {
					boolean[] int2bool = ssis.util.BooleanArray
							.short2boolean((short) blocco[j]);
					for (int k = 0; k < int2bool.length; k++) {
						encData[en++] = int2bool[k];
					}
				}
				for (int j = 0; j < parity.length; j++) {
					boolean[] int2bool = ssis.util.BooleanArray
							.short2boolean((short) parity[j]);
					for (int k = 0; k < int2bool.length; k++) {
						encData[en++] = int2bool[k];
					}
				}

			}
                        
			return encode(encData, iterazioni - 1);
		}
	}

	// TODO: metodo comune spostare
	private static boolean[] pad(boolean[] bitMsg, int multiplo) {
		boolean[] msg = bitMsg;
		// se la lunghezza non è multiplo faccio il padding
		if (bitMsg.length % multiplo != 0) {
			msg = new boolean[(multiplo * ((bitMsg.length / multiplo) + 1))];
			for (int i = 0; i < bitMsg.length; i++) {
				msg[i] = bitMsg[i];
			}
			for (int i = bitMsg.length; i < msg.length; i++) {
				msg[i] = false;
			}

		}
		return msg;
	}

	public static void main(String[] args) {
		boolean[] messaggio = new boolean[16 * 3];
		for (int i = 0; i < messaggio.length; i++) {
			messaggio[i] = (Math.random() > .5) ? true : false;
		}

		RsBinaryEncode enc = new RsBinaryEncode();
		boolean[] encMsg = enc.encode(messaggio);

		// simulo rumore
		for (int i = 0; i < encMsg.length; i++) {
			if (i % 10 == 0) {
				// if (Math.random() < .1) {
				// encMsg[i] = !encMsg[i];
				// System.out.println("cambiato: " + i);
			}
		}

		RsBinaryDecode dec = new RsBinaryDecode();
		boolean[] decMsg = BooleanArray.str2boolean(dec.decode(encMsg));

		int k = 0;
		for (int i = 0; i < decMsg.length && i < messaggio.length; i++) {
			if (decMsg[i] != messaggio[i]) {
				k++;
			}
		}
		System.out.println("ORIGINALE: " + messaggio.length);
		test.PrintUtil.stampa(messaggio, 8, 80);
		System.out.println("ENCODED: " + encMsg.length);
		test.PrintUtil.stampa(encMsg, 8, 2040);
		System.out.println("DECODED:");
		test.PrintUtil.stampa(decMsg, 8, 80);
		System.out.println("bit errati=" + k);
	}
}
