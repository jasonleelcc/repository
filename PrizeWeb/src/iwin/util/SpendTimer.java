package iwin.util;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DurationFormatUtils;

/**
 * 
 * @author leotu@nec.com.tw
 */
public class SpendTimer {

	private long startTimeMillis = 0L;
	private long endTimeMillis = 0L;

	public long getStartTimeMillis() {
		return startTimeMillis;
	}

	public long getEndTimeMillis() {
		return endTimeMillis;
	}

	/**
	 * start
	 */
	public void timerStart() {
		endTimeMillis = startTimeMillis = System.currentTimeMillis(); // System.nanoTime()
	}

	/**
	 * stop
	 */
	public void timerStop() {
		endTimeMillis = System.currentTimeMillis(); // System.nanoTime()
	}

	/**
	 * @return get Spend milliseconds
	 */
	public long getSpendMillis() {
		return (endTimeMillis - startTimeMillis);
	}

	/**
	 * @return get Spend seconds
	 */
	public double getSpendSeconds() {
		return (getSpendMillis() / 1000.0);
	}

	/**
	 * @return get Spend Minutes
	 */
	public double getSpendMinutes() {
		return (getSpendSeconds() / 60.0);
	}

	/**
	 * @return get Spend Hours
	 */
	public double getSpendHours() {
		return (getSpendMinutes() / 60.0);
	}

	/**
	 * @return get String just for log in Seconds
	 */
	public String logSpendSecondsStr() {
		return ("Spend (" + getSpendSeconds() + " sec.)");
	}

	public String logSpendSecondsStr(String extraMsg) {
		return (extraMsg + " spend (" + getSpendSeconds() + " sec.)");
	}

	/**
	 * @return get String just for log in Minutes
	 */
	public String logSpendMinutesStr() {
		return ("Spend (" + getSpendMinutes() + " min.)");
	}

	/**
	 * @return get String just for log in Hours
	 */
	public String logSpendHoursStr() {
		return ("Spend (" + getSpendHours() + " hrs.)");
	}

	/**
	 * to Time Format: "HH:mm:ss.999"
	 * 
	 * @return get String just for log
	 */
	public String toTimeFormatStr() {
		return toTimeFormatStr("HH:mm:ss.S");
		// int hrs = (int) getSpendHours();
		// int mins = (int) (getSpendMinutes() - (hrs * 60));
		// int secs = (int) (getSpendSeconds() - (hrs * 60 * 60 + mins * 60));
		// double millis = (getSpendMillis() - (hrs * 60 * 60 + mins * 60 + secs) * 1000) / 1000.0;
		//
		// NumberFormat nf = NumberFormat.getInstance();
		// nf.setMinimumIntegerDigits(2);
		//
		// NumberFormat nf2 = NumberFormat.getInstance();
		// nf2.setMinimumIntegerDigits(0);
		// // nf2.setMaximumFractionDigits(2);
		// // nf2.setMinimumFractionDigits(2);
		// String str = nf.format(hrs) + ":" + nf.format(mins) + ":" + nf.format(secs) + nf2.format(millis);
		// return str;
	}

	public String toTimeFormatStr(String format) {
		return DurationFormatUtils.formatDuration(getSpendMillis(), format);
	}
	
	public String toStartTimeFormatStr(String format) {
		return DateFormatUtils.format(getStartTimeMillis(), format);
	}
	
	public String toEndTimeFormatStr(String format) {
		return DateFormatUtils.format(getEndTimeMillis(), format);
	}
}