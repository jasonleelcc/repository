package iwin.front.net.server;

import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author leotu@nec.com.tw
 */
public class ChannelBufferHookHandler extends BaseUpstreamHandler {
	final protected static Logger log = LoggerFactory.getLogger(ChannelBufferHookHandler.class);

	protected List<ChannelBuffer> keepBuffers;

	public ChannelBufferHookHandler() {
		if (debug) {
			log.debug("...");
		}
	}

	// ========================================
	protected void create(final ChannelHandlerContext ctx) throws Exception {
		if (debug) {
			log.debug("...");
		}
		keepBuffers = new ArrayList<ChannelBuffer>();
	}

	protected void release(final ChannelHandlerContext ctx) throws Exception {
		if (debug) {
			log.debug("...");
		}
		if (keepBuffers != null) {
			keepBuffers.clear();
			keepBuffers = null;
		}
	}

	// ========================================
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		if (debug) {
			log.debug("...");
		}
		create(ctx); // TODO
		super.channelConnected(ctx, e);
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		if (debug) {
			log.debug("...");
		}
		release(ctx); // TODO
		super.channelDisconnected(ctx, e);
	}

	// ============================================
	/**
	 * TODO: after ClientEndOfStreamNotifyHandler.class or EndOfStreamHandler.class
	 */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent me) throws Exception {
		messageReceivedCounter++;
		if (debug) {
			log.debug(">>>(" + messageReceivedCounter + ")..." + me);
		}
		ChannelBuffer buf = (ChannelBuffer) me.getMessage();
		if (buf == EndOfStreamHandler.END_BUFFER) {
			ChannelBuffer bufs = ChannelBuffers.wrappedBuffer((ChannelBuffer[]) keepBuffers.toArray(new ChannelBuffer[keepBuffers.size()]));
			if (debug) {
				log.debug(">>>(END_BUFFER: readableBytes=" + bufs.readableBytes() + ")..." + me);
			}
			keepBuffers.clear();
			Channels.fireMessageReceived(ctx, bufs, ctx.getChannel().getRemoteAddress());
		} else {
			if (buf.readableBytes() == 0) {
				throw new RuntimeException("Need to check code: (msg.readableBytes() ==0)");
			}
			keepBuffers.add(buf);
		}
	}
}
