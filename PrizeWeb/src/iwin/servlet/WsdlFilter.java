package iwin.servlet;

import iwin.servlet.WsdlServletResponseWrapper.CacheData;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author leotu@nec.com.tw
 */
public class WsdlFilter implements Filter {
	protected static final Logger log = LoggerFactory.getLogger(WsdlFilter.class);

	protected boolean debug = false;
	protected boolean enable = false;
	protected Map<String, CacheData> cache = new ConcurrentHashMap<String, CacheData>();

	@Override
	public void destroy() {
		log.debug("enable=" + enable);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		debug = Boolean.parseBoolean(arg0.getInitParameter("debug"));
		enable = Boolean.parseBoolean(arg0.getInitParameter("enable"));
		log.debug("enable=" + enable + ", debug=" + debug);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (enable && request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
			StringBuffer url = ((HttpServletRequest) request).getRequestURL();
			String uri = ((HttpServletRequest) request).getRequestURI();
			if (debug) {
				log.debug("*** url=[" + url + "], uri=[" + uri + "]");
			}
			CacheData wsdlData = cache.get(uri);
			if (wsdlData == null) {
				chain.doFilter(request, response);
				WsdlServletResponseWrapper responseWrapper = new WsdlServletResponseWrapper((HttpServletResponse) response);
				chain.doFilter(request, responseWrapper);
				wsdlData = responseWrapper.getCacheData();
				if (wsdlData.getStatus() == HttpServletResponse.SC_OK) { // 200
					cache.put(uri, wsdlData);
					// if (debug) {
					log.debug("*** use NEW wsdl data: length=[" + wsdlData.getCaptureContentLength() + "], uri=[" + uri + "]");
					// }
				} else {
					log.warn("wsdlData.getStatus()=" + wsdlData.getStatus());
				}
				//
				// log.debug("*** getContentType=[" + responseWrapper.getContentType() + "]");
				// log.debug("*** getCharacterEncoding=[" + responseWrapper.getCharacterEncoding() + "]");
				// log.debug("wsdlData=" + new String(wsdlData, "UTF8"));
			} else {
				response.setContentType(wsdlData.captureContentType);
				// response.setCharacterEncoding(wsdlData.getCaptureCharacterEncoding());
				// response.setContentLength(wsdlData.getCaptureContentLength());
				response.setBufferSize(wsdlData.getBufferSize());
				for (String name : wsdlData.getHeaders().keySet()) {
					((HttpServletResponse) response).setHeader(name, wsdlData.getHeaders().get(name));
				}
				//
				response.getOutputStream().write(wsdlData.getBuffer());
				if (debug) {
					log.debug("*** use OLD cache wsdl data: length=[" + wsdlData.getCaptureContentLength() + "], uri=[" + uri + "]");
					// log.debug("*** getContentType=[" + response.getContentType() + "]");
					// log.debug("*** getCharacterEncoding=[" + response.getCharacterEncoding() + "]");
				}
			}
		} else {
			chain.doFilter(request, response);
		}
	}

}
