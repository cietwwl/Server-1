package com.rwbase.dao.gulid.faction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.apache.commons.lang3.StringUtils;

import com.rw.fsutil.cacheDao.mapItem.MapItemStore;

public class GuildApplyInfoHolder {

	final private String guildId;
	final private MapItemStore<GuildApplyInfo> itemStore;
	private Map<String, GuildApplyInfo> guildMap = new HashMap<String, GuildApplyInfo>();
	
	private final WriteLock writeLock;


	public GuildApplyInfoHolder(String guildIdP) {
		guildId = guildIdP;
		itemStore = new MapItemStore<GuildApplyInfo>("guildId", guildId, GuildApplyInfo.class);
		for (GuildApplyInfo itemTmp : getItemList()) {
			guildMap.put(itemTmp.getId(), itemTmp);
		}
		ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
		this.writeLock = rwLock.writeLock();
	}

	/**
	 * 是否已申请过
	 * @param userId
	 * @return
	 */
	public boolean hasApply(String userId){
		
		Enumeration<GuildApplyInfo> mapEnum = itemStore.getEnum();
		boolean hasApply = false;
		while (mapEnum.hasMoreElements()) {
			GuildApplyInfo item = (GuildApplyInfo) mapEnum.nextElement();
			if(StringUtils.equals(userId, item.getPlayerId())){
				hasApply = true;
				break;
			}
		}
		return hasApply;
	}

	public List<GuildApplyInfo> getItemList() {
		List<GuildApplyInfo> itemList = new ArrayList<GuildApplyInfo>();
		Enumeration<GuildApplyInfo> mapEnum = itemStore.getEnum();
		while (mapEnum.hasMoreElements()) {
			GuildApplyInfo item = (GuildApplyInfo) mapEnum.nextElement();
			itemList.add(item);
		}

		return itemList;
	}

	public void updateItem(GuildApplyInfo item) {
		itemStore.updateItem(item);
	}

	public GuildApplyInfo getItem(String userId) {
		return itemStore.getItem(userId);
	}

	public boolean removeItem(GuildApplyInfo item) {
		
		writeLock.lock();
		boolean success = false;
		try {
			success = itemStore.removeItem(item.getId());
			if(success){
				guildMap.remove(item);
			}
		} finally {
			writeLock.unlock();
		}
		
		return success;
	}

	public boolean addItem(GuildApplyInfo item) {
		writeLock.lock();
		boolean success = false;
		try {
			int applyLimit = 100;		
			if(itemStore.getSize() >= applyLimit){
				removeLastApply();
			}			
			success = itemStore.addItem(item);
			if(success){
				guildMap.put(item.getId(), item);
			}
		} finally {
			writeLock.unlock();
		}
		
		return success;
	}

	private void removeLastApply() {
		List<GuildApplyInfo> applyList = getItemList();
		Collections.sort(applyList, new Comparator<GuildApplyInfo>() {

			@Override
			public int compare(GuildApplyInfo source, GuildApplyInfo target) {
				
				return (source.getApplyTime()-target.getApplyTime())>0 ? -1:1;
			}
		});
		GuildApplyInfo last = applyList.get(applyList.size()-1);
		itemStore.removeItem(last.getId());
		
		
	}
	
	public void flush() {
		itemStore.flush();
	}
	
	
}