package com.rw.service.item.useeffect;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.readonly.ItemDataIF;
import com.rwproto.ItemBagProtos.MsgItemBagResponse;

/*
 * @author HC
 * @date 2016年5月18日 上午10:29:33
 * @Description 使用接口
 */
public interface IItemUseEffect {
	/**
	 * 使用道具的效果
	 * 
	 * @param player 角色
	 * @param itemData 道具的Id
	 * @param useCount 使用数量
	 * @param rsp
	 * @return
	 */
	public ByteString useItem(Player player, ItemDataIF itemData, int useCount, MsgItemBagResponse.Builder rsp);
}