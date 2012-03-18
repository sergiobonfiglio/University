/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ssis.ecc.reedSolomon;

import ssis.ecc.ECC;

public abstract class RS_ECC implements ECC {

    protected int n;
    protected int m;
    protected int iterazioni;
    public static final int STANDARD_N = 10;
    public static final int STANDARD_M = 2;
    public static final int STANDARD_ITER = 1;

    public RS_ECC(int n, int m, int iterazioni) {
        this.n = n;
        this.m = m;
        this.iterazioni = iterazioni;
    }

    public RS_ECC(int n, int m) {
        this.n = n;
        this.m = m;
        this.iterazioni = STANDARD_ITER;
    }

    public RS_ECC() {
        this.m = STANDARD_M;
        this.n = STANDARD_N;
        this.iterazioni = STANDARD_ITER;
    }

    public long getEncodedCharMsgLength(String msg) {
        return getEncodedBitMsgLength(msg.length() * 16);
    }

    public long getEncodedBitMsgLength(long bitMsgLength) {
        //per ogni bit ho
        return n/m * 8 * bitMsgLength;
    }
}
