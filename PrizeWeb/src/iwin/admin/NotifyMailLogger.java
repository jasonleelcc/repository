package iwin.admin;

import iwin.log.DiagnoseLogFactory;
import iwin.util.BeanToXmlUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author leotu@nec.com.tw
 */
public class NotifyMailLogger {
	final static Logger log = LoggerFactory.getLogger(NotifyMailLogger.class);
	final static Logger adminMail = DiagnoseLogFactory.getAdminMailLogger();

	public static void send(String subject, String body, String data, boolean isXmlData) {
		NotifyMailBean notify = new NotifyMailBean();
		//notify.setTo("leo.tu.taipei@gmail.com");
		notify.setSubject(subject);
		notify.setBody(body);
		notify.setData(data);
		notify.setXmlData(isXmlData);
		BeanToXmlUtil<NotifyMailBean> converter = new BeanToXmlUtil<NotifyMailBean>();
		String xml = converter.beanToXml(notify);
		//log.debug("xml=[" + xml + "]");
		adminMail.trace(xml);
	}

}
