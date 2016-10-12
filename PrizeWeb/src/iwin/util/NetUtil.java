package iwin.util;

import iwin.admin.Jmx;
import iwin.conf.Netconfig;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.validator.GenericValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author leotu@nec.com.tw
 */
public class NetUtil {
	final protected static Logger log = LoggerFactory.getLogger(NetUtil.class);

	public static String getEaiAsBankIp(Netconfig netconfig) {
		String ip = netconfig.getIniValue("eai.as.bank.ip");
		if (!GenericValidator.isBlankOrNull(ip)) {
			return ip;
		} else {
			List<String> ipList = getIpAddress(true);
			return ipList.get(0);
		}
	}

	public static String getEaiAsEcardIp(Netconfig netconfig) {
		String ip = netconfig.getIniValue("eai.as.ecard.ip");
		if (!GenericValidator.isBlankOrNull(ip)) {
			return ip;
		} else {
			List<String> ipList = getIpAddress(true);
			return ipList.get(0);
		}
	}

	public static String getEaiAsApIp(Netconfig netconfig) {
		String ip = netconfig.getIniValue("eai.as.ap.ip");
		if (!GenericValidator.isBlankOrNull(ip)) {
			return ip;
		} else {
			List<String> ipList = getIpAddress(true);
			return ipList.get(0);
		}
	}

	public static String getLocalIp() {
		List<String> ipList = getIpAddress(true);
		return ipList.get(0);
	}

	public static InetAddress getLocalAddress() {
		try {
			return InetAddress.getByName(getLocalIp());
		} catch (UnknownHostException e) {
			try {
				return InetAddress.getByName("127.0.0.1");
			} catch (UnknownHostException e1) {
				throw new RuntimeException(e1);
			}
		}
	}

	public static String getLocalHostIp() {
		try {
			// return InetAddress.getLocalHost().getHostAddress();
			return InetAddress.getByName("127.0.0.1").getHostAddress();
		} catch (UnknownHostException e) {
			return "127.0.0.1";
		}
	}

	public static InetAddress getLocalHostAddress() {
		try {
			return InetAddress.getByName(getLocalHostIp());
		} catch (UnknownHostException e) {
			// return getLocalAddress();
			try {
				return InetAddress.getLocalHost();
			} catch (UnknownHostException e1) {
				throw new RuntimeException(e1);
			}
		}
	}

	// protected static MBeanServer server;
	// static {
	// MBeanServer platformServer = ManagementFactory.getPlatformMBeanServer();
	// log.debug("platformServer=" + platformServer);
	// //
	// String agentId = null;
	// List<MBeanServer> servers = MBeanServerFactory.findMBeanServer(agentId);
	// if (servers != null && servers.size() > 0) {
	// if (servers.size() > 1) {
	// log.warn("Found more than one MBeanServer instance" + (agentId != null ? " with agent id [" + agentId + "]" : "")
	// + ". Returning first from list.");
	// }
	// // for (MBeanServer server : servers) {
	// // log.debug("server=" + server.getDefaultDomain());
	// // }
	// server = servers.get(0);
	// }
	// log.debug("server=" + server);
	// }

	public static String rebuildLocalUrl(String url) {
		String[] strAry = getTomcatBindingAddress();
		if (strAry == null) {
			log.debug("(strAry == null)");
			return url;
		}
		String host = strAry[0];
		if (host == null) {
			// getIpAddress(true);
			host = "127.0.0.1";
		}
		String port = strAry[1];
		//
		try {
			URL oldURL = new URL(url);
			URL newURL = new URL(oldURL.getProtocol(), host, Integer.parseInt(port), oldURL.getFile());
			// log.debug("newURL=" + newURL + " of url=" + url);
			return newURL.toString();
		} catch (Exception e) {
			throw new RuntimeException("url=" + url, e);
		}
	}

	// ===
	public static List<String> getSiteBindingAddress(MBeanServer server) {
		return getSiteBindingAddress(server, true);
	}

	public static List<String> getSiteBindingAddress(MBeanServer server, boolean checkNetworkInterface) {
		List<String> ipList = new ArrayList<String>();
		if (Global.IS_TOMCAT) {
			String[] checking = getTomcatBindingAddress(server);
			if (checking != null) {
				String address = checking[0];
				String port = checking[1];
				log.debug("address=[" + address + "], port=[" + port + "]");
				if (address != null) {
					log.debug("add tomcat address=[" + address + "], port=[" + port + "]");
					ipList.add(address);
				}
			}
		}
		if (ipList.size() == 0) {
			ipList = getIpAddress(checkNetworkInterface);
		}
		return ipList;
	}

	public static List<String> getIpAddress(boolean checkNetworkInterface) {
		List<String> ipList = new ArrayList<String>();
		if (checkNetworkInterface) {
			Enumeration<NetworkInterface> netInterfaces = null;
			try {
				netInterfaces = NetworkInterface.getNetworkInterfaces();
			} catch (SocketException e) {
				log.error("", e);
			}
			if (netInterfaces == null) {
				log.info("netInterfaces=" + netInterfaces);
			}
			while (netInterfaces != null && netInterfaces.hasMoreElements()) {
				NetworkInterface ni = netInterfaces.nextElement();
				Enumeration<InetAddress> address = ni.getInetAddresses();
				while (address.hasMoreElements()) {
					InetAddress addr = address.nextElement();
					// if (!addr.isLoopbackAddress() && !addr.isSiteLocalAddress() &&
					// !(addr.getHostAddress().indexOf(":") >
					// -1))
					if (!addr.isLoopbackAddress() && addr.isSiteLocalAddress()) {
						// log.debug("add ip=[" + addr.getHostAddress() + "] of addr=" + addr);
						ipList.add(addr.getHostAddress());
					}

				}
			}
		}
		if (ipList.size() == 0) {
			if (checkNetworkInterface && ipList.size() == 0) {
				log.info("(ipList.size() == 0");
			}
			try {
				ipList.add(InetAddress.getLocalHost().getHostAddress());
			} catch (UnknownHostException e) {
				log.error("", e);
				log.debug("add ip=[127.0.0.1] of ipList.size()=" + ipList.size());
				ipList.add("127.0.0.1");
			}
		}
		return ipList;
	}

	/**
	 * 
	 * @return String[0]: address, String[1]: port
	 */
	public static String[] getTomcatBindingAddress() {
		return getTomcatBindingAddress(null);
	}

	/**
	 * 
	 * @return String[0]: address, String[1]: port
	 */
	public static String[] getTomcatBindingAddress(MBeanServer server) {
		if (server == null) {
			log.debug("Parameter: (server == null)");
			server = Jmx.getMBeanServer();
			if (server == null) {
				log.warn("(server == null)");
				return null;
			}
		}
		try {
			// Catalina:type=Connector,port=80,address="/192.168.1.225"
			// Catalina:type=Connector,port=8080
			Set<ObjectName> names = server.queryNames(new ObjectName("Catalina:type=Connector,*"), null);
			Iterator<ObjectName> it = names.iterator();
			// log.debug("it.hasNext=[" + it.hasNext() + "], it=" + it + ", names=" + names);
			for (; it.hasNext();) {
				ObjectName oname = it.next();
				// log.debug("oname[" + c + "]=[" + oname + "]");
				try {
					MBeanInfo info = server.getMBeanInfo(oname);
					// log.info("className=[" + info.getClassName() + "");
					MBeanAttributeInfo attrs[] = info.getAttributes();
					boolean foundScheme = false;
					boolean foundProtocol = false;
					String addressValue = null;
					String portValue = null;
					for (int i = 0; i < attrs.length; i++) {
						String attName = attrs[i].getName();
						Object value = server.getAttribute(oname, attName);
						// log.debug("attName=[" + attName + "], value=[" + value + "], [" + (value == null ? "<null>" : value.getClass().getName())
						// + "]");
						if ("scheme".equals(attName) && "http".equals(value)) {
							foundScheme = true;
						}
						if ("protocol".equals(attName) && !"AJP/1.3".equals(value)) {
							foundProtocol = true;
						}
						if ("address".equals(attName)) {
							addressValue = value == null ? null : value.toString();
							if (addressValue != null && addressValue.startsWith("/")) {
								addressValue = addressValue.substring(1);
							}
						}
						if ("port".equals(attName)) {
							portValue = value == null ? null : value.toString();
						}
					}
					//
					if (foundScheme && foundProtocol && portValue != null) {
						return new String[] { addressValue, portValue };
					}
				} catch (Exception e) {
					log.error("", e);
				}
			}
		} catch (Exception e) {
			log.error("", e);
		}
		return null;

	}
}