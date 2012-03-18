package ssis.ecc.combined_H_RS;

import ssis.ecc.ECCDecoder;
import ssis.ecc.ECCEncoder;
import ssis.ecc.ECCFactory;
import ssis.ecc.reedSolomon.RsBinaryDecode;
import ssis.ecc.reedSolomon.RsBinaryEncode;
import ssis.util.BooleanArray;

public class CombinedHRSEncoder extends CombinedHammRS implements ECCEncoder {

	private ECCEncoder encHamming;
	private ECCEncoder encRS;

	public CombinedHRSEncoder() {
		super();
		encHamming = ECCFactory.getEncoderInstance(HAMMING_N, HAMMING_M, HAMMING_ITERATION, ECC_HAMMING);
		encRS = ECCFactory.getEncoderInstance(RS_N,RS_M, RS_ITERATION, ECC_REED_SOLOMON);
	}

	public CombinedHRSEncoder(int n, int m, int iterazioni) {
		super(n, m, iterazioni);
		encHamming = ECCFactory.getEncoderInstance(n, m, iterazioni,
				ECC_HAMMING);
		encRS = ECCFactory.getEncoderInstance(n, m, iterazioni,
				ECC_REED_SOLOMON);
	}

	public CombinedHRSEncoder(int n, int m) {
		super(n, m);
		encHamming = ECCFactory.getEncoderInstance(n, m, ECC_HAMMING);
		encRS = ECCFactory.getEncoderInstance(n, m, ECC_REED_SOLOMON);
	}

	public boolean[] encode(String msg) {
		return encHamming.encode(encRS.encode(msg));
	}

	public boolean[] encode(boolean[] bitMsg) {
		return encHamming.encode(encRS.encode(bitMsg));
	}

	public long getEncodedBitMsgLength(long bitMsgLength) {
		return encHamming.getEncodedBitMsgLength(encRS.getEncodedBitMsgLength(bitMsgLength));
	}

	public long getEncodedCharMsgLength(String msg) {
		return getEncodedBitMsgLength(msg.length()*16);
	}
	
	
	public static void main(String[] args) {
		boolean[] messaggio = new boolean[16 * 10];
		for (int i = 0; i < messaggio.length; i++) {
			messaggio[i] = (Math.random() > .5) ? true : false;
		}

		ECCEncoder enc = ECCFactory.getEncoderInstance(ECC_COMBINED);
		boolean[] encMsg = enc.encode(messaggio);

		// simulo rumore
		for (int i = 0; i < encMsg.length; i++) {
			//if (i % 3 == 0) {
				if (Math.random() < .2) {
				 encMsg[i] = !encMsg[i];
				// System.out.println("cambiato: " + i);
			}
		}
		ECCDecoder dec = ECCFactory.getDecoderInstance(ECC_COMBINED);
		boolean[] decMsg = BooleanArray.str2boolean(dec.decode(encMsg));

		int k = 0;
		for (int i = 0; i < decMsg.length && i < messaggio.length; i++) {
			if (decMsg[i] != messaggio[i]) {
				k++;
			}
		}
		//System.out.println("ORIGINALE: " + messaggio.length);
		//test.PrintUtil.stampa(messaggio, 8, 80);
		//System.out.println("ENCODED: " + encMsg.length);
		//test.PrintUtil.stampa(encMsg, 8, 2040);
		//System.out.println("DECODED:");
		//test.PrintUtil.stampa(decMsg, 8, 80);
		System.out.println("bit errati=" + k);
	}
	
}
