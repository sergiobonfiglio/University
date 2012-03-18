package ssis.util;

import java.awt.Point;
import java.util.Random;

import test.PrintUtil;

public class ExclusiveRandom2D extends Random {
	BitMap tabella;
	int bordo;

	public ExclusiveRandom2D(long seed, int bordo, int m, int n) {
		super(seed);
		tabella = new BitMap(m, n);
		this.bordo = bordo;
	}

	public Point nextPoint() {
		int x, y;
		int puntiMassimi = (tabella.getWidth() - 2 * bordo)
				* (tabella.getHeight() - 2 * bordo);
		
		if (tabella.contatore < puntiMassimi) {
			x = super.nextInt(tabella.getWidth() - bordo);
			y = super.nextInt(tabella.getHeight() - bordo);

			// scansione lineare dell'immagine
			while (tabella.contains(x, y) || x < bordo || y < bordo) {
				x++;
				if (x > tabella.getWidth() - bordo - 1) {
					x = bordo;
					y++;
					if (y > tabella.getHeight() - bordo - 1) {
						y = bordo;
					}
				}
			}
		} else {
			System.out.println("Punti validi finiti!");
			return new Point(-1, -1);
		}
		tabella.add(x, y);
		return new Point(x, y);
	}

	public static void main(String[] args) {
		int m=20;
		int n=21;
		ExclusiveRandom2D gen = new ExclusiveRandom2D(10, 1, m, n);

		int[][] mat = new int[m][n];


		PrintUtil.stampa(mat);

		for (int i = 0; i < (mat.length - 2) * (mat[0].length - 2) ; i++) {
			Point p = gen.nextPoint();
			mat[p.x][p.y] = 2;
		}
		PrintUtil.stampa(mat);
	}

}

class BitMap {

	boolean[][] tabella;
	int contatore;

	public BitMap(int m, int n) {
		tabella = new boolean[m][n];
		contatore = 0;
		// inizialmente sono tutti false
	}

	public void add(int x, int y) {
		tabella[x][y] = true;
		contatore++;
	}

	public boolean contains(int x, int y) {
		return tabella[x][y];
	}

	public int getWidth() {
		return tabella.length;
	}

	public int getHeight() {
		return tabella[0].length;
	}
}
