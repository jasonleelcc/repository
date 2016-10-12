/*
 * Copyright (c) 2010. All rights reserved.
 */
package iwin.util;

import java.io.OutputStream;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;

// import com.sun.org.apache.xml.internal.serialize.OutputFormat;

/**
 * Support "<![CDATA["
 * <p/>
 * "JDK 6.0:com.sun.org.apache.xml.internal.serialize.XMLSerializer.java" or "xercesImpl.jar:org.apache.xml.serialize.XMLSerializer.java"
 * 
 * @author leotu@nec.com.tw
 * @version $Revision: $
 */
public class BeanToXmlCdataUtil {
	protected static final Logger log = LoggerFactory.getLogger(BeanToXmlCdataUtil.class);

	protected boolean preserveSpace = false;
	protected boolean indenting = true;
	protected boolean noXmlDeclaration = false; // version="1.0" encoding="UTF-8"
	protected boolean standalone = false; // standalone="yes"

	// specify which of your elements you want to be handled as CDATA.
	// The use of the '^' between the namespaceURI and the localname
	// seems to be an implementation detail of the xerces code.
	// When processing xml that doesn't use namespaces, simply omit the
	// namespace prefix as shown in the third CDataElement below.
	protected String[] useCdataElements = {}; // ^UserId" for  <UserId>, "ns1^foo" for <ns1:foo>, ...

	public BeanToXmlCdataUtil() {
	}

	public BeanToXmlCdataUtil(String[] useCdataElements) {
		this.useCdataElements = useCdataElements;
	}

	// protected OutputFormat getOutputFormat()
	// {
	// OutputFormat of = new OutputFormat();
	// if (useCdataElements != null)
	// {
	// String elems[] = new String[useCdataElements.length];
	// for (int i = 0; i < useCdataElements.length; i++)
	// {
	// elems[i] = useCdataElements[i];
	// if (elems[i].startsWith("<") && elems[i].endsWith(">"))
	// {
	// elems[i] = elems[i].substring(1, elems[i].length() - 1);
	// }
	// if (elems[i].indexOf('^') == -1)
	// {
	// elems[i] = "^" + elems[i];
	// }
	// // log.debug("elems[" + i + "]=[" + elems[i] + "] of [" + useCdataElements[i] + "]");
	// }
	// of.setCDataElements(elems);
	// }
	// of.setOmitXMLDeclaration(noXmlDeclaration);
	// of.setPreserveSpace(preserveSpace);
	// of.setIndenting(indenting);
	// of.setStandalone(standalone);
	// of.setLineWidth(1024);
	// return of;
	// }

	/**
	 * use reflect to run none-standard API
	 */
	public ContentHandler createXMLSerializer(OutputStream output) throws Exception {
		try {
			Class<?> xmlSerializerCls = Class.forName("com.sun.org.apache.xml.internal.serialize.XMLSerializer");
			Class<?> baseMarkupSerializerCls = Class.forName("com.sun.org.apache.xml.internal.serialize.BaseMarkupSerializer");
			ContentHandler serializer = (ContentHandler) xmlSerializerCls.newInstance();
			Class<?> outputFormatCls = Class.forName("com.sun.org.apache.xml.internal.serialize.OutputFormat");
			Object outputFormat = ReflectUtil.doMethodInvoke(this, "getOutputFormat");
			ReflectUtil.doMethodInvoke(serializer, xmlSerializerCls, "setOutputFormat", new Class[] { outputFormatCls },
					new Object[] { outputFormat });
			ReflectUtil.doMethodInvoke(serializer, baseMarkupSerializerCls, "setOutputByteStream", new Class[] { OutputStream.class },
					new Object[] { output });
			// XMLSerializer serializer = new XMLSerializer();
			// serializer.setOutputFormat(getOutputFormat());
			// serializer.setOutputByteStream(output);
			return serializer;
		} catch (Throwable e) {
			log.warn("", e);
			return null;
		}
	}

	/**
	 * use reflect to run none-standard API
	 */
	public ContentHandler createXMLSerializer(Writer output) throws Exception {
		try {
			Class<?> xmlSerializerCls = Class.forName("com.sun.org.apache.xml.internal.serialize.XMLSerializer");
			Class<?> baseMarkupSerializerCls = Class.forName("com.sun.org.apache.xml.internal.serialize.BaseMarkupSerializer");
			ContentHandler serializer = (ContentHandler) xmlSerializerCls.newInstance();
			Class<?> outputFormatCls = Class.forName("com.sun.org.apache.xml.internal.serialize.OutputFormat");
			Object outputFormat = ReflectUtil.doMethodInvoke(this, "getOutputFormat");
			ReflectUtil.doMethodInvoke(serializer, xmlSerializerCls, "setOutputFormat", new Class[] { outputFormatCls },
					new Object[] { outputFormat });
			ReflectUtil.doMethodInvoke(serializer, baseMarkupSerializerCls, "setOutputCharStream", new Class[] { Writer.class },
					new Object[] { output });
			// XMLSerializer serializer = new XMLSerializer();
			// serializer.setOutputFormat(getOutputFormat());
			// serializer.setOutputCharStream(output);
			return serializer;
		} catch (Throwable e) {
			log.warn("", e);
			return null;
		}
	}

	// http://www.asciitable.com/
	// static public class XMLSerializerExt extends XMLSerializer
	// {
	//
	// public XMLSerializerExt()
	// {
	// super();
	// }
	//
	// public XMLSerializerExt(OutputFormat format)
	// {
	// super(format);
	// }
	//
	// public XMLSerializerExt(OutputStream output, OutputFormat format)
	// {
	// super(output, format);
	// }
	//
	// public XMLSerializerExt(Writer writer, OutputFormat format)
	// {
	// super(writer, format);
	// }
	//
	// // @Override
	// // public void startElement(String namespaceURI, String localName, String rawName, Attributes attrs) throws
	// // SAXException
	// // {
	// // try
	// // {
	// // prepare();
	// // } catch (IOException except)
	// // {
	// // log.error("namespaceURI=" + namespaceURI + ", localName=" + localName + ", rawName=" + rawName + ", attrs=" +
	// // attrs, except);
	// // throw new SAXException(except);
	// // }
	// // super.startElement(namespaceURI, localName, rawName, attrs);
	// // }
	// }

	public void setPreserveSpace(boolean preserveSpace) {
		this.preserveSpace = preserveSpace;
	}

	public void setIndenting(boolean indenting) {
		this.indenting = indenting;
	}

	public void setUseCdataElements(String[] useCdataElements) {
		this.useCdataElements = useCdataElements;
	}

	public void setNoXmlDeclaration(boolean noXmlDeclaration) {
		this.noXmlDeclaration = noXmlDeclaration;
	}

	public void setStandalone(boolean standalone) {
		this.standalone = standalone;
	}

}