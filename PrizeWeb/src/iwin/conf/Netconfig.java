package iwin.conf;

import iwin.admin.DynamicMapMBean;
import iwin.admin.Jmx;
import iwin.inireader.IniReader;
import iwin.util.Global;

import java.io.File;
import java.util.Enumeration;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author leotu@nec.com.tw
 */
public class Netconfig {
	protected static final Logger log = LoggerFactory.getLogger(Netconfig.class);

	private String config;
	private IniReader ini;
	private String referencePath;
	private String serverName;
	private DynamicMapMBean mbean = new DynamicMapMBean();
	private String jmxObjectName;
	private boolean jmxRegister = false;

	protected Netconfig() {
		log.debug("...");		
	}

	protected void init() {
		//log.debug("...");
		if (config == null || config.isEmpty()) {
			throw new RuntimeException("(config == null || config.isEmpty()), config=[" + config + "]");
		}
		referencePath = Global.REFERENCE_PATH;
		serverName = Global.SERVER_NAME;
		mbean.getModel().put("_referencePath", referencePath);
		mbean.getModel().put("_serverName", serverName);
		try {
			log.debug("referencePath=[" + referencePath + "], serverName=[" + serverName + "]");
			String configFile = referencePath + "/class/" + config;
			ini = new IniReader(configFile);
			mbean.getModel().put("_configFile", configFile);
			Properties prop = ini.getSection(serverName);
			for (Enumeration<?> e = prop.propertyNames(); e.hasMoreElements();) {
				String key = (String) e.nextElement();
				String value = prop.getProperty(key);
				mbean.getModel().put(key, value);
			}
			jmxObjectName = "Hexa:type=Netconfig,file=" + new File(configFile).getName();
			if (jmxRegister) {
				Jmx.registerMBean(this, mbean, jmxObjectName); // TODO
			}
		} catch (Exception e) {
			log.error("", e);
			throw new RuntimeException("", e);
		}
	}

	public void destroy() {
		log.debug("...");
		if (jmxRegister) {
			Jmx.unregisterMBean(jmxObjectName);
		}
	}

	public String getIniValue(String name) {
		return ini.getValue(serverName, name);
	}
	
	public boolean existKey(String name) {
		return ini.existKey(serverName, name);
	}

	// ===
	/**
	 * 
	 * @return Singleton Object
	 */
	public static Netconfig createInstance(String config, boolean jmxRegister) {
		log.debug("config=[" + config + "]");
		Netconfig instance = new Netconfig();
		instance.jmxRegister = jmxRegister;
		instance.config = config;
		instance.init();
		return instance;
	}

	public String getConfig() {
		return config;
	}

	// public IniReader getIni() {
	// return ini;
	// }

	// @Override
	// protected void finalize() throws Throwable {
	// destroy();
	// super.finalize();
	// }

	public String getServerName() {
		return serverName;
	}

	public String getReferencePath() {
		return referencePath;
	}

	
	@Override
	public String toString() {
		return "Netconfig [referencePath=" + referencePath + ", serverName=" + serverName + ", config=" + config + ", ini=" + ini + "]";
	}
}
