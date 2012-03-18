package gui.menu;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Estende SliderMenuItem per gestire valori decimali.
 */
public class SliderDoubleMenuItem extends SliderMenuItem {

    public SliderDoubleMenuItem(String titolo2, double defaultValue,
	    double min, double max, ChangeListener listener) {

	super(titolo2, (int) (defaultValue * 10), (int) (min * 10),
		(int) (max * 10), listener);

	// per mostrare il valore in decimale
	this.stateChanged(null);
    }

    @Override
    public int getIntValue() {
	return -1;
    }

    @Override
    public double getDoubleValue() {
	return slider.getValue() / 10d;
    }

    @Override
    public void stateChanged(ChangeEvent e) {

	this.valoreCorrente.setText("" + slider.getValue() / 10d);

    }

}
