package iwin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.Set;

/**
 * 
 * @author leotu@nec.com.tw
 */
public class ResourceBundle {

	public static java.util.ResourceBundle getBundle(String baseName) {
		return getBundle(baseName, Locale.getDefault());
	}

	public static java.util.ResourceBundle getBundle(String baseName, Locale locale) {
		return getBundle(baseName, null, locale);
	}

	public static java.util.ResourceBundle getBundle(String baseName, String extName, Locale locale) {
		return getBundle(new String[] { baseName }, extName, locale);
	}

	// ===
	public static java.util.ResourceBundle getBundle(String baseNames[]) {
		return getBundle(baseNames, Locale.getDefault());
	}

	public static java.util.ResourceBundle getBundle(String baseNames[], Locale locale) {
		return getBundle(baseNames, null, locale);
	}

	public static java.util.ResourceBundle getBundle(String baseNames[], String extName, Locale locale) {
		return new MultiReferencePathResourceBundle(baseNames, extName, locale);
	}

	// ===
	static protected class MultiReferencePathResourceBundle extends java.util.ResourceBundle {

		protected ReferencePathResourceBundle resAry[];

		public MultiReferencePathResourceBundle(String baseName[], String extName, Locale locale) {
			if (baseName == null || baseName.length == 0) {
				throw new IllegalArgumentException("(baseName == null || baseName.length == 0)");
			}
			resAry = new ReferencePathResourceBundle[baseName.length];
			for (int i = 0; i < baseName.length; i++) {
				resAry[i] = new ReferencePathResourceBundle();
				resAry[i].init(baseName[i], extName, locale);
			}
		}

		@Override
		protected Object handleGetObject(String key) {
			Object obj = null;
			for (ReferencePathResourceBundle res : resAry) {
				obj = res.handleGetObject(key);
				if (obj != null) { // get first
					break;
				}
			}
			return obj;
		}

		@Override
		public Enumeration<String> getKeys() {
			final Set<String> set = new HashSet<String>();
			for (ReferencePathResourceBundle res : resAry) {
				Enumeration<String> e = res.getKeys();
				for (; e.hasMoreElements();) {
					String key = e.nextElement();
					if (!set.contains(key)) {
						set.add(key);
					}
				}
			}
			return new Enumeration<String>() {
				private Iterator<String> it = set.iterator();

				@Override
				public boolean hasMoreElements() {
					return it.hasNext();
				}

				@Override
				public String nextElement() {
					return it.next();
				}
			};
		}
	}

	// ===
	static protected class ReferencePathResourceBundle extends java.util.ResourceBundle {
		private PropertyResourceBundle rb = null;
		private PropertyResourceBundle rbLang = null;
		private PropertyResourceBundle rbDefault = null;
		private Set<String> set = new HashSet<String>();

		public ReferencePathResourceBundle() {
			super();
		}

		//
		public void init(String baseName, String extName, Locale locale) {
			if (baseName == null || baseName.isEmpty()) {
				throw new IllegalArgumentException("(baseName == null || baseName.isEmpty()), baseName=[" + baseName + "]");
			}
			String refpath = Global.REFERENCE_PATH;
			String classPath = refpath + "/class";
			String fileNameWithoutExt = baseName;
			if (baseName.endsWith(".properties")) { // TODO
				fileNameWithoutExt = baseName.substring(0, baseName.length() - ".properties".length());
			}
			//
			String fileExtName;
			if (extName == null || extName.isEmpty()) {
				fileExtName = ".properties";
			} else {
				fileExtName = extName;
			}
			try {
				File file = new File(classPath, fileNameWithoutExt + fileExtName);
				// System.out.println("file=[" + file + "]");
				if (file.exists()) {
					InputStream stream = new FileInputStream(file);
					rb = new PropertyResourceBundle(stream);
					stream.close();
				}
				//
				file = new File(classPath, fileNameWithoutExt + "_" + locale.getLanguage() + ".properties");
				// System.out.println("file=[" + file + "]");
				if (file.exists()) {
					InputStream stream = new FileInputStream(file);
					rbLang = new PropertyResourceBundle(stream);
					stream.close();
				}
				//
				file = new File(classPath, fileNameWithoutExt + "_" + locale + ".properties");
				// System.out.println("file=[" + file + "]");
				if (file.exists()) {
					InputStream stream = new FileInputStream(file);
					rbDefault = new PropertyResourceBundle(stream);
					stream.close();
				}
				// ===
				if (rbDefault != null) {
					for (Enumeration<String> e = rbDefault.getKeys(); e.hasMoreElements();) {
						set.add(e.nextElement());
					}
				}
				if (rbLang != null) {
					for (Enumeration<String> e = rbLang.getKeys(); e.hasMoreElements();) {
						set.add(e.nextElement());
					}
				}
				if (rb != null) {
					for (Enumeration<String> e = rb.getKeys(); e.hasMoreElements();) {
						set.add(e.nextElement());
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(baseName, e);
			}
			// TODO: checking
			if (rbDefault == null && rbLang == null && rb == null) {
				throw new RuntimeException(baseName + " file not found !");
			}
		}

		@Override
		protected Object handleGetObject(String key) {
			Object obj = null;
			if (rbDefault != null) {
				obj = rbDefault.handleGetObject(key);
				if (obj != null) {
					return obj;
				}
			}
			if (rbLang != null) {
				obj = rbLang.handleGetObject(key);
				if (obj != null) {
					return obj;
				}
			}
			if (rb != null) {
				obj = rb.handleGetObject(key);
				if (obj != null) {
					return obj;
				}
			}
			return obj;
		}

		@Override
		public Enumeration<String> getKeys() {
			return new Enumeration<String>() {
				private Iterator<String> it = set.iterator();

				@Override
				public boolean hasMoreElements() {
					return it.hasNext();
				}

				@Override
				public String nextElement() {
					return it.next();
				}
			};
		}
	}
}
