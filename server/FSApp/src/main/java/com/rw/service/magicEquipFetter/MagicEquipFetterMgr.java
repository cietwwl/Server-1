package com.rw.service.magicEquipFetter;

import java.util.List;

import com.playerdata.ItemCfgHelper;
import com.playerdata.Player;
import com.rwbase.dao.fetters.MagicEquipFetterDataHolder;
import com.rwbase.dao.item.pojo.ItemBaseCfg;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwproto.ItemBagProtos.EItemAttributeType;
import com.rwproto.ItemBagProtos.EItemTypeDef;

/**
 * 法宝神器羁绊管理类
 * @author Alex
 *
 * 2016年7月18日 下午5:52:02
 */
public class MagicEquipFetterMgr {

	
	
	private MagicEquipFetterDataHolder holder;
	

	
	public void init(Player player){
		holder = new MagicEquipFetterDataHolder(player.getUserId());
		//检查一下旧数据,如果已经开启了的羁绊而数据库里又没有的，要添加
		checkPlayerData(player);
	}



	/**
	 * 检查角色数据
	 * @param player
	 */
	private void checkPlayerData(Player player) {
		checkAndAddMagic(player);
		
	}



	/**
	 * 检查角色法宝数据
	 * @param player
	 */
	private void checkAndAddMagic(Player player) {
		List<ItemData> list = player.getItemBagMgr().getItemListByType(EItemTypeDef.Magic);
		for (ItemData item : list) {
			String lvlStr = item.getExtendAttr(EItemAttributeType.Magic_Level_VALUE);
			//找配置表里的对应法宝和等级是否有羁绊
			String starStr = item.getExtendAttr(EItemAttributeType.Magic_State_VALUE);
			
		}
		
	}
	
	
	
	
}
