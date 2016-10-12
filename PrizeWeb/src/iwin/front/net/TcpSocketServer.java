package iwin.front.net;

import iwin.admin.AdminSupport;
import iwin.admin.DynamicMapMBean;
import iwin.admin.Jmx;
import iwin.conf.Netconfig;
import iwin.front.net.server.ServerSocketEngine;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author leotu@nec.com.tw
 */
public class TcpSocketServer implements AdminSupport {
	protected static final Logger log = LoggerFactory.getLogger(TcpSocketServer.class);

	private String endOfStreamMatch;
	private Netconfig netconfig;
	private ServerSocketEngine engine;
	private boolean started = false;
	private DynamicMapMBean mbean = new DynamicMapMBean();
	final private String jmxObjectName = "Hexa:type=TcpSocketServer";
	//
	protected int readTimeoutSeconds = 180;
	protected int writeTimeoutSeconds = 180;
	protected int logicThreadPoolSize = 100;
	protected boolean logicUseWorkerThread = false;

	protected TcpSocketServer() {
		log.debug("...");
	}

	@Override
	public synchronized void startUp() {
		log.debug("...");
		if (started) {
			log.warn("started=" + started);
			return;
		}
		String serverPortStr = netconfig.getIniValue("server.port1");
		try {
			int serverPort = Integer.parseInt(serverPortStr);
			log.info("serverPort=[" + serverPort + "], logicThreadPoolSize=[" + logicThreadPoolSize + "], endOfStreamMatch=[" + endOfStreamMatch
					+ "]");
			log.info("readTimeoutSeconds=[" + readTimeoutSeconds + "], writeTimeoutSeconds=[" + writeTimeoutSeconds + "]");
			mbean.getModel().put("serverPort", serverPort);
			engine = new ServerSocketEngine(new InetSocketAddress(serverPort));
			engine.setKeepAlive(false); // TODO
			mbean.getModel().put("keepAlive", false);
			engine.setEndOfStreamMatch(endOfStreamMatch.getBytes("ISO-8859-1"));
			mbean.getModel().put("endOfStreamMatch", endOfStreamMatch);
			engine.setIncludingEndToken(true);
			mbean.getModel().put("includingEndToken", true);
			engine.setReadTimeoutSeconds(readTimeoutSeconds);
			mbean.getModel().put("readTimeoutSeconds", readTimeoutSeconds);
			engine.setWriteTimeoutSeconds(writeTimeoutSeconds);
			mbean.getModel().put("writeTimeoutSeconds", writeTimeoutSeconds);
			engine.setLogicExecutorThreadPoolSize(logicThreadPoolSize);
			mbean.getModel().put("logicThreadPoolSize", logicThreadPoolSize);
			engine.setLogicUseWorkerThread(logicUseWorkerThread);
			mbean.getModel().put("logicUseWorkerThread", logicUseWorkerThread);
			//
			engine.useStringMode(BizProcessor.class, "UTF-8");
			mbean.getModel().put("stringMode", "UTF-8");
			Jmx.registerMBean(this, mbean, jmxObjectName);
			engine.start();
			started = true;
		} catch (Exception e) {
			log.error("serverPortStr=[" + serverPortStr + "]", e);
			throw new RuntimeException(this.toString(), e);
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
		try {
			if (engine != null) {
				engine.stop();
			} else {
				log.warn("engine=" + engine);
			}
		} catch (Exception e) {
			log.warn("", e);
		}
		started = false;
	}

	// ===
	private static class SingletonHolder {
		private static final TcpSocketServer INSTANCE = new TcpSocketServer();
	}

	/**
	 * 
	 * @return Singleton Object
	 */
	public static TcpSocketServer getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public void setNetconfig(Netconfig netconfig) {
		this.netconfig = netconfig;
	}

	public Netconfig getNetconfig() {
		return netconfig;
	}

	public void setEndOfStreamMatch(String endOfStreamMatch) {
		this.endOfStreamMatch = endOfStreamMatch;
	}

	public void setReadTimeoutSeconds(int readTimeoutSeconds) {
		this.readTimeoutSeconds = readTimeoutSeconds;
	}

	public void setWriteTimeoutSeconds(int writeTimeoutSeconds) {
		this.writeTimeoutSeconds = writeTimeoutSeconds;
	}

	public void setLogicThreadPoolSize(int logicThreadPoolSize) {
		this.logicThreadPoolSize = logicThreadPoolSize;
	}

	public void setLogicUseWorkerThread(boolean logicUseWorkerThread) {
		this.logicUseWorkerThread = logicUseWorkerThread;
	}

	public int getSocketChannelSize() {
		return engine.getAllChannels().size();
	}

	// ===
	@Override
	public String toString() {
		return "TcpSocketServer [endOfStreamMatch=" + endOfStreamMatch + ", netconfig=" + netconfig + ", engine=" + engine + "]";
	}
}
