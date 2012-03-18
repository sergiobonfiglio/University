package ssis.ecc.hamming;

import ssis.ecc.ECC;

public abstract class HammingECC implements ECC {

    protected int n;
    protected int m;
    protected int r;
    protected int iterazioni;
    public static final int STANDARD_N = 3;
    public static final int STANDARD_M = 1;
    public static final int STANDARD_R = 2;
    public static final int STANDARD_ITER = 3;

    public HammingECC(int n, int m, int iterazioni) {
        this.m = m;
        this.r = n - m;
        this.iterazioni = iterazioni;
    }

    public HammingECC(int n, int m) {
        this.m = m;
        this.r = n - m;
        this.iterazioni = STANDARD_ITER;
    }

    public HammingECC() {
        this.m = STANDARD_M;
        this.r = STANDARD_R;
        this.iterazioni = STANDARD_ITER;
    }

    public long getEncodedCharMsgLength(String msg) {
        return (long) Math.pow(m + r, iterazioni) * 16 * msg.length();
    }

    public long getEncodedBitMsgLength(long bitMsgLength) {
        return (long) Math.pow(m + r, iterazioni) * bitMsgLength;
    }

    protected static int[][] generaMatriceBitControllo_bitAssociati(
            int[] bitControllo, int m, int r) {
        /*
         * genero matrice di bit di associati (quelli che scritti come bit hanno
         * un uno nella stessa colonna del bit di controllo (i bit di controllo
         * hanno un solo bit a uno in quanto potenze di 2))
         */
        int[][] bitParita = new int[bitControllo.length][((m + r) / 2) + 1];
        // per ogni bit di controllo
        for (int i = 0; i < bitControllo.length; i++) {
            // considero i numeri a partire da 1
            int bitControlloCorrente = bitControllo[i] + 1;

            /*
             * per ogni posizione della codeword a partire dal bit di controllo
             * in poi (i numeri prima di esso non possono avere il bit
             * necessario a 1 perché sono più piccoli)
             */
            for (int j = bitControlloCorrente,  cont = 0; j < (m + r) + 1; j++) {
                // se l'i-esimo bit è 1
                if (ssis.util.BooleanArray.estraiBit((short) (j >> i), 0) == true) {
                    /*
                     * fa parte dei bit per cui controllare la parità per questo
                     * bit di controllo
                     */
                    bitParita[i][cont] = j - 1;
                    cont++;
                }
            }
        }
        return bitParita;
    }

    protected static int[] generaPosizioniBitControllo(int m, int r) {
        /*
         * genero sequenza bit di controllo (potenze di 2 ma -1 per non
         * incasinare troppo gli indici)
         */
        int[] bitControllo = new int[(int) (Math.log(m + r) / Math.log(2)) + 1];
        bitControllo[0] = 0;
        bitControllo[1] = 1;
        for (int i = 2,  potenza = 4; i < bitControllo.length; i++, potenza *= 2) {
            bitControllo[i] = potenza - 1;
        }
        return bitControllo;
    }

    protected static boolean[] pad(boolean[] bitMsg, int multiplo) {
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

    /**
     * 
     * @param array
     * @return <code>true</code> se l'array di bit è dispari
     *         <code>false</code> altrimenti
     */
    protected static boolean isDispari(boolean[] array) {
        int n = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == true) {
                n++;
            }
        }
        if (n % 2 == 0) {
            return false;
        } else {
            return true;
        }
    }

    public static void main(String[] args) {
        boolean[] messaggio = new boolean[12 * 3];
        for (int i = 0; i < messaggio.length; i++) {
            messaggio[i] = (Math.random() > .5) ? true : false;
        }
        int m = 4;
        int r = 3;
        int recursion = 3;
        HammingECCEncoder hEnc = new HammingECCEncoder(m, r, recursion);
        boolean[] encMsg = hEnc.encode(messaggio);
        // boolean[] encMsg = ECCCoDec.encode1_2(messaggio,1);
        // simulo rumore

        for (int i = 0; i < encMsg.length; i++) {
            if (i % (m + r) == m) {
                // if (Math.random() < .3) {
                encMsg[i] = !encMsg[i];
            // System.out.println("cambiato: " + i);
            }
        }

        HammingECCDecoder hDec = new HammingECCDecoder(m, r, recursion);
        String str = hDec.decode(encMsg);
        boolean[] decMsg = ssis.util.BooleanArray.str2boolean(str);

        int k = 0;
        for (int i = 0; i < decMsg.length && i < messaggio.length; i++) {
            if (decMsg[i] != messaggio[i]) {
                k++;
            }
        }
        System.out.println("ORIGINALE:");
        test.PrintUtil.stampa(messaggio, m);
        System.out.println("DECODED:");
        test.PrintUtil.stampa(decMsg, m);
        System.out.println("bit errati=" + k);

        for (int i = 1; i < 11; i++) {
            System.out.print("x=" + i + ": ");
            encodeFor1ErrOnX(i);
        }

    }

    private static void encodeFor1ErrOnX(int x) {
        int m = x - 1;
        int r = rOttimale(m);
        while (m + r > x) {
            m--;
            r = rOttimale(m);
        }
        System.out.println("m=" + m + ", r=" + r);
    }

    private static int rOttimale(int m) {
        int r = 1;
        while ((m + r + 1) > Math.pow(2, r)) {
            r++;
        }
        return r;
    }
}
