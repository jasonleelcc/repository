package iwin.servlet;

//import iwin.log.DiagnoseLogFactory;
import iwin.util.Global;
import iwin.util.ResponseUtil;
import iwin.util.UuidUtil;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.validator.GenericValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * 重置 ecard_login_check (信用卡會員第一次啟用記錄)
 * @author leotu@nec.com.tw
 */
public class ResetEcardLoginCheck extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected static final Logger log = LoggerFactory.getLogger(ResetEcardLoginCheck.class);
	//protected static final Logger resetLog = DiagnoseLogFactory.getResetLogger();

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
		MDC.put(Global.MDC_TX_UUID_KEY, UuidUtil.generateUuid()); // TODO
		// int status;
		request.setCharacterEncoding("UTF-8");
		String personalId = request.getParameter("personalId");
		String modifier = request.getParameter("modifier");
		//resetLog.trace("Ecard Input(" + request.getRemoteAddr() + "): parameter personalId=[" + personalId + "], modifier=[" + modifier + "]"); // TOOD
		try {
			if (GenericValidator.isBlankOrNull(personalId)) {
				responseMsg(request, response, ResponseUtil.getErrorCodeI18nMsg("Z213"));
				return;
			}
			if (GenericValidator.isBlankOrNull(modifier)) {
				responseMsg(request, response, ResponseUtil.getErrorCodeI18nMsg("Z214"));
				return;
			}
			// EcardLoginCheckDao dao = new EcardLoginCheckDao();
			// int count = dao.resetEcardLoginCheckFlag(personalId, modifier);
			// log.debug("count=[" + count + "], personalId=[" + personalId + "], modifier=[" + modifier + "]");
			// // status = HttpServletResponse.SC_OK;
			// if (count > 0) {
			// String textContent = "<Desc>" + ResponseUtil.getMessage("Z210") + "</Desc><Extend/>";
			// responseMsg(request, response, ResponseUtil.buildXml("ResetEcardLoginRs", textContent));
			// } else {
			// responseMsg(request, response, ResponseUtil.getErrorCodeI18nMsg("Z211", personalId));
			// }
		} catch (Throwable e) {
			log.error("", e);
			// status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
			responseMsg(request, response, ResponseUtil.getErrorCodeI18nMsg("Z212", e.toString())); // $NON-NLS-1$
		} finally {
			MDC.remove(Global.MDC_TX_UUID_KEY); // TODO
		}
		// response.setStatus(status);
	}

	protected void responseMsg(HttpServletRequest request, HttpServletResponse response, String msg) throws ServletException, IOException {
		response.setContentType("application/xml");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.write(msg);
		out.flush();
		//resetLog.trace("Ecard Output(" + request.getRemoteAddr() + "): " + msg); // TOOD
	}

}
