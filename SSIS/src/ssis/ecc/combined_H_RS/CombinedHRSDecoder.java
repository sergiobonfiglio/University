package ssis.ecc.combined_H_RS;

import ssis.ecc.ECCDecoder;
import ssis.ecc.ECCFactory;
import ssis.util.BooleanArray;

public class CombinedHRSDecoder extends CombinedHammRS implements ECCDecoder {

	private ECCDecoder decHamming;
	private ECCDecoder decRS;

	
	public CombinedHRSDecoder() {
		super();
		decHamming = ECCFactory.getDecoderInstance(HAMMING_N, HAMMING_M, HAMMING_ITERATION, ECC_HAMMING);
		decRS = ECCFactory.getDecoderInstance(RS_N,RS_M, RS_ITERATION, ECC_REED_SOLOMON);
	}

	public CombinedHRSDecoder(int n, int m, int iterazioni) {
		super(n, m, iterazioni);
		decHamming = ECCFactory.getDecoderInstance(n, m, iterazioni,
				ECC_HAMMING);
		decRS = ECCFactory.getDecoderInstance(n, m, iterazioni,
				ECC_REED_SOLOMON);
	}

	public CombinedHRSDecoder(int n, int m) {
		super(n, m);
		decHamming = ECCFactory.getDecoderInstance(n, m, ECC_HAMMING);
		decRS = ECCFactory.getDecoderInstance(n, m, ECC_REED_SOLOMON);
	}

	public String decode(boolean[] bitMsg) {

		return decRS.decode(BooleanArray.str2boolean(decHamming.decode(bitMsg)));

	}

	public long getEncodedBitMsgLength(long bitMsgLength) {
		return decHamming.getEncodedBitMsgLength(decRS.getEncodedBitMsgLength(bitMsgLength));
	}

	public long getEncodedCharMsgLength(String msg) {
		return getEncodedBitMsgLength(msg.length()*16);
	}
	
}
