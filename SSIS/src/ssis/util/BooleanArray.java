package ssis.util;

import test.PrintUtil;

public class BooleanArray {

	// bitMsg.length deve essere multiplo di 16!
	public static String boolean2str(boolean[] msg) {

		boolean[] bitMsg = msg;

		bitMsg = pad(msg, 16);

		String msgStr = "";// char = 16 bit

		for (int i = 0; i < bitMsg.length; i += 16) {
			short tmpChar = 0;
			for (int j = i; j < i + 16; j++) {// per ogni bit del carattere
				int bitCorrente = (bitMsg[j] == true) ? 1 : 0;
				tmpChar = (short) ((tmpChar << 1) | (bitCorrente));
			}
			// appendo il char appena finito di leggere
			msgStr += (char) (tmpChar);
		}

		return msgStr;
	}

	public static boolean estraiBit(char bits, int posizione) {
		return estraiBit((short) bits, posizione);
	}

	// posizione 0== meno significativa
	public static boolean estraiBit(long bits, int posizione) {
		long maschera = (1l << posizione);
		long risultato = ((maschera & bits) >> posizione);
		if (Math.abs(risultato) == 1)
			return true;
		else
			return false;

	}

	// posizione 0== meno significativa
	public static boolean estraiBit(short bits, int posizione) {
		// esempio con posizione==5 su 8 bit
		// 0 0 1 0 0 0 0 0 & (1 << posizione)
		// a b c d e f g h = bits
		// -------------------
		// 0 0 c 0 0 0 0 0 >> 5 =
		// -------------------
		// 0 0 0 0 0 0 0 c risultato

		short maschera = (short) (1 << posizione);
		short risultato = (short) ((maschera & bits) >> posizione);
		if (Math.abs(risultato) == 1)
			return true;
		else
			return false;

	}

	// posizione 0== meno significativa
	public static int inserisciBit(int sample, int bit, int posizione) {
		// esempio con posizione==5
		// 1 1 0 1 1 1 1 1 & //1<<5 ^ 0xff (mashera relativa alla posizione)
		// 7 6 5 4 3 2 1 0 = //byte originale
		// ------------------
		// 7 6 0 4 3 2 1 0 |
		// 0 0 b 0 0 0 0 0 = //b<<5 | (1<<5 ^ 0xff)
		// ------------------
		// 7 6 b 4 3 2 1 0

		int maschera = (0xffffffff ^ (1 << posizione));
		return (maschera & sample) | (bit << posizione);
	}
	
	// posizione 0== meno significativa
	public static long inserisciBit(long sample, long bit, int posizione) {
		// esempio con posizione==5
		// 1 1 0 1 1 1 1 1 & //1<<5 ^ 0xff (mashera relativa alla posizione)
		// 7 6 5 4 3 2 1 0 = //byte originale
		// ------------------
		// 7 6 0 4 3 2 1 0 |
		// 0 0 b 0 0 0 0 0 = //b<<5 | (1<<5 ^ 0xff)
		// ------------------
		// 7 6 b 4 3 2 1 0

		long m= (0xffffffff << 32) & 0xffffffff;
		long maschera = (m ^ (1l << posizione));
		return (maschera & sample) | (bit << posizione);
	}
	

	public static boolean[] long2boolean(long n) {

		boolean[] dataBit = new boolean[64];

		for (int j = 0; j < 64; j++) {
			dataBit[j] = estraiBit(n, j);
		}
		return dataBit;

	}

	public static long str2long(String str) {

		boolean[] bit = BooleanArray.str2boolean(str);
		
		long tmpLong = 0;
		for (int i = 0; i<64; i++) {
				int bitCorrente = (bit[i] == true) ? 1 : 0;
				tmpLong = inserisciBit(tmpLong, bitCorrente, i);
		}

		return tmpLong;
	}

	public static void main(String[] args) {
		
		long l= 1111;
		String str = boolean2str(long2boolean(l));
		long n= str2long(str);
		System.out.println("n="+n);
		System.out.println("str2boolean");
		test.PrintUtil.stampa(str2boolean(str), 16);
		//n=inserisciBit(n, 1, 38);
		System.out.println("n="+n);
		System.out.println("long2boolean:");
		test.PrintUtil.stampa(long2boolean(n),16);
		/*
		String strDec= boolean2str(long2boolean(n));
		System.out.println("str2boolean(boolean2str(long2boolean))");
		PrintUtil.stampa(str2boolean(strDec), 16);
		
		System.out.println("System.out.println(str2long(strDec))");
		System.out.println(str2long(strDec));
		*/
		
		
		
	}

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

	// in realtà short2boolean
	public static boolean[] short2boolean(short data) {
		boolean[] dataBit = new boolean[8];

		for (int j = 0; j < 8; j++) {
			dataBit[j] = estraiBit(data, j);
		}
		return dataBit;
	}

	public static boolean[] str2boolean(String msg) {

		boolean[] bitMsg = new boolean[msg.length() * 16];// char = 16 bit

		for (int i = 0; i < msg.length(); i++) {// per ogni char della stringa
			for (int b = 15; b >= 0; b--) {// per ogni bit del carattere
				int j = i * 16 + (15 - b);
				bitMsg[j] = estraiBit(msg.charAt(i), b);
			}
		}

		return bitMsg;
	}
}
