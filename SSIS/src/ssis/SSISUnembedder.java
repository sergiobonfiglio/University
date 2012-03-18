package ssis;

import java.awt.image.BufferedImage;

import ssis.ecc.ECCDecoder;
import ssis.ecc.ECCFactory;
import ssis.ecc.hamming.HammingECCDecoder;
import ssis.interleavedNoiseModulator.InterleavedMoDem;
import ssis.noise.NoiseEstimator;
import ssis.protocollo.ProtocolGenerator;
import eccezioni.MessageNotFoundException;

public class SSISUnembedder extends SSIS {

	public SSISUnembedder(BufferedImage immagine, String pwd, int raggio,
			int tipoECC) {
		super(immagine, pwd, raggio, tipoECC);
	}

	@Deprecated
	public String unembed(int numOfEmbeddedBit) {

		return this.unembed(numOfEmbeddedBit, -1, -1, -1);
	}

	public String unembed() throws MessageNotFoundException {

		return this.unembed(-1, -1, -1);
	}

	public String unembed(int numOfEmbeddedBit, int m, int r, int eccIteration) {

		immagine = util.ColorTransform.toGray(immagine);

		// stimare rumore
		double[][] stimaRumore = NoiseEstimator.estimateNoise(immagine, raggio,
				interleaverSeed, numOfEmbeddedBit);

		// demodulare rumore per ottenere stima messaggio ECC
		InterleavedMoDem imodem = new InterleavedMoDem(raggio, interleaverSeed,
				modulatorSeed);
		boolean[] encodedMsg = imodem.piecewiseInterleavedDemodulator(
				numOfEmbeddedBit, stimaRumore);
		// boolean[] encodedMsg=
		// imodem.signDetectorInterleavedDemodulator(lunghezza, stimaRumore);

		// demodulare stima messaggio ECC con demodulatore ECC
		ECCDecoder dec;
		if (m != -1) {
			dec = ECCFactory.getDecoderInstance(m, r, eccIteration, tipoECC);
		} else {
			dec = ECCFactory.getDecoderInstance(tipoECC);
		}

		String msg = dec.decode(encodedMsg);

		return msg;
	}

	// cerco header per sapere quanti bit devo estrarre
	public String unembed(int n, int m, int eccIteration)
			throws MessageNotFoundException {

		immagine = util.ColorTransform.toGray(immagine);

		ECCDecoder dec;
		if (n != -1)
			dec = ECCFactory.getDecoderInstance(n, m, eccIteration, tipoECC);
		else
			dec = ECCFactory.getDecoderInstance(tipoECC);

		int headerNumOfEmbeddedBit = (int) dec
				.getEncodedBitMsgLength(ProtocolGenerator.getHeaderBitLength());

		String header = unembed(headerNumOfEmbeddedBit, n, m, eccIteration);

		int w = immagine.getWidth();
		int h = immagine.getHeight();
		int capienzaImmagine = (w - raggio) * (h - raggio);
		long msgLenght = 0;
		if (ProtocolGenerator.checkHeader(header, capienzaImmagine)) {
			msgLenght = ProtocolGenerator.getEmbeddedLength(header)
					+ headerNumOfEmbeddedBit;
		} else {
			throw new MessageNotFoundException();
		}

		return unembed((int) msgLenght, n, m, eccIteration).substring(
				ProtocolGenerator.getHeaderCharLength());
	}

	public long getEncodedCharMsgLength(int lunghezzaStringa) {
		HammingECCDecoder dec = new HammingECCDecoder();
		return dec.getEncodedBitMsgLength(16L * lunghezzaStringa);
	}

	// TODO: test
	@Deprecated
	public String unembed(BufferedImage immOriginale, int numOfEmbeddedBit,
			int n, int m, int eccIteration) {
		immagine = util.ColorTransform.toGray(immagine);
		immOriginale = util.ColorTransform.toGray(immOriginale);

		// stimare rumore
		double[][] rumore = NoiseEstimator
				.estimateNoise(immagine, immOriginale);

		// demodulare rumore per ottenere stima messaggio ECC
		InterleavedMoDem imodem = new InterleavedMoDem(raggio, interleaverSeed,
				modulatorSeed);
		boolean[] encodedMsg = imodem.piecewiseInterleavedDemodulator(
				numOfEmbeddedBit, rumore);

		ECCDecoder dec;
		if (n != -1)
			dec = ECCFactory.getDecoderInstance(n, m, eccIteration, tipoECC);
		else
			dec = ECCFactory.getDecoderInstance(tipoECC);

		// demodulare stima messaggio ECC con demodulatore ECC
		String msg = dec.decode(encodedMsg);

		return msg;
	}
}
