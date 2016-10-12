package iwin.xml;

import iwin.util.BeanToXmlUtil;
import iwin.util.XmlToBeanUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

/**
 * taidi (20120702 已上正式)
 * @author leotu@nec.com.tw
 */
public class XmlManipulation {
	protected static final Logger log = LoggerFactory.getLogger(XmlManipulation.class);

	protected static TransformerFactory transformerFactory;
	protected static DocumentBuilderFactory documentBuilderFactory;
	protected static XPathFactory xPathFactory;
	static {
		transformerFactory = TransformerFactory.newInstance();
		// log.debug("transformerFactory=" + transformerFactory);
		//
		documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setValidating(false);
		documentBuilderFactory.setNamespaceAware(false);
		// log.debug("documentBuilderFactory=" + documentBuilderFactory);
		//
		xPathFactory = XPathFactory.newInstance();
		// log.debug("xPathFactory=" + xPathFactory);
	}

	/**
	 * 
	 * @return transform result (i.e, XML)
	 */
	static public String transform(File xsltFile, File xmlFile, Map<String, Object> paramContext) throws Exception {
		if (xsltFile == null) {
			throw new IllegalArgumentException("(xsltFile == null)");
		}
		if (xmlFile == null) {
			throw new IllegalArgumentException("(xmlFile == null)");
		}
		Source xsltSource = new StreamSource(xsltFile);
		Transformer transormer = transformerFactory.newTransformer(xsltSource);
		if (paramContext != null && !paramContext.isEmpty()) {
			Iterator<String> it = paramContext.keySet().iterator();
			while (it.hasNext()) {
				String paramName = (String) it.next();
				Object paramValue = paramContext.get(paramName);
				transormer.setParameter(paramName, paramValue);
			}
		}
		StringWriter sw = new StringWriter(1024 * 8);
		transormer.transform(new StreamSource(xmlFile), new StreamResult(sw));
		String newXmlData = sw.toString();
		// log.debug("newXmlData=[" + newXmlData + "]");
		return newXmlData;
	}

	/**
	 * 
	 * @return transform result (i.e, XML)
	 */
	static public String transform(File xsltFile, String xmlData, Map<String, Object> paramContext) throws Exception {
		if (xsltFile == null) {
			throw new IllegalArgumentException("(xsltFile == null)");
		}
		if (xmlData == null || xmlData.isEmpty()) {
			throw new IllegalArgumentException("(xmlData == null || xmlData.isEmpty())");
		}
		Source xsltSource = new StreamSource(xsltFile);
		Transformer transormer = transformerFactory.newTransformer(xsltSource);
		if (paramContext != null && !paramContext.isEmpty()) {
			Iterator<String> it = paramContext.keySet().iterator();
			while (it.hasNext()) {
				String paramName = (String) it.next();
				Object paramValue = paramContext.get(paramName);
				transormer.setParameter(paramName, paramValue);
			}
		}
		StringWriter sw = new StringWriter(1024 * 8);
		transormer.transform(new StreamSource(new StringReader(xmlData)), new StreamResult(sw));
		String newXmlData = sw.toString();
		// log.debug("newXmlData=[" + newXmlData + "]");
		return newXmlData;
	}

	/**
	 * 
	 * @return Java Bean object
	 */
	static public <T> T xmlToBean(Class<T> classesToBeBound, File xmlFile) throws Exception {
		if (classesToBeBound == null) {
			throw new IllegalArgumentException("(classesToBeBound == null)");
		}
		if (xmlFile == null) {
			throw new IllegalArgumentException("(xmlFile == null)");
		}
		InputStream input = new FileInputStream(xmlFile);
		try {
			XmlToBeanUtil<T> xtb = new XmlToBeanUtil<T>(classesToBeBound);
			T bean = xtb.xmlToBean(input);
			return bean;
		} catch (Exception e) {
			log.error("classesToBeBound=[" + classesToBeBound + "]", e);
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(input);
		}
	}

	/**
	 * 
	 * @return XML String
	 */
	static public <T> String beanToXml(T bean) throws Exception {
		if (bean == null) {
			throw new IllegalArgumentException("(bean == null)");
		}
		return beanToXml(bean, true, true, null);
	}

	/**
	 * 
	 * @return XML String
	 */
	static public <T> String beanToXml(T bean, boolean noXmlDeclaration, boolean formattedOutput, String rootTagName) throws Exception {
		if (bean == null) {
			throw new IllegalArgumentException("(bean == null)");
		}
		BeanToXmlUtil<T> btx = new BeanToXmlUtil<T>(noXmlDeclaration, formattedOutput, rootTagName);
		return btx.beanToXml(bean);
	}

	/**
	 * 
	 * @return XML String
	 */
	static public <T> String beanToXml(T bean, boolean noXmlDeclaration, boolean formattedOutput, String rootTagName, boolean standalone)
			throws Exception {
		if (bean == null) {
			throw new IllegalArgumentException("(bean == null)");
		}
		BeanToXmlUtil<T> btx = new BeanToXmlUtil<T>(noXmlDeclaration, formattedOutput, rootTagName);
		btx.setStandalone(standalone);
		return btx.beanToXml(bean);
	}

	/**
	 * 
	 * @return Document Object come from XML file
	 */
	static public Document getDocument(File xmlFile) throws Exception {
		if (xmlFile == null) {
			throw new IllegalArgumentException("(xmlFile == null)");
		}
		DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
		Document document = builder.parse(xmlFile);
		document.normalizeDocument();
		return document;
	}

	/**
	 * 
	 * @return Document Object come from XML String
	 */
	static public Document getDocument(String xmlString) throws Exception {
		if (xmlString == null) {
			throw new IllegalArgumentException("(xmlString == null)");
		}
		DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
		Document document = builder.parse(new InputSource(new StringReader(xmlString)));
		document.normalizeDocument();
		return document;
	}

	/**
	 * 
	 * @return Document Object come from XML String
	 */
	static public Document getDocument(InputStream input) throws Exception {
		if (input == null) {
			throw new IllegalArgumentException("(input == null)");
		}
		DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
		Document document = builder.parse(new InputSource(input));
		document.normalizeDocument();
		return document;
	}

	/**
	 * 
	 * @return The renamed node. This is either the specified node or the new node that was created to replace the specified node.
	 */
	static public Node renameNodeTagName(Node node, String newTagName) {
		if (node == null) {
			throw new IllegalArgumentException("(node == null)");
		}
		if (newTagName == null || newTagName.isEmpty()) {
			throw new IllegalArgumentException("(newTagName == null || newTagName.isEmpty())");
		}
		try {
			Document rootDoc = node.getOwnerDocument();
			if (rootDoc == null) {
				throw new RuntimeException("can't find root Document create this node: " + node.getNodeName());
			}
			return rootDoc.renameNode(node, "", newTagName);
		} catch (DOMException e) {
			log.error("", e);
			throw new RuntimeException("newTagName=[" + newTagName + "]", e);
		}
	}

	// ===
	static public Node createNode(Document doc, String parentPathExpression, String tagName) {
		Node parentNode = getNode(doc, parentPathExpression);
		if (parentNode == null) {
			throw new RuntimeException("(parentNode == null), parentPathExpression=[" + parentPathExpression + "], tagName=[" + tagName + "]");
		}
		Node newNode = doc.createElement(tagName);
		newNode = parentNode.appendChild(newNode);
		return newNode;
	}

	/**
	 * case-sensitive
	 * 
	 * @return Length() == 0 if not found
	 */
	static public NodeList getElementsByTagName(Document doc, String tagName) {
		if (doc == null) {
			throw new IllegalArgumentException("(doc == null)");
		}
		if (tagName == null || tagName.isEmpty()) {
			throw new IllegalArgumentException("(tagName == null || tagName.isEmpty())");
		}
		if (tagName.indexOf('/') != -1) {
			log.warn("(tagName.indexOf('/') != -1), please use getNodeList(...) instead. tagName=[" + tagName + "]");
		}
		NodeList nl = doc.getElementsByTagName(tagName);
		return nl;
	}

	/**
	 * case-sensitive
	 * 
	 * @return null if not found
	 */
	static public Node getFirstElementsByTagName(Document doc, String tagName) {
		if (doc == null) {
			throw new IllegalArgumentException("(doc == null)");
		}
		if (tagName == null || tagName.isEmpty()) {
			throw new IllegalArgumentException("(tagName == null || tagName.isEmpty())");
		}
		if (tagName.indexOf('/') != -1) {
			log.warn("(tagName.indexOf('/') != -1), please use getNodeList(...) instead. tagName=[" + tagName + "]");
		}
		NodeList nl = doc.getElementsByTagName(tagName);
		return nl.getLength() == 0 ? null : nl.item(0);
	}

	/**
	 * case-sensitive
	 * 
	 * @return null if not found
	 */
	static public Node getFirstElementsByTagName(Element elem, String tagName) {
		if (elem == null) {
			throw new IllegalArgumentException("(elem == null)");
		}
		if (tagName == null || tagName.isEmpty()) {
			throw new IllegalArgumentException("(tagName == null || tagName.isEmpty())");
		}
		if (tagName.indexOf('/') != -1) {
			log.warn("(tagName.indexOf('/') != -1), please use getNodeList(...) instead. tagName=[" + tagName + "]");
		}
		NodeList nl = elem.getElementsByTagName(tagName);
		return nl.getLength() == 0 ? null : nl.item(0);
	}

	// ===
	/**
	 * 
	 * @return Length() == 0 if not found
	 */
	static public NodeList getNodeList(Node evaluationNode, String pathExpression) {
		if (evaluationNode == null) {
			throw new IllegalArgumentException("(evaluationNode == null)");
		}
		if (pathExpression == null || pathExpression.isEmpty()) {
			throw new IllegalArgumentException("(pathExpression == null || pathExpression.isEmpty())");
		}
		try {
			XPath xpath = xPathFactory.newXPath();
			Node node = evaluationNode instanceof Document ? ((Document) evaluationNode).getDocumentElement() : evaluationNode;
			NodeList pathNodeList = (NodeList) xpath.evaluate(pathExpression, node, XPathConstants.NODESET);
			return pathNodeList;
		} catch (XPathExpressionException e) {
			throw new RuntimeException(pathExpression, e);
		}
	}

	/**
	 * 
	 * @return Length() == 0 if not found
	 */
	static public NodeList getChildNodes(Node evaluationNode) {
		if (evaluationNode == null) {
			throw new IllegalArgumentException("(evaluationNode == null)");
		}
		return getNodeList(evaluationNode, "*");
	}

	/**
	 * 
	 * @return null if not found
	 */
	static public Node getNode(Node evaluationNode, String pathExpression) {
		if (evaluationNode == null) {
			throw new IllegalArgumentException("(evaluationNode == null)");
		}
		if (pathExpression == null || pathExpression.isEmpty()) {
			throw new IllegalArgumentException("(pathExpression == null || pathExpression.isEmpty())");
		}
		try {
			XPath xpath = xPathFactory.newXPath();
			Node node = evaluationNode instanceof Document ? ((Document) evaluationNode).getDocumentElement() : evaluationNode;
			Node pathNode = (Node) xpath.evaluate(pathExpression, node, XPathConstants.NODE);
			return pathNode;
		} catch (XPathExpressionException e) {
			throw new RuntimeException(pathExpression, e);
		}
	}

	/**
	 * 
	 * @return false if not found, else true
	 */
	static public boolean existNode(Node evaluationNode, String pathExpression) {
		return getNode(evaluationNode, pathExpression) != null;
	}

	// ===
	// /**
	// * Including children node text
	// *
	// * @return null if not found
	// */
	// static public String getNodeValue(Node node, String pathExpression) throws Exception {
	// XPath xpath = xPathFactory.newXPath();
	// String pathValue = xpath.evaluate(pathExpression, node);
	// return pathValue == null ? null : pathValue.trim();
	// }

	/**
	 * Excluding children node text
	 * 
	 * @return null if not found
	 */
	static public String getNodeTextValue(Node evaluationNode, String pathExpression) {
		if (evaluationNode == null) {
			throw new IllegalArgumentException("(evaluationNode == null)");
		}
		if (pathExpression == null || pathExpression.isEmpty()) {
			throw new IllegalArgumentException("(pathExpression == null || pathExpression.isEmpty())");
		}
		String pathExpressionForText = pathExpression;
		if (!pathExpression.toLowerCase().endsWith("/text()")) {
			if (pathExpressionForText.endsWith("/") || pathExpressionForText.isEmpty()) {
				pathExpressionForText += "text()";
			} else {
				pathExpressionForText += "/text()";
			}
		}
		// log.debug("pathExpressionForText=[" + pathExpressionForText + "]");
		Node n = getNode(evaluationNode, pathExpressionForText);
		if (n == null) {
			return null;
		}
		String pathValue = n.getTextContent();
		return pathValue == null ? "" : pathValue.trim();
	}

	static public String getNodeTextValueByAttributeValue(Node evaluationNode, String pathExpression, String attributeName, String attributeValue) {
		Node node = getNodeByAttributeValue(evaluationNode, pathExpression, attributeName, attributeValue);
		if (node == null) {
			return null;
		}
		return getNodeTextValue(node);
	}

	/**
	 * 
	 * @return null if not found
	 */
	static public Node getNodeTextNode(Node node) {
		if (node == null) {
			throw new IllegalArgumentException("(node == null)");
		}
		return getNode(node, "text()");
	}

	static public void setNodeTextValue(Node node, String value) {
		if (node == null) {
			throw new IllegalArgumentException("(node == null)");
		}
		if (value == null) {
			value = "";
		}
		if (node.getNodeType() == Node.TEXT_NODE) {
			node.setNodeValue(value);
			return;
		}
		Node textNode = getNodeTextNode(node);
		if (textNode == null) {
			textNode = node.getOwnerDocument().createTextNode(value);
			node.appendChild(textNode);
		} else {
			// textNode.setTextContent(value);
			textNode.setNodeValue(value);
		}
	}

	// ===
	/**
	 * 
	 * @return null if not found
	 */
	static public Node getAttributeNode(Node node, String attributeName) {
		if (node == null) {
			throw new IllegalArgumentException("(node == null)");
		}
		if (attributeName == null || attributeName.isEmpty()) {
			throw new IllegalArgumentException("(attributeName == null || attributeName.isEmpty())");
		}
		return node.getAttributes().getNamedItem(attributeName);
	}

	/**
	 * 
	 * @return null if not found
	 */
	static public String getAttributeTextValue(Node node, String attributeName) {
		if (node == null) {
			throw new IllegalArgumentException("(node == null)");
		}
		if (attributeName == null || attributeName.isEmpty()) {
			throw new IllegalArgumentException("(attributeName == null || attributeName.isEmpty())");
		}
		Node attrNode = getAttributeNode(node, attributeName);
		if (attrNode == null) {
			return null;
		}
		return attrNode.getTextContent();
	}

	// ===
	/**
	 * Excluding children node text
	 * 
	 * @return The <code>String</code> that is the result of evaluating the expression and converting the result to a <code>String</code>.
	 */
	static public String getNodeTextValue(Node node) {
		if (node == null) {
			throw new IllegalArgumentException("(node == null)");
		}
		try {
			XPath xpath = xPathFactory.newXPath();
			Node n = node instanceof Document ? ((Document) node).getDocumentElement() : node;
			String pathValue = xpath.evaluate("text()", n);
			return pathValue == null ? "" : pathValue.trim();
		} catch (XPathExpressionException e) {
			throw new RuntimeException("text()", e);
		}
		//
		// if (!node.hasChildNodes()) {
		// String value = node.getTextContent();
		// return value == null ? "" : value.trim();
		// } else {
		// NodeList children = node.getChildNodes(); // node.getFirstChild().getNodeValue();
		// StringBuffer sb = new StringBuffer();
		// for (int i = 0; i < children.getLength(); i++) {
		// Node n = children.item(i);
		// if (n.getNodeType() == Node.TEXT_NODE) {
		// sb.append(n.getNodeValue().trim());
		// }
		// }
		// return sb.toString();
		// }
	}

	/**
	 * All children' text
	 */
	static public String getNodeTextContent(Node node) {
		if (node == null) {
			throw new IllegalArgumentException("(node == null)");
		}
		String value = node.getTextContent();
		return value == null ? "" : value.trim();
	}

	// ===
	/**
	 * 
	 * @return null if not found
	 */
	static public Node getNodeByAttributeValue(Node evaluationNode, String pathExpression, String attributeName, String attributeValue) {
		if (evaluationNode == null) {
			throw new IllegalArgumentException("(evaluationNode == null)");
		}
		if (pathExpression == null || pathExpression.isEmpty()) {
			throw new IllegalArgumentException("(pathExpression == null || pathExpression.isEmpty())");
		}
		if (attributeName == null || attributeName.isEmpty()) {
			throw new IllegalArgumentException("(attributeName == null || attributeName.isEmpty())");
		}
		if (attributeValue == null || attributeValue.isEmpty()) {
			throw new IllegalArgumentException("(attributeValue == null || attributeValue.isEmpty())");
		}
		NodeList nl = getNodeList(evaluationNode, pathExpression);
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if (attributeValue.equals(getAttributeTextValue(n, attributeName))) {
				return n;
			}
		}
		return null;
	}

	/**
	 * 
	 * @return size == 0 if not found
	 */
	static public Node[] getNodesByAttributeValue(Node evaluationNode, String pathExpression, String attributeName, String attributeValue) {
		if (evaluationNode == null) {
			throw new IllegalArgumentException("(evaluationNode == null)");
		}
		if (pathExpression == null || pathExpression.isEmpty()) {
			throw new IllegalArgumentException("(pathExpression == null || pathExpression.isEmpty())");
		}
		if (attributeName == null || attributeName.isEmpty()) {
			throw new IllegalArgumentException("(attributeName == null || attributeName.isEmpty())");
		}
		if (attributeValue == null || attributeValue.isEmpty()) {
			throw new IllegalArgumentException("(attributeValue == null || attributeValue.isEmpty())");
		}
		NodeList nl = getNodeList(evaluationNode, pathExpression);
		List<Node> list = new ArrayList<Node>();
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if (attributeValue.equals(getAttributeTextValue(n, attributeName))) {
				list.add(n);
			}
		}
		return (Node[]) list.toArray(new Node[0]);
	}

	static public Node removeNode(Node node) {
		if (node == null) {
			throw new IllegalArgumentException("(node == null)");
		}
		return node.getParentNode().removeChild(node);
	}

	// ===
	static public void trimTextNode(Node node) {
		if (node == null) {
			throw new IllegalArgumentException("(node == null)");
		}
		Node n = node instanceof Document ? ((Document) node).getDocumentElement() : node;
		Stack<Node> stack = new Stack<Node>();
		stack.push(n);
		// int count = 0;
		while (!stack.isEmpty()) {
			n = stack.pop();
			NodeList children = n.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				// count++;
				// if (child instanceof Text) {
				if (child.getNodeType() == Node.TEXT_NODE) {
					String textContent = ((Text) child).getTextContent();
					// log.debug("T) name=" + child.getNodeName() + " of parent=" + n.getNodeName() + ", textContent=[" + textContent + "]");
					child.setTextContent(textContent.trim());
				} else if (child.getNodeType() != Node.COMMENT_NODE) {
					// log.debug("N) name=" + child.getNodeName() + " of parent=" + n.getNodeName());
					stack.push(child);
				}
			}
		}
		// log.debug("count=" + count);
	}

	// static public Node getNodeByRef(Node node, String path) throws Exception {
	// StringTokenizer st = new StringTokenizer(path, "/");
	// Node currentNode = node;
	// Node matchNode = null;
	// while (st.hasMoreTokens()) {
	// String tagName = st.nextToken().trim();
	// if (tagName.equals("#text") || tagName.equals("#comment")) {
	// continue;
	// }
	// log.debug("*tagName=[" + tagName + "]");
	// NodeList nl = currentNode.getChildNodes();
	// Node match = null;
	// for (int i = 0; i < nl.getLength(); i++) {
	// Node n = nl.item(i);
	// if (n.getNodeName().equals(tagName)) {
	// match = n;
	// break;
	// } else {
	// log.debug("*skip n.nodeName=[" + n.getNodeName() + "] of tagName=[" + tagName + "]");
	// }
	// }
	// if (match == null) {
	// log.debug("(match == null), tagName=[" + tagName + "], currentNode=" + currentNode + ", matchNode=" + matchNode);
	// return null;
	// }
	// currentNode = match;
	// matchNode = match;
	// log.debug("@@ tagName=[" + tagName + "], currentNode=" + currentNode + ", matchNode=" + matchNode);
	// }
	// return matchNode;
	// }

	// static public String getNodeValue(Document document, String path) throws Exception {
	// return getNodeValue(document.getDocumentElement(), path);
	// }

	// ===
	/**
	 * 
	 * @return create new Node from XML string
	 */
	static public Node xmlStringToNode(String xmlString) {
		if (xmlString == null || xmlString.isEmpty()) {
			// throw new IllegalArgumentException("(xmlString == null || xmlString.isEmpty())");
			log.warn("(xmlString == null || xmlString.isEmpty()), xmlString=[" + xmlString + "]");
			return null;
		}
		try {
			return getDocument(xmlString);
		} catch (Exception e) {
			log.error("xmlString=[" + xmlString + "], " + e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @return XML string come from Node
	 */
	static public String nodeToXmlString(Node node) {
		if (node == null) {
			throw new IllegalArgumentException("(node == null)");
		}
		return nodeToXmlString(node, false, false);
	}

	/**
	 * 
	 * @return XML string come from Node
	 */
	static public String nodeToXmlString(Node node, boolean hasXmlDeclaration, boolean trimTextNode) {
		if (node == null) {
			// throw new IllegalArgumentException("(node == null)");
			log.warn("(node == null)");
			return null;
		}
		StringWriter outText = new StringWriter(512);
		StreamResult sr = new StreamResult(outText);
		Properties oprops = new Properties();
		oprops.put(OutputKeys.METHOD, "xml");
		oprops.put(OutputKeys.INDENT, "yes");
		oprops.put(OutputKeys.ENCODING, "UTF-8");
		// oprops.put(OutputKeys.STANDALONE, "no");
		oprops.put(OutputKeys.OMIT_XML_DECLARATION, hasXmlDeclaration ? "no" : "yes"); // <?xml version="1.0" encoding="UTF-8"?>
		oprops.put("{http://xml.apache.org/xslt}indent-amount", "4");
		if (trimTextNode) {
			node = node.cloneNode(true);
			trimTextNode(node);
		}
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer t = null;
		try {
			t = tf.newTransformer();
			t.setOutputProperties(oprops);
			if (hasXmlDeclaration && (node instanceof Document)) {
				// XXX:
				// false --> <?xml version="1.0" encoding="UTF-8" standalone="no"?>
				// true --> <?xml version="1.0" encoding="UTF-8"?>
				boolean notShowStandalone = true;
				((Document) node).setXmlStandalone(notShowStandalone);
			}
			t.transform(new DOMSource(node), sr);
		} catch (Exception e) {
			log.error("trimTextNode=" + trimTextNode + ", hasXmlDeclaration=" + hasXmlDeclaration + ", " + e.toString());
			throw new RuntimeException(e);
		}
		return outText.toString();
	}

}
