package gui.menu;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Un pannello con titolo, uno slider e un'etichetta che mostra il valore
 * corrente dello slider.
 */
public class SliderMenuItem extends JPanel implements ChangeListener,
	PropertyChangeListener {

    JSlider slider;
    JLabel titolo;
    JLabel valoreCorrente;

    public SliderMenuItem(String titolo, int defaultValue, int min, int max,
	    ChangeListener listener) {

	this.titolo = new JLabel(titolo);
	add(this.titolo);

	slider = new JSlider();
	slider.setMaximum(max);
	slider.setMinimum(min);
	slider.setValue(defaultValue);

	// slider.setMinorTickSpacing(1);
	// slider.setPaintTicks(true);

	slider.addChangeListener(this);
	slider.addChangeListener(listener);
	add(slider);

	valoreCorrente = new JLabel("" + slider.getValue());
	add(valoreCorrente);

	this.addPropertyChangeListener(this);
	this.setOpaque(false);

    }

    public int getIntValue() {
	return slider.getValue();
    }

    public double getDoubleValue() {
	return getIntValue();
    }

    public void stateChanged(ChangeEvent e) {

	valoreCorrente.setText("" + slider.getValue());

    }

    public void propertyChange(PropertyChangeEvent evt) {
	this.titolo.setEnabled(this.isEnabled());
	this.slider.setEnabled(this.isEnabled());
	this.valoreCorrente.setEnabled(this.isEnabled());
    }

}
