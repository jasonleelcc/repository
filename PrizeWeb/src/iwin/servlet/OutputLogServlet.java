package iwin.servlet;

import iwin.util.Global;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilterOutputStream;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * http://w3help.org/zh-cn/causes/CH9002
 * 
 * @author leotu@nec.com.tw
 * @date 2011/7/9 上午10:02:02
 */
public class OutputLogServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected static final Logger log = LoggerFactory.getLogger(OutputLogServlet.class);

	static private byte[] ie678HeaderByte;
	static private char[] ie678HeaderChar;
	static {
		ie678HeaderByte = new byte[256];
		for (int i = 0; i < ie678HeaderByte.length; i++) {
			ie678HeaderByte[i] = ' ';
		}
		ie678HeaderByte[ie678HeaderByte.length - 1] = '\n';
		//
		ie678HeaderChar = new char[256];
		for (int i = 0; i < ie678HeaderChar.length; i++) {
			ie678HeaderChar[i] = ' ';
		}
		ie678HeaderChar[ie678HeaderChar.length - 1] = '\n';
	}

	static protected void forIE678(Writer out) throws IOException {
		out.write(ie678HeaderChar, 0, ie678HeaderChar.length);
	}

	static protected void forIE678(OutputStream out) throws IOException {
		out.write(ie678HeaderByte, 0, ie678HeaderByte.length);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String file = request.getParameter("file");
		// log.debug("file=[" + file + "]");
		File osFile = new File(new File(Global.REFERENCE_PATH, "logs/txn"), file);
		// log.debug("osFile=[" + osFile + "]");
		request.setCharacterEncoding("UTF-8");
		String encoding = request.getParameter("encoding");
		// response.setHeader("Content-Transfer-Encoding", "quoted-printable"); // "binary"
		if (!osFile.exists()) {
			String mes = "File: " + osFile + " not exists!";
			log.warn(mes);
			if (encoding != null && !encoding.isEmpty()) {
				response.setContentType("text/plain ;charset=" + encoding);
				response.setCharacterEncoding(encoding);
				Writer out = response.getWriter(); // PrintWriter
				if (isIE678(request)) {
					forIE678(out);
				}
				out.write(mes);
			} else {
				response.setContentType("text/plain");
				OutputStream out = response.getOutputStream();
				if (isIE678(request)) {
					forIE678(out);
				}
				out.write(mes.getBytes("ISO-8859-1"));
			}
			response.flushBuffer();
			return;
		}
		if (encoding != null && !encoding.isEmpty()) {
			Reader input = null;
			try {
				response.setContentType("text/plain; charset=" + encoding);
				response.setCharacterEncoding(encoding);
				Writer out = response.getWriter(); // PrintWriter
				if (isIE678(request)) {
					forIE678(out);
				}
				if (file.indexOf("../") == -1 && file.indexOf("..\\") == -1) {
					out = new AddSeperateLineFilter(out);
				}
				input = new InputStreamReader(new FileInputStream(osFile), encoding);
				IOUtils.copy(input, out);
			} finally {
				if (input != null) {
					IOUtils.closeQuietly(input);
				}
			}
		} else {
			InputStream input = null;
			try {
				response.setContentType("text/plain");
				input = new FileInputStream(osFile);
				OutputStream out = response.getOutputStream();
				if (isIE678(request)) {
					forIE678(out);
				}
				if (file.indexOf("../") == -1 && file.indexOf("..\\") == -1) {
					out = new AddSeperateLineFilterStream(out);
				}
				IOUtils.copy(input, out);
			} finally {
				if (input != null) {
					IOUtils.closeQuietly(input);
				}
			}
		}
		response.flushBuffer();
	}

	class AddSeperateLineFilter extends FilterWriter {
		private char seperator[];
		private char match[] = { ']', ']', '!', '>' };
		private int matchIdx = -1;
		private int keep[] = new int[match.length];
		private long counter = 0;

		public AddSeperateLineFilter(Writer out) {
			super(out);
			// log.debug("...");
			seperator = new char[110];
			seperator[0] = '\n';
			for (int i = 1; i < seperator.length; i++) {
				seperator[i] = '-';
			}
		}

		@Override
		public void write(int c) throws IOException {
			int idx = matchIdx + 1;
			if (idx == match.length - 1) {
				if (c == match[idx]) {
					for (int i = 0; i <= matchIdx; i++) {
						super.write(keep[i]);
					}
					matchIdx = -1;
					super.write(c);
					//
					super.write(seperator);
					counter++;
					super.write("{" + counter + "}");
				} else { // reset
					for (int i = 0; i <= matchIdx; i++) {
						super.write(keep[i]);
					}
					matchIdx = -1;
					super.write(c);
				}
			} else {
				if (c == match[idx]) {
					keep[idx] = c;
					matchIdx = idx;
				} else { // reset
					if (matchIdx >= 0) {
						for (int i = 0; i <= matchIdx; i++) {
							super.write(keep[i]);
						}
						matchIdx = -1;
					}
					super.write(c);
				}
			}
		}

		@Override
		public void write(char[] cbuf, int off, int len) throws IOException {
			for (int i = 0; i < len; i++) {
				write(cbuf[off + i]);
			}
			// super.write(cbuf, off, len);
		}

		@Override
		public void write(String str, int off, int len) throws IOException {
			for (int i = 0; i < len; i++) {
				write(str.charAt(off + i));
			}
			// super.write(str, off, len);
		}

	}

	// ===
	class AddSeperateLineFilterStream extends FilterOutputStream {
		private byte seperator[];
		private byte match[] = { ']', ']', '!', '>' };
		private int matchIdx = -1;
		private int keep[] = new int[match.length];
		private long counter = 0;

		public AddSeperateLineFilterStream(OutputStream out) {
			super(out);
			// log.debug("...");
			seperator = new byte[110];
			seperator[0] = '\n';
			for (int i = 1; i < seperator.length; i++) {
				seperator[i] = '-';
			}
		}

		@Override
		public void write(byte[] b) throws IOException {
			for (int i = 0; i < b.length; i++) {
				write(b[i]);
			}
			// super.write(b);
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			for (int i = 0; i < len; i++) {
				write(b[off + i]);
			}
			// super.write(b, off, len);
		}

		@Override
		public void write(int c) throws IOException {
			int idx = matchIdx + 1;
			if (idx == match.length - 1) {
				if (c == match[idx]) {
					for (int i = 0; i <= matchIdx; i++) {
						super.write(keep[i]);
					}
					matchIdx = -1;
					super.write(c);
					//
					super.write(seperator);
					counter++;
					String str = "{" + counter + "}";
					super.write(str.getBytes("ISO-8859-1"));
				} else { // reset
					for (int i = 0; i <= matchIdx; i++) {
						super.write(keep[i]);
					}
					matchIdx = -1;
					super.write(c);
				}
			} else {
				if (c == match[idx]) {
					keep[idx] = c;
					matchIdx = idx;
				} else { // reset
					if (matchIdx >= 0) {
						for (int i = 0; i <= matchIdx; i++) {
							super.write(keep[i]);
						}
						matchIdx = -1;
					}
					super.write(c);
				}
			}
		}
	}

	public boolean isIE678(HttpServletRequest request) {
		String useragent = request.getHeader("User-Agent");
		if (useragent != null && useragent.trim().length() > 0) {
			String browser = useragent.toLowerCase();
			return (browser.indexOf("msie 8") != -1) || (browser.indexOf("msie 7.0") != -1) || (browser.indexOf("msie 6.0") != -1);

		}
		return false;
	}
}
