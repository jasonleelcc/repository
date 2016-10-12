package iwin.front.net.server;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OPEN --> BOUND --> CONNECTED --> RECEIVED --> ... --> RECEIVED --> DISCONNECTED --> UNBOUND --> CLOSED <br/>
 * http://www.prudentman.idv.tw/2007/11/big-endianlittle-endian.html
 * 
 * @author leotu@nec.com.tw
 */
abstract public class BaseUpstreamHandler extends SimpleChannelUpstreamHandler {
	final protected static Logger log = LoggerFactory.getLogger(BaseUpstreamHandler.class);

	protected boolean debug = false;
	protected volatile long messageReceivedCounter = 0;
	protected Throwable exception = null;

	// public BaseUpstreamHandler() {
	// super();
	// if (debug) {
	// log.debug("...");
	// }
	// }
	//
	// @Override
	// public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
	// if (debug) {
	// log.debug("..." + e);
	// }
	// super.channelOpen(ctx, e);
	// }
	//
	// @Override
	// public void channelBound(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
	// if (debug) {
	// log.debug("..." + e);
	// }
	// super.channelBound(ctx, e);
	// }
	//
	// @Override
	// public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
	// if (debug) {
	// log.debug("..." + e);
	// }
	// super.channelConnected(ctx, e);
	// }
	//
	// @Override
	// public void channelInterestChanged(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
	// if (debug) {
	// log.debug("..." + e);
	// }
	// super.channelInterestChanged(ctx, e);
	// }
	//
	// @Override
	// public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
	// if (debug) {
	// log.debug("..." + e);
	// }
	// super.channelDisconnected(ctx, e);
	// }
	//
	// @Override
	// public void channelUnbound(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
	// if (debug) {
	// log.debug("..." + e);
	// }
	// super.channelUnbound(ctx, e);
	// }
	//
	// @Override
	// public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
	// if (debug) {
	// log.debug("..." + e);
	// }
	// super.channelClosed(ctx, e);
	// }
	//
	// @Override
	// public void writeComplete(ChannelHandlerContext ctx, WriteCompletionEvent e) throws Exception {
	// if (debug) {
	// log.debug("..." + e);
	// }
	// super.writeComplete(ctx, e);
	// }
	//
	// @Override
	// public void childChannelOpen(ChannelHandlerContext ctx, ChildChannelStateEvent e) throws Exception {
	// if (debug) {
	// log.debug("..." + e);
	// }
	// super.childChannelOpen(ctx, e);
	// }
	//
	// @Override
	// public void childChannelClosed(ChannelHandlerContext ctx, ChildChannelStateEvent e) throws Exception {
	// if (debug) {
	// log.debug("..." + e);
	// }
	// super.childChannelClosed(ctx, e);
	// }

	/**
	 * Also call super.exceptionCaught(...)
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		if (debug) {
			log.warn("..." + e + ", " + e.getCause());
		}
		if (exception == null) {
			exception = e.getCause();
		}
		super.exceptionCaught(ctx, e);
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		if (debug) {
			log.debug("..." + e);
		}
		messageReceivedCounter++;
		super.messageReceived(ctx, e);
	}

	// ========================================
	protected ChannelFuture closeChannel(Channel channel) {
		if (debug) {
			log.debug("..., channel.isConnected()=" + channel.isConnected());
		}
		if (channel.isConnected()) {
			return channel.close();
		} else {
			log.warn("..., channel.isConnected()=" + channel.isConnected());
			return null;
		}
	}

	/**
	 * Closes the specified channel after all queued write requests are flushed.
	 */
	protected ChannelFuture closeChannelOnFlush(Channel channel) {
		if (debug) {
			log.debug("..., channel.isConnected()=" + channel.isConnected());
		}
		if (channel.isConnected()) {
			ChannelFuture writeFuture = channel.write(ChannelBuffers.EMPTY_BUFFER);
			writeFuture.addListener(ChannelFutureListener.CLOSE);
			return writeFuture;
		} else {
			log.warn("..., channel.isConnected()=" + channel.isConnected());
			return null;
		}
	}

	protected ChannelFuture closeChannelOnComplete(ChannelFuture future) {
		if (debug) {
			log.debug("..., future.channel.isConnected()=" + future.getChannel().isConnected());
		}
		if (future.getChannel().isConnected()) {
			future.addListener(ChannelFutureListener.CLOSE);
		} else {
			log.warn("..., future.channel.isConnected()=" + future.getChannel().isConnected());
		}
		return future;
	}
}
