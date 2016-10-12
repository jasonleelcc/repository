package iwin.jms;

import iwin.admin.NotifyMailBean;
import iwin.conf.Netconfig;
import iwin.jms.server.JmsServer;
import iwin.util.XmlToBeanUtil;

import javax.activation.DataSource;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.mail.ByteArrayDataSource;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.MultiPartEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.spi.LoggingEventVO;

/**
 * during the default configuration phase of the underlying logging system.
 * <p/>
 * http://www.slf4j.org/codes.html#substituteLogger
 * 
 * @author leotu@nec.com.tw
 */
public class AdminMailSendingListener implements MessageListener {
	protected static Logger log = null;
	private Netconfig netconfig = JmsServer.getInstance().getNetconfig();

	@Override
	public void onMessage(Message message) {
		log = initLogger();
		//
		try {
			// log.debug("@ message=" + message);
			Object obj = ((ObjectMessage) message).getObject();
			// object.class=ch.qos.logback.classic.spi.LoggingEventVO
			//log.debug("@ object.class=" + obj.getClass().getName());
			//
			boolean enable = Boolean.parseBoolean(netconfig.getIniValue("admin.mail.enable"));
			if (!enable) {
				log.debug("skip! enable=" + enable);
				return;
			}
			if (obj instanceof LoggingEventVO) {
				LoggingEventVO vo = (LoggingEventVO) obj;
				String msg = vo.getMessage();
				XmlToBeanUtil<NotifyMailBean> convert = new XmlToBeanUtil<NotifyMailBean>(NotifyMailBean.class);
				NotifyMailBean notify = convert.xmlToBean(msg);
				log.debug("notify=" + notify);

				// Email email = new SimpleEmail();
				MultiPartEmail email = new MultiPartEmail();
				email.setHostName(netconfig.getIniValue("admin.mail.smtp.host"));
				email.setSmtpPort(Integer.parseInt(netconfig.getIniValue("admin.mail.smtp.port")));
				String user = netconfig.getIniValue("admin.mail.smtp.user");
				if (user != null && !user.isEmpty()) {
					email.setAuthenticator(new DefaultAuthenticator(user, netconfig.getIniValue("admin.mail.smtp.password")));
				}
				email.setTLS(false);
				email.setFrom(netconfig.getIniValue("admin.mail.from"));
				String eventTime = DateFormatUtils.format(vo.getTimeStamp(), "yyyy-MM-dd HH:mm:ss");
				String subject = notify.getSubject() + " (" + eventTime + ")";
				email.setSubject(subject);
				email.setMsg(notify.getBody());
				email.addTo(netconfig.getIniValue("admin.mail.to"));
				// add the attachment
				if (notify.getData() != null && !notify.getData().isEmpty()) {
					String name;
					DataSource ds;
					if (notify.isXmlData()) {
						//String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" + notify.getData();
						String xml = notify.getData();
						ds = new ByteArrayDataSource(xml.getBytes("UTF-8"), "application/xml"); // "application/octet-stream"
						String fileName = "Information-" + DateFormatUtils.format(vo.getTimeStamp(), "yyyyMMdd-HHmmss") + ".xml";
						name = fileName;
					} else {
						ds = new ByteArrayDataSource(notify.getData().getBytes("UTF-8"), "application/octet-stream");
						String fileName = "Information-" + DateFormatUtils.format(vo.getTimeStamp(), "yyyyMMdd-HHmmss");
						name = fileName;
					}
					String description = notify.getSubject();
					String disposition = EmailAttachment.ATTACHMENT;
					// EmailAttachment attachment = new EmailAttachment();
					// email.attach(attachment);
					email.attach(ds, name, description, disposition);
				}
				email.send();
			} else {
				log.warn("skip: " + obj);
			}
		} catch (Exception e) {
			log.warn("message=" + message, e);
		}
	}

	//
	protected Logger initLogger() {
		if (log == null) {
			log = LoggerFactory.getLogger(AdminMailSendingListener.class);
		}
		return log;
	}
}
