package iwin.front.net.server;

import iwin.admin.Monitoring;

import java.net.SocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.AdaptiveReceiveBufferSizePredictor;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.ServerSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.handler.execution.OrderedMemoryAwareThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OPEN --> BOUND --> CONNECTED --> RECEIVED --> ... --> RECEIVED --> DISCONNECTED --> UNBOUND --> CLOSED
 * 
 * @author leotu@nec.com.tw
 */
public class ServerSocketEngine {
	final protected static Logger log = LoggerFactory.getLogger(ServerSocketEngine.class);

	protected ChannelGroup allChannels = new DefaultChannelGroup(ServerSocketEngine.class.getName());
	protected int stopTimeoutSeconds = 30;
	protected SocketAddress localAddress;
	protected ServerBootstrap bootstrap;
	protected Channel serverChannel;
	protected BasePipelineFactory pipelineFactory;
	protected Executor logicExecutor = null;
	protected ExecutionHandler logicExecutorHandler = null;
	protected byte[] endOfStreamMatch;
	protected int readTimeoutSeconds = 120;
	protected int writeTimeoutSeconds = 120;
	protected int logicExecutorThreadPoolSize = 200;
	protected boolean includingEndToken;
	protected boolean shutdownHook = false;
	protected int maxReadableBytes = 1024 * 1024 * 10; // 10MB
	protected boolean logicUseWorkerThread = false;
	protected boolean keepAlive = true;
	private boolean running = false;

	public ServerSocketEngine(SocketAddress localAddress) {
		this.localAddress = localAddress;
	}

	// ==============================================
	public void useStringMode(Class<? extends IStringProcessor> processorClass, String charsetName) {
		this.pipelineFactory = new StringPipelineFactory(processorClass, charsetName);
		this.pipelineFactory.setSharableStatisticsHandler(new SimpleChannelUpstreamHandler() {
			@Override
			public void channelOpen(ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
				allChannels.add(e.getChannel()); // TODO
				super.channelOpen(ctx, e);
			}
		});
	}

	public void setIncludingEndToken(boolean includingEndToken) {
		this.includingEndToken = includingEndToken;
	}

	public void setMaxReadableBytes(int maxReadableBytes) {
		this.maxReadableBytes = maxReadableBytes;
	}

	// ==============================================
	public void start() throws Exception {
		log.info("logicUseWorkerThread=" + logicUseWorkerThread + ", logicExecutorThreadPoolSize=" + logicExecutorThreadPoolSize);
		if (pipelineFactory == null) {
			new RuntimeException("(pipelineFactory == null)");
		}
		if (endOfStreamMatch == null) {
			new RuntimeException("(endOfStreamMatch == null)");
		}
		pipelineFactory.setLogicUseWorkerThread(logicUseWorkerThread);
		pipelineFactory.setReadTimeoutSeconds(readTimeoutSeconds);
		pipelineFactory.setWriteTimeoutSeconds(writeTimeoutSeconds);
		pipelineFactory.setEndOfStreamMatch(endOfStreamMatch);
		pipelineFactory.setIncludingEndToken(includingEndToken);
		pipelineFactory.setMaxReadableBytes(maxReadableBytes);
		//
		if (!logicUseWorkerThread) {
			logicExecutor = null;
			logicExecutorHandler = new ExecutionHandler(new OrderedMemoryAwareThreadPoolExecutor(logicExecutorThreadPoolSize, 0, 0));
		}
		log.info("logicExecutor=" + logicExecutor + ", logicExecutorHandler=" + logicExecutorHandler);
		pipelineFactory.setLogicExecutor(logicExecutor);
		pipelineFactory.setLogicExecutorHandler(logicExecutorHandler);
		//
		// Configure the server.
		ExecutorService bossExecutor = Executors.newCachedThreadPool();
		ExecutorService workerExecutor = Executors.newCachedThreadPool();
		ServerSocketChannelFactory serverSocketChannelFactory;
		if (!logicUseWorkerThread) { // OioServerSocketChannelFactory
			serverSocketChannelFactory = new NioServerSocketChannelFactory(bossExecutor, workerExecutor);
		} else { // OioServerSocketChannelFactory
			// Executor workerExecutor = Executors.newFixedThreadPool(logicExecutorThreadPoolSize);
			serverSocketChannelFactory = new NioServerSocketChannelFactory(bossExecutor, workerExecutor, logicExecutorThreadPoolSize);
		}
		// ServerSocketChannelFactory serverSocketChannelFactory = new OioServerSocketChannelFactory(bossExecutor, workerExecutor);
		bootstrap = new ServerBootstrap(serverSocketChannelFactory);
		//
		ChannelPipelineFactory cpf = pipelineFactory;
		cpf = Monitoring.add(cpf, "::Socket " + cpf.getClass().getSimpleName()); // TODO
		bootstrap.setPipelineFactory(cpf);

		//
		setOptions(bootstrap);
		log.info("localAddress=" + localAddress + ", pipelineFactory=" + pipelineFactory);
		serverChannel = bootstrap.bind(localAddress);

		allChannels.add(serverChannel); // TODO
		this.running = serverChannel.isBound();
		//
		log.debug("shutdownHook=" + shutdownHook + ", running=" + running);
		if (shutdownHook) {
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				public void run() {
					stop();
				}
			}));
		}
		//
		// bossExecutor.submit(new Runnable() {
		// private long counter = 0;
		//
		// @Override
		// public void run() {
		// while (running) {
		// counter++;
		// log.debug("[" + counter + "]: Socket channels size=" + allChannels.size());
		// for (int i = 0; i < 60 * 2 && running; i++) {
		// try {
		// Thread.sleep(500);
		// } catch (InterruptedException e) {
		// // nothing
		// }
		// }
		// }
		// }
		//
		// });
	}

	protected void setOptions(ServerBootstrap bootstrap) {
		bootstrap.setOption("backlog", 50 * 2);
		bootstrap.setOption("receiveBufferSize", 1024 * 32); // TODO: server
		bootstrap.setOption("receiveBufferSizePredictor", new AdaptiveReceiveBufferSizePredictor(1024 * 8, 1024 * 16, 1024 * 32));
		// ===
		bootstrap.setOption("child.tcpNoDelay", true);
		// XXX: keep all clients persisted forever (no time out)
		// Use "child.keepAlive" for ServerBootstrap and "keepAlive" for ClientBootstrap.
		bootstrap.setOption("child.keepAlive", keepAlive);
		bootstrap.setOption("child.receiveBufferSizePredictor", new AdaptiveReceiveBufferSizePredictor(1024 * 8, 1024 * 18, 1024 * 32));
		bootstrap.setOption("child.receiveBufferSize", 1024 * 32);
		bootstrap.setOption("child.sendBufferSize", 1024 * 64);
	}

	public void stop() {
		log.info("...");
		running = false; // TODO
		try {
			pipelineFactory.getTimer().stop(); // TODO
			allChannels.close().awaitUninterruptibly(stopTimeoutSeconds, TimeUnit.SECONDS);
		} catch (Throwable e) {
			log.warn("", e);
		} finally {
			bootstrap.releaseExternalResources();
		}
		if (logicExecutorHandler != null) {
			logicExecutorHandler.releaseExternalResources();
		}
		bootstrap = null;
		allChannels.clear();
	}

	// ==============================================
	public void setEndOfStreamMatch(byte[] endOfStreamMatch) {
		this.endOfStreamMatch = endOfStreamMatch;
	}

	public void setReadTimeoutSeconds(int readTimeoutSeconds) {
		this.readTimeoutSeconds = readTimeoutSeconds;
	}

	public void setWriteTimeoutSeconds(int writeTimeoutSeconds) {
		this.writeTimeoutSeconds = writeTimeoutSeconds;
	}

	public void setLogicExecutorThreadPoolSize(int logicExecutorThreadPoolSize) {
		this.logicExecutorThreadPoolSize = logicExecutorThreadPoolSize;
	}

	public void setStopTimeoutSeconds(int stopTimeoutSeconds) {
		this.stopTimeoutSeconds = stopTimeoutSeconds;
	}

	public void setShutdownHook(boolean shutdownHook) {
		this.shutdownHook = shutdownHook;
	}

	public void setLogicUseWorkerThread(boolean logicUseWorkerThread) {
		this.logicUseWorkerThread = logicUseWorkerThread;
	}

	public void setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
	}

	public ChannelGroup getAllChannels() {
		return allChannels;
	}

	public void setAllChannels(ChannelGroup allChannels) {
		this.allChannels = allChannels;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + " [localAddress=" + localAddress + ", pipelineFactory=" + pipelineFactory + "]";
	}
}
