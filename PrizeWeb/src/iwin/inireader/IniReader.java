package iwin.inireader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Refactor by leotu@nec.com.tw
 */
public class IniReader {
	protected static final Logger log = LoggerFactory.getLogger(IniReader.class);

	protected Map<String, Properties> sections = new HashMap<String, Properties>();

	private String currentSecion;
	private Properties current;

	public IniReader(String filename) throws IOException {
		this(filename, "MS950");
	}

	public IniReader(File filename) throws IOException {
		this(filename, "MS950");
	}

	public IniReader(String filename, String charsetName) throws IOException {
		this(new File(filename), charsetName);
	}

	public IniReader(File filename, String charsetName) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), charsetName));
		try {
			read(reader);
		} finally {
			reader.close();
		}
	}

	protected void read(BufferedReader reader) throws IOException {
		String line;
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			if (line.isEmpty() || line.startsWith("#")) {
				//log.debug("Skip line=[" + line + "]");
				continue;
			}
			parseLine(line);
		}
	}

	protected void parseLine(String line) {
		if (line.matches("\\[.*\\]")) {
			currentSecion = line.replaceFirst("\\[(.*)\\]", "$1");
			Properties obj = sections.get(currentSecion);
			if (obj == null) {
				log.debug("create new secion=[" + currentSecion + "]");
				current = new Properties();
				sections.put(currentSecion, current);
			} else {
				log.debug("use exist secion=[" + currentSecion + "]");
			}
		} else if (line.matches(".*=.*")) {
			int i = line.indexOf('=');
			String name = line.substring(0, i);
			String value = line.substring(i + 1);
			// log.debug("name=[" + name + "], value=[" + value + "] of current=" + current);
			current.setProperty(name, value);
		} else {
			log.warn("Skip line=[" + line + "]");
		}
	}

	/**
	 * 
	 * @return null if not found
	 */
	public String getValue(String section, String name) {
		Properties p = (Properties) sections.get(section);
		if (p == null) {
			log.warn("(p == null), section=[" + section + "], sections=[" + sections + "]");
			return "";
		}

		String value = p.getProperty(name);
		return value;
	}
	
	public boolean existKey(String section, String name) {
		Properties p = (Properties) sections.get(section);
		if (p == null) {
			log.warn("(p == null), section=[" + section + "], sections=[" + sections + "]");
			return false;
		}

		return p.containsKey(name);
	}

	public Properties getSection(String section) {
		Properties p = (Properties) sections.get(section);
		Properties ht = new Properties();
		if (p == null) {
			return ht;
		}
		for (Enumeration<?> e = p.propertyNames(); e.hasMoreElements();) {
			String key = e.nextElement().toString();
			String desc = p.getProperty(key);
			ht.put(key, desc);
		}
		return ht;
	}

}