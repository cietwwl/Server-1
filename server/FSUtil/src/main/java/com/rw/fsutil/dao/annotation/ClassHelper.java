package com.rw.fsutil.dao.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;

public class ClassHelper {

	@SuppressWarnings("unchecked")
	public static <T> Class<T> getEntityClass(Class<?> classP) {

		Class<T> entityClass = null;
		Type type = classP.getGenericSuperclass();
		if (!(type instanceof ParameterizedType)) {
			type = classP.getSuperclass().getGenericSuperclass();
		}

		if (((ParameterizedType) type).getActualTypeArguments()[0] instanceof ParameterizedType) {
			entityClass = null;
		} else {
			entityClass = (Class<T>) ((ParameterizedType) type).getActualTypeArguments()[0];
		}
		return entityClass;
	}

	public static JavaType getListGenericJavaType(Field field) {
		Type genericType = field.getGenericType();
		JavaType listType = null;
		if (genericType != null && (genericType instanceof ParameterizedType)) {
			ParameterizedType pt = (ParameterizedType) genericType;
			Type gType = pt.getActualTypeArguments()[0];
			JavaType genericJavaType = getGenericJavaType(gType);

			listType = TypeFactory.defaultInstance().constructCollectionType(List.class, genericJavaType);

		}
		return listType;

	}

	private static JavaType getGenericJavaType(Type gType) {
		JavaType genericJavaType = null;

		if (Class.class.isAssignableFrom(gType.getClass())) {
			genericJavaType = TypeFactory.defaultInstance().constructType(gType);
		} else {
			ParameterizedType pt = (ParameterizedType) gType;
			Type[] pTypeArray = pt.getActualTypeArguments();
			int pLength = pTypeArray.length;
			Class<?> pclassArray[] = new Class<?>[pLength];
			for (int i = 0; i < pLength; i++) {
				pclassArray[i] = (Class<?>) pTypeArray[i];
			}

			genericJavaType = TypeFactory.defaultInstance().constructParametricType((Class<?>) pt.getRawType(), pclassArray);
		}
		return genericJavaType;
	}

	public static JavaType getMapGenericJavaType(Field field) {
		Type genericType = field.getGenericType();
		JavaType mapType = null;
		if (genericType != null && (genericType instanceof ParameterizedType)) {
			ParameterizedType pt = (ParameterizedType) genericType;

			Type keyType = pt.getActualTypeArguments()[0];
			Type valueType = pt.getActualTypeArguments()[1];

			JavaType genericKeyJavaType = TypeFactory.defaultInstance().constructType(keyType);
			JavaType genericValueJavaType = getGenericJavaType(valueType);
			mapType = TypeFactory.defaultInstance().constructMapType(Map.class, genericKeyJavaType, genericValueJavaType);
		}
		return mapType;

	}

	public static JavaType getJUCMapGenericJavaType(Field field) {
		Type genericType = field.getGenericType();
		JavaType mapType = null;
		if (genericType != null && (genericType instanceof ParameterizedType)) {
			ParameterizedType pt = (ParameterizedType) genericType;

			Type keyType = pt.getActualTypeArguments()[0];
			Type valueType = pt.getActualTypeArguments()[1];

			JavaType genericKeyJavaType = TypeFactory.defaultInstance().constructType(keyType);
			JavaType genericValueJavaType = getGenericJavaType(valueType);
			mapType = TypeFactory.defaultInstance().constructMapType(ConcurrentHashMap.class, genericKeyJavaType, genericValueJavaType);
		}
		return mapType;

	}

	public static boolean isList(Field field) {
		return List.class.isAssignableFrom(field.getType());
	}

	public static boolean isMap(Field field) {
		return Map.class.isAssignableFrom(field.getType());
	}

	/**
	 * 获取第一个标记指定注解的属性
	 * 
	 * @param entityClass
	 * @param annotationClass
	 * @return
	 */
	public static Field getFirstAnnotateField(Class<?> entityClass, Class<? extends Annotation> annotationClass) {
		Field[] fields = entityClass.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];
			if (f.isAnnotationPresent(annotationClass)) {
				return f;
			}
		}
		return null;
	}

	/**
	 * 获取第一个标记指定注解的属性
	 * 
	 * @param entityClass
	 * @param annotationClass
	 * @return
	 */
	public static String getFirstAnnotateFieldName(Class<?> entityClass, Class<? extends Annotation> annotationClass) {
		Field f = getFirstAnnotateField(entityClass, annotationClass);
		return f == null ? null : f.getName();
	}

}
