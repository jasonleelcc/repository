package iwin.front.net.server;

import java.util.concurrent.Executor;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author leotu@nec.com.tw
 */
public class StringChannelHandler extends ServerBaseUpstreamHandler {
	final protected static Logger log = LoggerFactory.getLogger(StringChannelHandler.class);

	protected final Executor executor;
	protected IStringProcessor processor;
	protected String charsetName;
	
	public StringChannelHandler(Executor executor, IStringProcessor processor) {
		super();
		this.executor = executor;
		this.processor = processor;
		if (debug) {
			log.debug("...executor=" + executor);
		}
	}

	// ============================================
	/**
	 * TODO: after ChannelBufferHookHandler.class
	 */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, final MessageEvent me) {
		messageReceivedCounter++;
		if (debug) {
			log.debug(">>>(" + messageReceivedCounter + ")..., remoteAddress=" + ctx.getChannel().getRemoteAddress());
		}
		if (messageReceivedCounter > 1) {
			log.error("(messageReceivedCounter > 1), messageReceivedCounter=" + messageReceivedCounter);
			throw new RuntimeException("(messageReceivedCounter > 1), messageReceivedCounter=" + messageReceivedCounter);
		}
		final String inputData = (String) me.getMessage();
		if (executor == null) {
			processStreamIo(me, inputData);
		} else {
			executor.execute(new Runnable() {
				public void run() {
					processStreamIo(me, inputData);
				}
			});
		}
	}

	/**
	 * Note: maybe called by another thread !
	 */
	protected void processStreamIo(final MessageEvent me, String inputData) {
		if (debug) {
			log.debug("...");
		}
		boolean writeMode = false;
		try {
			String outputData = processor.handleRequest(inputData);
			if (!me.getChannel().isConnected()) {
				log.warn("Skip: channel.connected=" + me.getChannel().isConnected());
				return;
			}
			if (outputData != null && outputData.length() > 0) {
				if (outputData != null) {
					writeMode = true;
					ChannelFuture writeFuture = me.getChannel().write(outputData);
					closeChannelOnComplete(writeFuture); // TODO
				}
			}
		} finally {
			if (me.getChannel().isConnected() && !writeMode) {
				closeChannelOnFlush(me.getChannel());
				//closeChannel(me.getChannel());// TODO
			}
		}
	}
}
