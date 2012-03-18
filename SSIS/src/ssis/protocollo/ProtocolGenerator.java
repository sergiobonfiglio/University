package ssis.protocollo;

import ssis.util.BooleanArray;

public class ProtocolGenerator {

	private static final char START = 0xAAAA;

	public static void main(String[] args) {
		String str = addHeader("ciao", 1000);
		System.out.println(getEmbeddedLength(str));
	}

	public static String addHeader(String messaggio, long encodedMsgLength) {

		String length = BooleanArray.boolean2str(BooleanArray
				.long2boolean(encodedMsgLength));

		messaggio = START + length + messaggio;

		return messaggio;
	}

	public static boolean checkHeader(String header, int capienzaImmagine) {
		if (header.charAt(0) == START
				&& getEmbeddedLength(header) <= capienzaImmagine)
			return true;
		return false;
	}

	public static long getEmbeddedLength(String header) {
		return BooleanArray.str2long(header.substring(1));

	}

	public static int getHeaderBitLength() {
		return 16 + 64;
	}

	public static int getHeaderCharLength() {
		return getHeaderBitLength() / 16;
	}

}
