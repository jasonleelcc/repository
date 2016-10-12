/*
 * Copyright (c) 210. All rights reserved.
 */
package iwin.util;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.helpers.DefaultValidationEventHandler;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Xml to Bean Utility. </p> Support bean that declare or undeclared [@javax.xml.bind.annotation.XmlRootElement] annotation. (JDK 6.0)
 * 
 * @author leotu@nec.com.tw
 * @version $Revision: 1.5 $
 */
public class XmlToBeanUtil<T> {
	protected static final Log log = LogFactory.getLog(XmlToBeanUtil.class);

	protected boolean debugXML = true;
	protected Class<T> classesToBeBound = null;

	public XmlToBeanUtil(Class<T> classesToBeBound) {
		if (classesToBeBound == null) {
			throw new IllegalArgumentException("(classesToBeBound == null)");
		}
		this.classesToBeBound = classesToBeBound;
	}

	// ===========================================
	/**
	 * Bean with declare "@javax.xml.bind.annotation.XmlRootElement" annotation.
	 */
	public T xmlToBean(String xml) {
		StringReader in = new StringReader(xml);
		T bean = xmlToBean(in);
		try {
			in.close();
			return bean;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Bean with declare "@javax.xml.bind.annotation.XmlRootElement" annotation.
	 */
	public T xmlToBean(Reader input) {
		if (classesToBeBound == null) {
			throw new IllegalArgumentException("(classesToBeBound == null)");
		}
		if (input == null) {
			throw new IllegalArgumentException("(input == null)");
		}
		if (!hasXmlRootElementAnnotation()) {
			return xmlToBeanWithoutXmlRootElement(input);
		} else {
			try {
				Unmarshaller unmarshall = createUnmarshaller();
				@SuppressWarnings("unchecked")
				T bean = (T) unmarshall.unmarshal(input); // FIXME!
				return bean;
			} catch (Exception e) {
				log.error("classesToBeBound=[" + classesToBeBound + "]", e);
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Bean with declare "@javax.xml.bind.annotation.XmlRootElement" annotation.
	 */
	public T xmlToBean(InputStream input) {
		if (classesToBeBound == null) {
			throw new IllegalArgumentException("(classesToBeBound == null)");
		}
		if (input == null) {
			throw new IllegalArgumentException("(input == null)");
		}
		if (!hasXmlRootElementAnnotation()) {
			return xmlToBeanWithoutXmlRootElement(input);
		} else {
			try {
				Unmarshaller unmarshall = createUnmarshaller();
				//
				@SuppressWarnings("unchecked")
				T bean = (T) unmarshall.unmarshal(input); // FIXME!
				return bean;
			} catch (Exception e) {
				log.error("classesToBeBound=[" + classesToBeBound + "]", e);
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Bean without declare "@javax.xml.bind.annotation.XmlRootElement" annotation.
	 */
	public T xmlToBeanWithoutXmlRootElement(String xml) {
		if (xml == null) {
			throw new IllegalArgumentException("(xml == null)");
		}
		StringReader input = new StringReader(xml);
		T bean = xmlToBeanWithoutXmlRootElement(input);
		try {
			input.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return bean;
	}

	/**
	 * Bean without declare "@javax.xml.bind.annotation.XmlRootElement" annotation.
	 */
	public T xmlToBeanWithoutXmlRootElement(Reader input) {
		if (classesToBeBound == null) {
			throw new IllegalArgumentException("(classesToBeBound == null)");
		}
		if (input == null) {
			throw new IllegalArgumentException("(input == null)");
		}
		if (hasXmlRootElementAnnotation()) {
			return xmlToBean(input);
		} else {
			try {
				Unmarshaller unmarshall = createUnmarshaller();
				JAXBElement<T> je = unmarshall.unmarshal(new StreamSource(input), classesToBeBound);
				// je.setNil(value);
				return je.getValue();
			} catch (Exception e) {
				log.error("classesToBeBound=[" + classesToBeBound + "]", e);
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Bean without declare "@javax.xml.bind.annotation.XmlRootElement" annotation.
	 */
	public T xmlToBeanWithoutXmlRootElement(InputStream input) {
		if (classesToBeBound == null) {
			throw new IllegalArgumentException("(classesToBeBound == null)");
		}
		if (input == null) {
			throw new IllegalArgumentException("(input == null)");
		}
		if (hasXmlRootElementAnnotation()) {
			return xmlToBean(input);
		} else {
			try {
				Unmarshaller unmarshall = createUnmarshaller();
				JAXBElement<T> je = unmarshall.unmarshal(new StreamSource(input), classesToBeBound);
				return je.getValue();
			} catch (Exception e) {
				log.error("classesToBeBound=[" + classesToBeBound + "]", e);
				throw new RuntimeException(e);
			}
		}
	}

	// ===========================================
	/**
	 * To check Bean with or without declare "@javax.xml.bind.annotation.XmlRootElement" annotation.
	 */
	protected boolean hasXmlRootElementAnnotation() {
		return classesToBeBound.isAnnotationPresent(XmlRootElement.class);
	}

	protected Unmarshaller createUnmarshaller() throws Exception {
		JAXBContext context = JAXBContext.newInstance(classesToBeBound);
		Unmarshaller unmarshall = context.createUnmarshaller();
		if (debugXML) {
			// This is important because any XML that is not recognized will cause the unmarshalling to
			// fail silently -- you get only part of the object
			// unmarshall.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
			unmarshall.setEventHandler(new ValidationEventHandlerImpl());
		}
		// unmarshall.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.thsrc.com.tw/TWHSR");
		// unmarshall.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, "TWHSR.xsd");
		//
		return unmarshall;
	}

	// protected class ValidationEventHandlerImpl implements ValidationEventHandler {
	protected class ValidationEventHandlerImpl extends DefaultValidationEventHandler {
		public boolean handleEvent(ValidationEvent event) {
			if (event.getMessage() != null && event.getMessage().startsWith("unexpected element (uri:")) {
				log.warn("event=[" + event + "]");
				// log.warn("event=[" + toEventMessage(event) + "]");
				return true;
			} else {
				log.error("event=[" + event + "]");
				return false;
			}
		}

		protected String toEventMessage(ValidationEvent event) {
			log.error("event=[" + event + "]");
			int severity = event.getSeverity();
			if (severity == ValidationEvent.ERROR) {
				return "ValidationEvent.ERROR";
			} else if (severity == ValidationEvent.FATAL_ERROR) {
				return "ValidationEvent.FATAL_ERROR";
			} else if (severity == ValidationEvent.WARNING) {
				return "ValidationEvent.WARNING";
			} else {
				return "";
			}
		}
	}

	public boolean isDebugXML() {
		return debugXML;
	}

	public void setDebugXML(boolean debugXML) {
		this.debugXML = debugXML;
	}

	public Class<T> getClassesToBeBound() {
		return classesToBeBound;
	}

	public void setClassesToBeBound(Class<T> classesToBeBound) {
		this.classesToBeBound = classesToBeBound;
	}

}