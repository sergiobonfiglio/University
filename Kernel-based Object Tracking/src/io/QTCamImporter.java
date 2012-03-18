package io;

import gui.ImagePanel;
import gui.MediaFrame;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import quicktime.QTSession;
import quicktime.qd.QDGraphics;
import quicktime.qd.QDRect;
import quicktime.std.sg.SGVideoChannel;
import quicktime.std.sg.SequenceGrabber;

/**
 * Importa le immagini provenienti da una webcam compatibile con QuickTime.
 */
public class QTCamImporter extends Thread {
    SequenceGrabber sg;
    QDRect camSize;
    QDGraphics qdGraphics;

    public int[] pixelData;
    BufferedImage image;

    ImagePanel imagePanel;

    public QTCamImporter(ImagePanel imagePanel) throws Exception {
	this.imagePanel = imagePanel;

	QTSession.open();

	initSequenceGrabber();
	initBufferedImage();

	try {
	    startPreviewing();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void closeSession() {
	QTSession.close();
    }

    private void initSequenceGrabber() throws Exception {
	sg = new SequenceGrabber();
	SGVideoChannel vc = new SGVideoChannel(sg);
	camSize = vc.getSrcVideoBounds();

	qdGraphics = new QDGraphics(camSize);
	sg.setGWorld(qdGraphics, null);

	vc.setBounds(camSize);
	vc.setUsage(quicktime.std.StdQTConstants.seqGrabRecord
		| quicktime.std.StdQTConstants.seqGrabPreview
		| quicktime.std.StdQTConstants.seqGrabPlayDuringRecord);
	vc.setFrameRate(0);
	vc
		.setCompressorType(quicktime.std.StdQTConstants.kComponentVideoCodecType);
    }

    private void initBufferedImage() throws Exception {

	int size = qdGraphics.getPixMap().getPixelData().getSize();
	int intsPerRow = qdGraphics.getPixMap().getPixelData().getRowBytes() / 4;

	size = intsPerRow * camSize.getHeight();
	pixelData = new int[size];
	DataBuffer db = new DataBufferInt(pixelData, size);

	ColorModel colorModel = new DirectColorModel(32, 0x00ff0000,
		0x0000ff00, 0x000000ff);
	int[] masks = { 0x00ff0000, 0x0000ff00, 0x000000ff };
	WritableRaster raster = Raster.createPackedRaster(db, camSize
		.getWidth(), camSize.getHeight(), intsPerRow, masks, null);
	image = new BufferedImage(colorModel, raster, false, null);

    }

    private void startPreviewing() throws Exception {
	sg.setDataOutput(null,
		quicktime.std.StdQTConstants.seqGrabDontMakeMovie);
	sg.prepare(true, true);
	sg.startRecord();

	// setting up a thread, to idle the sequence grabber
	Runnable idleCamera = new Runnable() {
	    int taskingDelay = 25;

	    public void run() {
		try {
		    while (true) {
			Thread.sleep(taskingDelay);
			synchronized (sg) {
			    sg.idleMore();
			    sg.update(null);
			}
		    }
		} catch (Exception ex) {
		}
	    }
	};
	(new Thread(idleCamera)).start();
    }

    @Override
    public void run() {

	while (true) {
	    synchronized (sg) {
		qdGraphics.getPixMap().getPixelData().copyToArray(0, pixelData,
			0, pixelData.length);
		MediaFrame.getInstance().setImage(this.image, 0.3);
	    }

	    try {
		Thread.sleep(100);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
    }

}
