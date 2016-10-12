package iwin.log;

import iwin.util.ReflectUtil;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * 
 * @author leotu@nec.com.tw
 */
public class MaskFilterUseIndexOf extends Filter<ILoggingEvent> {
	protected static Logger logger = Logger.getLogger(MaskFilterUseIndexOf.class.getName());

	protected String maskBeginStrs = null;
	protected String maskEndStrs = null;
	protected String maskReplacements = null;
	protected boolean replaceFirst = true;
	protected boolean maskEnable = false;
	protected String separatorChars = ","; // TODO
	static {
		// logger.setLevel(Level.FINEST);
		logger.log(Level.INFO, MaskFilterUseIndexOf.class.getName());
	}

	@Override
	public FilterReply decide(ILoggingEvent event) {
		if (!isStarted()) {
			return FilterReply.NEUTRAL;
		}
		if (maskEnable && maskBeginStrs != null && !maskBeginStrs.trim().isEmpty() && maskEndStrs != null && !maskEndStrs.trim().isEmpty()
				&& maskReplacements != null && !maskReplacements.trim().isEmpty()) {
			String maskBeginStrAry[] = StringUtils.split(maskBeginStrs, separatorChars);
			String maskEndStrAry[] = StringUtils.split(maskEndStrs, separatorChars);
			String maskReplacementAry[] = StringUtils.split(maskReplacements, separatorChars);
			if (maskBeginStrAry.length != maskEndStrAry.length) {
				throw new RuntimeException("(maskBeginStrAry.length != maskEndStrAry.length),  maskBeginStrAry.length=" + maskBeginStrAry.length
						+ ", maskEndStrAry.length=" + maskEndStrAry.length + ", separatorChars=[" + separatorChars + "]");
			}
			if (maskBeginStrAry.length != maskReplacementAry.length) {
				throw new RuntimeException("(maskBeginStrAry.length != maskReplacementAry.length),  maskBeginStrAry.length=" + maskBeginStrAry.length
						+ ", maskReplacementAry.length=" + maskReplacementAry.length + ", separatorChars=[" + separatorChars + "]");
			}
			String formattedMessage = (String) ReflectUtil.getFieldReturnObj(event, LoggingEvent.class, "formattedMessage");
			String newFormattedMessage = formattedMessage;
			for (int i = 0; i < maskBeginStrAry.length; i++) {
				String maskBeginStr = maskBeginStrAry[i].trim();
				String maskEndStr = maskEndStrAry[i].trim();
				String maskReplacement = maskReplacementAry[i].trim();
				// logger.log(Level.INFO, "[" + i + "], maskBeginStr=[" + maskBeginStr + "], maskEndStr=[" + maskEndStr + "]");
				newFormattedMessage = doReplacementAll(maskBeginStr, maskEndStr, maskReplacement, newFormattedMessage);
			}
			ReflectUtil.setFieldNewObj(event, LoggingEvent.class, "formattedMessage", newFormattedMessage);
			// formattedMessage = (String) ReflectUtil.getFieldReturnObj(event, LoggingEvent.class, "formattedMessage");
		} else {
			logger.log(Level.INFO, "Skip: maskBeginStrs=[" + maskBeginStrs + "], maskEndStrs=[" + maskEndStrs + "], maskReplacements=["
					+ maskReplacements + "], separatorChars=[" + separatorChars + "], replaceFirst=" + replaceFirst + ", maskEnable=" + maskEnable);
		}
		return FilterReply.NEUTRAL;
	}

	protected String doReplacementAll(String maskBeginStr, String maskEndStr, String maskReplacement, String formattedMessage) {
		if (maskBeginStr == null || maskBeginStr.isEmpty()) {
			throw new IllegalArgumentException("(maskBeginStr == null || maskBeginStr.isEmpty()) , maskBeginStr=" + maskBeginStr);
		}
		if (maskEndStr == null || maskEndStr.isEmpty()) {
			throw new IllegalArgumentException("(maskEndStr == null || maskEndStr.isEmpty()), maskEndStr=" + maskEndStr);
		}
		if (maskReplacement == null || maskReplacement.isEmpty()) {
			throw new IllegalArgumentException("(maskReplacement == null || maskReplacement.isEmpty()), maskReplacement=" + maskReplacement);
		}
		int fromIndex = 0;
		int begin = formattedMessage.indexOf(maskBeginStr, fromIndex); // inclusive
		if (begin == -1) {
			return formattedMessage;
		}
		fromIndex = begin + maskBeginStr.length();
		int end = formattedMessage.indexOf(maskEndStr, fromIndex);
		if (end == -1) {
			return formattedMessage;
		}
		end += maskEndStr.length() - 1; // inclusive
		List<int[]> idxList = new ArrayList<int[]>();
		idxList.add(new int[] { begin, end });
		for (; !replaceFirst;) {
			fromIndex = end + 1;
			begin = formattedMessage.indexOf(maskBeginStr, fromIndex); // inclusive
			if (begin == -1) {
				break;
			}
			fromIndex = begin + maskBeginStr.length();
			end = formattedMessage.indexOf(maskEndStr, fromIndex);
			if (end == -1) {
				break;
			}
			end += maskEndStr.length() - 1; // inclusive
			idxList.add(new int[] { begin, end });
		}
		//
		StringBuffer sb = new StringBuffer(Math.max(128, formattedMessage.length() / 10));
		fromIndex = 0;
		for (int i = 0; i < idxList.size(); i++) {
			int[] pos = idxList.get(i);
			int beginIndex = pos[0]; // inclusive
			int endIndex = pos[1]; // inclusive
			String msg = formattedMessage.substring(fromIndex, beginIndex);
			sb.append(msg);
			// <Image><![CDATA[........]]></Image]
			String removedMsg = formattedMessage.substring(beginIndex, endIndex + 1);
			boolean error = false;
			if (!removedMsg.startsWith(maskBeginStr)) {
				error = true;
				// System.err.println("(!removedMsg.startsWith(maskBeginStr), maskBeginStr=[" + maskBeginStr + "], removedMsg=[" + removedMsg + "]");
				// throw new RuntimeException("(!removedMsg.startsWith(maskBeginStr), maskBeginStr=[" + maskBeginStr + "]");
			}
			if (!removedMsg.endsWith(maskEndStr)) {
				error = true;
				// System.err.println("(!removedMsg.endsWith(maskEndStr)), maskEndStr=[" + maskEndStr + "], removedMsg=[" + removedMsg + "]");
				// throw new RuntimeException("(!removedMsg.endsWith(maskEndStr)), maskEndStr=[" + maskEndStr + "]");
			}
			boolean useCdata = false;
			if (!error) {
				if (removedMsg.startsWith(maskBeginStr + "<![CDATA[") && removedMsg.endsWith("]]>" + maskEndStr)) {
					useCdata = true;
				}
			}
			//
			String len;
			if (useCdata) {
				len = error ? "?" : String.valueOf(removedMsg.length() - maskBeginStr.length() - maskEndStr.length() - "<![CDATA[".length()
						- "]]>".length());
			} else {
				len = error ? "?" : String.valueOf(removedMsg.length() - maskBeginStr.length() - maskEndStr.length());
			}
			// System.out.println("BEFORE: maskReplacement=[" + maskReplacement + "], useCdata=" + useCdata + ", len=[" + len + "], maskBeginStr=["
			// + maskBeginStr + "], maskEndStr=[" + maskEndStr + "], removedMsg=[" + removedMsg + "], maskBeginStr");
			String replacement = MessageFormat.format(maskReplacement, len);
			// System.out.println("AFTER: replacement=[" + replacement + "], len=[" + len + "]");
			sb.append(replacement);
			fromIndex = pos[1] + 1;
		}
		String msg = formattedMessage.substring(fromIndex, formattedMessage.length());
		sb.append(msg);
		return sb.toString();
	}

	public String getMaskReplacements() {
		return maskReplacements;
	}

	public void setMaskReplacements(String maskReplacements) {
		this.maskReplacements = maskReplacements;
	}

	public String getMaskBeginStrs() {
		return maskBeginStrs;
	}

	public void setMaskBeginStrs(String maskBeginStrs) {
		this.maskBeginStrs = maskBeginStrs;
	}

	public String getMaskEndStrs() {
		return maskEndStrs;
	}

	public void setMaskEndStrs(String maskEndStrs) {
		this.maskEndStrs = maskEndStrs;
	}

	public boolean isReplaceFirst() {
		return replaceFirst;
	}

	public void setReplaceFirst(boolean replaceFirst) {
		this.replaceFirst = replaceFirst;
	}

	public boolean isMaskEnable() {
		return maskEnable;
	}

	public void setMaskEnable(boolean maskEnable) {
		this.maskEnable = maskEnable;
	}
}
