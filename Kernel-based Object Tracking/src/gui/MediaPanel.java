package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;

/**
 * Pannello che visualizza il video con gli eventuali controlli.
 */
public class MediaPanel extends JPanel {

    ImagePanel imgPanel;
    private GlassPane glassPane;
    private JLayeredPane layeredPane;

    public MediaPanel(Component video, Component controls) {

	setLayout(new BorderLayout());

	if (video != null) {

	    imgPanel = new ImagePanel();

	    Dimension videoSize = video.getPreferredSize();
	    video.setSize(new Dimension(videoSize.width, videoSize.height));

	    layeredPane = initLayeredPane(video);

	    add(layeredPane, BorderLayout.CENTER);

	    int controlsHeight = 0;
	    if (controls != null) {
		controlsHeight = controls.getHeight();
		add(controls, BorderLayout.SOUTH);
	    }

	    this.setPreferredSize(new Dimension((int) layeredPane
		    .getPreferredSize().getWidth(), (int) layeredPane
		    .getPreferredSize().getHeight()
		    + controlsHeight));
	}

    }

    public ImagePanel getImgPanel() {
	return imgPanel;
    }

    private JLayeredPane initLayeredPane(Component video) {
	Dimension videoSize = video.getPreferredSize();

	JLayeredPane layeredPane = new JLayeredPane();
	this.imgPanel.getPreferredSize();
	layeredPane.setPreferredSize(imgPanel.getPreferredSize());

	layeredPane.add(imgPanel, Integer.valueOf(0));

	setGlassPane(new GlassPane());
	getGlassPane().setSize(videoSize);
	layeredPane.add(getGlassPane(), Integer.valueOf(2));
	return layeredPane;
    }

    public void setGlassPane(GlassPane glassPane) {
	this.glassPane = glassPane;
    }

    public GlassPane getGlassPane() {
	return glassPane;
    }

    public void setImage(BufferedImage img) {
	this.imgPanel.setImage(img);
	this.glassPane.setSize(imgPanel.getSize());

	this.layeredPane.setSize(imgPanel.getSize());

	this.setPreferredSize(layeredPane.getSize());
    }

}
