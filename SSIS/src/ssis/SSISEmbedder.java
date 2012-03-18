package ssis;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import ssis.ecc.ECCFactory;
import ssis.interleavedNoiseModulator.InterleavedMoDem;
import ssis.noise.GaussianNoiseGenerator;
import ssis.protocollo.ProtocolGenerator;

public class SSISEmbedder extends SSIS {

	public SSISEmbedder(BufferedImage immagine, String pwd, int raggio,
			int tipoECC) {
		super(immagine, pwd, raggio, tipoECC);
	}

	public BufferedImage embed(String messaggio, double deviazione) {
		return this.embed(messaggio, deviazione, -1, -1, -1);
	}

	public BufferedImage embed(String messaggio, double deviazione, int m,
			int r, int eccIteration) {

		immagine = util.ColorTransform.toGray(immagine);

		int w = immagine.getWidth();
		int h = immagine.getHeight();

		double[][] matriceRumore;
		// generare rumore
		GaussianNoiseGenerator gn = new GaussianNoiseGenerator(noiseSeed);
		matriceRumore = gn.generaRumoreGaussiano(w, h, deviazione, 0);

		// generare messaggio con ECC
		ssis.ecc.ECCEncoder enc;
		boolean[] encodedMsg;
		if (m != -1) {
			enc = ECCFactory.getEncoderInstance(m, r, eccIteration, tipoECC);
		} else {
			enc = ECCFactory.getEncoderInstance(tipoECC);
		}

		// aggiungo protocollo
		messaggio = ProtocolGenerator.addHeader(messaggio, enc
				.getEncodedCharMsgLength(messaggio));
		encodedMsg = enc.encode(messaggio);

		if (enc.getEncodedCharMsgLength(messaggio) > (w - raggio)
				* (h - raggio)) {
			System.out.println("Messaggio troppo lungo");
		} else {

			// unire rumore e messaggio con ECC (sign mod)
			double[][] modulatedNoise;
			InterleavedMoDem imodem = new InterleavedMoDem(raggio,
					interleaverSeed, modulatorSeed);
			modulatedNoise = imodem.piecewiseInterleavedModulator(encodedMsg,
					matriceRumore, deviazione);
			// modulatedNoise =
			// imodem.signDetectorInterleavedModulator(encodedMsg,
			// matriceRumore);

			// unire rumore modulato e immagine
			WritableRaster raster = immagine.getRaster();
			for (int x = 0; x < modulatedNoise.length; x++) {
				for (int y = 0; y < modulatedNoise[0].length; y++) {
					int s = (int) Math.round(raster.getSample(x, y, 0)
							+ modulatedNoise[x][y]);
					raster.setSample(x, y, 0, clip(s));
				}
			}
		}
		return immagine;

	}

	private int clip(int s) {
		if (s > 255)
			s = 255;
		else if (s < 0)
			s = 0;
		return s;
	}

}
