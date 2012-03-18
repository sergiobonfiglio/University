package gui.menu;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

public class BarraMenu extends JMenuBar {

    JMenu menuFile;
    JMenu menuTracker;

    public BarraMenu() {
	menuFile = new MenuFile();
	menuTracker = new MenuTracker();

	add(menuFile);
	add(menuTracker);
    }

}
