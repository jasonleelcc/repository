package iwin.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * http://download.oracle.com/javase/1.5.0/docs/guide/net/proxies.html
 * 
 * @author leotu@nec.com.tw
 */
public class HexaProxySelector extends ProxySelector {
	protected static final Logger log = LoggerFactory.getLogger(HexaProxySelector.class);

	static protected ProxySelector defaultSelector;
	// System.setProperty("http.proxyHost", "172.16.2.82");
	// System.setProperty("http.proxyPort", "3030");

	protected String httpProxyHost = null;
	protected int httpProxyPort = -1;

	static {
		defaultSelector = ProxySelector.getDefault();
		log.debug("defaultSelector=" + defaultSelector);
	}

	public String getHttpProxyHost() {
		return httpProxyHost;
	}

	public void setHttpProxyHost(String httpProxyHost) {
		this.httpProxyHost = httpProxyHost;
	}

	public int getHttpProxyPort() {
		return httpProxyPort;
	}

	public void setHttpProxyPort(int httpProxyPort) {
		this.httpProxyPort = httpProxyPort;
	}

	@Override
	public List<Proxy> select(URI uri) {
		// log.debug("uri=[" + uri + "]");
		if (uri == null) {
			throw new IllegalArgumentException("URI can't be null.");
		}
		String protocol = uri.getScheme();
		String host = uri.getHost();
		//int port = uri.getPort();
		//String path = uri.getPath();
		//String query = uri.getQuery();
		// log.debug("uri=[" + uri + "], protocol=[" + protocol + "], host=[" + host + "], port=[" + port + "], path=[" + path + "], query=[" + query
		// + "]");
		if ("127.0.0.1".equals(host) || "localhost".equals(host)) {
			return defaultSelector.select(uri);
		}
		if (httpProxyHost != null && "http".equalsIgnoreCase(protocol)) {
			Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress(httpProxyHost, httpProxyPort == -1 ? 80 : httpProxyPort));
			log.debug("proxy=" + proxy);
			List<Proxy> l = new ArrayList<Proxy>();
			l.add(proxy);
			return l;
		} else {
			// ArrayList<Proxy> l = new ArrayList<Proxy>();
			// l.add(Proxy.NO_PROXY);
			// return l;
			return defaultSelector.select(uri);
		}
	}

	@Override
	public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
		log.warn("URI=[" + uri + "], SocketAddress=" + sa + ", IOException=" + ioe);
	}

}
