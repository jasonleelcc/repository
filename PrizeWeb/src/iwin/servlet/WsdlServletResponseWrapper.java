package iwin.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author leotu@nec.com.tw
 */
public class WsdlServletResponseWrapper extends HttpServletResponseWrapper implements HttpServletResponse {
	protected static final Logger log = LoggerFactory.getLogger(WsdlServletResponseWrapper.class);
	protected boolean debug = false;
	protected HttpServletResponse response;
	protected ServletOutputStream servletOutputStream;
	protected PrintWriter printWriter;
	protected ByteArrayOutputStream baos;
	protected CacheData data;

	public static class CacheData {
		protected byte[] buffer = new byte[0];
		protected int captureContentLength = -1;
		protected String captureContentType = null;
		protected String captureCharacterEncoding = null;
		protected String printWriterCharsetName = null;
		protected int status = -1;
		protected int bufferSize = -1;
		protected Map<String, String> headers = new HashMap<String, String>();

		public byte[] getBuffer() {
			return buffer;
		}

		public int getCaptureContentLength() {
			return captureContentLength;
		}

		public String getCaptureContentType() {
			return captureContentType;
		}

		public String getCaptureCharacterEncoding() {
			return captureCharacterEncoding;
		}

		public String getPrintWriterCharsetName() {
			return printWriterCharsetName;
		}

		public int getStatus() {
			return status;
		}

		public int getBufferSize() {
			return bufferSize;
		}

		public Map<String, String> getHeaders() {
			return headers;
		}

	}

	/**
	 * @see HttpServletResponseWrapper#HttpServletResponseWrapper(HttpServletResponse)
	 */
	public WsdlServletResponseWrapper(HttpServletResponse response) {
		super(response);
		this.response = response;
		this.baos = new ByteArrayOutputStream();
		this.data = new CacheData();
		if (debug) {
			log.debug("...");
		}
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if (debug) {
			log.debug("...");
		}
		if (printWriter != null) {
			throw new IllegalStateException("(printWriter != null)");
		}
		if (servletOutputStream == null) {
			servletOutputStream = new ServletOutputStream() {

				@Override
				public void write(int b) throws IOException {
					baos.write(b);
				}

				@Override
				public void flush() throws IOException {
					super.flush();
				}

			};
		}
		return servletOutputStream;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		if (debug) {
			log.debug("...");
		}
		if (servletOutputStream != null) {
			throw new IllegalStateException("(servletOutputStream != null)");
		}
		if (printWriter == null) {
			data.printWriterCharsetName = getResponse().getCharacterEncoding();
			if (debug) {
				log.debug("printWriterCharsetName=" + data.printWriterCharsetName);
			}
			if (data.printWriterCharsetName == null) {
				data.printWriterCharsetName = "ISO-8859-1"; // TODO: "UTF8" ?
				super.setCharacterEncoding(data.printWriterCharsetName); // TODO
			}
			printWriter = new PrintWriter(new OutputStreamWriter(servletOutputStream, data.printWriterCharsetName));
		}
		return printWriter;
	}

	// ===
	public CacheData getCacheData() {
		if (data.captureContentLength != -1 && data.captureContentLength != baos.size()) {
			// throw new RuntimeException("captureContentLength=" + data.captureContentLength + ", baos.size=" + baos.size());
			data.status = HttpServletResponse.SC_NOT_ACCEPTABLE;
			return data;
		}
		// if (data.printWriterCharsetName != null && data.captureCharacterEncoding != null
		// && data.printWriterCharsetName.equals(data.captureCharacterEncoding)) {
		// throw new RuntimeException(
		// "(data.printWriterCharsetName != null && data.captureCharacterEncoding != null && data.printWriterCharsetName.equals(data.captureCharacterEncoding))");
		// }
		// if (data.captureContentType == null) {
		// data.captureContentType = super.getContentType();
		// }
		// if (data.captureCharacterEncoding == null) {
		// data.captureCharacterEncoding = super.getContentType();
		// }
		// if (data.captureContentLength == -1) {
		// data.captureContentLength = baos.size();
		// }
		data.captureContentType = super.getContentType();
		data.captureCharacterEncoding = super.getContentType();
		// data.status = super.getStatus();
		data.status = HttpServletResponse.SC_OK;
		data.bufferSize = super.getBufferSize();
		// for (String name : super.getHeaderNames()) {
		// data.headers.put(name, super.getHeaders(name));
		// }
		//
		data.captureContentLength = baos.size();
		data.buffer = baos.toByteArray();
		//
		// log.debug("data.status=" + data.status);
		// log.debug("data.headers=" + data.headers);
		return data;
	}

	// =========
	@Override
	public void setContentLength(int len) {
		if (debug) {
			log.debug("len=" + len);
		}
		data.captureContentLength = len;
		super.setContentLength(len);
	}

	@Override
	public void setContentType(String type) {
		if (debug) {
			log.debug("type=" + type);
		}
		// data.captureContentType = type;
		super.setContentType(type);
	}

	@Override
	public void setCharacterEncoding(String charset) {
		if (debug) {
			log.debug("charset=" + charset);
		}
		// data.captureCharacterEncoding = charset;
		// data.printWriterCharsetName = charset;
		super.setCharacterEncoding(charset);
	}

	// @Override
	// public void addCookie(Cookie arg0) {
	// if (debug) {
	// log.debug("...");
	// }
	// response.addCookie(arg0);
	// }
	//
	// @Override
	// public void addDateHeader(String arg0, long arg1) {
	// if (debug) {
	// log.debug("...");
	// }
	// response.addDateHeader(arg0, arg1);
	// }
	//
	// @Override
	// public void addHeader(String arg0, String arg1) {
	// if (debug) {
	// log.debug("...");
	// }
	// response.addHeader(arg0, arg1);
	// }
	//
	// @Override
	// public void addIntHeader(String arg0, int arg1) {
	// if (debug) {
	// log.debug("...");
	// }
	// response.addIntHeader(arg0, arg1);
	// }
	//
	// @Override
	// public boolean containsHeader(String arg0) {
	// if (debug) {
	// log.debug("...");
	// }
	// return response.containsHeader(arg0);
	// }
	//
	// @Override
	// public String encodeRedirectURL(String arg0) {
	// if (debug) {
	// log.debug("...");
	// }
	// return response.encodeRedirectURL(arg0);
	// }
	//
	// @Override
	// public String encodeRedirectUrl(String arg0) {
	// if (debug) {
	// log.debug("...");
	// }
	// return response.encodeRedirectUrl(arg0);
	// }
	//
	// @Override
	// public String encodeURL(String arg0) {
	// if (debug) {
	// log.debug("...");
	// }
	// return response.encodeURL(arg0);
	// }
	//
	// @Override
	// public String encodeUrl(String arg0) {
	// if (debug) {
	// log.debug("...");
	// }
	// return response.encodeUrl(arg0);
	// }
	//
	// @Override
	// public void flushBuffer() throws IOException {
	// if (debug) {
	// log.debug("...");
	// }
	// response.flushBuffer();
	// }
	//
	// @Override
	// public int getBufferSize() {
	// if (debug) {
	// log.debug("...");
	// }
	// return response.getBufferSize();
	// }
	//
	// @Override
	// public String getCharacterEncoding() {
	// if (debug) {
	// log.debug("...");
	// }
	// return response.getCharacterEncoding();
	// }
	//
	// @Override
	// public String getContentType() {
	// if (debug) {
	// log.debug("...");
	// }
	// return response.getContentType();
	// }
	//
	// @Override
	// public Locale getLocale() {
	// if (debug) {
	// log.debug("...");
	// }
	// return response.getLocale();
	// }
	//
	// @Override
	// public boolean isCommitted() {
	// if (debug) {
	// log.debug("...");
	// }
	// return response.isCommitted();
	// }
	//
	// @Override
	// public void reset() {
	// if (debug) {
	// log.debug("...");
	// }
	// response.reset();
	// }
	//
	// @Override
	// public void resetBuffer() {
	// if (debug) {
	// log.debug("...");
	// }
	// response.resetBuffer();
	// }
	//
	// @Override
	// public void sendError(int sc, String msg) throws IOException {
	// if (debug) {
	// log.debug("...");
	// }
	// response.sendError(sc, msg);
	// }

	// @Override
	// public void sendError(int sc) throws IOException {
	// if (debug) {
	// log.debug("...");
	// }
	// response.sendError(sc);
	// }
	//
	// @Override
	// public void sendRedirect(String location) throws IOException {
	// if (debug) {
	// log.debug("...");
	// }
	// response.sendRedirect(location);
	// }
	//
	// @Override
	// public void setBufferSize(int size) {
	// if (debug) {
	// log.debug("size=[" + size + "]");
	// }
	// response.setBufferSize(size);
	// }
	//
	// @Override
	// public void setDateHeader(String name, long date) {
	// if (debug) {
	// log.debug("...");
	// }
	// response.setDateHeader(name, date);
	// }

	@Override
	public void setHeader(String name, String value) {
		if (debug) {
			log.debug("name=[" + name + "], value=[" + value + "]");
		}
		data.getHeaders().put(name, value);
		response.setHeader(name, value);
	}

	// @Override
	// public void setIntHeader(String name, int value) {
	// if (debug) {
	// log.debug("...");
	// }
	// response.setIntHeader(name, value);
	// }
	//
	// @Override
	// public void setLocale(Locale loc) {
	// if (debug) {
	// log.debug("...");
	// }
	// response.setLocale(loc);
	// }
	//
	// @Override
	// public void setStatus(int sc, String sm) {
	// if (debug) {
	// log.debug("...");
	// }
	// response.setStatus(sc, sm);
	// }
	//
	// @Override
	// public void setStatus(int sc) {
	// if (debug) {
	// log.debug("...");
	// }
	// response.setStatus(sc);
	// }

	// =========
	// /**
	// * @since Servlet 3.0 TODO SERVLET3 - Add comments
	// */
	// @Override
	// public String getHeader(String name) {
	// log.debug("...");
	// return response.getHeader(name);
	// }
	//
	// /**
	// * @since Servlet 3.0 TODO SERVLET3 - Add comments
	// */
	// @Override
	// public Collection<String> getHeaderNames() {
	// log.debug("...");
	// return response.getHeaderNames();
	// }
	//
	// /**
	// * @since Servlet 3.0 TODO SERVLET3 - Add comments
	// */
	// @Override
	// public Collection<String> getHeaders(String name) {
	// log.debug("...");
	// return response.getHeaders(name);
	// }
	//
	// /**
	// * @since Servlet 3.0 TODO SERVLET3 - Add comments
	// */
	// @Override
	// public int getStatus() {
	// log.debug("...");
	// return response.getStatus();
	// }

}
