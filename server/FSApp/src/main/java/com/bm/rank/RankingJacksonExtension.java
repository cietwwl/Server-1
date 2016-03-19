package com.bm.rank;

import java.io.IOException;
import java.io.StringWriter;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import com.rw.fsutil.ranking.RankingExtension;

/**
 * <pre>
 * 排行榜扩展的Jackson实现
 * 通过Jackson转换成对象
 * </pre>
 * @author Jamaz
 *
 * @param <K>
 * @param <E>
 */
public abstract class RankingJacksonExtension<C extends Comparable<C>,E> implements RankingExtension<C, E>{

	private final Class<C> compareClass;
	private final Class<E> extensionClass;
	private ObjectMapper mapper = new ObjectMapper();
	
	public RankingJacksonExtension(Class<C> compareClass, Class<E> extensionClass) {
		super();
		this.compareClass = compareClass;
		this.extensionClass = extensionClass;
	}

	private String serializeObject(Object t) {
		StringWriter sw = new StringWriter();
		try {
			mapper.writeValue(sw, t);
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
	public String encodeComparable(C compare) {
		return serializeObject(compare);
	}

	@Override
	public C decodeComparable(String dbString) {
		try {
			return mapper.readValue(dbString, compareClass);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String encodeExtendedAttribute(E exntension) {
		return serializeObject(exntension);
	}

	@Override
	public E decodeExtendedAttribute(String dbString) {
		try {
			return mapper.readValue(dbString, extensionClass);
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