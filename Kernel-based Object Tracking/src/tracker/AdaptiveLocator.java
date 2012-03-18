package tracker;

import java.awt.image.BufferedImage;

import target.Region;
import target.RegionCoefficient;
import target.TargetCandidate;
import target.TargetModel;

/**
 * Localizza un TargetModel all'interno di un frame adattando le dimensioni del
 * modello a quelle del TargetCandidate più simile.
 */
public class AdaptiveLocator extends SimpleLocator {

    private static int RAGGIO_MINIMO;
    private static final float DELTA = 0.1f;
    private static final float GAMMA = 0.1f;

    public AdaptiveLocator(float bandaRicerca) {
	super(bandaRicerca);
    }

    public AdaptiveLocator(boolean checkBhattCoeff, int raggioMinimo,
	    float bandaRicerca) {
	super(checkBhattCoeff, bandaRicerca);
	RAGGIO_MINIMO = raggioMinimo;
    }

    public TargetModel locateModel(BufferedImage nextFrame) {

	TargetCandidate modelloCalcolato;

	modelloCalcolato = adaptiveScaleLocate(nextFrame);

	this.modello = modelloCalcolato.toTargetModel();

	return modelloCalcolato;
    }

    private TargetCandidate adaptiveScaleLocate(TargetCandidate candidato) {
	float hDeltaX = DELTA * modello.getRaggioX();
	float hDeltaY = DELTA * modello.getRaggioY();

	Region regioneOriginale = candidato.getRegion();

	RegionCoefficient regioneMax = getBestScale(candidato, hDeltaX, hDeltaY);

	Region adjustedRegion = adjustRegion(regioneOriginale, regioneMax);

	candidato.setRegion(adjustedRegion);

	return candidato;
    }

    private TargetCandidate adaptiveScaleLocate(BufferedImage nextFrame) {
	TargetCandidate candidato = new TargetCandidate(nextFrame, modello
		.getCentro(), modello.getRaggioX(), modello.getRaggioY());
	return adaptiveScaleLocate(candidato);
    }

    private RegionCoefficient getBestScale(TargetCandidate candidato,
	    float hDeltaX, float hDeltaY) {
	int raggioCandX = candidato.getRaggioX();
	int raggioCandY = candidato.getRaggioY();

	// calcolo similarità per il candidato di dimensioni minori
	RegionCoefficient regionMinus = getRegionCoefficientOfScaledCandidate(
		candidato, (int) (raggioCandX - hDeltaX),
		(int) (raggioCandY - hDeltaY));

	// calcolo similarità per il candidato di dimensioni normali
	RegionCoefficient regionSame = getRegionCoefficientOfScaledCandidate(
		candidato, raggioCandX, raggioCandY);

	// calcolo similarità per il candidato di dimensioni maggiori
	RegionCoefficient regionPlus = getRegionCoefficientOfScaledCandidate(
		candidato, (int) (raggioCandX + hDeltaX),
		(int) (raggioCandY + hDeltaY));

	RegionCoefficient regioneMax = RegionCoefficient.getMaxCoeffRegion(
		regionMinus, regionSame, regionPlus);

	if (regioneMax.getRaggioX() < RAGGIO_MINIMO
		|| regioneMax.getRy() < RAGGIO_MINIMO) {
	    regioneMax = regionSame;
	}

	return regioneMax;

    }

    /**
     * Scala un candidato, lo localizza e calcola il coefficiente di similarità
     * della regione trovata.
     * 
     * @param candidato
     *            - il candidato di cui calcolare la similarità.
     * @param raggioX
     *            - il raggio relativo alla larghezza a cui deve essere scalato
     *            il candidato
     * @param raggioY
     *            - il raggio relativo all'altezza a cui deve essere scalato il
     *            candidato
     * @return la regione col coefficiente di similarità relativo al candidato
     *         scalato.
     */
    private RegionCoefficient getRegionCoefficientOfScaledCandidate(
	    TargetCandidate candidato, int raggioX, int raggioY) {

	candidato.setRegion(candidato.getCentro(), raggioX, raggioY);

	candidato = locateModel(candidato);

	float bhattCoeff = candidato.bhattacharyyaCoeff(modello);

	RegionCoefficient region = new RegionCoefficient(candidato.getRegion(),
		bhattCoeff);
	return region;
    }

    private Region adjustRegion(Region regioneOriginale, Region optimalRegion) {

	int rx = (int) (GAMMA * optimalRegion.getRaggioX() + (1 - GAMMA)
		* (regioneOriginale.getRaggioX()));
	int ry = (int) (GAMMA * optimalRegion.getRy() + (1 - GAMMA)
		* (regioneOriginale.getRy()));

	return new Region(optimalRegion.getCentro(), rx, ry);

    }

}
