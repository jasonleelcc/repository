package iwin.front.net.server;

import java.util.concurrent.Executor;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.handler.timeout.ReadTimeoutHandler;
import org.jboss.netty.handler.timeout.WriteTimeoutHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Only-One instance factory running.
 * 
 * @author leotu@nec.com.tw
 */
public abstract class BasePipelineFactory implements ChannelPipelineFactory {
	final protected static Logger log = LoggerFactory.getLogger(BasePipelineFactory.class);
	static protected boolean debug = false;
		
	protected HashedWheelTimer timer;
	protected int readTimeoutSeconds = -1;
	protected int writeTimeoutSeconds = -1;
	protected Executor logicExecutor;
	protected ExecutionHandler logicExecutorHandler;
	protected byte[] endOfStreamMatch;
	protected boolean includingEndToken;
	protected int maxReadableBytes = 1024 * 1024 * 5; // 5MB
	protected SimpleChannelUpstreamHandler sharableStatisticsHandler = null;
	protected boolean logicUseWorkerThread;

	public BasePipelineFactory() {
		if (debug) {
			log.debug("...");
		}
		this.timer = new HashedWheelTimer();
		this.timer.start();
	}

	public void setReadTimeoutSeconds(int readTimeoutSeconds) {
		this.readTimeoutSeconds = readTimeoutSeconds;
	}

	public void setWriteTimeoutSeconds(int writeTimeoutSeconds) {
		this.writeTimeoutSeconds = writeTimeoutSeconds;
	}

	public void setLogicExecutor(Executor logicExecutor) {
		this.logicExecutor = logicExecutor;
	}

	public void setLogicExecutorHandler(ExecutionHandler logicExecutorHandler) {
		this.logicExecutorHandler = logicExecutorHandler;
	}

	public void setEndOfStreamMatch(byte[] endOfStreamMatch) {
		this.endOfStreamMatch = endOfStreamMatch;
	}

	public void setIncludingEndToken(boolean includingEndToken) {
		this.includingEndToken = includingEndToken;
	}

	public void setMaxReadableBytes(int maxReadableBytes) {
		this.maxReadableBytes = maxReadableBytes;
	}

	public void setSharableStatisticsHandler(SimpleChannelUpstreamHandler sharableStatisticsHandler) {
		this.sharableStatisticsHandler = sharableStatisticsHandler;
	}

	public HashedWheelTimer getTimer() {
		return timer;
	}

	public void setLogicUseWorkerThread(boolean logicUseWorkerThread) {
		this.logicUseWorkerThread = logicUseWorkerThread;
	}

	/**
	 * ChunkedWriteHandler(optional) --> ReadTimeoutHandler(optional) --> EndOfStreamHandler(optional)
	 */
	@Override
	public ChannelPipeline getPipeline() throws Exception {
		// log.debug("*** timeoutSeconds=" + timeoutSeconds + ", chunkedWrite=" + chunkedWrite);
		if (debug) {
			log.debug("...");
		}
		//
		if (logicExecutorHandler != null && logicExecutor != null) {
			throw new Exception("(logicExecutorHandler != null && logicExecutor != null)");
		}
		if (logicExecutorHandler == null && logicExecutor == null) {
			if (logicUseWorkerThread) {
				if (debug) {
					log.debug("logicUseWorkerThread=" + logicUseWorkerThread);
				}
			} else {
				throw new Exception("(logicExecutorHandler == null && logicExecutor == null)");
			}
		}
		//
		// log.debug("timeoutSeconds=" + timeoutSeconds + ", chunkedWrite=" + chunkedWrite);
		ChannelPipeline pipeline = Channels.pipeline();
		if (sharableStatisticsHandler != null) {
			pipeline.addFirst("StatisticsChannelHandler", sharableStatisticsHandler);
		}
		if (readTimeoutSeconds > 0) {
			pipeline.addLast("ReadTimeoutHandler", new ReadTimeoutHandler(timer, readTimeoutSeconds)); // XXX: timer must be shared
		}
		if (writeTimeoutSeconds > 0) {
			pipeline.addLast("WriteTimeoutHandler", new WriteTimeoutHandler(timer, writeTimeoutSeconds));// XXX: timer must be shared
		}
		if (endOfStreamMatch != null && endOfStreamMatch.length > 0) {
			pipeline.addLast("EndOfStreamHandler", new EndOfStreamHandler(endOfStreamMatch, includingEndToken, maxReadableBytes));
		}
		addHandler(pipeline);
		return pipeline;
	}

	public abstract void addHandler(ChannelPipeline pipeline) throws Exception;

}
