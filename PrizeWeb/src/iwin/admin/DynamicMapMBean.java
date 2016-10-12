/*
 * Copyright (c) 2008.
 * All rights reserved.
 */
package iwin.admin;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.ReflectionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author leotu@nec.com.tw
 */
public class DynamicMapMBean implements DynamicMBean {
	protected static final Logger log = LoggerFactory.getLogger(DynamicMapMBean.class);

	private Map<Object, Object> model = new TreeMap<Object, Object>();

	// private String name = "";

	private MBeanInfo mBeanInfo = null;

	// private String className;
	//
	private MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[0];
	//
	private MBeanConstructorInfo[] constructors = new MBeanConstructorInfo[0];
	//
	private MBeanOperationInfo[] operations = new MBeanOperationInfo[0];
	//
	private MBeanNotificationInfo[] notifications = new MBeanNotificationInfo[0];

	public Map<Object, Object> getModel() {
		return model;
	}

	public void init(Class<?> clazz) {
		//String className = model.getClass().getName();
		String className = clazz.getName();
		Set<Object> keySet = model.keySet();
		Iterator<Object> it = keySet.iterator();
		attributes = new MBeanAttributeInfo[keySet.size()];
		log.debug("keySet.size=" + keySet.size());
		for (int i = 0; it.hasNext(); i++) {
			String name = (String) it.next();
			Object value = model.get(name);
			String type = name.getClass().getName();
			String desc = type + ":" + name;
			boolean isReadable = true;
			boolean isWritable = false;
			log.debug("[" + i + "], name=[" + name + "], type=[" + type + "], desc=[" + desc + "], value=[" + value + "]");
			attributes[i] = new MBeanAttributeInfo(name, type, desc, isReadable, isWritable, false);
		}
		mBeanInfo = new MBeanInfo(className, className, attributes, constructors, operations, notifications);
	}

	public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
		// log.debug("attribute=[" + attribute + "]");
		if (attribute == null) {
			throw new AttributeNotFoundException("(attribute == null)");
		}
		return model.get(attribute);
	}

	public AttributeList getAttributes(String[] attributes) {
		AttributeList resultList = new AttributeList();
		if (attributes == null) {
			log.warn("attributes=[" + attributes + "]");
		} else {
			for (int i = 0; i < attributes.length; i++) {
				// log.debug("attributes[" + i + "]=[" + attributes[i] + "]");
				try {
					Object value = getAttribute((String) attributes[i]);
					resultList.add(new Attribute(attributes[i], value));
				} catch (Exception e) {
					log.warn("", e);
				}
			}
		}
		return resultList;
	}

	public MBeanInfo getMBeanInfo() {
		return mBeanInfo;
	}

	public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
		throw new MBeanException(new RuntimeException("Unsupport invoke(...), actionName=[" + actionName + "]"));
	}

	public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException,
			ReflectionException {
		throw new MBeanException(new InvalidAttributeValueException("Unsupport setAttribute(...), Attribute=[" + attribute + "]"));
	}

	public AttributeList setAttributes(AttributeList attributes) {
		throw new RuntimeException("Unsupport setAttribute(...), AttributeList=[" + attributes + "]");
	}
}
