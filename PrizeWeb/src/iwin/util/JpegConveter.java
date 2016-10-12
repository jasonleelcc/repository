package iwin.util;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.randelshofer.media.jpeg.JPEGImageIO;

/**
 * CMYK/YCCK <br/>
 * http://vovo2000.com/phpbb2/viewtopic-332050.html <br/>
 * http://blog.miniasp.com/post/2008/07/JPEG-Image-cannot-be-displayed-in-Internet-Explorer.aspx<br/>
 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4799903<br/>
 * 
 * @author leotu@nec.com.tw
 */
public class JpegConveter {
	final protected static Logger log = LoggerFactory.getLogger(JpegConveter.class);

	private String messageKey;
	private String conveterBy;

	private boolean specialImageType = false;

	public JpegConveter(String messageKey) {
		this.messageKey = messageKey;
	}

	public BufferedImage read(File input) throws IOException {
		try {
			BufferedImage bi = ImageIO.read(input);
			conveterBy = "ImageIO";
			return bi;
		} catch (javax.imageio.IIOException e) {
			log.info("messageKey=[" + messageKey + "], input=[" + input + "], " + e.toString() + ", change to call readMore(...)");
			specialImageType = true;
			return readMore(input);
		}
	}

	public BufferedImage read(InputStream input) throws IOException {
		byte[] bAry = IOUtils.toByteArray(input);
		// log.debug("bAry.length=" + bAry.length);
		try {
			BufferedImage bi = ImageIO.read(new ByteArrayInputStream(bAry));
			conveterBy = "ImageIO";
			return bi;
		} catch (javax.imageio.IIOException e) {
			log.info("messageKey=[" + messageKey + "], input=[" + input + "], " + e.toString() + ", change to call readMore(...)");
			specialImageType = true;
			return readMore(new ByteArrayInputStream(bAry));
		}
	}

	public BufferedImage read(byte[] bAry) throws IOException {
		try {
			BufferedImage bi = ImageIO.read(new ByteArrayInputStream(bAry));
			conveterBy = "ImageIO";
			return bi;
		} catch (javax.imageio.IIOException e) {
			log.info("messageKey=[" + messageKey + "], bAry.length=[" + (bAry == null ? "<null>" : bAry.length) + "], " + e.toString()
					+ ", change to call readMore(...)");
			specialImageType = true;
			return readMore(new ByteArrayInputStream(bAry));
		}
	}

	public boolean isSpecialImageType() {
		return specialImageType;
	}

	public String getConveterBy() {
		return conveterBy;
	}

	// ==========
	protected BufferedImage readMore(File file) throws IOException {
		InputStream input = new BufferedInputStream(new FileInputStream(file), 1024 * 8);
		try {
			BufferedImage image = read(input);
			return image;
		} finally {
			if (input != null) {
				IOUtils.closeQuietly(input);
			}
		}
	}

	protected BufferedImage readMore(InputStream input) throws IOException {
		BufferedImage bi;
		bi = JPEGImageIO.read(input);
		conveterBy = "JPEGImageIO";
		return bi;
	}
}
