package ssis.ecc;

public interface ECC {

    public static final int ECC_HAMMING = 0;
    public static final int ECC_REED_SOLOMON = 1;
    public static final int ECC_COMBINED = 2;

    public long getEncodedCharMsgLength(String msg);

    public long getEncodedBitMsgLength(long bitMsgLength);
}
