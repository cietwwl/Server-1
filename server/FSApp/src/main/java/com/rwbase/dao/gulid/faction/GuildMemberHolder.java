package com.rwbase.dao.gulid.faction;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.apache.commons.lang3.StringUtils;

import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.dao.gulid.GuildCfg;

public class GuildMemberHolder {

	final private String guildId;
	final private MapItemStore<GuildMember> itemStore;
	private Map<String, GuildMember> guildMap = new HashMap<String, GuildMember>();
	
	private final ReadLock readLock;	
	private final WriteLock writeLock;

	public GuildMemberHolder(String guildIdP) {
		guildId = guildIdP;
		itemStore = new MapItemStore<GuildMember>("guildId", guildId, GuildMember.class);
		for (GuildMember itemTmp : getItemList()) {
			guildMap.put(itemTmp.getUserId(), itemTmp);
		}
		ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
		this.readLock = rwLock.readLock();
		this.writeLock = rwLock.writeLock();
	}

	public List<GuildMember> getItemList() {
		List<GuildMember> itemList = new ArrayList<GuildMember>();
		readLock.lock();
		try {

			Enumeration<GuildMember> mapEnum = itemStore.getEnum();
			while (mapEnum.hasMoreElements()) {
				GuildMember item = (GuildMember) mapEnum.nextElement();
				itemList.add(item);
			}

		} finally {
			readLock.unlock();
		}
		
		return itemList;
	}

	public void updateItem(GuildMember item) {
		itemStore.updateItem(item);
	}

	public GuildMember getItem(String userId) {
		GuildMember item  = null;
		readLock.lock();
		try {
			item = itemStore.getItem(userId);
		} finally {
			readLock.unlock();
		}
		
		return item;
	}
	
	/**
	 * 获取帮主
	 * @param userId
	 * @return
	 */
	public GuildMember getMaster() {
		GuildMember master  = null;
		readLock.lock();
		try {

			Enumeration<GuildMember> mapEnum = itemStore.getEnum();
			while (mapEnum.hasMoreElements()) {
				GuildMember item = (GuildMember) mapEnum.nextElement();
				if(item.getPosition() == GuildPositionType.MASTER){
					master = item;
				}
			}

		} finally {
			readLock.unlock();
		}
		return master;
	}
	/**
	 * 是否帮主
	 * @param userId
	 * @return
	 */
	public boolean isMaster(String userId) {
		GuildMember master  = getMaster();
		return master!=null && StringUtils.equals(userId, master.getUserId());
	}
	/**
	 * 是否副帮主
	 * @param userId
	 * @return
	 */
	public boolean isSlaveMaster(String userId) {
		GuildMember member  = getItem(userId);
		return member!=null && member.getPosition() == GuildPositionType.SLAVE_MASTER;
	}
	 

	public boolean removeItem(GuildMember item) {
		boolean success = false;
		writeLock.lock();
		try {
			success = itemStore.removeItem(item.getId());
		} finally {
			writeLock.unlock();
		} 
		return success;
	}

	public boolean addItem(GuildMember item, GuildCfg guildCfg) {
		int maxSize = 20;
		if (guildCfg != null) {
			maxSize = guildCfg.num;
		}
		
		boolean success = false;
		writeLock.lock();
		try {
			int size = getItemList().size();
			if (size < maxSize){
				success = itemStore.addItem(item);
			}
		} finally {
			writeLock.unlock();
		} 

		return success;
	}

	public void flush() {
		itemStore.flush();
	}

}