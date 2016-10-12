package iwin.util;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author leotu@nec.com.tw
 */
public class UuidUtil {

	/**
	 * 
	 * @return uuid without '-' character
	 */
	static public String generateUuid() {
		String uuid = UUID.randomUUID().toString();
		return StringUtils.remove(uuid, "-");
	}
}
