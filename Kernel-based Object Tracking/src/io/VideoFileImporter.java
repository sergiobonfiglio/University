package io;

import java.net.URL;

import javax.media.Manager;
import javax.media.protocol.DataSource;

public class VideoFileImporter {

    public static DataSource getDataSource(URL fileUrl) {
	DataSource dataSource = null;
	try {
	    dataSource = Manager.createDataSource(fileUrl);
	} catch (Exception e) {
	    e.printStackTrace();
	}

	return dataSource;
    }

}
