package com.rw.fsutil.common;

import java.util.Arrays;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.introspect.AnnotatedClass;
import org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector;

public class NameFilterIntrospector extends JacksonAnnotationIntrospector {

	private String[] ignorePropertyArray;

	public NameFilterIntrospector(String... ignoreProperties) {
		this.ignorePropertyArray = Arrays.copyOf(ignoreProperties, ignoreProperties.length);
	}

	@Override
	public String[] findPropertiesToIgnore(AnnotatedClass ac) {
		JsonIgnoreProperties ignore = ac.getAnnotation(JsonIgnoreProperties.class);
		if (ignore != null) {
			String[] array = ignore.value();
			if (array.length > 0) {
				String[] copy = new String[ignorePropertyArray.length + array.length];
				System.arraycopy(array, 0, copy, 0, array.length);
				System.arraycopy(ignorePropertyArray, 0, copy, array.length, ignorePropertyArray.length);
				return copy;
			}
		}
		return ignorePropertyArray;
	}
}
