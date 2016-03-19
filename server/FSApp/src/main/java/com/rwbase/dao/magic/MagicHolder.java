package com.rwbase.dao.magic;

import java.util.ArrayList;
import java.util.List;

import com.common.Action;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rwbase.common.MapItemStoreFactory;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

/*
 * @author HC
 * @date 2015年10月15日 下午5:22:17
 * @Description 
 */
public class MagicHolder {
	private String userId;// 角色Id
	// private int version;// 版本
	private static final eSynType syncType = eSynType.USER_MAGIC;

	public MagicHolder(String userId) {
		this.userId = userId;
	}

	/**
	 * 获取法宝
	 * 
	 * @param player 角色
	 * @return
	 */
	public ItemData getUserMagic(Player player) {
		Magic magic = getItemStore().getItem(userId);
		if (magic == null) {
			return null;
		}

		return switchMagic2ItemData(player, magic.getMagicId());
	}

	/**
	 * 替换新的法宝数据
	 * 
	 * @param player 角色
	 * @param magicId 替换的新的法宝Id
	 */
	public void replaceMagic(Player player, String magicId) {
		MapItemStore<Magic> magicInfo = getItemStore();
		Magic magic = magicInfo.getItem(userId);
		eSynOpType type = eSynOpType.ADD_SINGLE;
		if (magic == null) {
			magic = new Magic();
			magic.setId(userId);
			magic.setMagicId(magicId);
			// 增加新的
			magicInfo.addItem(magic);
		} else {
			magic.setMagicId(magicId);
			type = eSynOpType.UPDATE_SINGLE;
			// 设置更新
			magicInfo.updateItem(magic);
		}

		// 推送法宝处理
		ItemData magicItem = switchMagic2ItemData(player, magicId);
		if (magicItem != null) {
			ClientDataSynMgr.updateData(player, magicItem, syncType, type);
			notifyChange();
		}
	}

	/**
	 * 更新法宝数据
	 * 
	 * @param player
	 */
	public void updateMagic(Player player) {
		MapItemStore<Magic> magicInfo = getItemStore();
		Magic magic = magicInfo.getItem(userId);
		if (magic == null) {
			return;
		}

		// 设置更新
		magicInfo.updateItem(magic);

		// 推送法宝处理
		ItemData magicItem = switchMagic2ItemData(player, magic.getMagicId());
		if (magicItem != null) {
			ClientDataSynMgr.updateData(player, magicItem, syncType, eSynOpType.UPDATE_SINGLE);
			notifyChange();
		}
	}

	/**
	 * 获取法宝的信息
	 * 
	 * @param player 角色
	 * @param magicId 法宝Id
	 * @return
	 */
	private ItemData switchMagic2ItemData(Player player, String magicId) {
		// ItemData magicData = player.getItemBagMgr().findBySlotId(magicId);
		// if (magicData == null) {
		// return null;
		// }
		//
		// return new ItemData(magicData);
		return player.getItemBagMgr().findBySlotId(magicId);
	}

	/**
	 * 发送所有的数据
	 * 
	 * @param player
	 * @param version
	 */
	public void syncAllData(Player player, int version) {
		ItemData heroMagic = getUserMagic(player);
		if (heroMagic == null) {
			return;
		}

		boolean needSyn = true;
		if (needSyn) {
			ClientDataSynMgr.synData(player, heroMagic, syncType, eSynOpType.ADD_SINGLE);
		}
	}

	/**
	 * 刷新数据
	 */
	public void flush() {
		getItemStore().flush();
	}

	private List<Action> callbackList = new ArrayList<Action>();

	public void regChangeCallBack(Action callBack) {
		callbackList.add(callBack);
	}

	private void notifyChange() {
		for (Action action : callbackList) {
			action.doAction();
		}
	}
	
	private MapItemStore<Magic> getItemStore(){
		MapItemStoreCache<Magic> cache = MapItemStoreFactory.getMagicCache();
		return cache.getMapItemStore(userId, Magic.class);
	}
}