package com.rwbase.common;

import java.util.List;

import com.playerdata.Player;

/**
 * 
 * @author CHEN.P
 *
 * @param <T>
 */
public interface DataItemHolderIF<T> {

	public List<T> getItemList(String ownerKey);
	
	public void updateItem(Player player, T entity);
	
	public void updateItemList(Player player, List<T> entityList);
	
	public T getItem(String ownerKey, String key);
}
