package iwin.util;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author leotu@nec.com.tw
 */
public class ImageResize {
	final protected static Logger log = LoggerFactory.getLogger(ImageResize.class);

	public enum OutputFormat {
		JPG, PNG, GIF
	}

	protected static byte[] resize(OutputFormat outputFormat, String messageKey, byte[] image, int width, int height) throws IOException {
		return resize(outputFormat, messageKey, image, width, height, false, false, false);
	}

	public static byte[] resize(OutputFormat outputFormat, String messageKey, byte[] image, int width, int height, boolean forceResizeLarge,
			boolean fixedByWidth, boolean fixedByHeight) throws IOException {
		// Date startTime = new Date();
		// Date stopTime = startTime;
		byte[] newImage = image;
		try {
			JpegConveter conveter = new JpegConveter(messageKey);
			BufferedImage src = conveter.read(image); // TODO
			int srcWidth = src.getWidth(null);
			int srcHeight = src.getHeight(null);
			int destWidth = srcWidth;
			int destHeight = srcHeight;
			if (srcHeight <= height && srcWidth <= width) {
				if (conveter.isSpecialImageType()) {
					ByteArrayOutputStream out = new ByteArrayOutputStream(image.length);
					ImageIO.write(src, outputFormat.name(), out);
					newImage = out.toByteArray();
					out.close();
				}
				//
				if (forceResizeLarge && fixedByWidth && srcWidth < width) {
					destWidth = width;
					destHeight = (int) ((double) width / (double) srcWidth * srcHeight);
					//log.info("fixedByWidth: destWidth=" + destWidth + ", destHeight=" + destHeight);
					newImage = outputFormatImage(outputFormat, newImage, src, destWidth, destHeight);
				} else if (forceResizeLarge && fixedByHeight && srcHeight < height) {
					destHeight = height;
					destWidth = (int) ((double) height / (double) srcHeight * srcWidth);
					//log.info("fixedByWidth: destWidth=" + destWidth + ", destHeight=" + destHeight);
					newImage = outputFormatImage(outputFormat, newImage, src, destWidth, destHeight);
				}
				// log.debug("(srcHeight <= height && srcWidth <= width) && (conveter.isSpecialImageType()), messageKey=[" + messageKey +
				// "], width=["
				// + width + "], height=[" + height + "], srcWidth=[" + srcWidth + "], srcHeight=[" + srcHeight + "], ispecialImageType=["
				// + conveter.isSpecialImageType() + "], *conveterBy=[" + conveter.getConveterBy() + "]");
				return newImage;
			}
			if (srcWidth >= width) {
				destWidth = width;
				destHeight = (int) ((double) width / (double) srcWidth * srcHeight);
				if (!fixedByWidth && destHeight > height) {
					destHeight = height;
					destWidth = (int) ((double) height / (double) srcHeight * srcWidth);
				}
			} else { // XXX: destHeight >= height
				destHeight = height;
				destWidth = (int) ((double) height / (double) srcHeight * srcWidth);
				if (!fixedByHeight && destWidth > width) {
					destWidth = width;
					destHeight = (int) ((double) width / (double) srcWidth * srcHeight);
				}
			}
			return outputFormatImage(outputFormat, image, src, destWidth, destHeight);
		} catch (Exception e) {
			log.error("messageKey=[" + messageKey + "], outputFormat=" + outputFormat + ", " + e);
			return newImage;
		}
	}

	protected static byte[] outputFormatImage(OutputFormat outputFormat, byte[] image, BufferedImage src, int destWidth, int destHeight)
			throws IOException {
		// if (width == destWidth && height == destHeight) {
		// log.debug("messageKey=[" + messageKey + "], width=[" + width + "], height=[" + height + "], srcWidth=[" + srcWidth + "], srcHeight=["
		// + srcHeight + "], destWidth=[" + destWidth + "], destHeight=[" + destHeight + "]");
		// }
		// BufferedImage tag = new BufferedImage(destWidth, destHeight, BufferedImage.TYPE_3BYTE_BGR);
		int srcType = src.getType();
		// log.debug("messageKey=[" + messageKey + "], srcType=" + srcType);
		BufferedImage tag = new BufferedImage(destWidth, destHeight, srcType);
		Graphics g = tag.getGraphics();
		g.drawImage(src.getScaledInstance(destWidth, destHeight, Image.SCALE_SMOOTH), 0, 0, null);
		ByteArrayOutputStream out = new ByteArrayOutputStream(image.length / 4);
		ImageIO.write(tag, outputFormat.name(), out);
		byte[] newImage = out.toByteArray();
		out.close();
		//
		g.dispose();
		// stopTime = new Date();
		// long durationMillis = stopTime.getTime() - startTime.getTime();
		// if (durationMillis > 2000) {
		// String spendStr = DurationFormatUtils.formatDurationHMS(durationMillis);
		// log.info("messageKey=[" + messageKey + "], Image resize spend time=[" + spendStr + "], image.length=[" + image.length + "] to ["
		// + newImage.length + "]");
		// }
		//
		// if (conveter.isSpecialImageType()) {
		// log.debug("messageKey=[" + messageKey + "], source image.length=[" + image.length + "], dest image.length=[" + newImage.length
		// + "], width=[" + width + "], height=[" + height + "], srcWidth=[" + srcWidth + "], srcHeight=[" + srcHeight
		// + "], destWidth=[" + destWidth + "], destHeight=[" + destHeight + "], ispecialImageType=[" + conveter.isSpecialImageType()
		// + "], conveterBy=[" + conveter.getConveterBy() + "]");
		// }
		//
		return newImage;
	}
}
