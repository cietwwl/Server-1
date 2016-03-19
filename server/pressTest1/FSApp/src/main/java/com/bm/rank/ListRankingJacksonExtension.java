package com.bm.rank;

import java.io.IOException;
import java.io.StringWriter;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.rw.fsutil.ranking.ListRankingExtension;

public abstract class ListRankingJacksonExtension<E> implements ListRankingExtension<String, E>{

	private final Class<E> clazz;
	
	public ListRankingJacksonExtension(Class<E> clazz){
		this.clazz = clazz;
	}
	
	private ObjectMapper mapper = new ObjectMapper();
	
	@Override
	public String encodeKey(String key) {
		return key;
	}

	@Override
	public String decodeKey(String dbString) {
		return dbString;
	}

	@Override
	public String encodeExtension(E exntension) {
		StringWriter sw = new StringWriter();
		try {
			mapper.writeValue(sw, exntension);
		} catch (JsonGenerationException e1) {
			e1.printStackTrace();
		} catch (JsonMappingException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String value = sw.toString();
		return value;
	}

	@Override
	public E decodeExtension(String json) {
		try {
			return mapper.readValue(json, clazz);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
