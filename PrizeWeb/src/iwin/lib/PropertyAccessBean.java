package iwin.lib;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * taidi (20120702 已上正式)
 * @author Refactor by leotu@nec.com.tw
 * @update 20120907 taidi
 */
public class PropertyAccessBean {
	protected static final Logger log = LoggerFactory.getLogger(PropertyAccessBean.class);

	protected ResourceBundle errMsgResource;

	public void initResourceBundle(String nationCode, String... fileName) {
		if (nationCode == null || nationCode.equals("")) {
			nationCode = "zh_TW";
		}
		// DebuggerAccessBean debugger = new DebuggerAccessBean();
		// debugger.print(this);

		StringTokenizer st = new StringTokenizer(nationCode, "_");
		int i = 0;
		String nation[] = { "", "", "" };
		while (st.hasMoreElements()) {
			nation[i] = st.nextToken();
			i++;
		}
		Locale curLocale = new Locale(nation[0], nation[1]);
		errMsgResource = iwin.util.ResourceBundle.getBundle(fileName, curLocale);
	}

	public String getMessage(String keyCode) {
		String errorString = null;
		try {
			errorString = errMsgResource.getString(keyCode.trim());
		} catch (Exception e) {
			errorString = keyCode;
			log.info("keyCode=[" + keyCode + "]", e);
		}
		return errorString;
	}
	
	public String getMessage(String keyCode, Object ...args) {
		String errorString = null;
		try {
			errorString = errMsgResource.getString(keyCode.trim());
			return MessageFormat.format(errorString, args);
		} catch (Exception e) {
			errorString = keyCode;
			log.info("keyCode=[" + keyCode + "]", e);
		}
		return errorString;
	}

	public Enumeration<String> getKeys() {
		return errMsgResource.getKeys();
	}

	// =================================
	/**
	 * Insert the method's description here. Creation date: (2001/3/1 AM 09:54:41)
	 * 
	 * @return java.lang.String
	 * @param nationCode
	 *            java.lang.String
	 * @param errorCode
	 *            java.lang.String
	 */
	static public String getMessage(String nationCode, String keyCode, String... fileNames) {
		String nation[] = { "", "", "" };
		String errorString = null;

		if (nationCode != null && nationCode.equals("Default")) {
			return keyCode;
		}
		if (nationCode == null || nationCode.equals("")) {
			nationCode = "zh_TW";
		}
		// DebuggerAccessBean debugger = new DebuggerAccessBean();
		// debugger.print(this);

		StringTokenizer st = new StringTokenizer(nationCode, "_");
		int i = 0;
		while (st.hasMoreElements()) {
			nation[i] = st.nextToken();
			i++;
		}
		Locale curLocale = new Locale(nation[0], nation[1]);
		ResourceBundle errMsg = iwin.util.ResourceBundle.getBundle(fileNames, curLocale);
		try {
			errorString = errMsg.getString(keyCode.trim());
		} catch (Exception e) {
			errorString = keyCode;
			log.info("nationCode=[" + nationCode + "], keyCode=[" + keyCode + "], fileNames=[" + fileNames + "], " + e);
		}
		return errorString;
	}

	// public void clearCache(){
	// try {
	// java.lang.reflect.Field cacheList = iwin.util.ResourceBundle.class.getDeclaredField("cacheList");
	// cacheList.setAccessible(true);
	// SoftCache cache = (SoftCache) cacheList.get(null);
	// cache.clear();
	// cacheList.setAccessible(false);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
}
