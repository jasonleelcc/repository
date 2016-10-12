package iwin.servlet;

import iwin.admin.Monitoring;
import iwin.front.net.BizProcessor;
import iwin.front.net.server.IStringProcessor;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data Input/Output Servlet
 * 
 * @author leotu@nec.com.tw
 */
public class DataIOServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	final protected static Logger log = LoggerFactory.getLogger(DataIOServlet.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DataIOServlet() {
		super();
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
		request.setCharacterEncoding("UTF-8");
		InputStream input = request.getInputStream();
		String inputData = IOUtils.toString(input, "UTF-8");
		//log.debug("inputData=[" + inputData + "]");
		try {
			IStringProcessor processor = new BizProcessor(); // TODO
			processor = Monitoring.add(processor, "::Http " + processor.getClass().getSimpleName()); // TODO
			String outputData = processor.handleRequest(inputData);
			//
			toXML(response, outputData);
			response.flushBuffer();
		} catch (Exception e) {
			response.setCharacterEncoding("UTF-8");
			// response.setContentType("text/plain");
			// log.warn("input=[" + data + "]", e);
			throw new IOException("input=[" + inputData + "]", e);
		} finally {
			input.close();
		}
	}

	protected void toXML(HttpServletResponse response, String result) throws Exception {
		// response.setContentType("text/xml");
		response.setContentType("application/xml");
		response.setCharacterEncoding("UTF-8");
		//
		response.getWriter().write(result);
	}

	// protected String formatXML(String xml) {
	// return XmlManipulation.nodeToXmlString(XmlManipulation.xmlStringToNode(xml), false, true);
	// }
}
