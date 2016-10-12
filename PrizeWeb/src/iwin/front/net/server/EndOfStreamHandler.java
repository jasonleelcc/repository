package iwin.front.net.server;

import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.buffer.BigEndianHeapChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffer;
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
public class EndOfStreamHandler extends BaseUpstreamHandler {
	final protected static Logger log = LoggerFactory.getLogger(EndOfStreamHandler.class);
	public static final ChannelBuffer END_BUFFER = new BigEndianHeapChannelBuffer(0);

	protected byte endOfStreamMatch[];
	protected volatile int matchIdx = -1;

	protected volatile boolean endOfStream = false;
	protected List<ChannelBuffer> keepChannelBuffer;
	protected boolean includingEndToken = true;

	protected volatile int totalReadableBytes = 0;
	protected int maxReadableBytes = -1;

	public EndOfStreamHandler(byte[] endOfStreamMatch, boolean includingEndToken, int maxReadableBytes) throws Exception {
		super();
		if (debug) {
			log.debug("..., endOfStreamMatch=[" + new String(endOfStreamMatch, "ISO-8859-1") + "], maxReadableBytes=" + maxReadableBytes);
		}
		this.endOfStreamMatch = endOfStreamMatch;
		this.includingEndToken = includingEndToken;
		this.maxReadableBytes = maxReadableBytes;
	}

	// ========================================
	protected void create(final ChannelHandlerContext ctx) throws Exception {
		if (debug) {
			log.debug("...");
		}
		keepChannelBuffer = new ArrayList<ChannelBuffer>();
	}

	protected void release(final ChannelHandlerContext ctx) throws Exception {
		if (debug) {
			log.debug("...");
		}
		if (keepChannelBuffer != null) {
			keepChannelBuffer.clear();
			keepChannelBuffer = null;
		}
	}

	// ========================================
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		if (debug) {
			log.debug("...");
		}
		if (!includingEndToken) {
			create(ctx); // TODO
		}
		super.channelConnected(ctx, e);
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		if (debug) {
			log.debug("...");
		}
		if (!includingEndToken) {
			release(ctx); // TODO
		}
		super.channelDisconnected(ctx, e);
	}

	// ============================================
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent me) throws Exception {
		messageReceivedCounter++;
		if (debug) {
			log.debug(">>>(" + messageReceivedCounter + ")..." + me);
		}
		ChannelBuffer buf = (ChannelBuffer) me.getMessage();
		//
		if (endOfStream) {
			//throw new RuntimeException("Need to check code: endOfStream, buf.readableBytes()=" + buf.readableBytes());
			log.warn("Had end of stream, but still received input date, skip it! buf.readableBytes()=" + buf.readableBytes());
			return;
		}
		totalReadableBytes += buf.readableBytes();
		// if (isDebug()) {
		// log.debug(">>>(readableBytes=" + buf.readableBytes() + ")..." + me);
		// }
		if (!buf.readable()) {
			throw new Exception("checking code: (!buf.readable()), endOfStream=" + endOfStream + ", " + me);
		}
		if (includingEndToken) {
			includingEndTokenAction(ctx, me, buf);
		} else {
			excludingEndTokenAction(ctx, me, buf);
		}
	}

	protected void includingEndTokenAction(ChannelHandlerContext ctx, MessageEvent e, ChannelBuffer buf) throws Exception {
		int checkIdx = matchIdx + 1;
		int start = buf.readerIndex(); // including
		int end = buf.writerIndex() - 1; // including
		int matchCount = 0;
		int offset;
		for (offset = start; offset <= end; offset++) {
			byte b = buf.getByte(offset);
			if (b == endOfStreamMatch[checkIdx]) {
				matchIdx++;
				matchCount++;
				if (matchIdx == endOfStreamMatch.length - 1) {
					endOfStream = true;
					break;
				}
				checkIdx++;
			} else {
				if (b == endOfStreamMatch[0]) {
					matchIdx = 0;
					matchCount = 1;
					checkIdx = 1;
				} else if (matchIdx > 0) {
					matchIdx = -1;
					matchCount = 0;
					checkIdx = 0;
				}
			}
			if (offset + 1 > end) {
				break;
			}
		}
		if (endOfStream) {
			matchIdx = -1;
			//
			int newWriterIndex = offset + 1;
			buf.writerIndex(newWriterIndex);
			if (buf.readable()) {
				super.messageReceived(ctx, e);
			}
			sentEndOfStreamMessage(ctx); // TODO
		} else if (matchCount > 0) {
			super.messageReceived(ctx, e);
		} else {
			matchIdx = -1;
			super.messageReceived(ctx, e);
		}
	}

	protected void excludingEndTokenAction(ChannelHandlerContext ctx, MessageEvent e, ChannelBuffer buf) throws Exception {
		int checkIdx = matchIdx + 1;
		int start = buf.readerIndex(); // including
		int end = buf.writerIndex() - 1; // including
		int matchCount = 0;
		int offset;
		boolean writeKeepBuffer = matchIdx == -1;
		for (offset = start; offset <= end; offset++) {
			byte b = buf.getByte(offset);
			if (b == endOfStreamMatch[checkIdx]) {
				matchIdx++;
				matchCount++;
				if (matchIdx == endOfStreamMatch.length - 1) {
					endOfStream = true;
					break;
				}
				checkIdx++;
			} else {
				if (!writeKeepBuffer) {
					writeKeepBuffer = true;
				}
				if (b == endOfStreamMatch[0]) {
					matchIdx = 0;
					matchCount = 1;
					checkIdx = 1;
				} else if (matchIdx > 0) {
					matchIdx = -1;
					matchCount = 0;
					checkIdx = 0;
				}
			}
			if (offset + 1 > end) {
				break;
			}
		}
		//
		if (!endOfStream) {
			if (maxReadableBytes != -1 && totalReadableBytes > maxReadableBytes) {
				throw new Exception("(totalReadableBytes > maxReadableBytes), totalReadableBytes=" + totalReadableBytes + ", maxReadableBytes="
						+ maxReadableBytes);
			}
			if (totalReadableBytes > 1024 * 128) {
				log.warn("(totalReadableBytes > 1024 * 128), totalReadableBytes=" + totalReadableBytes + ", messageReceivedCounter="
						+ messageReceivedCounter + ", channel=" + ctx.getChannel()); // 128K bytes
			}
		}
		//
		if (writeKeepBuffer && keepChannelBuffer.size() > 0) {
			writeKeepChannelBuffers(ctx);
		}
		if (endOfStream) {
			matchIdx = -1;
			//
			int newWriterIndex = offset - matchCount + 1;
			buf.writerIndex(newWriterIndex);
			if (buf.readable()) {
				super.messageReceived(ctx, e);
			}
			sentEndOfStreamMessage(ctx); // TODO
		} else if (matchCount > 0) {
			int newWriterIndex = offset - matchCount + 1;
			ChannelBuffer keep = buf.copy(newWriterIndex, matchCount);
			keepChannelBuffer.add(keep);
			buf.writerIndex(newWriterIndex);
			if (buf.readable()) {
				super.messageReceived(ctx, e);
			}
		} else {
			matchIdx = -1;
			super.messageReceived(ctx, e);
		}
	}

	protected void writeKeepChannelBuffers(ChannelHandlerContext ctx) throws Exception {
		for (ChannelBuffer keep : keepChannelBuffer) {
			Channels.fireMessageReceived(ctx, keep, ctx.getChannel().getRemoteAddress());
		}
		keepChannelBuffer.clear();
	}

	protected void sentEndOfStreamMessage(ChannelHandlerContext ctx) throws Exception {
		Channels.fireMessageReceived(ctx, END_BUFFER, ctx.getChannel().getRemoteAddress());
	}

}
