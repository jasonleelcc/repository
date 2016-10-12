/*
 * Copyright (c) 2010. All rights reserved.
 */
package iwin.util;

import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;

/**
 * Bean to Xml Utility. </p> Support bean that declare or undeclared [@javax.xml.bind.annotation.XmlRootElement] annotation. (JDK 6.0)
 * 
 * @author leo.tu.taipei@gmail.com
 */
public class BeanToXmlUtil<T> {
	protected static final Logger log = LoggerFactory.getLogger(BeanToXmlUtil.class);

	protected boolean formattedOutput = true;
	protected boolean noXmlDeclaration = false; // false: <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
	protected String rootTagName = null;
	protected String[] useCdataElements = null; // "UserId" or <UserId>, "ns1^foo" or "<ns1^foo>", ...
	protected boolean standalone = false;
	protected String noneStandaloneXmlDeclaration = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	public BeanToXmlUtil() {
	}

	public BeanToXmlUtil(boolean noXmlDeclaration) {
		this.noXmlDeclaration = noXmlDeclaration;
	}

	public BeanToXmlUtil(boolean noXmlDeclaration, boolean formattedOutput) {
		this.formattedOutput = formattedOutput;
		this.noXmlDeclaration = noXmlDeclaration;
	}

	public BeanToXmlUtil(boolean noXmlDeclaration, boolean formattedOutput, String rootTagName) {
		this.formattedOutput = formattedOutput;
		this.noXmlDeclaration = noXmlDeclaration;
		this.rootTagName = rootTagName;
	}

	/**
	 * Bean with declare "@javax.xml.bind.annotation.XmlRootElement" annotation.
	 */
	public String beanToXml(T bean) {
		StringWriter output = new StringWriter();
		beanToXml(bean, output);
		String xml = output.toString();
		try {
			output.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return xml;
	}

	/**
	 * Bean with declare "@javax.xml.bind.annotation.XmlRootElement" annotation.
	 */
	public void beanToXml(T bean, Writer output) {
		if (bean == null) {
			throw new IllegalArgumentException("(bean == null)");
		}
		if (output == null) {
			throw new IllegalArgumentException("(output == null), bean=" + bean);
		}
		if (!hasXmlRootElementAnnotation(bean)) {
			beanToXmlWithoutXmlRootElement(bean, output);
		} else {
			try {
				if (useCdataElements != null && useCdataElements.length > 0) {
					ContentHandler contentHandler = getXMLSerializer(output);
					if (contentHandler != null) {
						createMarshaller(bean).marshal(bean, contentHandler);
					} else {
						createMarshaller(bean).marshal(bean, output);
					}
				} else {
					if (!noXmlDeclaration && !standalone) {
						output.write(noneStandaloneXmlDeclaration);
						output.write('\n');
					}
					createMarshaller(bean).marshal(bean, output);
				}
			} catch (Exception e) {
				log.error("bean=" + bean, e);
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Bean with declare "@javax.xml.bind.annotation.XmlRootElement" annotation.
	 */
	public void beanToXml(T bean, OutputStream output) {
		if (bean == null) {
			throw new IllegalArgumentException("(bean == null)");
		}
		if (output == null) {
			throw new IllegalArgumentException("(output == null), bean=" + bean);
		}
		if (!hasXmlRootElementAnnotation(bean)) {
			beanToXmlWithoutXmlRootElement(bean, output);
		} else {
			try {
				if (useCdataElements != null && useCdataElements.length > 0) {
					ContentHandler contentHandler = getXMLSerializer(output);
					if (contentHandler != null) {
						createMarshaller(bean).marshal(bean, contentHandler);
					} else {
						createMarshaller(bean).marshal(bean, output);
					}
				} else {
					if (!noXmlDeclaration && !standalone) {
						output.write(noneStandaloneXmlDeclaration.getBytes("ISO-8859-1"));
						output.write('\n');
					}
					createMarshaller(bean).marshal(bean, output);
				}
			} catch (Exception e) {
				log.error("bean=" + bean, e);
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Bean without declare "@javax.xml.bind.annotation.XmlRootElement" annotation.
	 */
	public String beanToXmlWithoutXmlRootElement(T bean) {
		if (bean == null) {
			throw new IllegalArgumentException("(bean == null)");
		}
		if (hasXmlRootElementAnnotation(bean)) {
			return beanToXml(bean);
		} else {
			StringWriter output = new StringWriter();
			beanToXmlWithoutXmlRootElement(bean, output);
			String xml = output.toString();
			try {
				output.close();
				return xml;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Bean without declare "@javax.xml.bind.annotation.XmlRootElement" annotation.
	 */
	public void beanToXmlWithoutXmlRootElement(T bean, Writer output) {
		if (bean == null) {
			throw new IllegalArgumentException("(bean == null)");
		}
		if (output == null) {
			throw new IllegalArgumentException("(output == null)");
		}
		if (hasXmlRootElementAnnotation(bean)) {
			beanToXml(bean, output);
		} else {
			beanToXml(bean, output, createJAXBElement(bean));
		}
	}

	/**
	 * Bean without declare "@javax.xml.bind.annotation.XmlRootElement" annotation.
	 */
	public void beanToXmlWithoutXmlRootElement(T bean, OutputStream output) {
		if (bean == null) {
			throw new IllegalArgumentException("(bean == null)");
		}
		if (output == null) {
			throw new IllegalArgumentException("(output == null)");
		}
		if (hasXmlRootElementAnnotation(bean)) {
			beanToXml(bean, output);
		} else {
			beanToXml(bean, output, createJAXBElement(bean));
		}
	}

	/**
	 * Bean without declare "@javax.xml.bind.annotation.XmlRootElement" annotation.
	 */
	protected void beanToXml(T bean, Writer output, JAXBElement<T> je) {
		if (je == null) {
			throw new IllegalArgumentException("(je == null)");
		}
		if (bean == null) {
			throw new IllegalArgumentException("(bean == null)");
		}
		if (output == null) {
			throw new IllegalArgumentException("(output == null)");
		}
		try {
			if (useCdataElements != null && useCdataElements.length > 0) {
				ContentHandler contentHandler = getXMLSerializer(output);
				if (contentHandler != null) {
					createMarshaller(bean).marshal(je, contentHandler);
				} else {
					createMarshaller(bean).marshal(je, output);
				}
			} else {
				if (!noXmlDeclaration && !standalone) {
					output.write(noneStandaloneXmlDeclaration);
					output.write('\n');
				}
				createMarshaller(bean).marshal(je, output);
			}
		} catch (Exception e) {
			log.error("bean=" + bean + ", je=" + je, e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Bean without declare "@javax.xml.bind.annotation.XmlRootElement" annotation.
	 */
	protected void beanToXml(T bean, OutputStream output, JAXBElement<T> je) {
		if (je == null) {
			throw new IllegalArgumentException("(je == null)");
		}
		if (bean == null) {
			throw new IllegalArgumentException("(bean == null)");
		}
		if (output == null) {
			throw new IllegalArgumentException("(output == null)");
		}
		try {
			if (useCdataElements != null && useCdataElements.length > 0) {
				ContentHandler contentHandler = getXMLSerializer(output);
				if (contentHandler != null) {
					createMarshaller(bean).marshal(je, contentHandler);
				} else {
					createMarshaller(bean).marshal(je, output);
				}
			} else {
				if (!noXmlDeclaration && !standalone) {
					output.write(noneStandaloneXmlDeclaration.getBytes("ISO-8859-1"));
					output.write('\n');
				}
				createMarshaller(bean).marshal(je, output);
			}
		} catch (Exception e) {
			log.error("bean=" + bean + ", je=" + je, e);
			throw new RuntimeException(e);
		}
	}

	// ===========================================
	/**
	 * Checking annotation declaration or not. <br>
	 * To check Bean with or without declare "@javax.xml.bind.annotation.XmlRootElement" annotation.
	 */
	protected boolean hasXmlRootElementAnnotation(T bean) {
		return bean.getClass().isAnnotationPresent(XmlRootElement.class);
	}

	// SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	protected Marshaller createMarshaller(T bean) throws Exception {
		@SuppressWarnings("unchecked")
		Class<T> classesToBeBound = (Class<T>) bean.getClass();
		JAXBContext context = JAXBContext.newInstance(classesToBeBound);
		Marshaller marshal = context.createMarshaller();
		marshal.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, formattedOutput);
		if (!noXmlDeclaration && !standalone) {
			marshal.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		} else {
			marshal.setProperty(Marshaller.JAXB_FRAGMENT, noXmlDeclaration);
		}
		// marshal.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.thsrc.com.tw/TWHSR");
		// marshal.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, "TWHSR.xsd");
		//
		return marshal;
	}

	protected ContentHandler getXMLSerializer(OutputStream output) throws Exception {
		BeanToXmlCdataUtil cdataUtil = new BeanToXmlCdataUtil();
		cdataUtil.setUseCdataElements(useCdataElements);
		cdataUtil.setNoXmlDeclaration(noXmlDeclaration);
		if (!noXmlDeclaration) {
			cdataUtil.setStandalone(standalone);
		}
		if (formattedOutput) {
			cdataUtil.setIndenting(true);
		} else {
			cdataUtil.setIndenting(false);
		}
		return cdataUtil.createXMLSerializer(output);
	}

	// XMLStreamWriter xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(output);
	// log.info("xmlStreamWriter=" + xmlStreamWriter);
	protected ContentHandler getXMLSerializer(Writer output) throws Exception {
		BeanToXmlCdataUtil cdataUtil = new BeanToXmlCdataUtil();
		cdataUtil.setUseCdataElements(useCdataElements);
		cdataUtil.setNoXmlDeclaration(noXmlDeclaration);
		if (!noXmlDeclaration) {
			cdataUtil.setStandalone(standalone);
		}
		if (formattedOutput) {
			cdataUtil.setIndenting(true);
		} else {
			cdataUtil.setIndenting(false);
		}
		return cdataUtil.createXMLSerializer(output);
	}

	protected JAXBElement<T> createJAXBElement(T bean) {
		@SuppressWarnings("unchecked")
		Class<T> classesToBeBound = (Class<T>) bean.getClass();
		if (rootTagName == null || rootTagName.length() == 0) {
			rootTagName = Character.toLowerCase(bean.getClass().getSimpleName().charAt(0)) + bean.getClass().getSimpleName().substring(1);
		}
		// log.debug("rootTagName=[" + rootTagName + "]");
		JAXBElement<T> je = new JAXBElement<T>(new QName("", rootTagName), classesToBeBound, bean);
		return je;
	}

	public boolean isFormattedOutput() {
		return formattedOutput;
	}

	public void setFormattedOutput(boolean formattedOutput) {
		this.formattedOutput = formattedOutput;
	}

	public boolean isNoXmlDeclaration() {
		return noXmlDeclaration;
	}

	public void setNoXmlDeclaration(boolean noXmlDeclaration) {
		this.noXmlDeclaration = noXmlDeclaration;
	}

	public String getRootTagName() {
		return rootTagName;
	}

	public void setRootTagName(String rootTagName) {
		this.rootTagName = rootTagName;
	}

	public String[] getUseCdataElements() {
		return useCdataElements;
	}

	public void setUseCdataElements(String[] useCdataElements) {
		this.useCdataElements = useCdataElements;
	}

	public boolean isStandalone() {
		return standalone;
	}

	public void setStandalone(boolean standalone) {
		this.standalone = standalone;
	}
}