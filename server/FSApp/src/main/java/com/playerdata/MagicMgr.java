package com.playerdata;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.common.Action;
import com.common.RandomMgr;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.magic.MagicHolder;
import com.rwproto.ItemBagProtos.EItemAttributeType;
import com.rwproto.ItemBagProtos.EItemTypeDef;

/*
 * @author HC
 * @date 2015年10月15日 下午5:43:08
 * @Description 法宝Mgr
 */
public class MagicMgr extends RandomMgr {

	private MagicHolder holder;// 法宝Holder
	private Player player;// 角色

	public void init(Player player) {
		this.player = player;
		holder = new MagicHolder(player.getUserId());
	}

	public void regChangeCallBack(Action callBack) {
		holder.regChangeCallBack(callBack);
	}

	/**
	 * 获取法宝信息
	 * 
	 * @return
	 */
	public ItemData getMagic() {
		return holder.getUserMagic(this.player);
	}

	/**
	 * 穿戴法宝
	 * 
	 * @param magicId
	 * @return
	 */
	public boolean wearMagic(String magicId) {
		ItemBagMgr itemBagMgr = player.getItemBagMgr();
		ItemData item = itemBagMgr.findBySlotId(magicId);
		if (item == null) {
			return false;
		}

		if (item.getType() != EItemTypeDef.Magic) {// 不是法宝
			return false;
		}

		String magicState = item.getExtendAttr(EItemAttributeType.Magic_State_VALUE);
		if (!StringUtils.isEmpty(magicState) && "1".equalsIgnoreCase(magicState)) {// 已经穿戴在身上了
			return false;
		}

		List<ItemData> updateItems = new ArrayList<ItemData>(2);
		// ItemData oldItem = itemBagMgr.findBySlotId(holder.getMagicId());
		ItemData oldItem = getMagic();
		if (oldItem != null) {// 旧法宝数据设定
		// oldItem = itemBagMgr.findBySlotId(oldItem.getId());// 获取数据库中的道具
			oldItem.setExtendAttr(EItemAttributeType.Magic_State_VALUE, "0");// 设置新的状态
			updateItems.add(oldItem);
			itemBagMgr.updateItem(oldItem);// 刷新
			// itemBagMgr.addSyncItemData(oldItem);// 旧法宝状态修改
		}

		item.setExtendAttr(EItemAttributeType.Magic_State_VALUE, "1");// 设置新法宝的状态
		// itemBagMgr.addSyncItemData(item);// 新法宝状态修改
		updateItems.add(item);
		itemBagMgr.updateItem(item);// 刷新
		// 刷新数据
		itemBagMgr.syncItemData(updateItems);
		// 替换法宝
		// replaceMagic(magicId);
		holder.replaceMagic(this.player, magicId);
		return true;
	}

	/**
	 * 更新法宝
	 */
	public void updateMagic() {
		holder.updateMagic(this.player);
	}

	/**
	 * 推送所有法宝信息
	 * 
	 * @param version
	 */
	public void syncAllMagicData(int version) {
		holder.syncAllData(this.player, version);
	}

	/**
	 * 保存数据
	 */
	public boolean save() {
		holder.flush();
		return true;
	}
}