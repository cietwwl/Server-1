package com.rounter.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.CollectionType;
import org.codehaus.jackson.map.type.MapType;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

/**
 * 
 * @author Lanux
 * 
 */
public class JsonUtil {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private static TypeReference<Map<String, String>> stringType = new TypeReference<Map<String, String>>() {
	};

	private JsonUtil() {
	}

	public static <T> T readValue(String value, Class<T> Object) {
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
	
	public static <T> T readValue(String value, Class<T> Object,ObjectMapper mapper) {
		if (value != null) {
			try {
				return mapper.readValue(value, Object);
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
	
	public static <T> T readValue(String value, JavaType Object,ObjectMapper mapper) {
		if (value != null) {
			try {
				return mapper.readValue(value, Object);
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

	@SuppressWarnings("unchecked")
	public static <T> T readValue(String value, TypeReference<T> Object) {
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

	public static <T> List<T> readList(String value, Class<T> Object) {
		if (value != null && !value.trim().equals("")) {
			try {
				// CollectionType constructMapType =
				// ArrayType.construct(componentType, valueHandler,
				// typeHandler).getTypeFactory().constructMapType(Map.class,
				// String.class , Object);
				// Map<String, Object> maps =
				// MAPPER.readValue(value,constructMapType);

				CollectionType constructListType = MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, Object);
				List<T> list = MAPPER.readValue(value, constructListType);

				return list;
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

	public static <T> Map<String, Object> readJson2Map(String value, Class<T> Object) {
		if (value != null && !value.trim().equals("")) {
			try {
				MapType constructMapType = MAPPER.getTypeFactory().constructMapType(Map.class, String.class, Object);
				Map<String, Object> maps = MAPPER.readValue(value, constructMapType);
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

	public static <K, T> Map<String, Object> readJson2Map(String value, JavaType keyType, JavaType Object) {
		if (value != null && !value.trim().equals("")) {
			try {
				MapType constructMapType = MAPPER.getTypeFactory().constructMapType(Map.class, keyType, Object);
				Map<String, Object> maps = MAPPER.readValue(value, constructMapType);
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

	public static <T> Map<String, T> readToMap(String value, Class<T> Object) {
		if (value != null && !value.trim().equals("")) {
			try {
				Map<String, T> maps = MAPPER.readValue(value, new TypeReference<Map<String, T>>() {
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

	public static Map<String, String> readToMap(String value) {
		try {
			Map<String, String> maps = MAPPER.readValue(value, stringType);
			return maps;
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Map<String, String> readToMap(String value, ObjectMapper mapper) {
		try {
			Map<String, String> maps = mapper.readValue(value, stringType);
			return maps;
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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

	/**
	 * 
	 * 获取ObjectMapper的TypeFactory
	 * 
	 * @return
	 */
	public static TypeFactory getTypeFactory() {
		return MAPPER.getTypeFactory();
	}
}
