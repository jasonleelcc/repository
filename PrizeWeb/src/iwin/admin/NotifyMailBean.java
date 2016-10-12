package iwin.admin;

import java.io.Serializable;

/**
 * 
 * @author leotu@nec.com.tw
 */
public class NotifyMailBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private String to;
	private String subject;
	private boolean xmlData;
	private String body;
	private String data;

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public boolean isXmlData() {
		return xmlData;
	}

	public void setXmlData(boolean xmlData) {
		this.xmlData = xmlData;
	}

	@Override
	public String toString() {
		return "NotifyMailBean [to=" + to + ", subject=" + subject + ", xmlData=" + xmlData + ", body=" + body + ", data=" + data + "]";
	}

}
