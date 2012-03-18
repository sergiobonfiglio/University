package tracker;

/**
 * Si occupa della creazione della giusta sottoclasse Locator in base alle
 * opzioni scelte dall'utente.
 * 
 */
public class LocatorFactory {

    public static Locator createLocator(boolean adattaScala,
	    boolean controllaCoefficiente, int raggioMinimo, float bandaRicerca) {

	if (adattaScala) {
	    return new AdaptiveLocator(controllaCoefficiente, raggioMinimo,
		    bandaRicerca);
	} else {
	    return new SimpleLocator(controllaCoefficiente, bandaRicerca);
	}

    }

}
