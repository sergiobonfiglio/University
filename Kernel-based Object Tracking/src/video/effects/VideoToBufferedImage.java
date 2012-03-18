/*
 * 
 */
package video.effects;

import gui.MediaFrame;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.media.Buffer;
import javax.media.Effect;
import javax.media.Format;
import javax.media.ResourceUnavailableException;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;

import com.sun.media.codec.video.colorspace.JavaRGBConverter;

/**
 * Questa classe si occupa della conversione dei fotogrammi di uno stream video
 * in oggetti BufferedImage.
 */
public class VideoToBufferedImage implements Effect {

    private Format inputFormat;
    private Format outputFormat;
    private Format[] inputFormats;
    private Format[] outputFormats;

    public VideoToBufferedImage() {

	inputFormats = getDefaultSupportedFormats();
	outputFormats = getDefaultSupportedFormats();

    }

    private Format[] getDefaultSupportedFormats() {
	Format[] supportati = new Format[] { new RGBFormat(null,// size(dimension)
		Format.NOT_SPECIFIED, // maxdatalength
		Format.byteArray, // dataType
		Format.NOT_SPECIFIED,// frameRate
		24,// bitsPerPixel
		3,// red
		2,// green
		1,// blue
		3,// pixelStride
		Format.NOT_SPECIFIED,// lineStride
		Format.TRUE,// flipped
		Format.NOT_SPECIFIED) // endian
	};
	return supportati;
    }

    private Format matches(Format in, Format outs[]) {
	for (int i = 0; i < outs.length; i++) {
	    if (in.matches(outs[i])) {
		return outs[i];
	    }
	}

	return null;
    }

    public Format[] getSupportedInputFormats() {
	return inputFormats;
    }

    public Format[] getSupportedOutputFormats(Format input) {
	if (input == null) {
	    return outputFormats;
	}

	if (matches(input, inputFormats) != null) {
	    return new Format[] { outputFormats[0].intersects(input) };
	} else {
	    return new Format[0];
	}
    }

    public int process(Buffer inBuffer, Buffer outBuffer) {

	if (outBuffer.getData() == null) {
	    outBuffer.setData(new byte[((RGBFormat) outputFormat)
		    .getMaxDataLength()]);
	}

	int outputDataLength = ((VideoFormat) outputFormat).getMaxDataLength();
	outBuffer.setLength(outputDataLength);
	outBuffer.setFormat(outputFormat);
	outBuffer.setFlags(inBuffer.getFlags());

	byte[] outData = (byte[]) outBuffer.getData();
	Dimension sizeIn = ((RGBFormat) inBuffer.getFormat()).getSize();
	Dimension sizeOut = ((RGBFormat) outBuffer.getFormat()).getSize();

	if (inBuffer.isDiscard() || inBuffer.getData() == null) {
	    return BUFFER_PROCESSED_FAILED;
	}
	if (sizeIn.width != sizeOut.width || sizeIn.height != sizeOut.height) {
	    return BUFFER_PROCESSED_FAILED;
	}
	if (outData.length < sizeIn.width * sizeIn.height * 3) {
	    return BUFFER_PROCESSED_FAILED;
	}

	try {

	    Buffer tmpBuffer = new Buffer();

	    Dimension size = ((VideoFormat) (inBuffer.getFormat())).getSize();
	    RGBFormat imgFormat = new RGBFormat(size, sizeIn.width
		    * sizeIn.height, Format.intArray, -1, 32, 0x00FF0000,
		    0x0000FF00, 0x000000FF);
	    JavaRGBConverter converterIn = getConverter(inBuffer.getFormat(),
		    imgFormat);
	    converterIn.process(inBuffer, tmpBuffer);

	    // creo immagine temporanea
	    BufferedImage tmpImg = new BufferedImage(sizeIn.width,
		    sizeIn.height, BufferedImage.TYPE_INT_RGB);
	    tmpImg.setRGB(0, 0, sizeIn.width, sizeIn.height, (int[]) tmpBuffer
		    .getData(), 0, sizeIn.width);

	    MediaFrame.getInstance().setImage(tmpImg);

	    // aggiorno il buffer temporaneo dall'immagine temporanea
	    tmpImg.getRGB(0, 0, sizeIn.width, sizeIn.height, (int[]) tmpBuffer
		    .getData(), 0, sizeIn.width);

	    JavaRGBConverter converterOut = getConverter(imgFormat, inBuffer
		    .getFormat());

	    converterOut.process(tmpBuffer, outBuffer);

	    return BUFFER_PROCESSED_OK;
	} catch (NullPointerException e) {
	    // e.printStackTrace();
	    // System.out.println("errore " + e);

	    outBuffer.copy(inBuffer);
	    return BUFFER_PROCESSED_FAILED;
	}

    }

    private JavaRGBConverter getConverter(Format formatIn, Format formatOut) {
	JavaRGBConverter converter = new JavaRGBConverter();
	converter.setInputFormat(formatIn);
	converter.setOutputFormat(formatOut);
	try {
	    converter.open();
	} catch (Exception e) {
	}
	return converter;
    }

    public Format setInputFormat(Format input) {
	inputFormat = input;
	return input;
    }

    public void close() {
    }

    public String getName() {
	return "Object Tracker Effect";
    }

    public void open() throws ResourceUnavailableException {
    }

    public void reset() {
    }

    public Object getControl(String arg0) {
	return null;
    }

    public Object[] getControls() {
	return null;
    }

    byte[] validateByteArraySize(Buffer buffer, int newSize) {
	Object objectArray = buffer.getData();
	byte[] typedArray;

	if (objectArray instanceof byte[]) {
	    typedArray = (byte[]) objectArray;
	    if (typedArray.length >= newSize) {
		return typedArray;
	    }

	    byte[] tempArray = new byte[newSize];
	    System.arraycopy(typedArray, 0, tempArray, 0, typedArray.length);
	    typedArray = tempArray;
	} else {
	    typedArray = new byte[newSize];
	}

	buffer.setData(typedArray);
	return typedArray;
    }

    public Format setOutputFormat(Format output) {
	if (output == null || matches(output, outputFormats) == null) {
	    return null;
	}
	RGBFormat incoming = (RGBFormat) output;

	Dimension size = incoming.getSize();
	int maxDataLength = incoming.getMaxDataLength();
	int lineStride = incoming.getLineStride();
	float frameRate = incoming.getFrameRate();
	int flipped = incoming.getFlipped();
	int endian = incoming.getEndian();

	if (size == null) {
	    return null;
	}
	if (maxDataLength < size.width * size.height * 3) {
	    maxDataLength = size.width * size.height * 3;
	}
	if (lineStride < size.width * 3) {
	    lineStride = size.width * 3;
	}
	if (flipped != Format.FALSE) {
	    flipped = Format.FALSE;
	}

	outputFormat = outputFormats[0].intersects(new RGBFormat(size,
		maxDataLength, null, frameRate, Format.NOT_SPECIFIED,
		Format.NOT_SPECIFIED, Format.NOT_SPECIFIED,
		Format.NOT_SPECIFIED, Format.NOT_SPECIFIED, lineStride,
		Format.NOT_SPECIFIED, Format.NOT_SPECIFIED));

	return outputFormat;
    }

}
