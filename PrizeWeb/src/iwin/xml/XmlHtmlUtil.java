package iwin.xml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author leotu@nec.com.tw
 */
public class XmlHtmlUtil {
	protected static final Logger log = LoggerFactory.getLogger(XmlHtmlUtil.class);

	/**
	 * http://commons.apache.org/io/api-release/org/apache/commons/io/input/BOMInputStream.html
	 * <p/>
	 * BOMInputStream input = new BOMInputStream(new FileInputStream(...), false);
	 * <p/>
	 * PushbackInputStream();
	 */
	static public String removeXmlBomChar(String xml) {
		if (xml == null || xml.length() == 0 || xml.length() < 2) {
			return xml;
		}
		int bom = xml.charAt(0);
		int openTag = xml.charAt(1);
		// FEFF: Big-Endian, FFFE: Little-Endian
		if ((bom == 0xFEFF || bom == 0xFFFE) && openTag == '<') {
			log.debug("[" + Integer.toHexString(bom) + "],[" + openTag + "]");
			return xml.substring(1, xml.length());
		} else {
			return xml;
		}
	}

	static public String deleteChar(String oldStr, char ch) {
		if (oldStr == null || oldStr.length() == 0)
			return oldStr;

		int len = oldStr.length();
		StringBuffer sb = new StringBuffer(len);
		char c;
		for (int i = 0; i < len; i++) {
			c = oldStr.charAt(i);
			if (c != ch)
				sb.append(c);
		}
		return sb.toString();
	}

/**
     * Replaces all occurrence of special characters <code>'&'</code>,<code>'<'</code>,<code>'>'</code>,
     * <code>'"'</code>, and <code>'\''</code> in an xml data string with their specific substitute strings.
     * 
     * @param str the xml data string to be processed.
     * 
     * @return a new xml data string that is derived from <code>str</code> by replacing every occurrence of
     *         <code>'&'</code>,<code>'<'</code>,<code>'>'</code>,<code>'"'</code>, and
     *         <code>'\''</code> with their substitute strings; <code>str</code> if those special characters
     *         does not occur in <code>str</code>.
     */
	static public String specialChar(String str) {
		if (str == null || str.length() == 0)
			return str;
		// str = StringUtils.replace(str, "&", "&#038;"); // must first to be replaced
		// str = StringUtils.replace(str, "<", "&#060;");
		// str = StringUtils.replace(str, ">", "&#062;");
		// str = StringUtils.replace(str, "\"", "&#034;");
		// str = StringUtils.replace(str, "'", "&#039;");
		StringBuffer sb = new StringBuffer(str.length() + 128);
		int size = str.length();
		for (int i = 0; i < size; i++) {
			char ch = str.charAt(i);
			if (ch == '&') // "&amp;"
				sb.append("&#038;");
			else if (ch == '<') // "&lt;"
				sb.append("&#060;");
			else if (ch == '>')
				sb.append("&#062;"); // "&gt;"
			else if (ch == '"')
				sb.append("&#034;"); // "&quot;"
			else if (ch == '\'')
				sb.append("&#039;"); // "&apos;"
			else
				sb.append(ch);
		}
		return sb.toString();
	}

	static public String specialChar(char ch) {
		if (ch == '&') // "&amp;"
			return "&#038;";
		else if (ch == '<') // "&lt;"
			return "&#060;";
		else if (ch == '>')
			return "&#062;"; // "&gt;"
		else if (ch == '"')
			return "&#034;"; // "&quot;"
		else if (ch == '\'')
			return "&#039;"; // "&#39;"
		else
			return "" + ch;
	}

	static public byte[] specialChar(byte content[]) {
		if (content == null) {
			return new byte[0];
		}
		ByteArrayOutputStream output = new ByteArrayOutputStream(content.length + 128);

		byte[] it = { '&', 'l', 't', ';' };
		byte[] gt = { '&', 'g', 't', ';' };
		byte[] amp = { '&', 'a', 'm', 'p', ';' };
		byte[] quot = { '&', 'q', 'u', 'o', 't', ';' };
		byte[] squot = { '&', '#', '3', '9', ';' };

		for (int i = 0; i < content.length; i++) {
			switch (content[i]) {
			case '<':
				output.write(it, 0, it.length);
				break;
			case '>':
				output.write(gt, 0, gt.length);
				break;
			case '&':
				output.write(amp, 0, amp.length);
				break;
			case '"':
				output.write(quot, 0, quot.length);
				break;
			case '\'':
				output.write(squot, 0, squot.length);
				break;
			default:
				output.write(content[i]);
			}
		}
		return output.toByteArray();
	}

	static public void specialChar(Writer out, int ch) throws IOException {
		String str = specialChar((char) ch);
		out.write(str, 0, str.length());
	}

	static public void specialChar(Writer out, char ch) throws IOException {
		String str = specialChar(ch);
		out.write(str, 0, str.length());
	}

	static public boolean isSpecialChar(int ch) {
		if (ch == '&' || ch == '<' || ch == '>' || ch == '"' || ch == '\'')
			return true;
		else
			return false;
	}

	static public boolean isSpecialChar(char ch) {
		if (ch == '&' || ch == '<' || ch == '>' || ch == '"' || ch == '\'')
			return true;
		else
			return false;
	}

	/**
	 * convert special character to fit for XML format.
	 * 
	 * @param str
	 *            string to be converted
	 * @return new string
	 */
	static public String specialXmlChar(String str) {
		return specialChar(str);
	}

	static public String specialXmlCharP(String str) {
		str = StringUtils.replace(str, "\r", "<P>\r");
		return specialChar(str);
	}

	/**
	 * convert special character to fit for HTML or Javascript format.
	 * 
	 * @param str
	 *            string to be converted
	 * @return new string
	 */
	static public String specialHtmlCharValue(String str) {
		str = StringUtils.replace(str, "\"", "&#034;");
		str = StringUtils.replace(str, "'", "&#039;");
		return str;
	}

	/**
	 * convert special character to fit for HTML tag or Javascript format.
	 * 
	 * @param str
	 *            string to be converted
	 * @return new string
	 */
	static public String specialHtmlCharTag(String str) {
		str = StringUtils.replace(str, "<", "&#060;");
		str = StringUtils.replace(str, ">", "&#062;");
		return str;
	}

	/**
	 * convert special character to fit Javascript function format. <br>
	 * Example: onMouseOver='javascript:showLayer(this, " <%=HtmlXmlLib.specialHtmlJavascriptFuncCharValue(careAttMemo)%>");'
	 * 
	 * @param str
	 *            string to be converted
	 * @return new string
	 */
	static public String specialHtmlJavascriptFuncCharValueDQuote(String str) {
		str = StringUtils.replace(str, "\n", "\\n");
		str = deleteChar(str, '\r'); // delete
		str = StringUtils.replace(str, "'", "&#039;");
		str = StringUtils.replace(str, "\"", "\\\"");
		return str;
	}

	/**
	 * convert special character to fit Javascript function format. <br>
	 * Example: onMouseOver="javascript:showLayer(this, ' <%=HtmlXmlLib.specialHtmlJavascriptFuncCharValue(careAttMemo)%>');"
	 * 
	 * @param str
	 *            string to be converted
	 * @return new string
	 */
	static public String specialHtmlJavascriptFuncCharValueSQuote(String str) {
		str = StringUtils.replace(str, "\n", "\\n");
		str = deleteChar(str, '\r'); // delete
		str = StringUtils.replace(str, "\"", "&#034;");
		str = StringUtils.replace(str, "'", "\\\'");
		return str;
	}

	/**
	 * convert special character to fit Javascript function format. <br>
	 * Example: return ' <%=XmlHtmlLib.specialHtmlJavascriptFuncCharValueSQuote(content)%>';
	 * 
	 * @param str
	 *            string to be converted
	 * @return new string
	 */
	static public String specialJavascriptFuncCharValueSQuote(String str) {
		//System.out.println("OLD=[" + str + "]");
		str = StringUtils.replace(str, "\n", "\\n");
		str = deleteChar(str, '\r'); // delete
		str = StringUtils.replace(str, "'", "\\\'");
		//System.out.println("NEW=[" + str + "]");
		return str;
	}

	/**
	 * convert special character to fit for display HTML format.
	 * 
	 * @param str
	 *            string to be converted
	 * @return new string
	 */
	static public String specialHtmlCharDisplay(String str) {
		str = StringUtils.replace(str, "<", "&#060;");
		str = StringUtils.replace(str, ">", "&#062;");
		str = StringUtils.replace(str, "\n", "<br>");
		str = deleteChar(str, '\r'); // delete
		return str;
	}

	/**
	 * To fit javascript function call <br>
	 * Example: <a href='javascript:abc("xxx");'>...
	 * 
	 * @param value
	 *            the value
	 * @return correct value
	 */
	static public String javascriptFuncValueSQ(String value) {
		if (value == null || value.length() == 0)
			return "";
		return specialHtmlJavascriptFuncCharValueSQuote(value);
	}

	/**
	 * To fit javascript function call <br>
	 * Example: <a href="javascript:abc('xxx');">...
	 * 
	 * @param value
	 *            the value
	 * @return correct value
	 */
	static public String javascriptFuncValueDQ(String value) {
		if (value == null || value.length() == 0)
			return "";
		return specialHtmlJavascriptFuncCharValueDQuote(value);
	}

	static public boolean isHtmlSpace(int ch) {
		if (ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t') // || ch == '\f')
			return true;
		else
			return false;

	}

	static public boolean isHtmlSpace(char ch) {
		if (ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t') // || ch == '\f')
			return true;
		else
			return false;

	}

	static public int getLeftHtmlTrimLastIndex(char[] data) {
		for (int i = 0; i < data.length; i++) {
			if (!XmlHtmlUtil.isHtmlSpace(data[i])) {
				return i - 1;
			}
		}
		return -1;
	}
}