package com.rw.fsutil.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * This class saves a map of spring-bean ids to their corresponding interfaces. <br/>
 * Any bean-lookup can use this class getBeanId method to obtain a spring bean only specifying the interface class. <br/>
 * The bean-id-map of this class must be consistent <br/>
 * to the applicationContext.xml file.
 */
public class SpringContextUtil implements ApplicationContextAware {
	private static ApplicationContext applicationContext;

	/**
	 * implement ApplicationContextAware interface callback method to setup the context environment
	 * 
	 * @param context
	 * @throws BeansException
	 */
	public void setApplicationContext(ApplicationContext context) {
		applicationContext = context;
	}

	/**
	 * get bean object
	 * 
	 * @param name
	 * @return Object registered bean instance
	 * @throws BeansException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		return (T) applicationContext.getBean(name);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(Class<?> name) {
		return (T) applicationContext.getBean(name);
	}
}