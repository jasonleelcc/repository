package iwin.xml;

import iwin.exception.HexaException;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Used to replace iwin.xmlUtil.XMLParser.java
 * 
 * @author leotu@nec.com.tw
 * @see iwin.xmlUtil.XMLParser
 */
public class XMLParser2 {
	protected static final Logger log = LoggerFactory.getLogger(XMLParser2.class);

	/**
	 * 將XML String轉換成Document
	 * 
	 * @return org.w3c.dom.Document
	 * @param XMLString
	 *            java.lang.String
	 * @see iwin.xmlUtil.XMLParser#stringToDoc(String XMLString)
	 */
	public Document stringToDoc(String XMLString)  {
		try {
			return XmlManipulation.getDocument(XMLString);
		} catch (Exception e) {
			log.error("", e);
			HexaException ex = new HexaException();
			ex.setFunction(this.getClass().getName() + ".stringToDoc(...)");
			ex.setException(e);
			throw ex;
		}
	}

	/**
	 * 同fileToDoc
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 * @see iwin.xmlUtil.XMLParser#getDocfromFName(String fileName)
	 * @deprecated
	 */
	public Document getDocfromFName(String fileName) {
		try {
			return XmlManipulation.getDocument(new File(fileName));
		} catch (Exception e) {
			log.error("", e);
			HexaException ex = new HexaException();
			ex.setFunction(this.getClass().getName() + ".getDocfromFName(" + fileName + ")");
			ex.setException(e);
			throw ex;
		}
	}

	/**
	 * 將指定XML檔案轉換成XML文件
	 * 
	 * @param FileName
	 *            欲轉成XML文件的XML檔案
	 * @return 轉換完成的XML文件
	 * @throws XMLException
	 * @see iwin.xmlUtil.XMLParser#fileToDoc(String FileName)
	 */
	public Document fileToDoc(String FileName) {
		try {
			return XmlManipulation.getDocument(new File(FileName));
		} catch (Exception e) {
			log.error("", e);
			HexaException ex = new HexaException();
			ex.setFunction(this.getClass().getName() + ".fileToDoc(" + FileName + ")");
			ex.setException(e);
			throw ex;
		}
	}

	/**
	 * 取出指定Tag的Value
	 * 
	 * @param XMLDoc
	 * @param TagName
	 * @return
	 * @throws HexaException
	 * @see iwin.xmlUtil.XMLParser#getTagData(Document XMLDoc, String TagName)
	 */
	public String getTagData(Document XMLDoc, String TagName)  {
		try {
			Node node = XmlManipulation.getFirstElementsByTagName(XMLDoc, TagName);
			if (node == null) {
				return null;
			}
			return XmlManipulation.getNodeTextValue(node);
		} catch (Exception e) {
			log.error("TagName=[" + TagName + "]", e);
			HexaException ex = new HexaException();
			ex.setFunction(this.getClass().getName() + ".getTagData(...," + TagName + ")");
			ex.setException(e);
			throw ex;
		}
	}
	
	public String getTagData(Element entryElement, String TagName)  {
		try {
			Node node = XmlManipulation.getFirstElementsByTagName(entryElement, TagName);
			if (node == null) {
				return null;
			}
			return XmlManipulation.getNodeTextValue(node);
		} catch (Exception e) {
			log.error("TagName=[" + TagName + "]", e);
			HexaException ex = new HexaException();
			ex.setFunction(this.getClass().getName() + ".getTagData(...," + TagName + ")");
			ex.setException(e);
			throw ex;
		}
	}

	/**
	 * 將XML文件轉成字串
	 * 
	 * @param souDoc
	 *            欲轉成字串的XML文件
	 * @return 轉換完成的字串
	 * @see iwin.xmlUtil.XMLParser#docToString(Document souDoc)
	 */
	public String docToString(Document souDoc) {
		return XmlManipulation.nodeToXmlString(souDoc);
	}

	/**
	 * 設定指定Tag的Value
	 * 
	 * @param XMLDoc
	 * @param TreeTagName
	 * @param TagData
	 * @return
	 * @see iwin.xmlUtil.XMLParser#setTagData(Document XMLDoc, String TreeTagName, String TagData)
	 */
	public Document setTagData(Document XMLDoc, String TreeTagName, String TagData) {
		Node node;
		if (TreeTagName.indexOf('/') != -1) {
			node = XmlManipulation.getNode(XMLDoc, TreeTagName);
		} else {
			node = XmlManipulation.getFirstElementsByTagName(XMLDoc, TreeTagName);
		}
		if (node == null) {
			log.warn("(node == null) for TreeTagName=[" + TreeTagName + "]");
			return XMLDoc;
		}
		if (TagData == null)
			TagData = "";
		XmlManipulation.setNodeTextValue(node, TagData);
		return XMLDoc;
	}

	/**
	 * 將Node轉成String
	 * 
	 * @param node
	 *            欲轉換的Node
	 * @return 轉換完成的字串
	 * @throws Exception
	 * @see iwin.xmlUtil.XMLParser#nodeToString(Node node)
	 */
	public String nodeToString(Node node) {
		return XmlManipulation.nodeToXmlString(node);
	}
}
