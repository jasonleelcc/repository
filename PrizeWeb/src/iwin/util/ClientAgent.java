package iwin.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author TaiOne International Ltd. 電子金融伺服平台前置通訊模組
 * @author Refactor by leotu@nec.com.tw
 */
public class ClientAgent {
	protected static final Logger log = LoggerFactory
			.getLogger(ClientAgent.class);

	private java.net.InetAddress address = null;
	private int port = -1;
	private Socket connection = null;
	private Reader in = null;
	private Writer out = null;
	private String readerEncoding = "UTF8";
	private String writerEncoding = "UTF8";

	public ClientAgent() {
		super();
	}

	/**
	 * 指定欲連線的主機IP ＆ Port
	 * 
	 * @param add
	 *            java.net.InetAddress：連線的主機IP
	 * @param p
	 *            java.lang.Integer：連線的主機Port
	 */
	public ClientAgent(InetAddress add, int port) {
		this.address = add;
		this.port = port;
	}

	/**
	 * 與主機連線
	 */
	public void connect() throws java.io.IOException {
		// log.debug("...");
		try {
			connection = new Socket(address, port);
			out = new OutputStreamWriter(connection.getOutputStream(),
					writerEncoding);
			in = new InputStreamReader(connection.getInputStream(),
					readerEncoding); // new BufferedReader(
		} catch (Exception e) {
			throw new IOException("address=[" + address + "], port=[" + port
					+ "], " + e, e);
		}
	}

	/**
	 * 與主機切斷連線
	 */

	public void disconnect() {
		// log.debug("...");
		try {
			if (out != null) {
				IOUtils.closeQuietly(out);
				out.close();
			}
			if (in != null) {
				IOUtils.closeQuietly(in);
			}
		} catch (Exception e) {
			log.warn(e.toString());
		} finally {
			if (connection != null) {
				IOUtils.closeQuietly(connection);
			}
			connection = null;
			in = null;
			out = null;
		}
	}

	/**
	 * 等候主機回應
	 * 
	 * @return java.lang.String
	 * @exception java.io.IOException
	 */

	public String retrieve() throws java.io.IOException {
		return IOUtils.toString(in);
	}

	/**
	 * 傳送Data給主機
	 * 
	 * @return java.lang.String
	 * @exception java.io.IOException
	 */
	public void send(String xmlString) throws java.io.IOException {
		out.write(xmlString);
		out.flush(); // XXX: must do it !
	}

	/**
	 * 指定欲連線的主機IP ＆ Port 傳送 Data，不等候主機回傳資料
	 * 
	 * @param add
	 *            java.net.InetAddress：連線的主機IP
	 * @param p
	 *            java.lang.Integer：連線的主機Port
	 * @param data
	 *            java.lang.Integer：傳給主機的Data主機
	 * @exception java.io.IOException
	 */
	public void sendOnce(InetAddress add, int p, String data)
			throws java.io.IOException {
		address = add;
		port = p;
		try {
			connect();
			send(data);
		} finally {
			disconnect();
		}
	}

	/**
	 * 指定欲連線的主機IP ＆ Port 傳送 Data，並等候主機回傳資料
	 * 
	 * @return java.lang.String
	 * @param add
	 *            java.net.InetAddress：連線的主機IP
	 * @param p
	 *            java.lang.Integer：連線的主機Port
	 * @param data
	 *            java.lang.Integer：傳給主機的Data
	 * @exception java.io.IOException
	 */
	public String sendMsg(InetAddress add, int p, String data)
			throws java.io.IOException {
		address = add;
		port = p;
		String msg = "";
		try {
			connect();
			send(data);
			msg = retrieve();
		} finally {
			disconnect();
		}
		return msg;
	}

	public String getReaderEncoding() {
		return readerEncoding;
	}

	public void setReaderEncoding(String readerEncoding) {
		this.readerEncoding = readerEncoding;
	}

	public String getWriterEncoding() {
		return writerEncoding;
	}

	public void setWriterEncoding(String writerEncoding) {
		this.writerEncoding = writerEncoding;
	}
}