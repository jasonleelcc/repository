package iwin.admin;

import net.bull.javamelody.MonitoringProxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * http://127.0.0.1:8080/IwinWeb/monitoring
 * 
 * @author leotu@nec.com.tw
 */
public class Monitoring {
	protected static final Logger log = LoggerFactory.getLogger(Monitoring.class);

	/**
	 * 
	 * @param <T>
	 * @param facade
	 *            muse be interface
	 * @param name
	 * @return
	 */
	public static <T> T add(T facade, String name) {
		// if (!facade.getClass().isInterface()) {
		// throw new RuntimeException("(!facade.getClass().isInterface()), facade.class=" + facade.getClass());
		// }
		return MonitoringProxy.createProxy(facade, name);
	}

	// public interface IWarp<W> {
	// public W execute();
	// }

}
