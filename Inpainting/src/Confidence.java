
class Confidence {
	private double[][] MatriceC;

	private int window, path;

	private int w, h;

	private byte[][] mask;

	/**
	 * Metodo Costruttore che Inizializza La Matrice Confidence
	 * 
	 * @param matrixmaschera
	 *            la maschera dell'immagine
	 * @param width
	 *            la larghezza dell'immagine
	 * @param height
	 *            l' altezza dell'immagine
	 * @param path
	 *            la dimensione di met lato della window usata
	 */
	public Confidence(byte[][] mask, int width, int height, int window) {
		MatriceC = new double[width][height];
		this.path = window / 2;
		this.window = window;
		this.mask = mask;
		this.w = width;
		this.h = height;

		for (int j = 0; j < height; j++)
			for (int i = 0; i < width; i++) {
				// se il pixel fa parte della frontiera o di fi setto il valore
				// a 1 altrimeti se fa parte di omega metto zero
				if (mask[i][j] == PaintFrame.SOURCE)
					MatriceC[i][j] = 1;
				else
					MatriceC[i][j] = 0;

			}
	}

	/**
	 * Metodo UpdateC:Aggiorna la matrice Confidence applicando la Window
	 * 
	 * Calcola il valore di confidence (numero di pixel 'sani' all'interno della
	 * finestra centrata su di esso) dell pixel i,j e aggiorna il suo valore
	 * nella matrice MatriceC
	 * 
	 * @param i
	 *            coordinata x
	 * @param j
	 *            coordinata y
	 */
	public void UpdateC(int i, int j) {
		double somma = 0;
		for (int y = Math.max(j - path, 0); y < Math.min(j + path + 1, h - 1); y++)
			for (int x = Math.max(i - path, 0); x < Math.min(i + path + 1, w - 1); x++)
				if (mask[x][y] == PaintFrame.SOURCE) {
					somma += MatriceC[x][y];
					//
					// System.out.println("---------------------------------------");
					// System.out.println("Coordinate"+x+","+y+"="+MatriceC[x][y]);
					// System.out.println("---------------------------------------");
				}
		// System.out.println("Somma"+somma);
		MatriceC[i][j] = (double) somma / (window * window);
		// System.out.println("MatriceC"+i +","+j+"="+MatriceC[i][j]);
	}

	/**
	 * Metodo getMatriceC:Aggiorna la matrice Confidenze applicando la Window
	 * 
	 * @return MatriceC la matrice che esprime il valore di Confidence per ogni
	 *         pixel (i,j) di DELTA OMEGA
	 */
	public double[][] getMatriceC() {
		return MatriceC;
	}

	/**
	 * Metodo setValueC:Aggiorna il valore di Confidence del pixel (i,j)
	 * 
	 * @param i
	 *            coordinata x
	 * @param j
	 *            coordinata y
	 * @param valore
	 *            il valore di confidenze del pixel (i,j)
	 */
	public void setValueC(int i, int j, int valore) {
		MatriceC[i][j] = valore;
	}
}