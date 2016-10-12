package com.nec.test;

import iwin.util.Global;

/**
 * 
 * @author leotu@nec.com.tw
 */
public class TestBase {
	static protected String referencePath;
	static {
		System.setProperty("ReferencePath", "D:/Hexa");// -DReferencePath="D:\Hexa"

		if (System.getenv("COMPUTERNAME").equals("010842-NB")) { // XXX: Leo's NoteBook Name
			System.setProperty("ReferencePath", "C:/ZZ/eclipse-workspace/workspace-nec/Hexa");// -DReferencePath="..."
		}
		System.setProperty("ServerName", "ZIWIN"); // -DServerName="ZIWIN"
		System.setProperty("logback.configurationFile", "logback-testing.xml"); // -Dlogback.configurationFile=logback-testing.xml
		//
		referencePath = Global.REFERENCE_PATH;
		//
		// LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		// try {
		// JoranConfigurator configurator = new JoranConfigurator();
		// configurator.setContext(lc);
		// // the context was probably already configured by default configuration rules
		// URL url = SimulateBankLoginClient.class.getClassLoader().getResource("logback-testing.xml");
		// System.out.println("url=" + url);
		// lc.reset();
		// configurator.doConfigure(url);
		// } catch (JoranException je) {
		// je.printStackTrace();
		// }
	}
}
