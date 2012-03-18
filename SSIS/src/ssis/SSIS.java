package ssis;

import java.awt.image.BufferedImage;
import java.util.Random;

public abstract class SSIS {
	
	protected BufferedImage immagine;
	protected int raggio;
	protected long noiseSeed;
	protected long interleaverSeed;
	protected long modulatorSeed;
        
        protected int tipoECC;

	public SSIS(BufferedImage immagine, String pwd, int raggio, int tipoECC) {
		this.immagine=immagine;
		this.raggio = raggio;

		// inizializzo i generatori pseudocasuali
		long masterSeed = ssis.util.Digest.generaLong(pwd);
		Random seedGenerator = new Random(masterSeed);

		noiseSeed = seedGenerator.nextLong();
		interleaverSeed = seedGenerator.nextLong();
		modulatorSeed = seedGenerator.nextLong();
                
                this.tipoECC= tipoECC;

	}
	
}
