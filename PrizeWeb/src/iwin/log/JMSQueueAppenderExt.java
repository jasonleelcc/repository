package iwin.log;

import iwin.jms.server.JmsServer;
import iwin.jms.server.JmsServiceLisenter;

import javax.jms.MessageListener;

import ch.qos.logback.classic.net.JMSQueueAppender;

/**
 * 
 * @author leotu@nec.com.tw
 */
public class JMSQueueAppenderExt extends JMSQueueAppender {

	protected boolean enable = false;
	protected String messageListenerClass;
	protected JmsServiceLisenter.Repository repo = null;

	// @Override
	// public synchronized void doAppend(ILoggingEvent eventObject) {
	// System.out.println("JMSQueueAppenderExt.doAppend(...), eventObject=" +eventObject);
	// super.doAppend(eventObject);
	// }

	/**
	 * @see ch.qos.logback.core.spi.LifeCycle#start()
	 */
	@Override
	public void start() {
		if (!enable) {
			started = true; // TODO
			return;
		}
		Class<?> clazz;
		try {
			clazz = Class.forName(messageListenerClass);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		if (!MessageListener.class.isAssignableFrom(clazz)) {
			throw new RuntimeException("(!MessageListener.class.isAssignableFrom(clazz)), clazz=" + clazz.getName());
		}
		Thread t = new Thread(new Runnable() {
			public void run() {
				JmsServer jmsServer = JmsServer.getInstance();
				while (!jmsServer.readyPing(JMSQueueAppenderExt.this.getQueueBindingName())) {
					try {
						// System.out.println("@@@ queueBindingName=[" + JMSQueueAppenderExt.this.getQueueBindingName() + "]");
						Thread.sleep(200); // TODO
					} catch (InterruptedException e) {
						// Nothing
					}
				}
				JMSQueueAppenderExt.this.superStart();
			}
		});
		t.start();
	}

	public void superStart() {
		super.start();
		if (isStarted()) {
			// System.out.println("messageListenerClass=" + messageListenerClass + ", queueBindingName=" + getQueueBindingName());
			try {
				MessageListener messageListener = (MessageListener) Class.forName(messageListenerClass).newInstance();
				JmsServiceLisenter service = new JmsServiceLisenter();
				repo = service.bindListener(getQueueBindingName(), messageListener);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public synchronized void stop() {
		if (!isStarted()) {
			return;
		}
		if (repo != null) {
			JmsServiceLisenter.stop(repo);
		}
		super.stop();
	}

	public String getMessageListenerClass() {
		return messageListenerClass;
	}

	public void setMessageListenerClass(String messageListenerClass) {
		this.messageListenerClass = messageListenerClass;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}
}
