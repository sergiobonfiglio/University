package ssis.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Digest {

	
	// genera un long dalla password per poi passarlo al generatore di numeri
	// casuali
	public static long generaLong(String psw) {
		long seed = 0;

		try {
			MessageDigest md = MessageDigest.getInstance("SHA");
			md.update(psw.getBytes());
			byte[] digest = md.digest();

			/*
			 * digest è un array di 20 byte. a me ne servono solo 8. faccio lo
			 * xor dei primi 8 byte con i secondi 8 e poi anche gli ultimi 4
			 * byte degli 8 ottenuti con gli ultimi 4 byte rimasti
			 */
			byte[] byteLong = new byte[8];
			for (int i = 0, j = byteLong.length; i < byteLong.length; i++, j++) {
				byteLong[i] = (byte) (digest[i] ^ digest[j]);
			}
			for (int i = 0, j = digest.length - 4; j < digest.length; i++, j++) {
				byteLong[i] = (byte) (byteLong[i] ^ digest[j]);
			}

			seed = (byteLong[0] << 24) | (byteLong[1] << 16)
					| (byteLong[2] << 8) | (byteLong[3]);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return seed;
	}
	
}
