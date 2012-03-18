package ssis.noise;

import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.util.Random;

import ssis.util.ExclusiveRandom2D;

public class AlphaTrimmedFilter implements BufferedImageOp {

	private int raggio;
	private long interleaverSeed;
	int numOfEmbeddedBit;

	public AlphaTrimmedFilter(int raggio, long interleaverSeed, int numOfEmbeddedBit) {
		this.raggio = raggio;
		this.interleaverSeed = interleaverSeed;
		this.numOfEmbeddedBit = numOfEmbeddedBit;
	}

	@Deprecated
	public AlphaTrimmedFilter(int raggio) {
		this.raggio = raggio;
	}

	public BufferedImage createCompatibleDestImage(BufferedImage src,
			ColorModel destModel) {
		if (destModel == null)
			destModel = src.getColorModel();
		BufferedImage image = new BufferedImage(destModel,
				destModel.createCompatibleWritableRaster(src.getWidth(), src
						.getHeight()), destModel.isAlphaPremultiplied(), null);
		return image;
	}

	public BufferedImage filter(BufferedImage src, BufferedImage dest) {

		int w = src.getWidth();
		int h = src.getHeight();

		if (numOfEmbeddedBit != (w - raggio) * (h - raggio)) {

			src = util.ColorTransform.toGray(src);
			if (dest == null)
				dest = this.createCompatibleDestImage(src, src.getColorModel());

			dest = util.ColorTransform.toGray(dest);

			Raster raster = src.getRaster();

			Random interleaverGen = new Random(interleaverSeed);
			ExclusiveRandom2D genPunti = new ExclusiveRandom2D(interleaverGen.nextLong(),
					raggio, w, h);

			for (int l = 0; l < numOfEmbeddedBit; l++) {

				Point p = genPunti.nextPoint();
				int r = p.x;
				int c = p.y;				
				
				int somma = 0;
				int max = Integer.MIN_VALUE;
				int min = Integer.MAX_VALUE;
				for (int i = r - raggio; i < r + raggio+1; i++) {
					for (int j = c - raggio; j < c + raggio+1; j++) {
						int valore=0;
						try {
							valore = raster.getSample(i, j, 0);
						} catch (Exception e) {
							System.out.println("(r,c)=("+r+","+c+")");
							System.out.println("(i,j)=("+i+","+j+")");
							e.printStackTrace();
						}
						if (valore > max)
							max = valore;
						if (valore < min)
							min = valore;
						somma += valore;
					}
				}
				int alphaTrimmedSum = somma - min - max;
				int dimIntorno = (2 * raggio + 1) * (2 * raggio + 1);
				int alphaTrimmedMean = alphaTrimmedSum / (dimIntorno - 2);

				dest.getRaster().setSample(r, c, 0, alphaTrimmedMean);

			}

			return dest;
		}

		return filterAll(src, dest);
	}

	@Deprecated
	public BufferedImage filterAll(BufferedImage src, BufferedImage dest) {
		int w = src.getWidth();
		int h = src.getHeight();

		src = util.ColorTransform.toGray(src);
		if (dest == null)
			dest = this.createCompatibleDestImage(src, src.getColorModel());

		dest = util.ColorTransform.toGray(dest);

		Raster raster = src.getRaster();

		for (int r = raggio; r < w - raggio; r++) {
			for (int c = raggio; c < h - raggio; c++) {

				int somma = 0;
				int max = Integer.MIN_VALUE;
				int min = Integer.MAX_VALUE;
				for (int i = r - raggio; i < r + raggio + 1; i++) {
					for (int j = c - raggio; j < c + raggio + 1; j++) {
						int valore = raster.getSample(i, j, 0);
						if (valore > max)
							max = valore;
						if (valore < min)
							min = valore;
						somma += valore;
					}
				}
				int alphaTrimmedSum = somma - min - max;
				int dimIntorno = (2 * raggio + 1) * (2 * raggio + 1);
				int alphaTrimmedMean = alphaTrimmedSum / (dimIntorno - 2);

				dest.getRaster().setSample(r, c, 0, alphaTrimmedMean);
			}
		}

		return dest;
	}

	public Rectangle2D getBounds2D(BufferedImage src) {
		return src.getRaster().getBounds();
	}

	public Point2D getPoint2D(Point2D srcPoint, Point2D destPoint) {
		if (destPoint == null)
			destPoint = new Point2D.Float();
		destPoint.setLocation(srcPoint.getX(), srcPoint.getY());
		return destPoint;
	}

	public RenderingHints getRenderingHints() {
		// TODO Auto-generated method stub
		return null;
	}

}
