package tracker;

import gui.menu.SliderMenuItem;

import javax.swing.JCheckBoxMenuItem;

/**
 * Questa classi contiene le opzioni che determinano il comportamento del
 * Locator. Mediatore tra algoritmo e GUI.
 */
public class LocatorOptions {

    private JCheckBoxMenuItem adattaScala;
    private JCheckBoxMenuItem controllaCoefficiente;
    private SliderMenuItem raggioMinimo;
    private SliderMenuItem bandaRicerca;

    private static LocatorOptions instance = new LocatorOptions();

    private LocatorOptions() {
    }

    public static LocatorOptions getInstance() {
	return instance;
    }

    public boolean isAdattaScala() {
	return adattaScala.isSelected();
    }

    public void registerAdattaScala(JCheckBoxMenuItem adattaScala) {
	this.adattaScala = adattaScala;
    }

    public boolean isControllaCoefficiente() {
	return controllaCoefficiente.isSelected();
    }

    public void registerControllaCoefficiente(
	    JCheckBoxMenuItem controllaCoefficiente) {
	this.controllaCoefficiente = controllaCoefficiente;
    }

    public int getRaggioMinimo() {
	return raggioMinimo.getIntValue();
    }

    public void registerRaggioMinimo(SliderMenuItem raggioMinimo) {
	this.raggioMinimo = raggioMinimo;
    }

    public float getBandaRicerca() {

	return (float) bandaRicerca.getDoubleValue();
    }

    public void registerBandaRicerca(SliderMenuItem bandaRicerca) {
	this.bandaRicerca = bandaRicerca;
    }

    @Override
    public String toString() {
	String str = "adatta scala: " + this.isAdattaScala();
	str += "\n" + "controlla coefficiente: "
		+ this.isControllaCoefficiente();
	str += "\n" + "raggio minimo: " + this.getRaggioMinimo();
	str += "\n" + "banda di ricerca: " + this.getBandaRicerca();
	return str;
    }

}