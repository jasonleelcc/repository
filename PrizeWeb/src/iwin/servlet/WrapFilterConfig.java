package iwin.servlet;

import java.util.Enumeration;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author leotu@nec.com.tw
 */
public class WrapFilterConfig implements FilterConfig {
	protected static final Logger log = LoggerFactory.getLogger(WrapFilterConfig.class);

	private FilterConfig config;

	public WrapFilterConfig(FilterConfig config) {
		super();
		this.config = config;
		log.debug("...");
	}

	public String getFilterName() {
		return config.getFilterName();
	}

	public String getInitParameter(String name) {
		String value = config.getInitParameter(name);
		//log.debug("name=[" + name + "], value=[" + value + "]");
		return (value == null || value.isEmpty()) ? value : valueWithSystemEnv(value);
	}

	public Enumeration<String> getInitParameterNames() {
		return config.getInitParameterNames();
	}

	public ServletContext getServletContext() {
		return config.getServletContext();
	}

	/**
	 * System.getProperty(...)
	 */
	protected String valueWithSystemEnv(String value) {
		boolean found = value.indexOf("${") != -1 && value.indexOf("}") != -1;
		if (!found) {
			return value;
		}
		//log.debug("OLD: value=[" + value + "]");
		StringBuffer sb = new StringBuffer(value.length());
		//
		int stokenPrefixLen = "${".length();
		int stokenPostfixLen = "}".length();
		//
		int startFromIndex = 0;
		int start = value.indexOf("${", startFromIndex);
		int end = -1;
		int leftFromIndex = 0;
		while (start != -1) {
			int endFromIndex = start + stokenPrefixLen;
			end = value.indexOf("}", endFromIndex);
			if (end == -1) {
				break;
			}
			//
			String substr = value.substring(start + stokenPrefixLen, end); // TODO
			// log.debug("substr=[" + substr + "], start=" + start + ", startFromIndex=" + startFromIndex + ", endFromIndex=" + endFromIndex
			// + ", end=" + end);
			String propertyValue = System.getProperty(substr);
			// log.debug("propertyValue=[" + propertyValue + "], substr=[" + substr + "]");
			String prefix = value.substring(startFromIndex, start);
			// log.debug("sb=[" + sb + "], prefix=[" + prefix + "]");
			sb.append(prefix);
			if (propertyValue != null) {
				sb.append(propertyValue);
				// log.debug("* sb=[" + sb + "]");
			} else { // FIXME
				sb.append("${");
				sb.append(substr);
				sb.append("}");
				// log.debug("* sb=[" + sb + "]");
			}
			//
			startFromIndex = end + stokenPostfixLen;
			start = value.indexOf("${", startFromIndex);
			// log.debug("@ start=[" + start + "]");
			leftFromIndex = startFromIndex;
		}
		//
		String left = value.substring(leftFromIndex);
		// log.debug("left=[" + left + "], leftFromIndex=[" + leftFromIndex + "]");
		sb.append(left);
		log.debug("OLD: [" + value + "] to NEW: [" + sb + "]");
		return sb.toString();
	}

}
