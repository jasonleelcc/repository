package iwin.util;

import iwin.conf.Netconfig;

import java.net.ProxySelector;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnRoutePNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author leotu@nec.com.tw
 */
public class ConfigProxy {
	final protected static Logger log = LoggerFactory.getLogger(ConfigProxy.class);
	protected boolean debug = false;

	// protected Netconfig netconfig;
	//
	// public ConfigProxy(Netconfig netconfig) {
	// this.netconfig = netconfig;
	// }

	/**
	 * 
	 * @param httpClient
	 * @throws Exception
	 */
	public void setHttpClientProxy(HttpClient httpClient, String proxyHost, String proxyPort) {
		if (proxyHost == null || proxyHost.isEmpty()) {
			return;
		}
		if (proxyPort == null || proxyPort.isEmpty()) {
			proxyPort = "80";
		}
		HttpHost hcProxyHost = new HttpHost(proxyHost, Integer.parseInt(proxyPort), "http");
		if (debug) {
			log.debug("hcProxyHost=" + hcProxyHost + ", httpClient=" + httpClient);
		}
		// httpClient.getCredentialsProvider().setCredentials(new AuthScope(proxyHost, proxyPort),
		// new UsernamePasswordCredentials(proxyUsername, new String(proxyPassword)));
		httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, hcProxyHost);
	}

	// /**
	// *
	// * @param httpClient
	// * @throws Exception
	// */
	// public void setInternetProxy(Netconfig netconfig, HttpClient httpClient) {
	// String proxyHost = netconfig.getIniValue("internet.proxy.host");
	// if (proxyHost == null || proxyHost.isEmpty()) {
	// return;
	// }
	// String proxyPort = netconfig.getIniValue("internet.proxy.port");
	// if (proxyPort == null || proxyPort.isEmpty()) {
	// proxyPort = "80";
	// }
	// setProxy(httpClient, proxyHost, proxyPort);
	// }

	/**
	 * Only for debugging
	 */
	public boolean setEcardProxySelector(Netconfig netconfig) {
		String proxyHost = netconfig.getIniValue("ecard.proxy.host");
		if (proxyHost == null || proxyHost.isEmpty()) {
			return false;
		}
		String proxyPort = netconfig.getIniValue("ecard.proxy.port");
		if (proxyPort == null || proxyPort.isEmpty()) {
			proxyPort = "80";
		}
		if (debug) {
			log.debug("ecard proxyHost=" + proxyHost + ", proxyPort=" + proxyPort);
		}
		HexaProxySelector ps = new HexaProxySelector();
		ps.setHttpProxyHost(proxyHost); // TODO
		ps.setHttpProxyPort(Integer.parseInt(proxyPort));
		ProxySelector.setDefault(ps);
		return true;
		// AxisProperties.setProperty("http.proxyHost", "172.16.2.82");
		// AxisProperties.setProperty("http.proxyPort", "3030");
		//
		// System.setProperty("http.proxyHost", "172.16.2.82");
		// System.setProperty("http.proxyPort", "3030");
		//
		// AxisProperties.setProperty("axis.http.client.connection.pool.timeout", "60000");
		// AxisProperties.setProperty("axis.http.client.connection.default.so.timeout", "60000");
		// AxisProperties.setProperty("axis.http.client.connection.default.connection.timeout", "60000");
	}
}
