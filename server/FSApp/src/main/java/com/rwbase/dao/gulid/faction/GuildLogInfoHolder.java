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

import com.playerdata.readonly.PlayerIF;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;

public class GuildLogInfoHolder {

	final private String guildId;
	final private MapItemStore<GuildLogInfo> itemStore;
	private Map<String, GuildLogInfo> guildMap = new HashMap<String, GuildLogInfo>();
	private final WriteLock writeLock;

	public GuildLogInfoHolder(String guildIdP) {
		guildId = guildIdP;
		itemStore = new MapItemStore<GuildLogInfo>("guildId", guildId, GuildLogInfo.class);
		for (GuildLogInfo itemTmp : getItemList()) {
			guildMap.put(itemTmp.getId(), itemTmp);
		}
		ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
		this.writeLock = rwLock.writeLock();
	}

	public List<GuildLogInfo> getItemList() {
		List<GuildLogInfo> itemList = new ArrayList<GuildLogInfo>();
		Enumeration<GuildLogInfo> mapEnum = itemStore.getEnum();
		while (mapEnum.hasMoreElements()) {
			GuildLogInfo item = (GuildLogInfo) mapEnum.nextElement();
			itemList.add(item);
		}
		return itemList;
	}

	public void updateItem(GuildLogInfo item) {
		itemStore.updateItem(item);
	}

	public GuildLogInfo getItem(String userId) {
		return itemStore.getItem(userId);
	}

	public boolean removeItem(GuildLogInfo item) {

		writeLock.lock();
		boolean success = false;
		try {
			success = itemStore.removeItem(item.getId());
			if (success) {
				guildMap.remove(item);
			}
		} finally {
			writeLock.unlock();
		}

		return success;
	}

	public boolean addLog(PlayerIF player, Guild guild, int type, String otherName, String des) {
		if (guild == null) {
			return false;
		}

		if (otherName == null) {
			otherName = "";
		}

		if (des == null) {
			des = "";
		}

		GuildLogInfo item = new GuildLogInfo();
		item.setType(type);
		item.setMyName(player.getTableUser().getUserName());
		item.setOtherName(otherName);
		item.setCreateTime(System.currentTimeMillis());
		item.setDes(des);
		return addItem(item);

	}

	public boolean addItem(GuildLogInfo itemTmp) {
		writeLock.lock();
		boolean success = false;
		try {
			int applyLimit = 300;
			if (itemStore.getSize() >= applyLimit) {
				removeLastLog();
			}
			success = itemStore.addItem(itemTmp);
			if (success) {
				guildMap.put(itemTmp.getId(), itemTmp);
			}
		} finally {
			writeLock.unlock();
		}

		return success;
	}

	private void removeLastLog() {
		List<GuildLogInfo> applyList = getItemList();
		Collections.sort(applyList, new Comparator<GuildLogInfo>() {

			@Override
			public int compare(GuildLogInfo source, GuildLogInfo target) {

				return (source.getCreateTime() - target.getCreateTime()) > 0 ? -1 : 1;
			}
		});
		GuildLogInfo last = applyList.get(applyList.size() - 1);
		itemStore.removeItem(last.getId());

	}

	public void flush() {
		itemStore.flush();
	}

}