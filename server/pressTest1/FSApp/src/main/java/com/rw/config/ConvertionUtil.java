package com.rw.config;
/**
 * @author Franky
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.log.GameLog;

public class ConvertionUtil {
	public interface Filter<K,V>{
		boolean accept(K key,V item);
	}
	
	public interface Consumer<K,V>{
		void forEach(K key,V item);
	}
	
	public interface Constructor<K,V>{
		V build(K key,CSVRecord rec);
	}
	
	public interface Selector<T,V>{
		V select(T item);
	}
	
	public interface Parser<T>{
		T parse(String str);
	}
	
	public static <K,V> void forEach(HashMap<K,V> dict,ConvertionUtil.Consumer<K,V> consumer){
		Collection<Entry<K,V>> sets = dict.entrySet();
		for (Entry<K,V> entry : sets) {
			consumer.forEach(entry.getKey(), entry.getValue());
		}
	}
	
	//get can be defined by forEach, but this is more explicit
	public static <K,V> List<V> get(HashMap<K,V> dict,ConvertionUtil.Filter<K,V> filter){
		LinkedList<V> result = new LinkedList<V>();
		Collection<Entry<K,V>> sets = dict.entrySet();
		for (Entry<K,V> entry : sets) {
			if (filter.accept(entry.getKey(),entry.getValue())){
				result.addLast(entry.getValue());
			}
		}
		return result;
	}
	
	public static <K,V,S> HashMap<S,List<V>> BuildIndex(HashMap<K,V> dict,final ConvertionUtil.Selector<V,S> selector){
		final HashMap<S,List<V>> result = new HashMap<S,List<V>>();
		forEach(dict,new ConvertionUtil.Consumer<K, V>() {
			@Override
			public void forEach(K key, V item) {
				S keySelected = selector.select(item);
				List<V> lst = result.get(keySelected);
				if (lst != null) lst.add(item);
				else{
					lst = new LinkedList<V>();
					lst.add(item);
					result.put(keySelected, lst);
				}
			}
		});
		return result;		
	}
	
	public static <K,V> HashMap<K,V> LoadConfig(ClassLoader configClassLoader,String configFileName,Constructor<K,V> builder,Parser<K> keyBuilder) {
		String csvFn = configClassLoader.getResource(configFileName).getFile();
		File csvFile = new File(csvFn);
		HashMap<K,V> dict = null;
		try {
			CSVParser parser = new CSVParser(new InputStreamReader(new FileInputStream(csvFile), "UTF-8"), CSVFormat.DEFAULT.withHeader());
			dict = new HashMap<K,V>();
			int line = 0;
			for (CSVRecord csvRecord : parser) {
				line++;
				String keyStr = ConvertionUtil.parseString(csvRecord, 0, csvRecord.size());
				if (keyStr.isEmpty()){
					GameLog.error("Config error in "
							+ configFileName
							+ ": empty key at line: "+line);
				}else{
					try {
						K key = keyBuilder.parse(keyStr);
						V item = builder.build(key,csvRecord);
						if (dict.put(key, item) != null){
							GameLog.error("Config error in "
									+ configFileName
									+ ": duplicated ID ["+keyStr+"] at line: "+line);
						}
					} catch (Exception e) {
						GameLog.error("Config error in "
								+ configFileName
								+ ": failed to parse record of ID ["+keyStr+"] at line: "+line);
					}
				}
			}
			parser.close();
		} catch (FileNotFoundException e) {
			GameLog.error("Config error","", "open "
					+ configFileName
					+ " failed",e);	
		} catch (IOException e) {
			GameLog.error("Config error","", "read "
					+ configFileName
					+ " failed",e);
		}
		return dict;
	}
	
	//although you can define general identity function
	private static class StringIDFunc implements Parser<String> {
		@Override
		public String parse(String keyStr) {
			return keyStr;
		}
	}
	public static final Parser<String> StringKeyParser = new ConvertionUtil.StringIDFunc();

	private static class IntegerKeyParser implements Parser<Integer> {
		@Override
		public Integer parse(String keyStr) {
			return Integer.valueOf(keyStr);
		}
	}
	public static final Parser<Integer> IntegerKeyParser = new ConvertionUtil.IntegerKeyParser();

	private static class FloatKeyParser implements Parser<Float> {
		@Override
		public Float parse(String keyStr) {
			return Float.valueOf(keyStr);
		}
	}
	public static final Parser<Float> FloatKeyParser = new ConvertionUtil.FloatKeyParser();

	//don't use this way to parse primitive type, it is inefficient
	public static <T> T parse(CSVRecord csvRecord,int index,int columnSize,T defaultVal,Parser<T> parser){
		if (index < columnSize){
			String str = csvRecord.get(index);
			if (str == null || str.isEmpty()) return defaultVal; 
			try {
				return parser.parse(str);
			} catch (Exception e) {
				GameLog.error("Config", "", "parse failed value="+str, e);
			}
		}else{
			GameLog.error("Config error: no field at column ["+index+"]");
		}
		return defaultVal;
	}
	
	public static int parseint(CSVRecord csvRecord,int index,int columnSize){
		if (index < columnSize){
			String str = csvRecord.get(index);
			if (str == null || str.isEmpty()) return 0; 
			try {
				return Integer.parseInt(str);
			} catch (NumberFormatException e) {
				GameLog.error("Config", "", "parse int failed value=" + str
						+ ", try use alternative way to parse", e);
				try {
					Float tmp = Float.parseFloat(str);
					GameLog.error("use alternative way to parse int from \""+str+"\" success");
					return tmp.intValue();					
				} catch (Exception e2) {
					GameLog.error("Config", "", "parse int failed value=" + str, e);
				}
			} catch (Exception e) {
				GameLog.error("Config", "", "parse int failed value=" + str, e);
			}
		}else{
			GameLog.error("Config error: no field at column ["+index+"]");
		}
		return 0;
	}
	
	public static float parsefloat(CSVRecord csvRecord,int index,int columnSize){
		if (index < columnSize){
			String str = csvRecord.get(index);
			if (str == null || str.isEmpty()) return 0; 
			try {
				return Float.parseFloat(str);
			} catch (Exception e) {
				GameLog.error("Config", "", "parse float failed value="+str, e);
			}
		}else{
			GameLog.error("Config error: no field at column ["+index+"]");
		}
		return 0;
	}
	
	public static String parseString(CSVRecord csvRecord,int index,int columnSize){
		if (index < columnSize){
			String str = csvRecord.get(index).trim();
			if (str == null) return "";
			return str;
		}else{
			GameLog.error("Config error: no field at column ["+index+"]");
		}
		return "";		
	}
}
