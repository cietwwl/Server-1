package Json;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

/**
 * 
 * @author figo
 * 
 */
public class JsonUtil<T> {
	@SuppressWarnings("unchecked")
	public static String getJsonValue(Object classInstance, String className) throws Exception{
		
		Class<?> instance = Class.forName(className);
		
		Field[] fields = instance.getDeclaredFields();
		JSONObject json = new JSONObject();
		for (Field field : fields) {
			field.setAccessible(true);
			if(field.getGenericType().toString().equals("class java.util.Map")){
				Map<String, Object> map = (Map<String, Object>)field.get(classInstance);
				JSONObject subJson = new JSONObject();
				for (Iterator<Entry<String, Object>> iterator = map.entrySet().iterator(); iterator.hasNext();) {
					Entry<String, Object> entry = iterator.next();
					subJson.put(entry.getKey(), entry.getValue());
				}
				json.put(field.getName(), subJson.toString());
			}else{
				json.put(field.getName(), field.get(classInstance));
			}
			
		}
		return json.toString();
	}
	
	private static final ObjectMapper MAPPER = new ObjectMapper();

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
	
	public static <T> Map<String, T> readToMap(String value, Class<T> Object) {
		if (value != null && !value.trim().equals("")) {
			try {				
				Map<String, T> maps = MAPPER.readValue(value, new TypeReference<Map<String, T>>() {});
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
}
