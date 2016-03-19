package com.rw.config;

import java.util.HashMap;
import java.util.List;

public abstract class ConfigMemHelper<K,V>{
	private HashMap<K,V> dict;

	protected abstract HashMap<K, V> initDict();
	
	public ConfigMemHelper() {
		dict = initDict();
	}
	
	public HashMap<K,V> UnLoad(){
		HashMap<K,V> result = dict;
		dict = null;
		return result;
	}
	
	public void ReLoad(){
		UnLoad();
		dict = initDict();
	}

	public int ConfigCount(){
		return dict.size();
	}
	
	public List<V> get(ConvertionUtil.Filter<K,V> filter){
		return ConvertionUtil.get(dict, filter);
	}
	
	public Iterable<K> Keys(){
		return dict.keySet();
	}
	
	public Iterable<V> Values(){
		return dict.values();
	}
	
	public V getByKey(K key){
		return dict.get(key);
	}
	
	public <S> HashMap<S,List<V>> BuildIndex(ConvertionUtil.Selector<V, S> selector){
		return ConvertionUtil.BuildIndex(dict, selector);
	}
	
	public void forEach(ConvertionUtil.Consumer<K, V> consumer) {
		ConvertionUtil.forEach(dict, consumer);
	}

}