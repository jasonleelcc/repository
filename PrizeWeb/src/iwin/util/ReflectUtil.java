package iwin.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reflect Utility.
 * 
 * @author leotu@nec.com.tw
 */
public class ReflectUtil {

	static protected Logger log = LoggerFactory.getLogger(ReflectUtil.class);

	/**
	 * If the underlying field is a static field, the reflectObj argument is ignored; it may be null.
	 */
	// @SuppressWarnings("unchecked")
	static public Object getFieldReturnObj(Object reflectObj, Class<?> clsObj, String fieldName) {
		// if (reflectObj == null) {
		// throw new IllegalArgumentException("(reflectObj == null)");
		// }
		if (clsObj == null) {
			throw new IllegalArgumentException("(clsObj == null)");
		}
		if (fieldName == null || fieldName.length() == 0) {
			throw new IllegalArgumentException("(fieldName == null || fieldName.length() == 0), fieldName=[" + fieldName + "]");
		}
		try {
			Field field = clsObj.getDeclaredField(fieldName);
			boolean keepStatus = field.isAccessible();
			if (!keepStatus) {
				field.setAccessible(true);
			}
			try {
				Object fieldObj = field.get(reflectObj);
				field.setAccessible(keepStatus);
				// log.debug("isAccessible=[" + keepStatus +
				// "], getFieldReturnObj: field.getName()=[" +
				// field.getName() + "], fieldObj=[" + fieldObj + "]");
				return fieldObj;
			} finally {
				field.setAccessible(keepStatus);
			}
		} catch (Exception e) {
			log.warn("reflectObj=[" + reflectObj + "], clsObj=[" + clsObj.getName() + "], fieldName=[" + fieldName + "]: " + e.toString());
			throw new RuntimeException(e);
		}
	}

	/**
	 * If the underlying field is a static field, the reflectObj argument is ignored; it may be null.
	 */
	// @SuppressWarnings("unchecked")
	static public void setFieldNewObj(Object reflectObj, Class<?> clsObj, String fieldName, Object newValue) {
		// if (reflectObj == null) {
		// throw new IllegalArgumentException("(reflectObj == null)");
		// }
		if (clsObj == null) {
			throw new IllegalArgumentException("(clsObj == null)");
		}
		if (fieldName == null || fieldName.length() == 0) {
			throw new IllegalArgumentException("(fieldName == null || fieldName.length() == 0), fieldName=[" + fieldName + "]");
		}
		try {
			Field field = clsObj.getDeclaredField(fieldName);
			boolean keepStatus = field.isAccessible();
			if (!keepStatus) {
				field.setAccessible(true);
			}
			try {
				// Object oldFieldObj = field.get(reflectObj);
				field.set(reflectObj, newValue);
				// Object newFieldObj = field.get(reflectObj);
				field.setAccessible(keepStatus);
				// log.debug("isAccessible=[" + keepStatus +
				// "], setFieldNewObj: field.getName()=[" +
				// field.getName()
				// + "], oldFieldObj=[" + oldFieldObj + "], newFieldObj=[" +
				// newFieldObj + "]");
			} finally {
				field.setAccessible(keepStatus);
			}
		} catch (Exception e) {
			log.warn("reflectObj=[" + reflectObj + "], clsObj=[" + clsObj.getName() + "], fieldName=[" + fieldName + "]: " + e.toString());
			throw new RuntimeException(e);
		}
	}

	// @SuppressWarnings("unchecked")
	static public Object getFieldReturnObj(Object reflectObj, String fieldName) {
		Class<?> clsObj = reflectObj.getClass();
		return getFieldReturnObj(reflectObj, clsObj, fieldName);
	}

	// @SuppressWarnings("unchecked")
	static public Object getFieldReturnObj(Class<?> clsObj, String staticFieldName) {
		return getFieldReturnObj(null, clsObj, staticFieldName);
	}

	// @SuppressWarnings("unchecked")
	static public void setFieldNewObj(Object reflectObj, String fieldName, Object newValue) {
		Class<?> clsObj = reflectObj.getClass();
		setFieldNewObj(reflectObj, clsObj, fieldName, newValue);
	}

	// @SuppressWarnings("unchecked")
	static public void listField(Class<?> clsObj) {
		if (clsObj == null) {
			throw new IllegalArgumentException("(clsObj == null)");
		}
		Field[] fields = clsObj.getDeclaredFields(); // getFields();
		for (int i = 0; i < fields.length; i++) {
			log.debug("fields[" + i + "], getName=[" + fields[i].getName() + "], toString=[" + fields[i].toString() + "], isAccessible=["
					+ fields[i].isAccessible() + "]");
		}
	}

	static public void listField(Object reflectObj) {
		listField(reflectObj.getClass());
	}

	// @SuppressWarnings("unchecked")
	static public void listMethod(Class<?> clsObj) {
		if (clsObj == null) {
			throw new IllegalArgumentException("(clsObj == null)");
		}
		Method[] methods = clsObj.getDeclaredMethods(); // getMethods();
		for (int i = 0; i < methods.length; i++) {
			log.debug("methods[" + i + "], getName=[" + methods[i].getName() + "], toString=[" + methods[i].toString() + "], isAccessible=["
					+ methods[i].isAccessible() + "]");
		}
	}

	static public void listMethod(Object reflectObj) {
		listMethod(reflectObj.getClass());
	}

	//
	// @SuppressWarnings("unchecked")
	static public Object doMethodInvoke(Object reflectObj, Class<?> clsObj, String methodName, Class<?>[] argsTypes, Object[] argsValue)
			throws Exception {
		// if (reflectObj == null) {
		// throw new IllegalArgumentException("(reflectObj == null)");
		// }
		if (clsObj == null) {
			throw new IllegalArgumentException("(clsObj == null)");
		}
		if (methodName == null || methodName.length() == 0) {
			throw new IllegalArgumentException("(methodName == null || methodName.length() == 0), methodName=[" + methodName + "]");
		}
		try {
			Method method = clsObj.getDeclaredMethod(methodName, argsTypes);
			boolean keepStatus = method.isAccessible();
			if (!keepStatus) {
				method.setAccessible(true);
			}
			try {
				Object obj = method.invoke(reflectObj, argsValue);
				return obj;
			} finally {
				method.setAccessible(keepStatus);
			}
		} catch (Exception e) {
			// throw new RuntimeException(e);
			Throwable ee = e;
			while (ee.getCause() != null && ee instanceof InvocationTargetException && ee.getCause() instanceof Exception) {
				ee = (Exception) ee.getCause();
			}
			if (ee != e && ee instanceof Exception) {
				log.warn("reflectObj=[" + reflectObj + "], clsObj=[" + clsObj.getName() + "], methodName=[" + methodName + "]: " + ee.toString());
				throw (Exception) ee;
			} else {
				log.warn("reflectObj=[" + reflectObj + "], clsObj=[" + clsObj.getName() + "], methodName=[" + methodName + "]: " + e.toString());
				throw e;
			}
		}
	}

	// @SuppressWarnings("unchecked")
	static public Object doMethodInvoke(Class<?> clsObj, String staticMethodName) throws Exception {
		return doMethodInvoke(null, clsObj, staticMethodName);
	}

	// @SuppressWarnings("unchecked")
	static public Object doMethodInvoke(Class<?> clsObj, String staticMethodName, Class<?>[] argsTypes, Object[] argsValue) throws Exception {
		return doMethodInvoke(null, clsObj, staticMethodName, argsTypes, argsValue);
	}

	// @SuppressWarnings("unchecked")
	static public Object doMethodInvoke(Object reflectObj, Class<?> clsObj, String methodName) throws Exception {
		return doMethodInvoke(reflectObj, clsObj, methodName, new Class[0], new Object[0]);
	}

	// @SuppressWarnings("unchecked")
	static public Object doMethodInvoke(Object reflectObj, String methodName, Class<?>[] argsTypes, Object[] argsValue) throws Exception {

		Class<?> clsObj = reflectObj.getClass();
		return doMethodInvoke(reflectObj, clsObj, methodName, argsTypes, argsValue);
	}

	// @SuppressWarnings("unchecked")
	static public Object doMethodInvoke(Object reflectObj, String methodName) throws Exception {
		Class<?> clsObj = reflectObj.getClass();
		return doMethodInvoke(reflectObj, clsObj, methodName, new Class[0], new Object[0]);
	}
}