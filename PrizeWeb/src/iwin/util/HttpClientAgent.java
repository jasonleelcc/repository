package iwin.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestUserAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author leotu@nec.com.tw
 */
public class HttpClientAgent {
	protected static final Logger log = LoggerFactory.getLogger(HttpClientAgent.class);

	private String httpUrl;
	private HttpPost httpPost = null;
	// private HttpClient httpClient = null;
	private DefaultHttpClient httpClient = null;
	private Reader in = null;
	private String readerEncoding = "UTF-8";
	private String writerEncoding = "UTF-8";

	// private String proxyHost = null;
	// private String proxyPort = null;

	// public HttpClientAgent() {
	// super();
	// }

	/**
	 * 指定欲連線的主機URL
	 * 
	 * @param httpUrl
	 */
	public HttpClientAgent(String httpUrl) {
		this.httpUrl = httpUrl;
	}

	/**
	 * 與主機連線
	 */
	public void send(String xmlString) throws java.io.IOException {
		// log.debug("...");
		try {
			// URI uri = URIUtils.createURI("http", "www.google.com", -1, "/search", URLEncodedUtils.format(qparams, "UTF-8"), null);
			httpPost = new HttpPost(new URL(httpUrl).toURI());
			log.debug("httpPost.URI=[" + httpPost.getURI() + "]");
			//
			// httpPost.setHeader("Content-type", "text/plain; charset=" + writerEncoding);
			// httpPost.setHeader("Accept", "text/xml");
			// XXX: DefaultConnectionReuseStrategy
			HttpParams httpParams = new BasicHttpParams();
			HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setUserAgent(httpParams, Global.IE7);
			//
			BasicHttpProcessor httpProcessor = new BasicHttpProcessor();
			httpProcessor.addInterceptor(new RequestContent());
			httpProcessor.addInterceptor(new RequestUserAgent());
			//
			StringEntity dataEntity = new StringEntity(xmlString, "text/xml", writerEncoding);
			httpPost.setEntity(dataEntity);

			InputStream content = null;

			// DefaultClientConnection a;
			httpClient = new DefaultHttpClient(httpParams);
			// HTTP.CONN_KEEP_ALIVE
			// ConnectionKeepAliveStrategy keepAliveStrategy= httpClient.getConnectionKeepAliveStrategy();
			// org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy
			// log.debug("keepAliveStrategy=" + keepAliveStrategy);
			//
			// ConnectionReuseStrategy reuseStrategy= httpClient.getConnectionReuseStrategy();
			// org.apache.http.impl.DefaultConnectionReuseStrategy
			// log.debug("reuseStrategy=" + reuseStrategy);
			//
			// httpClient.setReuseStrategy( new DefaultConnectionReuseStrategy() {
			//
			// @Override
			// public boolean keepAlive(HttpResponse response, HttpContext context) {
			// return false;
			// //return super.keepAlive(response, context);
			// }
			//
			// });
			//

			// httpParams.removeParameter("Connection"); // Connection: Keep-Alive
			// httpParams.removeParameter("Keep-Alive"); // Connection: Keep-Alive
			//
			// log.debug("Before: execute");
			HttpResponse response = httpClient.execute(httpPost);
			// log.debug("After: execute");
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				entity = new BufferedHttpEntity(entity);
				content = entity.getContent();
			}
			//
			// log.debug("Before: InputStream");
			in = content == null ? null : new InputStreamReader(content, readerEncoding); // new BufferedReader(
			// log.debug("After: InputStream");
		} catch (Exception e) {
			throw new IOException("httpUrl=[" + httpUrl + "], " + e, e);
		}
	}

	/**
	 * 與主機切斷連線
	 */

	public void disconnect() {
		// log.debug("...");
		try {
			if (in != null) {
				IOUtils.closeQuietly(in);
			}
		} catch (Exception e) {
			log.warn(e.toString());
		} finally {
			if (httpClient != null) {
				// org.apache.http.impl.conn.SingleClientConnManager;
				try {
					httpClient.getConnectionManager().shutdown();
				} catch (Exception e) {
					log.warn("httpUrl=" + httpUrl, e);
				}
			}
		}
	}

	/**
	 * 等候主機回應
	 * 
	 * @return java.lang.String
	 * @exception java.io.IOException
	 */

	public String retrieve() throws java.io.IOException {
		return in == null ? null : IOUtils.toString(in);
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