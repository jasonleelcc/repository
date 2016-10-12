package iwin.log;

import iwin.util.ReflectUtil;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * 
 * @author leotu@nec.com.tw
 */
public class MaskFilter extends Filter<ILoggingEvent> {
	protected String maskRegularExpression = null;
	protected String maskReplacement = null;
	protected boolean replaceFirst = true;
	protected boolean maskEnable = false;

	@Override
	public FilterReply decide(ILoggingEvent event) {
		if (!isStarted()) {
			return FilterReply.NEUTRAL;
		}
		if (maskEnable && maskRegularExpression != null && !maskRegularExpression.trim().isEmpty() && maskReplacement != null
				&& !maskReplacement.trim().isEmpty()) {
			String formattedMessage = (String) ReflectUtil.getFieldReturnObj(event, LoggingEvent.class, "formattedMessage");
			String newFormattedMessage;
			if (replaceFirst) {
				newFormattedMessage = formattedMessage.replaceFirst(maskRegularExpression, maskReplacement);
			} else {
				newFormattedMessage = formattedMessage.replaceAll(maskRegularExpression, maskReplacement);
			}
			ReflectUtil.setFieldNewObj(event, LoggingEvent.class, "formattedMessage", newFormattedMessage);
			formattedMessage = (String) ReflectUtil.getFieldReturnObj(event, LoggingEvent.class, "formattedMessage");
		}
		return FilterReply.NEUTRAL;
	}

	public String getMaskRegularExpression() {
		return maskRegularExpression;
	}

	public void setMaskRegularExpression(String maskRegularExpression) {
		this.maskRegularExpression = maskRegularExpression;
	}

	public String getMaskReplacement() {
		return maskReplacement;
	}

	public void setMaskReplacement(String maskReplacement) {
		this.maskReplacement = maskReplacement;
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
