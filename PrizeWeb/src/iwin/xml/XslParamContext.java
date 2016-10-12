package iwin.xml;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

/**
 * 
 * @author leotu@nec.com.tw
 */
public class XslParamContext {

	protected static final Logger log = LoggerFactory.getLogger(XslParamContext.class);

	public static final ThreadLocal<XslParamContext> threadLocal = new ThreadLocal<XslParamContext>();

	protected Map<String, Object> container = new HashMap<String, Object>();

	public XslParamContext() {
	}

	// public XslParamContext(Object another) {
	// this.container = ((XslParamContext)another).container;
	// }

	// @Override
	public Object put(String name, Object value) {
		if (container.containsKey(name)) {
			log.warn("containsKey(" + name + ")");
		}
		log.debug("name=[" + name + "], value=" + value);
		return container.put(name, value);
	}

	// @Override
	public Object get(Object name) {
		if (!container.containsKey(name)) {
			log.warn("!containsKey(" + name + "), this=" + this);
		}
		return container.get(name);
	}

	// ==================
	// public Object getValue(String name) {
	// Object value = get(name);
	// log.debug("non-static name=[" + name + "], value=[" + value + "]");
	// return value;
	// }

	static public Object getValue(Object paramContext, String name) {
		if (!(paramContext instanceof XslParamContext)) {
			log.error("!(paramContext instanceof  XslParamContext), paramContext=" + paramContext);
		}
		XslParamContext context = (XslParamContext) paramContext;
		Object value = context.get(name);
		log.debug("sttaic name=[" + name + "], value=[" + value + "], paramContext=" + paramContext);
		return value;
	}

	static public Object getXPathValue(Object paramContext, String name, String path) {
		XslParamContext context = (XslParamContext) paramContext;
		Object value = context.get(name);
		if (value == null) {
			log.warn("(value == null), name=[" + name + "], path=[" + path + "]");
		}
		if (!(value instanceof Node)) {
			log.warn("!(value instanceof Node), name=[" + name + "], path=[" + path + "], value.class=" + value.getClass().getName());
		}
		try {
			String pathValue = XmlManipulation.getNodeTextValue((Node) value, path);
			log.debug("pathValue=[" + pathValue + "] by path=" + path + " of name=[" + name + "]");
			return pathValue;
		} catch (Exception e) {
			log.error("name=[" + name + "], path=[" + path + "]", e);
			throw new RuntimeException(e);
		}
	}

	// ==================
	static public XslParamContext initThreadVar() {
		XslParamContext context = new XslParamContext();
		threadLocal.set(context);
		return context;
	}

	static public void removeThreadVar() {
		threadLocal.remove();
	}

	// ==================
	static public XslParamContext getThreadVar() {
		XslParamContext context = threadLocal.get();
		if (context == null) {
			log.info("(context == null)");
			context = initThreadVar();
		}
		return context;
	}

	static public void setAttribute(String name, Object value) {
		getThreadVar().put(name, value);
	}

	static public Object getAttribute(String name) {
		return getThreadVar().get(name);
	}

	@Override
	public String toString() {
		return "XslParamContext [container=" + container + "]";
	}

}
