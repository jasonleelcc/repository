package iwin.front.net.server;

import iwin.admin.Monitoring;

import java.nio.charset.Charset;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author leotu@nec.com.tw
 */
public class StringPipelineFactory extends BasePipelineFactory {
	final protected static Logger log = LoggerFactory.getLogger(StringPipelineFactory.class);
	static protected boolean debug = false;

	// private final ChannelHandler sharableStringDecoder;
	// private final ChannelHandler sharableStringEncoder;

	protected String charsetName;
	protected Class<? extends IStringProcessor> processorClass;

	public StringPipelineFactory(Class<? extends IStringProcessor> processorClass, String charsetName) {
		super();
		if (debug) {
			log.debug("...");
		}
		this.processorClass = processorClass;
		this.charsetName = charsetName;
		// this.sharableStringDecoder = new StringDecoder(Charset.forName(charsetName));
		// this.sharableStringEncoder = new StringEncoder(Charset.forName(charsetName));
	}

	/**
	 * ChunkedWriteHandler(optional) --> ReadTimeoutHandler(optional) --> EndOfStreamHandler(optional) --> ChannelBufferHookHandler --> StringDecoder
	 * --> StringEncoder --> logicExecutorHandler(optional) --> handler
	 */
	@Override
	public void addHandler(ChannelPipeline pipeline) throws Exception {
		pipeline.addLast("ChannelBufferHookHandler", new ChannelBufferHookHandler());
		pipeline.addLast("StringDecoder", new StringDecoder(Charset.forName(charsetName)));
		pipeline.addLast("StringEncoder", new StringEncoder(Charset.forName(charsetName)));
		// pipeline.addLast("StringDecoder", sharableStringDecoder);
		// pipeline.addLast("StringEncoder", sharableStringEncoder);
		//
		if (logicExecutorHandler != null) {
			pipeline.addLast("logicExecutorHandler", logicExecutorHandler); // XXX: must be shared
		}
		IStringProcessor processor = processorClass.newInstance(); // TODO
		processor = Monitoring.add(processor, "::Socket " + processor.getClass().getSimpleName()); // TODO
		pipeline.addLast("handler", new StringChannelHandler(logicExecutor, processor));// logic
	}
}
