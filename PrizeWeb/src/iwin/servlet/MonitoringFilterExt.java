package iwin.servlet;

import iwin.util.Global;
import iwin.util.ReflectUtil;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import net.bull.javamelody.MonitoringFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author leotu@nec.com.tw
 */
public class MonitoringFilterExt extends MonitoringFilter {
	protected static final Logger log = LoggerFactory.getLogger(MonitoringFilterExt.class);

	@Override
	public void init(FilterConfig config) throws ServletException {
		log.debug("IS_TOMCAT=" + Global.IS_TOMCAT);
		if (Global.IS_TOMCAT) {
			super.init(config);
		} else {
			super.init(new WrapFilterConfig(config));
		}
		//
		try {
			Class<?> clsObj = Class.forName("net.bull.javamelody.Parameters");
			//log.debug("clsObj=[" + clsObj + "]");
			String applicationStorageDirectory = (String) ReflectUtil.doMethodInvoke(clsObj, "getCurrentApplication");
			// String applicationStorageDirectory = Parameters.getCurrentApplication();
			log.info("applicationStorageDirectory=[" + applicationStorageDirectory + "]");
		} catch (Exception e) {
			log.warn(e.toString());
		}
	}

}
