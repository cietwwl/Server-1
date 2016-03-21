package com.rw.dataSyn;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

/**
 * 
 * @author Lanux
 * 
 */
public class JsonUtil {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private JsonUtil() {
	}

	public static <T> T readValue(String value, JavaType Object) {
		if (value != null && !value.trim().equals("")) {
			try {
				return MAPPER.readValue(value, Object);
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static <T> List<T> readList(String json, Class<T> Object) {
		List<T> listItem = readValue(json, new TypeReference<List<T>>() {
		});
		if (listItem == null) {
			listItem = new ArrayList<T>();
		}
		return listItem;

	}

	@SuppressWarnings("unchecked")
	private static <T> T readValue(String value, TypeReference<T> Object) {
		if (value != null && !value.trim().equals("")) {
			try {
				return (T) MAPPER.readValue(value, Object);
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static Map<String, String> readToMap(String value) {
		if (value != null && !value.trim().equals("")) {
			try {
				Map<String, String> maps = MAPPER.readValue(value, new TypeReference<Map<String, String>>() {
				});
				return maps;
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String writeValue(Object object) {
		if (object == null) {
			return null;
		}
		StringWriter sw = new StringWriter();
		try {
			MAPPER.writeValue(sw, object);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sw.toString();
	}
}