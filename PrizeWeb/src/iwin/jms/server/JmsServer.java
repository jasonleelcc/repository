package iwin.jms.server;

import iwin.admin.AdminSupport;
import iwin.admin.DynamicMapMBean;
import iwin.admin.Jmx;
import iwin.conf.Netconfig;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.naming.InitialContext;

import org.hornetq.core.config.impl.FileConfiguration;
import org.hornetq.core.server.HornetQServer;
import org.hornetq.core.server.impl.HornetQServerImpl;
import org.hornetq.jms.server.JMSServerManager;
import org.hornetq.jms.server.impl.JMSServerManagerImpl;
import org.hornetq.spi.core.logging.LogDelegate;
import org.hornetq.spi.core.logging.LogDelegateFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author leotu@nec.com.tw
 */
public class JmsServer implements AdminSupport {
	protected static final Logger log = LoggerFactory.getLogger(JmsServer.class);

	private FileConfiguration serverConfig;
	private HornetQServer server;
	private JMSServerManager manager;
	private DynamicMapMBean mbean = new DynamicMapMBean();
	final private String jmxObjectName = "Hexa:type=JmsServer";
	private String connectionFactoryLookupName = "/ConnectionFactory";
	private boolean started = false;
	private Netconfig netconfig;

	static {
		org.hornetq.core.logging.Logger.setDelegateFactory(new LogDelegateFactory() { // TODO
					@Override
					public LogDelegate createDelegate(Class<?> clazz) {
						return new Slf4jLogDelegate(clazz);
					}
				});
	}

	protected JmsServer() {
		log.debug("...");
	}

	@Override
	public synchronized void startUp() {
		if (started) {
			log.warn("started=" + started);
			return;
		}
		mbean.getModel().put("connectionFactoryLookupName", connectionFactoryLookupName);
		try {
			String configurationUrl = "hornetq-configuration.xml";
			log.info("configurationUrl=[" + configurationUrl + "]");
			serverConfig = new FileConfiguration();
			serverConfig.setConfigurationUrl(configurationUrl);
			mbean.getModel().put("configurationUrl", configurationUrl);
			serverConfig.start();
			server = new HornetQServerImpl(serverConfig);
			String jmsConfig = "hornetq-jms.xml";
			log.info("jmsConfig=[" + jmsConfig + "]");
			mbean.getModel().put("jmsConfig", jmsConfig);
			manager = new JMSServerManagerImpl(server, jmsConfig);
			Jmx.registerMBean(this, mbean, jmxObjectName);
			manager.start();
			started = true;
			// readyPing(null);
		} catch (Exception e) {
			log.error("", e);
			throw new RuntimeException(this.toString(), e);
		}
	}

	public boolean readyPing(String queueName) {
		try {
			InitialContext ic = new InitialContext();
			ConnectionFactory cf = (ConnectionFactory) ic.lookup(connectionFactoryLookupName);
			log.debug("cf=" + cf);
			//
			if (queueName != null) {
				Queue queue = (Queue) ic.lookup(queueName);
				log.debug("queue=" + queue + " of queueName=" + queueName);
			}
			return cf != null;
		} catch (Exception e) {
			// e.printStackTrace();
			log.info("queueName=[" + queueName + "], " + e.toString());
			return false;
		}
	}

	@Override
	public synchronized void shutDown() {
		log.debug("...");
		if (!started) {
			log.warn("started=" + started);
			return;
		}
		Jmx.unregisterMBean(jmxObjectName);
		JmsServiceLisenter.stop();
		try {
			if (manager != null) {
				manager.stop();
			} else {
				log.warn("manager=" + manager);
			}
		} catch (Exception e) {
			log.warn("", e);
		}
		//
		try {
			if (serverConfig != null) {
				serverConfig.stop();
			} else {
				log.warn("serverConfig=" + serverConfig);
			}
		} catch (Exception e) {
			log.warn("", e);
		}
		started = false;
	}

	// ===
	private static class SingletonHolder {
		private static final JmsServer INSTANCE = new JmsServer();
	}

	/**
	 * 
	 * @return Singleton Object
	 */
	public static JmsServer getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public void setNetconfig(Netconfig netconfig) {
		this.netconfig = netconfig;
	}

	public Netconfig getNetconfig() {
		return netconfig;
	}

	// ===
	static class Slf4jLogDelegate implements LogDelegate {
		private final Logger logger;

		public Slf4jLogDelegate(Class<?> clazz) {
			logger = LoggerFactory.getLogger(clazz);
		}

		@Override
		public void debug(Object message) {
			logger.debug(message.toString());
		}

		@Override
		public void debug(Object message, Throwable t) {
			logger.debug(message.toString(), t);
		}

		@Override
		public void error(Object message) {
			logger.error(message.toString());
		}

		@Override
		public void error(Object message, Throwable t) {
			logger.error(message.toString(), t);
		}

		@Override
		public void fatal(Object message) {
			logger.error(message.toString());
		}

		@Override
		public void fatal(Object message, Throwable t) {
			logger.error(message.toString(), t);
		}

		@Override
		public void info(Object message) {
			logger.info(message.toString());
		}

		@Override
		public void info(Object message, Throwable t) {
			logger.info(message.toString(), t);
		}

		@Override
		public boolean isDebugEnabled() {
			return logger.isDebugEnabled();
		}

		@Override
		public boolean isInfoEnabled() {
			return logger.isInfoEnabled();
		}

		@Override
		public boolean isTraceEnabled() {
			return logger.isTraceEnabled();
		}

		@Override
		public void trace(Object message) {
			logger.trace(message.toString());
		}

		@Override
		public void trace(Object message, Throwable t) {
			logger.trace(message.toString(), t);
		}

		@Override
		public void warn(Object message) {
			logger.warn(message.toString());
		}

		@Override
		public void warn(Object message, Throwable t) {
			logger.warn(message.toString(), t);
		}
	}

}
