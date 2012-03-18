/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kMedoids;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Classe rappresentante un cluster di punti. Contiene la lista dei punti che
 * appartengono ad esso, il medoide del cluster e la somma delle distanze dei
 * punti dal medoide
 * 
 * @author Sergio Bonfiglio
 * 
 */
public class Cluster {

    public Distanceable medoide;
    public int numPunti;
    public List<Distanceable> punti;
    double sommaDistanze;

    public Cluster() {
	this.sommaDistanze = 0;
	numPunti = 0;
    }

    public Cluster(Distanceable medoide) {
	this();
	this.medoide = medoide;
    }

    /** Aggiunge al cluster il punto p e aggiorna le statistiche del cluster */
    public void addToCluster(Distanceable p) {
	if (punti == null) {
	    punti = new ArrayList<Distanceable>();
	}
	if (!punti.contains(p) && !p.equals(this.medoide)) {
	    punti.add(p);
	    sommaDistanze += p.distance(this.medoide);
	    numPunti++;
	}
    }

    /**
     * Aggiunge al cluster tutti i punti specificati nella lista e aggiorna le
     * statistiche del cluster
     */
    public void addToCluster(List<Distanceable> punti) {
	Iterator<Distanceable> iter = punti.iterator();
	while (iter.hasNext()) {
	    this.addToCluster(iter.next());
	}
    }

    /**
     * Restituisce il cluster risultante settando il medoide a newMedoid
     * 
     * @param newMedoid
     *            - il nuovo medoide del Cluster
     * @return null se il punto newMedoid non appartiene al Cluster, il Cluster
     *         costruito scambiando il vecchio medoide con quello passato come
     *         parametro altrimenti.
     */
    public Cluster calcChangedMedoid(Distanceable newMedoid) {
	if (punti != null) {
	    int indexM = punti.lastIndexOf(newMedoid);
	    if (indexM != -1) {
		Cluster c = new Cluster(punti.get(indexM));

		// aggiungo ai punti del cluster di prova sia il medoide che gli
		// altri punti
		c.addToCluster(this.medoide);
		c.addToCluster(this.punti);

		return c;
	    }
	}
	return null;
    }

    public List<Distanceable> getPoints() {
	return punti;
    }

    public double getSumOfDistance() {
	return this.sommaDistanze;

    }

}
