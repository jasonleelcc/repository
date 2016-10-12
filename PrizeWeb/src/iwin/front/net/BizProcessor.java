package iwin.front.net;

import iwin.dispatch.Dispatcher;
import iwin.front.net.server.IStringProcessor;
import iwin.util.SpendTimer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * taidi (20120702 已上正式)
 * @author leotu@nec.com.tw
 */
public class BizProcessor implements IStringProcessor {
	final protected static Logger log = LoggerFactory.getLogger(BizProcessor.class);
	static protected boolean debug = true; // TODO

	@Override
	public String handleRequest(String input) {
		SpendTimer st = null;
		if (debug) {
			st = new SpendTimer();
			st.timerStart();
		}
		// log.debug("input=[" + input + "]");
		String output = null;
		try {
			Dispatcher dispatcher = new Dispatcher();
			output = dispatcher.dispatch(input);
			return output;
		} catch (RuntimeException e) {
			log.error("input=" + input, e);
			throw e;
		} catch (Exception e) {
			log.error("input=" + input, e);
			throw new RuntimeException(e);
		} finally {
			if (debug && st != null) {
				st.timerStop();
				log.debug("Spend time=" + st.toTimeFormatStr() + ", output.length=" + (output == null ? null : output.length()) + ", input=[" + input
						+ "]");
			}
		}
	}

}
