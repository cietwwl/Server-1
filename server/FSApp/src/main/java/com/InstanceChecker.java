package com;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.MessageOrBuilder;

public class InstanceChecker {

	private static ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
	private static List<Callable<Object>> taskList = new ArrayList<Callable<Object>>();
	private static String systemPath;
	public static void main(String[] args) throws Exception {
		URL systemResource = ClassLoader.getSystemResource("com");
		String file = systemResource.getPath();
		systemPath = file.substring(1, file.indexOf("com")).replace("/", File.separator);
		check(file);
		List<Future<Object>> list = es.invokeAll(taskList);
		while (list.size() > 0) {
			for (Iterator<Future<Object>> itr = list.iterator(); itr.hasNext();) {
				if (itr.next().isDone()) {
					itr.remove();
				}
			}
		}
		System.exit(0);
	}
 	
	private static void check(String path) {
		final File file = new File(path);
		if (file.isDirectory()) {
			File[] listFiles = file.listFiles();
			for (int i = 0, size = listFiles.length; i < size; i++) {
				check(listFiles[i].getPath());
			}
		} else {
			if (file.getName().contains(".class")) {
				taskList.add(new Callable<Object>() {
					@Override
					public Object call() throws Exception {
						String classPath = file.getPath().replace(systemPath, "").replace("\\", ".").replace("/", ".").replace(".class", "");
						try {
							Class<?> loadClass = ClassLoader.getSystemClassLoader().loadClass(classPath);
							if (!loadClass.isEnum() && !loadClass.isInterface() && !GeneratedMessage.class.isAssignableFrom(loadClass) && !MessageOrBuilder.class.isAssignableFrom(loadClass)) {
								checkClass(loadClass);
							}
						} catch (Exception e) {
							System.err.println(classPath);
							e.printStackTrace();
						}
						return "DONE";
					}
				});
			}
		}
	}
	
	private static void checkField(Class<?> clazz, Field tempField) throws IllegalAccessException {
		int modifiers = tempField.getModifiers();
		if ((modifiers & Modifier.STATIC) == Modifier.STATIC) {
			if ((modifiers & Modifier.FINAL) == Modifier.FINAL) {
				System.out.println(String.format("class:[%s], %s 修饰符包含final!", clazz.getName(), tempField.getName()));
			}
			tempField.setAccessible(true);
			if (tempField.get(null) == null) {
				System.out.println(String.format("class:[%s], %s 为null", clazz.getName(), tempField.getName()));
			}
			tempField.setAccessible(false);
		} else {
			System.out.println(String.format("class:[%s], [%s]不是静态的", clazz.getName(), tempField.getName()));
		}
	}
	
	private static void checkConstructor(Class<?> clazz) {
		Constructor<?>[] constructors = clazz.getDeclaredConstructors();
		boolean allConPrivate = true;
		for (Constructor<?> con : constructors) {
			if ((con.getModifiers() & Modifier.PRIVATE) != Modifier.PRIVATE) {
				allConPrivate = false;
				break;
			}
		}
		if (allConPrivate) {
			System.out.println(String.format("class:[%s], 所有构造函数都是private", clazz.getName()));
		}
	}
	
	private static void checkMethod(Class<?> clazz) throws InvocationTargetException, IllegalAccessException {
		Method[] allMethods = clazz.getDeclaredMethods();
		for (int j = 0, mSize = allMethods.length; j < mSize; j++) {
			Method tempMethod = allMethods[j];
			if (tempMethod.getReturnType() == clazz) {
				int methodModifiers = tempMethod.getModifiers();
				if ((methodModifiers & Modifier.STATIC) == Modifier.STATIC) {
					tempMethod.setAccessible(true);
					if (tempMethod.getParameterTypes().length > 0) {
						System.out.println(String.format("class:[%s], 方法[%s]执行需要参数 : %s", clazz.getName(), tempMethod.getName(), Arrays.toString(tempMethod.getParameterTypes())));
					} else {
						if (tempMethod.invoke(null) == null) {
							System.out.println(String.format("class:[%s], [%s] 执行之后返回null", clazz.getName(), tempMethod.getName()));
						}
					}
					tempMethod.setAccessible(false);
				} else {
					System.out.println(String.format("class:[%s], [%s] 不是static的", clazz.getName(), tempMethod.getName()));
				}
				break;
			}
		}
	}
	
	private static void checkClass(Class<?> clazz) throws SecurityException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Field[] declaredFields = clazz.getDeclaredFields();
		for (int i = 0, size = declaredFields.length; i < size; i++) {
			Field tempField = declaredFields[i];
			if (tempField.getType() == clazz) {
				checkConstructor(clazz);
				checkField(clazz, tempField);
				checkMethod(clazz);
				break;
			}
		}
	}
}
