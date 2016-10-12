/**
 * 
 */
package iwin.servlet;

import iwin.admin.Jmx;
import iwin.conf.Netconfig;
import iwin.front.net.TcpSocketServer;
import iwin.jms.server.JmsServer;
//import iwin.timer.HexaTimer;
import iwin.util.ResponseUtil;
//import iwin.ws.StartWsServer;

import java.io.InputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import javax.management.ObjectName;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.validator.GenericValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jdmk.comm.AuthInfo;
import com.sun.jdmk.comm.HtmlAdaptorServer;

/**
 * 
 * @author leotu@nec.com.tw
 */
public class StartupContextListener implements ServletContextListener {
	protected static final Logger log = LoggerFactory.getLogger(StartupContextListener.class);

	protected boolean debug = false;
	protected boolean debugWsServer = false;

	protected TcpSocketServer server;
	protected JmsServer jmsServer;
//	protected HexaTimer hs;
	protected Netconfig netconfig;
	//
//	protected StartWsServer wsServer = null;
	//
    public static String jmxAdapterPort = "8092"; // TODO
	protected HtmlAdaptorServer adaptor = null;
	// protected AuthInfo htmlAdaptorServerAuth = new AuthInfo("necadmin", "`1qaz2wsx"); // XXX
	protected String htmlAdaptorObjectName = null; // = "Sun:type=HtmlAdaptorServer,port=" + adapterPort;

	public static final String START_TIME_KEY = "contextStartTime";
	public static final String BUILD_VERSION_KEY = "buildVersion";
	public static final String SERVER_KEY = "server";

	public void contextDestroyed(ServletContextEvent event) {
		log.debug("...");
		ServletContext application = event.getServletContext();
//		if (hs != null) {
//			try {
//				hs.shutDown();
//				Thread.sleep(1000);
//			} catch (Exception e) {
//				log.warn(e.toString());
//			}
//		}
		//
		if (server != null) {
			try {
				server.shutDown();
				Thread.sleep(1000);
			} catch (Exception e) {
				log.warn(e.toString());
			}
		}
		//
		if (jmsServer != null) {
			try {
				jmsServer.shutDown();
				Thread.sleep(1000);
			} catch (Exception e) {
				log.warn(e.toString());
			}
		}
		//
		try {
			netconfig.destroy();
			ResponseUtil.destroy();
		} catch (Exception e) {
			log.warn(e.toString());
		}
		//
		if (adaptor != null && htmlAdaptorObjectName != null) {
			try {
				Jmx.unregisterMBean(htmlAdaptorObjectName);
				adaptor.stop();
				Thread.sleep(1000);
			} catch (Exception e) {
				log.warn(e.toString());
			}
		}
		//
		// try {
		// // TODO: for slf4j JMX <jmxConfigurator />
		// LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		// lc.stop();
		// } catch (Exception e) {
		// java.util.logging.Logger logger = java.util.logging.Logger.getLogger(StartupContextListener.class.getName());
		// logger.log(Level.WARNING, e.toString());
		// }
		//
		application.removeAttribute(BUILD_VERSION_KEY);
		application.removeAttribute(START_TIME_KEY);
		application.removeAttribute(SERVER_KEY);
		//
//		if (wsServer != null) {
//			try {
//				wsServer.shutDown();
//				Thread.sleep(1000);
//			} catch (Exception e) {
//				log.warn(e.toString());
//			}
//		}
	}

	public void contextInitialized(ServletContextEvent event) {
		//
		netconfig = Netconfig.createInstance("Netconfig.ini", true);
		//
		ServletContext application = event.getServletContext();
	    jmxAdapterPort = "8092"; //netconfig.getIniValue("server.jmx.adapter.port");
		log.debug("jmxAdapterPort=" + jmxAdapterPort);
		try {
			String jmxAdapterAdminUsername = netconfig.getIniValue("server.jmx.adapter.admin.username");
			log.debug("jmxAdapterAdminUsername=" + jmxAdapterAdminUsername);
			String jmxAdapterAdminPassword = netconfig.getIniValue("server.jmx.adapter.admin.password");
			AuthInfo htmlAdaptorServerAuth = new AuthInfo(jmxAdapterAdminUsername, jmxAdapterAdminPassword); // XXX
			//
			adaptor = new HtmlAdaptorServer();
			adaptor.addUserAuthenticationInfo(htmlAdaptorServerAuth);
			//
			htmlAdaptorObjectName = "Sun:type=HtmlAdaptorServer,port=" + jmxAdapterPort;
			log.debug("htmlAdaptorObjectName=" + htmlAdaptorObjectName);
			ObjectName adapterName = new ObjectName(htmlAdaptorObjectName);
			adaptor.setPort(Integer.parseInt(jmxAdapterPort));
			Jmx.getMBeanServer().registerMBean(adaptor, adapterName);
			adaptor.start();
		} catch (Exception e) {
			log.warn("jmxAdapterPort=" + jmxAdapterPort, e);
		}
		//
		ResponseUtil.init();
		//
		jmsServer = JmsServer.getInstance();
		jmsServer.setNetconfig(netconfig);
		jmsServer.startUp();
		//
		server = TcpSocketServer.getInstance();
		server.setNetconfig(netconfig);
		server.setEndOfStreamMatch("</DataXML>");
		String serverReadTimeoutSeconds = netconfig.getIniValue("server.read.timeout.seconds");
		if (!GenericValidator.isBlankOrNull(serverReadTimeoutSeconds)) {
			server.setReadTimeoutSeconds(Integer.parseInt(serverReadTimeoutSeconds));
		}
		String serverWriteTimeoutSeconds = netconfig.getIniValue("server.write.timeout.seconds");
		if (!GenericValidator.isBlankOrNull(serverWriteTimeoutSeconds)) {
			server.setWriteTimeoutSeconds(Integer.parseInt(serverWriteTimeoutSeconds));
		}
		String poolSize = netconfig.getIniValue("server.pool.size");
		if (!GenericValidator.isBlankOrNull(poolSize)) {
			server.setLogicThreadPoolSize(Integer.parseInt(poolSize));
		}
		server.setLogicUseWorkerThread(false);
		server.startUp();
		//
//		hs = new HexaTimer();
//		hs.startUp();
		//

		application.setAttribute(BUILD_VERSION_KEY, getBuildVersion(application));
		application.setAttribute(START_TIME_KEY, new Date());
		application.setAttribute(SERVER_KEY, server);
		//
//		if (debugWsServer) {
//			wsServer = new StartWsServer();
//			try {
//				wsServer.startUp();
//			} catch (Exception e) {
//				log.error("", e);
//			}
//		}
	}

	protected String getBuildVersion(ServletContext application) {
		String version = null;
		InputStream in = null;
		String versionFile = "/META-INF/MANIFEST.MF";
		try {
			in = application.getResourceAsStream(versionFile);
			if (in != null) {
				Properties props = new Properties();
				props.load(in);
				for (Enumeration<?> e = props.propertyNames(); e.hasMoreElements();) {
					String key = (String) e.nextElement();
					String value = props.getProperty(key);
					if (key.equalsIgnoreCase("Implementation-Version")) {
						version = value;
						log.info(versionFile + ", " + key + ":" + value);
					}
				}
			} else {
				log.warn("File: " + versionFile + " not found!");
			}
		} catch (Exception e) {
			log.warn(e.toString());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					log.warn(e.toString());
				}
			}
		}
		// (Build: <jsp:include page="/build-version.txt" />)
		if (version == null) {
			log.warn("Version not found of File: " + versionFile);
			version = "N/A";
		}
		return version;
	}
}
