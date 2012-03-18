package test;

public class PrintUtil {

	public static void stampa(boolean[] msg, int n) {
		stampa(msg, n, '\t');
	}
	public static void stampa(boolean[] msg, int n, int m) {
		for (int i = 0; i < msg.length; i++) {
			System.out.print((msg[i] == true) ? 1 : 0);
			if ((i + 1) % n == 0 && i != 0)
				System.out.print("\t");
			if((i + 1) % m == 0 && i != 0)
				System.out.print("\n");
		}
		System.out.println();
	}
	
	public static void stampa(boolean[] msg, int n, char sep) {
		for (int i = 0; i < msg.length; i++) {
			System.out.print((msg[i] == true) ? 1 : 0);
			if ((i + 1) % n == 0 && i != 0)
				System.out.print(sep);
		}
		System.out.println();
	}
	

	public static void stampa(int[][] mat) {
		for (int r = 0; r < mat.length; r++) {
			for (int c = 0; c < mat[r].length; c++) {
				System.out.print(mat[r][c] + "\t");
			}
			System.out.println();
		}
		System.out.println("\n");
	}

	public static void stampa(int[] mat) {
		for (int c = 0; c < mat.length; c++) {
			System.out.print(mat[c] + "\t");
		}
		System.out.println();
	}
	
}
