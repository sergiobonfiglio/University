package video.processor;

import javax.media.Codec;
import javax.media.ConfigureCompleteEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Format;
import javax.media.Manager;
import javax.media.NotConfiguredError;
import javax.media.PrefetchCompleteEvent;
import javax.media.Processor;
import javax.media.RealizeCompleteEvent;
import javax.media.UnsupportedPlugInException;
import javax.media.control.TrackControl;
import javax.media.format.VideoFormat;
import javax.media.protocol.DataSource;

import video.effects.VideoToBufferedImage;

/**
 * Questa classe si occupa della creazione di un Player JMF e della sua
 * configurazione.
 * 
 */
public class ProcessorFactory implements ControllerListener {

    private Processor processor;
    private VideoToBufferedImage effect;
    private DataSource source;

    private boolean renderable;

    private Object lock;

    public ProcessorFactory(DataSource source, boolean renderable) {
	this.source = source;
	this.effect = new VideoToBufferedImage();
	this.lock = new Object();
	this.renderable = renderable;

    }

    /**
     * Crea e configura un Player in grado di trasformare i suoi fotogrammi in
     * oggetti BufferedImage.
     * 
     * @return un Player nello stato prefetched.
     */
    public Processor createPrefetchedProcessor() {

	try {
	    Manager.setHint(Manager.LIGHTWEIGHT_RENDERER, Boolean.TRUE);
	    processor = Manager.createProcessor(source);
	    processor.addControllerListener(this);

	    processor.configure();

	} catch (Exception e) {
	    e.printStackTrace();
	}

	try {
	    synchronized (lock) {
		while (processor.getState() != Processor.Prefetched)
		    lock.wait();
	    }
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}

	return processor;
    }

    public void controllerUpdate(ControllerEvent event) {

	if (event instanceof ConfigureCompleteEvent) {

	    if (renderable == true) {
		processor.setContentDescriptor(null);
	    } else {
//		Assert.assertTrue(processor
//			.setContentDescriptor(new ContentDescriptor(
//				FileTypeDescriptor.RAW)) != null);
	    }

	    TrackControl[] tracks = processor.getTrackControls();

	    Format[] supportati = this.effect.getSupportedInputFormats();

	    for (int i = 0; i < tracks.length; i++) {
		if (tracks[i].getFormat() instanceof VideoFormat) {

		    Format setted = null;
		    int j = 0;
		    while (j < supportati.length && setted == null) {

			setted = tracks[i].setFormat(supportati[j].relax());

			try {
			    Codec[] chain = { effect };
			    tracks[i].setCodecChain(chain);
			} catch (UnsupportedPlugInException e1) {
			    Format[] formati = tracks[i].getSupportedFormats();
			    System.out
				    .println("questa traccia video supporta:");
			    for (Format format : formati) {
				System.out.println("" + format + " ;");
			    }
			    // e1.printStackTrace();
			} catch (NotConfiguredError e1) {
			    e1.printStackTrace();
			}

		    }

		}

	    }
	    processor.realize();

	} else if (event instanceof RealizeCompleteEvent) {
	    processor.prefetch();

	} else if (event instanceof PrefetchCompleteEvent) {

	    synchronized (lock) {
		lock.notifyAll();
	    }

	}
    }
}
