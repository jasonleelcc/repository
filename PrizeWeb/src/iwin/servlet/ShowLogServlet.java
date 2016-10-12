package iwin.servlet;

import iwin.util.Global;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author leotu@nec.com.tw
 */
public class ShowLogServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected static final Logger log = LoggerFactory.getLogger(ShowLogServlet.class);

	protected String servletURL = "/readlog.txt"; // <url-pattern>/readlog.txt</url-pattern>

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
		PrintWriter out = response.getWriter();
		File file = new File(Global.REFERENCE_PATH, "logs");
		NumberFormat nf = NumberFormat.getInstance();
		nf.setGroupingUsed(true);
		nf.setMaximumFractionDigits(1);
		//
		out.println("<table border='1' cellspacing='0' cellpadding='1' bgcolor='#FFFFFF' bordercolor='green' width='100%'>");
		File[] files = file.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && pathname.getName().endsWith(".log");
			}

		});
		if (files != null) {
			Map<String, List<File>> group = groupFiles(files);
			String keys[] = group.keySet().toArray(new String[0]);
			for (String key : keys) {
				List<File> grpFiles = group.get(key);
				String rowspanPrefix = "<td rowspan='" + grpFiles.size() + "'>" + key + "</td>";
				//
				sortFiles(grpFiles);
				for (int i = 0; i < grpFiles.size(); i++) {
					File ligFile = grpFiles.get(i);
					String filename = "../" + ligFile.getName();
					if (i == 0) {
						printFile(out, ligFile, filename, nf, rowspanPrefix);
					} else {
						printFile(out, ligFile, filename, nf, "");
					}
				}
			}
		}
		out.println("</table>");
		out.println("<br />");
		//
		file = new File(Global.REFERENCE_PATH, "logs/txn");
		if (file.isDirectory()) {
			out.println("<table border='1' cellspacing='0' cellpadding='1' bgcolor='#FFFFFF' bordercolor='#fccda7' width='100%'>");
			files = file.listFiles(new FileFilter() {

				@Override
				public boolean accept(File pathname) {
					return pathname.isFile() && pathname.getName().endsWith(".log");
				}

			});
			if (files != null) {
				Map<String, List<File>> group = groupFiles(files);
				String keys[] = group.keySet().toArray(new String[0]);
				for (String key : keys) {
					List<File> grpFiles = group.get(key);
					String rowspanPrefix = "<td rowspan='" + grpFiles.size() + "'>" + key + "</td>";
					//
					sortFiles(grpFiles);
					for (int i = 0; i < grpFiles.size(); i++) {
						File ligFile = grpFiles.get(i);
						if (i == 0) {
							printFile(out, ligFile, ligFile.getName(), nf, rowspanPrefix);
						} else {
							printFile(out, ligFile, ligFile.getName(), nf, "");
						}
					}
				}
			}
			out.println("</table>");
		}
	}

	// target='_blank'
	protected void printFile(PrintWriter out, File ligFile, String filename, NumberFormat nf, String rowspanPrefix) {
		if (ligFile.length() > 0) {
			double size = ligFile.length() / 1024.0;
			String url;
			try {
				url = this.getServletContext().getContextPath() + servletURL + "?file=" + URLEncoder.encode(filename, "UTF8") + "&encoding=UTF-8";
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
			out.println("<tr>");
			out.println(rowspanPrefix);
			out.println("<td><a href='" + url + "' target='_blank'>" + filename + "</a></td><td align='right'>" + nf.format(size) + " k</td><td align='right'>"
					+ new Date(ligFile.lastModified()) + "</td>");
			out.println("</tr>");
		} else {
			out.println("<tr>");
			out.println(rowspanPrefix);
			out.println("<td>" + filename + "</a></td><td align='right'>0 byte</td><td align='right'>" + new Date(ligFile.lastModified()) + "</td>");
			out.println("</tr>");
		}
	}

	protected Map<String, List<File>> groupFiles(File[] files) {
		String todayKey = "Now";
		Map<String, List<File>> group = new TreeMap<String, List<File>>(new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return o2.compareTo(o1);
			}

		});
		group.put(todayKey, new ArrayList<File>());
		String regex = "\\.\\d\\d\\d\\d\\-\\d\\d\\-\\d\\d\\.log$"; // .yyyy-MM-dd.log
		final Pattern pattern = Pattern.compile(regex);
		for (int i = 0; i < files.length; i++) {
			File ligFile = files[i];
			Matcher match = pattern.matcher(ligFile.getName());
			if (match.find()) {
				String key = ligFile.getName().substring(match.start(), match.end());
				key = key.substring(".".length(), key.length() - ".log".length());
				// System.out.println("key=[" + key + "] of ligFile.getName()=[" + ligFile.getName() + "]");
				List<File> grpFiles = group.get(key);
				if (grpFiles == null) {
					grpFiles = new ArrayList<File>();
					group.put(key, grpFiles);
				}
				grpFiles.add(ligFile);
			} else {
				group.get(todayKey).add(ligFile);
			}
		}
		return group;
	}

	// protected void sortFiles(File[] files) {
	protected void sortFiles(List<File> files) {
		String regex = "\\.\\d\\d\\d\\d\\-\\d\\d\\-\\d\\d\\.log$"; // .yyyy-MM-dd.log
		final Pattern pattern = Pattern.compile(regex);
		// Arrays.sort(files, new Comparator<File>() {
		Collections.sort(files, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				String filename1 = o1.getName();
				String filename2 = o2.getName();
				boolean match1 = pattern.matcher(filename1).find();
				boolean match2 = pattern.matcher(filename2).find();
				// System.out.println("match1=" + match1 + ", filename1=[" + filename1 + "]");
				// System.out.println("match2=" + match2 + ", filename2=[" + filename2 + "]");
				if (match1 && !match2) {
					return 1;
				} else if (!match1 && match2) {
					return -1;
				} else {
					return filename1.compareTo(filename2);
				}
			}

		});
	}
}
