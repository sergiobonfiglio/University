package io;

import java.io.IOException;
import java.util.Vector;

import javax.media.CannotRealizeException;
import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.media.Player;

public class JMFCamImporter {

    public static Player getJMFWebCam() {
	Player player = null;
	try {
	    player = initialise(autoDetect());
	} catch (Exception e) {

	}

	return player;
    }

    private static CaptureDeviceInfo autoDetect() {
	Vector<CaptureDeviceInfo> list = CaptureDeviceManager
		.getDeviceList(null);
	CaptureDeviceInfo devInfo = null;
	if (list != null) {
	    String name;
	    for (int i = 0; i < list.size(); i++) {
		devInfo = list.elementAt(i);
		name = devInfo.getName();
		if (name.startsWith("vfw:")) {
		    break;
		}
	    }
	    if (devInfo != null && devInfo.getName().startsWith("vfw:")) {
		return devInfo;
	    }
	}
	return null;
    }

    private static Player initialise(CaptureDeviceInfo _deviceInfo)
	    throws Exception {
	// avvio
	CaptureDeviceInfo webCamDeviceInfo = _deviceInfo;
	if (webCamDeviceInfo != null) {
	    // connessione alla webcam
	    System.out.println("Connessione alla webcam : "
		    + webCamDeviceInfo.getName());
	    try {

		MediaLocator ml = webCamDeviceInfo.getLocator();
		if (ml != null) {
		    Player player = Manager.createRealizedPlayer(ml);
		    if (player != null) {
			player.start();
			// FormatControl formatControl = (FormatControl) player
			// .getControl("javax.media.control.FormatControl");
			// Format[] videoFormats =
			// webCamDeviceInfo.getFormats();
			// Component visualComponent =
			// player.getVisualComponent();

			// MediaFrame.getInstance().setVideo(player);
			return player;
		    } else {
			System.out
				.println("Errore nella creazione del player.");
			return null;
		    }
		} else {
		    System.out.println("Nessun MediaLocator per: "
			    + webCamDeviceInfo.getName());

		    return null;
		}
	    } catch (IOException ioEx) {
		return null;
	    } catch (NoPlayerException npex) {
		return null;
	    } catch (CannotRealizeException nre) {
		return null;
	    }
	} else {
	    return null;
	}
    }

}
