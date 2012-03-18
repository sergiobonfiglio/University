package tracker;

import java.awt.image.BufferedImage;

import target.Region;
import target.TargetModel;

/**
 * Classe astratta che definisce l'interfaccia di un Locator.
 */
public abstract class Locator {

    public static float BANDWIDTH_RICERCA;

    public Locator(float bandaRicerca) {
	BANDWIDTH_RICERCA = bandaRicerca;
    }

    public abstract TargetModel locateModel(BufferedImage currentFrame);

    public abstract void setModello(BufferedImage image, Region region);

    public abstract TargetModel getModello();

}
