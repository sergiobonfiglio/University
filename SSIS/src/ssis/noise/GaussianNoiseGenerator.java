package ssis.noise;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Random;


public class GaussianNoiseGenerator {

	private Random rndGenerator;

	public GaussianNoiseGenerator() {
		this.rndGenerator = new Random();
	}

	public GaussianNoiseGenerator(long seed) {
		this.rndGenerator = new Random(seed);
	}

	//FIXME: test
	public BufferedImage addGaussianNoise(BufferedImage immagine, boolean grayScale,
			double deviazione, double media) {

		int numBands=3;
		if(grayScale) {
			numBands=1;
		}
		WritableRaster outRaster = immagine.getRaster();
		//int numBands = outRaster.getNumBands();
		//System.out.println("NumBands" + numBands);
		for (int r = 0; r < outRaster.getWidth(); r++) {
			for (int c = 0; c < outRaster.getHeight(); c++) {
				for (int b = 0; b < numBands; b++) {
					
					
					double noise = rndGenerator.nextGaussian() * deviazione
							+ media;
					int corrente = outRaster.getSample(r, c, b);
					double corretto = corrente + noise;

					if (corretto < 0)
						corretto = 0;
					else if (corretto > 255)
						corretto = 255;

					outRaster.setSample(r, c, b, corretto);

				}
			}
		}

		return immagine;
	}

	//FIXME: test
	public BufferedImage addGaussianNoiseToGrayScale(BufferedImage immagine,
			double deviazione, double media) {

		immagine = util.ColorTransform.toGray(immagine);
		return this.addGaussianNoise(immagine, true, deviazione, media);
	}

 
	//TODO: remove
	public BufferedImage addGaussianNoiseInterleaved(BufferedImage immagine,
			double deviazione, double media) {

		immagine= util.ColorTransform.toGray(immagine);
		
		WritableRaster outRaster = immagine.getRaster();

			for (int i = 0; i < (outRaster.getHeight()*outRaster.getWidth())/3; i++) {

					int x= (int)(Math.random()*outRaster.getWidth());
					int y= (int)(Math.random()*outRaster.getHeight());
					
					double noise = rndGenerator.nextGaussian() * deviazione
							+ media;
					int corrente = outRaster.getSample(x, y, 0);
					double corretto = corrente + noise;

					if (corretto < 0)
						corretto = 0;
					else if (corretto > 255)
						corretto = 255;

					outRaster.setSample(x, y, 0, corretto);

				
			}

		return immagine;
	}
	
	
	public double[][] generaRumoreGaussiano(int w, int h, double deviazione, double media){
		double[][] matriceRumore= new double[w][h];
		
		for (int r = 0; r < matriceRumore.length; r++) {
			for (int c = 0; c < matriceRumore[0].length; c++) {
				matriceRumore[r][c] = (rndGenerator.nextGaussian() * deviazione + media);
			}
		}
		
		return matriceRumore;
	}
	// TODO remove
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
