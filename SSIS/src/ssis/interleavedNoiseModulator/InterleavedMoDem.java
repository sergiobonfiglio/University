package ssis.interleavedNoiseModulator;

import java.awt.Point;
import java.util.Random;

import ssis.util.ExclusiveRandom2D;

public class InterleavedMoDem {

	private int raggio;
	private Random interleaverGenerator;
	private Random rndUniform;

	public InterleavedMoDem(int raggio, long seedInterleaver, long seedModulator) {
		this.raggio = raggio;
		this.interleaverGenerator = new Random(seedInterleaver);
		this.rndUniform = new Random(seedModulator);
	}

	
	@Deprecated
	public double[][] signDetectorInterleavedModulator(boolean[] bitMsg,
			double[][] rumoreGaussiano) {
		int width = rumoreGaussiano.length;
		int height = rumoreGaussiano[0].length;

		ExclusiveRandom2D pointGen = new ExclusiveRandom2D(interleaverGenerator
				.nextLong(), raggio, width, height);

		for (int i = 0; i < bitMsg.length; i++) {
			// genera coordinate a caso

			/*
			 * lungo il bordo non posso stimare il rumore (il filtro che lo fà è
			 * convolutivo) quindi non potrei recuperarlo
			 */
			Point p = pointGen.nextPoint();
			int x = p.x;
			int y = p.y;
			// System.out.println(i+"-Inserted:"+p);
			// modula il valore del bit col valore del rumore nel punto preso a
			// caso
			double u = rndUniform.nextDouble();
			if (bitMsg[i] == true) {// bitMsg[i]=true=1
				rumoreGaussiano[x][y] = ssis.interleavedNoiseModulator.util.StatUtil.getInvCDF(f(u), false);
				// System.out.println(i+"-invcdf(f(u)):
				// "+rumoreGaussiano[x][y]);
			} else {// bitMsg[i]=false=0
				rumoreGaussiano[x][y] = ssis.interleavedNoiseModulator.util.StatUtil.getInvCDF(u, false);
				// System.out.println(i+"-invcdf(u): "+rumoreGaussiano[x][y]);
			}

		}

		return rumoreGaussiano;

	}

	@Deprecated
	public boolean[] signDetectorInterleavedDemodulator(int lunghezzaMsg,
			double[][] modulatedSignal) {

		int width = modulatedSignal.length;
		int height = modulatedSignal[0].length;
		boolean[] messaggioStimato = new boolean[lunghezzaMsg];

		ExclusiveRandom2D pointGen = new ExclusiveRandom2D(interleaverGenerator
				.nextLong(), raggio, width, height);

		for (int i = 0; i < lunghezzaMsg; i++) {
			// genera coordinate pseudocasuali

			/*
			 * lungo il bordo non posso stimare il rumore (il filtro che lo fà è
			 * convolutivo) quindi non potrei recuperarlo
			 */
			Point p = pointGen.nextPoint();
			// System.out.println(i+"-Search:"+p);
			int x = p.x;
			int y = p.y;

			// modula il valore del bit col valore del rumore nel punto preso a
			// caso
			double u = rndUniform.nextDouble();
			double cdfu = ssis.interleavedNoiseModulator.util.StatUtil.getInvCDF(u, false);
			double cdfgu = ssis.interleavedNoiseModulator.util.StatUtil.getInvCDF(f(u), false);
			double soglia = (cdfu + cdfgu) / 2;

			// System.out.println(i+"-DEMOD:");
			// System.out.println("invcdf(f(u)): "+cdfgu);
			// System.out.println("invcdf(u): "+cdfu);
			// System.out.println("soglia: "+soglia);
			// System.out.println("segnale: "+ modulatedSignal[x][y]);

			if (Math.max(cdfu, cdfgu) == cdfgu) {
				if (modulatedSignal[x][y] > soglia) {
					messaggioStimato[i] = true;// 1
					// System.out.println("scelto invcdf(u)");//*
				} else {
					messaggioStimato[i] = false;// 0
					// System.out.println("scelto invcdf(f(u))");
				}
			} else {// if max == cdfu
				if (modulatedSignal[x][y] > soglia) {
					messaggioStimato[i] = false;// 0
					// System.out.println("scelto invcdf(u)");//*
				} else {
					messaggioStimato[i] = true;// 1
					// System.out.println("scelto invcdf(f(u))");
				}
			}

		}

		return messaggioStimato;
	}

	public double[][] piecewiseInterleavedModulator(boolean[] bitMsg,
			double[][] rumoreGaussiano, double deviazione) {
		int width = rumoreGaussiano.length;
		int height = rumoreGaussiano[0].length;
		ExclusiveRandom2D pointGen = new ExclusiveRandom2D(interleaverGenerator
				.nextLong(), raggio, width, height);
		for (int i = 0; i < bitMsg.length; i++) {
			// genera coordinate a caso
			/*
			 * lungo il bordo non posso stimare il rumore (il filtro che lo fà è
			 * convolutivo) quindi non potrei recuperarlo
			 */
			Point p = pointGen.nextPoint();
			int x = p.x;
			int y = p.y;
			 //System.out.println("Inserted:"+p);
			// modula il valore del bit col valore del rumore nel punto preso a
			// caso
			double u = rndUniform.nextDouble();
			if (bitMsg[i] == true) {// bitMsg[i]=true=1
				rumoreGaussiano[x][y] = ssis.interleavedNoiseModulator.util.StatUtil.getInvCDF(g(u), false) * deviazione;
				//System.out.println(rumoreGaussiano[x][y]);
			} else {// bitMsg[i]=false=0
				rumoreGaussiano[x][y] = ssis.interleavedNoiseModulator.util.StatUtil.getInvCDF(u, false) * deviazione;
			}

		}

		return rumoreGaussiano;
	}

	public boolean[] piecewiseInterleavedDemodulator(int numOfEmbeddedBit,
			double[][] modulatedSignal) {

		boolean[] messaggioStimato = new boolean[numOfEmbeddedBit];

		int width = modulatedSignal.length;
		int height = modulatedSignal[0].length;

		ExclusiveRandom2D pointGen = new ExclusiveRandom2D(interleaverGenerator
				.nextLong(), raggio, width, height);

		for (int i = 0; i < numOfEmbeddedBit; i++) {
			// genera coordinate pseudocasuali
			/*
			 * lungo il bordo non posso stimare il rumore (il filtro che lo fà è
			 * convolutivo) quindi non potrei recuperarlo
			 */

			Point p = pointGen.nextPoint();
			// System.out.println("Search:"+p);
			int x = p.x;
			int y = p.y;

			// modula il valore del bit col valore del rumore nel punto preso a
			// caso
			double u = rndUniform.nextDouble();
			double cdfu = ssis.interleavedNoiseModulator.util.StatUtil.getInvCDF(u, true);
			double cdfgu = ssis.interleavedNoiseModulator.util.StatUtil.getInvCDF(g(u), true);
			double soglia = (cdfu + cdfgu) / 2;

			if (Math.max(cdfu, cdfgu) == cdfgu) {
				if (modulatedSignal[x][y] > soglia)
					messaggioStimato[i] = true;// 1
				else
					messaggioStimato[i] = false;// 0
			} else {// if max == cdfu
				if (modulatedSignal[x][y] > soglia)
					messaggioStimato[i] = false;// 0
				else
					messaggioStimato[i] = true;// 1
			}

		}

		return messaggioStimato;
	}

	private double g(double u) {

		if (u < 0.5 && u >= 0) {
			return u + 0.5;
		} else if (u <= 1 && u > 0.5) {
			return u - 0.5;
		} else {
			return 0;
		}
	}

	private double f(double u) {

		return 1 - u;
	}

	public static void main(String[] args) {

		Random rnd = new Random();
		int raggio = 1;
		long seedInterleaver = rnd.nextLong();
		long seedModulator = rnd.nextLong();
		InterleavedMoDem imodem = new InterleavedMoDem(raggio, seedInterleaver,
				seedModulator);

		boolean[] msg = new boolean[16 * 2];
		for (int i = 0; i < msg.length; i++) {
			msg[i] = (Math.random() > .5) ? true : false;
		}

		double[][] rumore = new double[10][10];

		for (int x = 0; x < rumore.length; x++) {
			for (int y = 0; y < rumore[0].length; y++) {
				//rumore[x][y] = Math.random() * 255;
			}
		}

		rumore = imodem.piecewiseInterleavedModulator(msg, rumore, 25);
		 System.out.println("matrice:");
		 stampa(rumore);

		InterleavedMoDem imodem2 = new InterleavedMoDem(raggio,
				seedInterleaver, seedModulator);
		boolean[] msgEstratto = new boolean[msg.length];
		msgEstratto = imodem2.piecewiseInterleavedDemodulator(msg.length,
				rumore);

		System.out.println("Messaggio originale:");
		stampa(msg);
		System.out.println("Messaggio estratto:");
		stampa(msgEstratto);

		int sbagliati = 0;
		for (int i = 0; i < msgEstratto.length; i++) {
			if (msg[i] != msgEstratto[i])
				sbagliati++;
		}
		System.out.println("sbagliati:" + sbagliati);

	}

	private static void stampa(boolean[] msg) {
		for (int i = 0; i < msg.length; i++) {
			System.out.print((msg[i] == true) ? 1 : 0);
		}
		System.out.println("\n");
	}

	private static void stampa(double[][] mat) {
		for (int c = 0; c < mat[0].length; c++) {
			for (int r = 0; r < mat.length; r++) {
				System.out.print(mat[r][c] + "\t");
			}
			System.out.println();
		}
		System.out.println("\n\n");
	}
}
