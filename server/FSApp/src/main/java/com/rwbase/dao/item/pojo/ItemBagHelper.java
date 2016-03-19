package com.rwbase.dao.item.pojo;

import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.util.StringUtils;

import com.playerdata.Player;
import com.rwproto.ItemBagProtos.EItemBagEventType;
import com.rwproto.ItemBagProtos.MsgItemBagResponse;
import com.rwproto.ItemBagProtos.TagItemAttriData;
import com.rwproto.ItemBagProtos.TagItemData;
import com.rwproto.MsgDef.Command;

/*
 * @author HC
 * @date 2015年10月8日 下午2:49:05
 * @Description 
 */
public class ItemBagHelper {
	/**
	 * 发送背包的修改数据
	 * 
	 * @param player 角色
	 * @param syncItemMap 背包中修改的内容，或者是背包中的所有物品列表
	 */
	public static void sendItemInfo(Player player, Map<String, ItemData> syncItemMap) {
		MsgItemBagResponse.Builder response = MsgItemBagResponse.newBuilder();
		for (Entry<String, ItemData> entry : syncItemMap.entrySet()) {
			String soltId = entry.getKey();
			ItemData item = entry.getValue();

			TagItemData.Builder tagItem = TagItemData.newBuilder();
			tagItem.setDbId(soltId);
			tagItem.setModelId(item.getModelId());
			tagItem.setCount(item.getCount());

			Enumeration<Integer> keys = item.getEnumerationKeys();
			while (keys.hasMoreElements()) {
				Integer attrId = keys.nextElement();
				if (StringUtils.isEmpty(attrId)) {
					continue;
				}

				TagItemAttriData.Builder attrData = TagItemAttriData.newBuilder();
				attrData.setAttrId(attrId.intValue());
				attrData.setAttValue(item.getExtendAttr(attrId));
				tagItem.addExtendAttr(attrData);
			}

			response.addItemSyncDatas(tagItem.build());
		}

		response.setEventType(EItemBagEventType.ItemBag_Sync);
		player.SendMsg(Command.MSG_ItemBag, response.build().toByteString());
	}
}