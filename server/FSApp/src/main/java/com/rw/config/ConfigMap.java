/**
 * 
 */
package com.rw.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * @author Franky
 */
public abstract class ConfigMap<K,V> {
	@SuppressWarnings("rawtypes")
	private static HashSet<ConfigMap> allMaps = new HashSet<ConfigMap>();
	
	protected ConfigMemHelper<K,V> helper;
	protected ConfigMap(){
		allMaps.add(this);
	}
	
	public abstract void loadIndex();
	public abstract void unloadIndex();

	@SuppressWarnings("rawtypes")
	public static void ReLoadAllLoaded(){
		for (Iterator iterator = allMaps.iterator(); iterator.hasNext();) {
			ConfigMap cfgJsonDao = (ConfigMap) iterator.next();
			cfgJsonDao.reload();
		}
	}

	public final V getCfgById(K id) {
		return helper.getByKey(id);
	}

	public final int size() {
		return helper.ConfigCount();
	}

	public final void clearMap() {	
		helper.UnLoad();
		unloadIndex();
	}

	public final void reload() {
		helper.ReLoad();
		unloadIndex();
		loadIndex();
	}

	public final <S> HashMap<S,List<V>> buildIndex(ConvertionUtil.Selector<V,S> selector) {
		return helper.BuildIndex(selector);
	}
}
