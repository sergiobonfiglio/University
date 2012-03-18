package ssis.ecc.reedSolomon;

import ssis.ecc.ECCDecoder;
import ssis.util.BooleanArray;

public class RsBinaryDecode extends RS_ECC implements ECCDecoder {

	public RsBinaryDecode() {
	}

	public RsBinaryDecode(int n, int m) {
		super(n, m);
	}

	public RsBinaryDecode(int n, int m, int iterazioni) {
		super(n, m, iterazioni);
	}

	private boolean[] decode_aux(boolean[] data, int iterazioni) {

		if (iterazioni == 0){
			return data;
		}else {
			boolean[] decData = new boolean[(int) this
					.getEncodedBitMsgLength(data.length)];
			int decDataIndex = 0;

			// per ogni blocco
			for (int a = 0; a < data.length / (n * 8); a++) {
				// costruisco il blocco di m interi
				int[] blocco = new int[n];
				for (int i = 0; i < blocco.length; i++) {
					int tmpInt = 0;
					for (int j = 0; j < 8; j++) {
						int bit = (data[(i * 8) + j + (a * n * 8)]) ? 1 : 0;
						tmpInt = ssis.util.BooleanArray.inserisciBit(tmpInt,
								bit, j);
					}
					blocco[i] = tmpInt;
				}

				// li decodifico un blocco (255 interi) alla volta
				RsDecode dec = new RsDecode(n - m);
				int r = dec.decode(blocco);

				// prendo solo i primi m int del blocco di n int
				// e li trasformo in un array di boolean
				for (int i = 0; i < m; i++) {
					boolean[] tmp = ssis.util.BooleanArray
							.short2boolean((short) blocco[i]);
					for (int k = 0; k < tmp.length; k++) {
						decData[decDataIndex++] = tmp[k];
					}
				}
                                 
			}
                        
			return decode_aux(decData, iterazioni-1);
		}
	}

	public String decode(boolean[] bitMsg) {
		return BooleanArray.boolean2str(decode_aux(bitMsg, this.iterazioni));
	}

}
