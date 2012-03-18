package ssis.ecc.combined_H_RS;

import ssis.ecc.ECC;

public abstract class CombinedHammRS implements ECC {

	protected int n;
	protected int m;
	protected int r;
	protected int iterazioni;

	public static final int HAMMING_N=3;
	public static final int HAMMING_M=1;
	public static final int HAMMING_ITERATION=3;

	public static final int RS_N=10;
	public static final int RS_M=4;
	public static final int RS_ITERATION=1;
	
	
	public CombinedHammRS() {
	}

	public CombinedHammRS(int n, int m) {
		this.n = n;
		this.m = m;
		this.r = n - m;
	}

	public CombinedHammRS(int n, int m, int iterazioni) {
		this.n = n;
		this.m = m;
		this.r = n - m;
		this.iterazioni = iterazioni;
	}

	public abstract long getEncodedBitMsgLength(long bitMsgLength);

	public abstract long getEncodedCharMsgLength(String msg);

}
