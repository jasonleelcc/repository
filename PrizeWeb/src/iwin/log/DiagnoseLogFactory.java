package iwin.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author leotu@nec.com.tw
 */
public class DiagnoseLogFactory {

	protected static final Logger fromLogin = LoggerFactory.getLogger("from-login");
	protected static final Logger fromMobile = LoggerFactory.getLogger("from-mobile");
	protected static final Logger toEai = LoggerFactory.getLogger("to-eai");
	protected static final Logger fromEai = LoggerFactory.getLogger("from-eai");
	protected static final Logger toMobile = LoggerFactory.getLogger("to-mobile");
	protected static final Logger toEcard = LoggerFactory.getLogger("to-ecard");
	protected static final Logger fromEcard = LoggerFactory.getLogger("from-ecard");
	protected static final Logger imei = LoggerFactory.getLogger("imei");
	// protected static final Logger reset = LoggerFactory.getLogger("reset");
	//
	protected static final Logger schedulerJob = LoggerFactory.getLogger("scheduler-job");
	protected static final Logger adminMail = LoggerFactory.getLogger("admin-mail");

	public static Logger getFromLoginLogger() {
		return fromLogin;
	}

	public static Logger getFromMobileLogger() {
		return fromMobile;
	}

	public static Logger getToEaiLogger() {
		return toEai;
	}

	public static Logger getFromEaiLogger() {
		return fromEai;
	}

	public static Logger getToMobileLogger() {
		return toMobile;
	}

	public static Logger getToEcardLogger() {
		return toEcard;
	}

	public static Logger getFromEcardLogger() {
		return fromEcard;
	}

	public static Logger getImeiLogger() {
		return imei;
	}

	// public static Logger getResetLogger() {
	// return reset;
	// }

	// =====================
	public static Logger getSchedulerJobLogger() {
		return schedulerJob;
	}

	public static Logger getAdminMailLogger() {
		return adminMail;
	}

	// =====================
	// import static iwin.log.DiagnoseLogFactory.args;
	// log.info(String.format("%s = %s: %s", "a", "b", "c"));
	// log.info("{} = {}: {}", args("a", "b", "c"));
	public static Object[] args(Object... args) {
		return args;
	}
}
