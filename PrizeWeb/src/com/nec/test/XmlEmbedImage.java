package com.nec.test;

import iwin.util.ImageResize;
import iwin.util.ImageResize.OutputFormat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.security.MessageDigest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author leotu@nec.com.tw
 */
public class XmlEmbedImage {
	static {
		System.setProperty("ReferencePath", "D:/Hexa");// -DReferencePath="D:\Hexa"
		System.setProperty("ServerName", "ZIWIN"); // -DServerName="ZIWIN"
		System.setProperty("logback.configurationFile", "logback-testing.xml"); // -Dlogback.configurationFile=logback-testing.xml
	}
	final protected static Logger log = LoggerFactory.getLogger(XmlEmbedImage.class);

	private String inputImageFile = "d:/temp/a.jpg";
	private String outputXmlFile = "C:/temp/image.xml";
	private String outputImageFile = "d:/temp/a.jpg";

	static public void main(String[] args) {
		log.info("BEGIN...");
		try {
			XmlEmbedImage img = new XmlEmbedImage();
			img.encode();
			//
			img.decode();
			//
			File source = new File(img.inputImageFile);
			File dest = new File(img.outputImageFile);
			long sourceSize = source.length();
			long destSize = dest.length();
			log.info("sourceSize=" + sourceSize);
			log.info("  destSize=" + destSize);
			//
			if (sourceSize != destSize) {
				throw new Exception("(sourceSize != destSize)");
			}
			//
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			FileInputStream sourceIs = new FileInputStream(source);
			byte[] sourceData = IOUtils.toByteArray(sourceIs);
			byte[] sourceMessage = md5.digest(sourceData);
			String sourceHash = new BigInteger(1, sourceMessage).toString(16);
			sourceIs.close();
			log.info("  sourceHash=" + sourceHash);
			//
			FileInputStream destIs = new FileInputStream(dest);
			byte[] destData = IOUtils.toByteArray(destIs);
			byte[] destMessage = md5.digest(destData);
			String destHash = new BigInteger(1, destMessage).toString(16);
			destIs.close();
			log.info("    destHash=" + destHash);
			if (!sourceHash.equals(destHash)) {
				throw new Exception("(!sourceHash.equals(destHash))");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("END.");
	}

	public void encode() throws Exception {
		log.info("BEGIN...encode");
		InputStream input = null;
		Writer output = null;
		try {
			input = new BufferedInputStream(new FileInputStream(inputImageFile));
			//
			File file = new File(outputXmlFile);
			if (!file.getParentFile().exists()) {
				file.mkdirs();
			}
			output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputXmlFile), "UTF8"), 1024 * 16);
			//
			byte[] binaryData = IOUtils.toByteArray(input);
			byte[] encodeData = Base64.encodeBase64(binaryData);
			String content = new String(encodeData, "ISO-8859-1");
			//
			StringBuffer xml = new StringBuffer(1024 * 64);
			xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append("<DataXML>");
			xml.append("<Header></Header>");
			xml.append("<Text>").append("<FileName>").append("after.jpg").append("</FileName>");
			xml.append("<content>").append(content).append("</content>");
			xml.append("</Text></DataXML>");
			//
			IOUtils.write(xml.toString(), output);
			output.flush();
		} finally {
			IOUtils.closeQuietly(input);
			IOUtils.closeQuietly(output);
		}
		log.info("END.encode");
	}

	public void decode() throws Exception {
		log.info("BEGIN...decode");
		InputStream input = null;
		OutputStream output = null;
		try {
			input = new BufferedInputStream(new FileInputStream(outputXmlFile));
			//
			File file = new File(outputImageFile);
			if (!file.getParentFile().exists()) {
				file.mkdirs();
			}
			output = new BufferedOutputStream(new FileOutputStream(outputImageFile), 1024 * 16);
			//
			String xml = IOUtils.toString(input, "UTF8");
			int beginContent = xml.indexOf("<content>");
			int endContent = xml.indexOf("</content>");
			String content = xml.substring(beginContent + "<content>".length(), endContent).trim();

			byte[] binaryData = Base64.decodeBase64(content.getBytes("ISO-8859-1"));
			IOUtils.write(binaryData, output);
			//
			output.flush();
		} finally {
			IOUtils.closeQuietly(input);
			IOUtils.closeQuietly(output);
		}
		log.info("END.decode");
	}

	// =====================================================
	@Test
	public void testJpegConveter() throws Exception {
		log.info("BEGIN...");
		// File files[] = { new File("D:/temp/img/9789577484369.jpg"), new File("D:/temp/img/9789577484383.jpg") };
		//File files[] = { new File("d:/temp/a.jpg"), new File("d:/temp/b.jpg") }; // 1024*768
		File files[] = { new File("d:/temp/a_2.png"), new File("d:/temp/b_2.png") }; // 320*480
		OutputFormat outputFormat = OutputFormat.PNG;
		for (File file : files) {
			InputStream input = null;
			OutputStream output = null;
			try {
//				int width = 320;
//				int height = 480;
				int width =  480;
				int height = 800;
				input = new BufferedInputStream(new FileInputStream(file));
				String str = file.getName();
				int idx = str.lastIndexOf(".");
				// if (idx == -1) {
				// str = str.substring(0, idx) + "_resize" + str.substring(idx);
				// }
				// else {
				str = str.substring(0, idx) + "_resize." + outputFormat.toString().toLowerCase();
				// }
				File outFile = new File(file.getParentFile(), str);
				output = new BufferedOutputStream(new FileOutputStream(outFile));
				byte[] img = ImageResize.resize(outputFormat, file.toString(), IOUtils.toByteArray(input), width, height, true, true, true);
				IOUtils.write(img, output);
			} catch (Exception e) {
				log.error("file=" + file, e);
			} finally {
				IOUtils.closeQuietly(input);
				IOUtils.closeQuietly(output);
			}
		}
		log.info("END.");
	}
}
