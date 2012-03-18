package tracker;

import java.awt.Point;
import java.awt.image.BufferedImage;

import target.Region;
import target.TargetCandidate;
import target.TargetModel;

/**
 * Localizza un modello all'interno di un frame.
 */
public class SimpleLocator extends Locator {

    private float iterazioniTotali;
    private int locateScaledModelExec;
    private static final int MS_MAX_ITERAZIONI = 20;
    protected TargetModel modello;

    protected boolean checkBhattCoeff;

    public SimpleLocator(float bandaRicerca) {
	super(bandaRicerca);
	checkBhattCoeff = false;
    }

    public SimpleLocator(boolean checkBhattCoeff, float bandaRicerca) {
	super(bandaRicerca);
	iterazioniTotali = 0;
	locateScaledModelExec = 0;
	this.checkBhattCoeff = checkBhattCoeff;
    }

    public void setCheckBhattCoeff(boolean checkBhattCoeff) {
	this.checkBhattCoeff = checkBhattCoeff;
    }

    public TargetModel locateModel(BufferedImage nextFrame) {

	TargetCandidate modelloIniziale = new TargetCandidate(nextFrame,
		modello.getCentro(), modello.getRaggioX(), modello.getRaggioY());

	TargetCandidate modelloCalcolato;

	modelloCalcolato = locateModel(modelloIniziale);

	this.modello = modelloCalcolato.toTargetModel();

	return modelloCalcolato;

    }

    protected TargetCandidate locateModel(TargetCandidate modelloCalcolato) {
	if (modello != null) {
	    Point y0 = modello.getCentro();
	    Point y1 = y0;
	    int cont = 0;

	    do {
		y0 = y1;

		modelloCalcolato.spostaCentro(y0);

		if (checkBhattCoeff == true) {
		    float bhattCoeff0 = modelloCalcolato
			    .bhattacharyyaCoeff(modello);

		    y1 = modelloCalcolato.nextLocation(modello);

		    y1 = checkBhattCoeff(modelloCalcolato, y0, y1, bhattCoeff0);
		} else {
		    y1 = modelloCalcolato.nextLocation(modello);
		}

		cont++;
	    } while (!(y0.equals(y1)) && cont < MS_MAX_ITERAZIONI);

	    this.iterazioniTotali += cont;
	    this.locateScaledModelExec++;

	    modelloCalcolato.spostaCentro(y1);

	    return modelloCalcolato;

	}
	return null;
    }

    private Point checkBhattCoeff(TargetCandidate modelloTmp, Point _y0,
	    Point _y1, float bhattCoeff0) {

	Point y1 = _y1;
	modelloTmp.spostaCentro(y1);
	float bhattCoeff1 = modelloTmp.bhattacharyyaCoeff(modello);

	while (bhattCoeff1 < bhattCoeff0) {

	    Point tmp = y1;
	    y1 = new Point((int) ((_y0.x + y1.x) / 2d),
		    (int) ((_y0.y + y1.y) / 2d));

	    if (tmp.equals(y1)) {
		break;
	    }

	    modelloTmp.spostaCentro(y1);
	    bhattCoeff1 = modelloTmp.bhattacharyyaCoeff(modello);
	}
	return y1;
    }

    public TargetModel getModello() {
	return modello;
    }

    public void setModello(BufferedImage frame, Region regionOfInterest) {
	this.modello = new TargetModel(frame, regionOfInterest.getCentro(),
		regionOfInterest.getRaggioX(), regionOfInterest.getRy());
    }

}
