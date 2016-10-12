package iwin.admin;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author leotu@nec.com.tw
 */
public class Jmx {
	protected static final Logger log = LoggerFactory.getLogger(Jmx.class);

	static public MBeanServer getMBeanServer() {
		return ManagementFactory.getPlatformMBeanServer();
	}

	static public void registerMBean(Object owner, DynamicMapMBean mbean, String objectNameStr) {
		log.debug("objectNameStr=[" + objectNameStr + "]");
		if (owner == null) {
			throw new IllegalArgumentException("(owner == null)");
		}
		if (mbean == null) {
			throw new IllegalArgumentException("(mbean == null)");
		}
		if (objectNameStr == null || objectNameStr.isEmpty()) {
			throw new IllegalArgumentException("(objectNameStr == null || objectNameStr.isEmpty())");
		}
		if (objectNameStr == null || objectNameStr.isEmpty()) {
			throw new IllegalArgumentException("(objectNameStr == null || objectNameStr.isEmpty())");
		}
		ObjectName objName = null;
		try {
			objName = new ObjectName(objectNameStr);
			mbean.init(owner.getClass()); // TODO
			getMBeanServer().registerMBean(mbean, objName);
		} catch (Exception e) {
			log.warn(objName == null ? "" : objName.toString(), e);
		}
	}

	static public void unregisterMBean(String objectNameStr) {
		log.debug("objectNameStr=[" + objectNameStr + "]");
		if (objectNameStr == null || objectNameStr.isEmpty()) {
			throw new IllegalArgumentException("(objectNameStr == null || objectNameStr.isEmpty())");
		}
		ObjectName objName = null;
		try {
			objName = new ObjectName(objectNameStr);
			getMBeanServer().unregisterMBean(objName);
		} catch (Exception e) {
			log.warn(objName == null ? "" : objName.toString(), e);
		}
	}
}
