package iwin.util;

import java.io.File;

import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author leotu@nec.com.tw
 */
public class Global {
	final protected static Logger log = LoggerFactory.getLogger(Global.class);

	// check "bea.home" (like "catalina.home")
	public static final boolean IS_WEBLOGIC_APPSERVER = (System.getProperties().getProperty("bea.home") != null);
	public static final boolean IS_JBOSS_APPSERVER = (System.getProperties().getProperty("jboss.home.dir") != null);
	// com.sun.aas.domainsRoot, com.sun.aas.installRoot, com.sun.aas.instanceRoot
	public static final boolean IS_GLASSFISH_APPSERVER = (System.getProperties().getProperty("com.sun.aas.instanceRoot") != null)
			|| (System.getProperties().getProperty("com.sun.aas.domainsRoot") != null);
	public static final boolean IS_TOMCAT = (System.getProperties().getProperty("catalina.home") != null);

	public static final String REFERENCE_PATH;
	public static final String SERVER_NAME;

	public static final String MDC_TX_UUID_KEY = "txn-uuid"; // TODO: for logback
	public static final String KEEP_PREVIOUS_MDC_TX_UUID_KEY = Global.class.getName() + ".KEEP_PREVIOUS_MDC_TX_UUID_KEY"; // TODO: for Dispatcher

	public static final String IE6 = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)";
	public static final String IE7 = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; GTB6; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)";

	static {
		REFERENCE_PATH = System.getProperty("ReferencePath");
		SERVER_NAME = System.getProperty("ServerName");
		log.debug("System.getProperty(\"ReferencePath\")=[" + REFERENCE_PATH + "]");
		log.debug("System.getProperty(\"ServerName\")=[" + SERVER_NAME + "]");
		//
		if (REFERENCE_PATH == null) {
			log.error("Can't find System Property: ReferencePath");
			Thread.dumpStack();
			throw new ExceptionInInitializerError("Can't find System Property: ReferencePath");
		}
		if (SERVER_NAME == null) {
			log.error("Can't find System Property: ServerName");
			Thread.dumpStack();
			throw new ExceptionInInitializerError("Can't find System Property: ServerName");
		}
		//
		if (SystemUtils.IS_OS_WINDOWS) {
			log.debug("IS_OS_WINDOWS=" + SystemUtils.IS_OS_WINDOWS);
		} else if (SystemUtils.IS_OS_LINUX) {
			log.debug("IS_OS_LINUX=" + SystemUtils.IS_OS_LINUX);
		} else if (SystemUtils.IS_OS_MAC_OSX) {
			log.debug("IS_OS_MAC_OSX=" + SystemUtils.IS_OS_MAC_OSX);
		} else if (SystemUtils.IS_OS_MAC) {
			log.debug("IS_OS_MAC=" + SystemUtils.IS_OS_MAC);
		}
		//
		if (SystemUtils.IS_JAVA_1_5) {
			log.debug("IS_JAVA_1_5=" + SystemUtils.IS_JAVA_1_5);
		} else if (SystemUtils.IS_JAVA_1_6) {
			log.debug("IS_JAVA_1_6=" + SystemUtils.IS_JAVA_1_6);
		} else if (SystemUtils.IS_JAVA_1_7) {
			log.debug("IS_OS_LINUX=" + SystemUtils.IS_JAVA_1_7);
		}
		//
		log.debug("    javaHome=[" + SystemUtils.getJavaHome() + "]");
		log.debug("    userHome=[" + SystemUtils.getUserHome() + "]");
		log.debug("     userDir=[" + SystemUtils.getUserDir() + "]");
		log.debug("javaIoTmpDir=[" + SystemUtils.getJavaIoTmpDir() + "]");
		//
		if (SystemUtils.IS_OS_MAC_OSX) {
			File hornetqDataDir = new File(SystemUtils.getUserHome(), "hornetq/opt/mining");
			log.debug("IS_OS_MAC_OSX: hornetqDataDir=[" + hornetqDataDir + "]");
			// TODO: ${data.dir:/opt/mining} with xxx
			System.setProperty("data.dir", hornetqDataDir.toString());
		}
	}

	public static final String REQUEST_TIMEOUT = "com.sun.xml.internal.ws.request.timeout"; // BindingProviderProperties.REQUEST_TIMEOUT;
	public static final String CONNECT_TIMEOUT = "com.sun.xml.internal.ws.connect.timeout"; // BindingProviderProperties.CONNECT_TIMEOUT;

	public static final String FIRE_FROM_TIMER = "@timer@";
}
