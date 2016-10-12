package iwin.front.net.server;

import java.nio.channels.ClosedChannelException;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OPEN --> BOUND --> CONNECTED --> RECEIVED --> ... --> RECEIVED --> DISCONNECTED --> UNBOUND --> CLOSED <br/>
 * http://www.prudentman.idv.tw/2007/11/big-endianlittle-endian.html
 * 
 * @author leotu@nec.com.tw
 */
abstract public class ServerBaseUpstreamHandler extends BaseUpstreamHandler {
	final protected static Logger log = LoggerFactory.getLogger(ServerBaseUpstreamHandler.class);

	public ServerBaseUpstreamHandler() {
		super();
		if (debug) {
			log.debug("...");
		}
	}

	/**
	 * Not call super.exceptionCaught(...) and close channel
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		log.warn("..." + e + ", " + e.getCause());
		exception = e.getCause();
		try {
			if (exception instanceof ClosedChannelException) {
				log.warn("(exception instanceof ClosedChannelException), channel=" + e.getChannel());
			}
			if (this != ctx.getPipeline().getLast()) {
				super.exceptionCaught(ctx, e);
			}
		} finally {
			closeChannel(e.getChannel());
		}
	}
}
