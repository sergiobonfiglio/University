package test;

public class Generatore {

	public static String generaLettere(int lung) {
		String psw = "";
		for (int i = 0; i < lung; i++) {
			psw += (char) (Math.random() * 25 + 'a');
		}
		return psw;
	}
	
}
