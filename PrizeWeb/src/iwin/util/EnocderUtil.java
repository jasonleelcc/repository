package iwin.util;

import iwin.util.ImageResize.OutputFormat;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.validator.GenericValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * taidi (20120702 已上正式)
 * Encoder Utility
 * @author leotu@nec.com.tw
 * @update 20120907 taidi
 */
public class EnocderUtil {
	protected static final Logger log = LoggerFactory.getLogger(EnocderUtil.class);

	static public class Pixel {
		private int width = -1;
		private int height = -1;

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

		public boolean isNewSize() {
			return (width > 0 && height > 0);
		}

		@Override
		public String toString() {
			return "Pixel [width=" + width + ", height=" + height + "]";
		}
	}

	public static Pixel determineImageSize(String phonePixel, boolean phonePixelTagExist) {
		if (GenericValidator.isBlankOrNull(phonePixel)) {
			// log.warn("(GenericValidator.isBlankOrNull(phonePixel)), phonePixel=[" + phonePixel + "], xmlStr=" + xmlStr);
			// retStr = ResponseUtil.getErrorCodeI18nMsgWithPrefix("X0001", errorCodePrefix);// 沒有解析度資料
			return null;
		}
		Pixel p = null;
		// 240*320的解析度已不支援
		// if ("10".equals(phonePixel)) { // 若是解析度為10(240*320)者，回傳的圖片必須縮小。
		// // 【ImagePath】改為213*53。
		// p.width = 213;
		// p.height = 53;
		// }
		if ("12".equals(phonePixel)) { // 若是解析度為12(480*800)者，回傳的圖片必須放大。 (TODO: Only for Android手機)
			p = new Pixel();
			p.width = 800;
			p.height = 480;
			log.debug("p=" + p + " of phonePixel=[" + phonePixel + "]");
		}
		return p;
	}

	protected static StringBuffer encodeBase64WithCdata(StringBuffer sb, byte[] binaryData) throws Exception {
		return encodeBase64WithCdata(sb, binaryData, null);
	}

	/**
	 * <pre>
	 * 00: 640*960 (iPhone4)
	 * 10: 240*320
	 * 11: 320:480 (iPhone3)
	 * 12: 480*800
	 * </pre>
	 * 
	 * @param sb
	 * @param binaryData
	 * @param phonePixel
	 * @return encoded Base64
	 * @throws Exception
	 */
	public static StringBuffer encodeBase64WithCdata(StringBuffer sb, byte[] binaryData, Pixel pixel) throws Exception {
		if (pixel != null && pixel.isNewSize()) {
			OutputFormat outputFormat = OutputFormat.PNG;
			byte[] img = ImageResize.resize(outputFormat, "width=" + pixel.width + ", height=" + pixel.height, binaryData, pixel.width, pixel.height,
					true, true, false);
			binaryData = img;
		}
		return surroundWithCdata(sb, encodeBase64(binaryData));
	}

	protected static String encodeBase64(byte[] binaryData) throws Exception {
		if (binaryData == null || binaryData.length == 0) {
			return "";
		}
		byte[] encodeData = Base64.encodeBase64(binaryData);
		return new String(encodeData, "ISO-8859-1");
	}

	/**
	 * @return <![CDATA[xxx]]>
	 */
	protected static StringBuffer surroundWithCdata(StringBuffer sb, String data) throws Exception {
		if (data == null) {
			return sb;
		}
		sb.append("<![CDATA[").append(data).append("]]>");
		return sb;
	}

	// ========================================
	final static private char digitAry[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' };
	final static private char digitAryLeft[] = { '9', '8', '7', '6', '5', '4', '3', '2', '1', '0', '9', '8', '7', '6', '5', '4', '3', '2', '1', '0' };
	final static private char letterAry[] = new char[26 * 2];
	final static private char letterAryLeft[] = new char[26 * 2];

	static {
		int k = 0;
		for (int c = 'A'; c <= 'Z'; k++, c++) {
			letterAry[k] = (char) c;
		}
		for (int c = 'A'; c <= 'Z'; k++, c++) {
			letterAry[k] = (char) c;
		}
		// for (int i = 0; i < letterAry.length; i++) {
		// log.debug("[" + i + "]=" + (char) letterAry[i]);
		// }
		// ===
		// log.debug("===");
		k = 0;
		for (int c = 'Z'; c >= 'A'; k++, c--) {
			letterAryLeft[k] = (char) c;
		}
		for (int c = 'Z'; c >= 'A'; k++, c--) {
			letterAryLeft[k] = (char) c;
		}
		// for (int i = 0; i < letterAryLeft.length; i++) {
		// log.debug("[" + i + "]=" + (char) letterAryLeft[i]);
		// }
	}

	/**
	 * Only for Testing
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.setProperty("ReferencePath", "C:/ZZZ/eclipse-workspace/workspace-nec/Hexa");// -DReferencePath="D:\Hexa"
		System.setProperty("ServerName", "ZIWIN"); // -DServerName="ZIWIN"
		//System.setProperty("logback.configurationFile", "logback-testing.xml"); // -Dlogback.configurationFile=logback-testing.xml

		log.debug("BEGIN...");
		try {
			EnocderUtil call = new EnocderUtil();
			String value = "A123471811";
			log.debug("value=[" + value + "]");
			String encodeValue = call.encodeReveId(value); // B357938601
			log.debug("encodeValue=[" + encodeValue + "]");
			String decodeValue = call.decodeReveId(encodeValue); // A123471811
			log.debug("decodeValue=[" + decodeValue + "]");
			//
			if (!decodeValue.equals(value)) {
				throw new Exception("(!decodeValue.equals(value))");
			}
		} catch (Throwable e) {
			log.error("", e);
		} finally {
			log.debug("END.");
		}
		System.exit(0); // TODO
	}

	/**
	 * <pre>
	 * 編碼規則:A-Z，0-9
	 * 視所在位置i往右shift  i位。盡頭迴轉。
	 * Ex: A123471811
	 * A為第1位，往右shift 1位，為B。
	 * 1為第2位，往右shift 2位，為3。
	 * 2為第3位，往右shift 3位，為5。
	 * 3為第4位，往右shift 4位，為7。
	 * 4為第5位，往右shift 5位，為9。
	 * 7為第6位，往右shift 6位，迴轉為3。
	 * 1為第7位，往右shift 7位，為8。
	 * 8為第8位，往右shift 8位，迴轉為6。
	 * 1為第9位，往右shift 9位，迴轉為8。
	 * 1為第10位，往右shift 10位，迴轉為1。
	 * </pre>
	 */
	public String encodeReveId(String value) {
		StringBuffer newReveId = new StringBuffer(value.length());
		for (int i = 1; i <= value.length(); i++) {
			char ch = value.charAt(i - 1);
			if (ch >= '0' && ch <= '9') {
				int pos = Integer.parseInt(String.valueOf(ch));
				int offset = pos + i;
				char newCh = digitAry[offset];
				newReveId.append(newCh);
			} else if (ch >= 'A' && ch <= 'Z') {
				int pos = ch - 'A';
				int offset = pos + i;
				char newCh = letterAry[offset];
				newReveId.append(newCh);
			} else {
				return null;
			}
		}
		return newReveId.toString();
	}

	public String decodeReveId(String value) {
		StringBuffer newReveId = new StringBuffer(value.length());
		for (int i = 1; i <= value.length(); i++) {
			char ch = value.charAt(i - 1);
			if (ch >= '0' && ch <= '9') {
				int pos = Integer.parseInt(String.valueOf(ch));
				int offset = pos - i;
				char newCh;
				if (offset >= 0) {
					newCh = digitAry[offset];
				} else {
					offset = -offset;
					offset--; // TODO: start from 0 index
					newCh = digitAryLeft[offset];
				}
				// log.debug("digit: i=" + i + ", ch=" + ch + ", pos=" + pos + ", offset=" + offset + ", newCh=" + newCh);
				newReveId.append(newCh);
			} else if (ch >= 'A' && ch <= 'Z') {
				int pos = ch - 'A';
				int offset = pos - i;
				char newCh;
				if (offset >= 0) {
					newCh = letterAry[offset];
				} else {
					offset = -offset;
					offset--; // TODO: start from 0 index
					newCh = letterAryLeft[offset];
				}
				// log.debug("letter: i=" + i + ", ch=" + ch + ", pos=" + pos + ", offset=" + offset + ", newCh=" + newCh);
				newReveId.append(newCh);
			} else {
				return null;
			}
		}
		return newReveId.toString();
	}
}
