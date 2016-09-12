package com.playerdata;

import java.util.ArrayList;
import java.util.List;

import com.rwbase.common.enu.ESex;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.item.PlayerInitialItemCfgDAO;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.item.pojo.PlayerInitialItemCfg;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwproto.ItemBagProtos.EItemAttributeType;

public class PlayerFreshHelper {

	/**
	 * <pre>
	 * <b>注意有潜规则</b>
	 * 创建角色添加初始物品，一定要把<b>【角色穿戴法宝】</b>这一步放在最前边
	 * 这跟现在服务器的穿戴法宝的机制有关，具体也不是三两句话说的清，记住这句就行了
	 * </pre>
	 * 
	 * @param player
	 * @param user
	 * @return
	 */
	public static boolean initFreshPlayer(Player player) {
		return initFreshPlayer(player, null);
	}

	/**
	 * <pre>
	 * <b>注意有潜规则</b>
	 * 创建角色添加初始物品，一定要把<b>【角色穿戴法宝】</b>这一步放在最前边
	 * 这跟现在服务器的穿戴法宝的机制有关，具体也不是三两句话说的清，记住这句就行了
	 * </pre>
	 * 
	 * @param player
	 * @param user
	 * @return
	 */
	public static boolean initFreshPlayer(Player player, RoleCfg playerCfg) {
		// 初始主角英雄
		int sex = player.getSex();
		String roleId = sex == ESex.Women.getOrder() ? "101001_1" : "100001_1";
		if (playerCfg == null) {
			playerCfg = RoleCfgDAO.getInstance().getConfig(roleId);
		}
		player.getHeroMgr().addMainRoleHero(player, playerCfg);
		// // 初始佣兵
		// initHeros(player);
		// // 初始化角色法宝
		// initMagic(player);
		// // 初始化角色初始物品
		// initItems(player);
		return true;
	}

	/**
	 * 初始化角色的时候奖励的物品
	 * 
	 * @param player
	 */
	public static void initCreateItem(Player player) {
		PlayerInitialItemCfg uniqueCfg = PlayerInitialItemCfgDAO.getUniqueCfg();
		if (uniqueCfg != null) {
			// 获取穿戴的法宝
			int[] initMagicInfo = uniqueCfg.getInitMagicInfo();
			int cfgId = initMagicInfo[0];
			player.getItemBagMgr().addItem(cfgId, 1);// 增加一个法宝
			ItemData item = player.getItemBagMgr().getItemListByCfgId(cfgId).get(0);
			int level = initMagicInfo[1];
			if (level > 1) {
				item.setExtendAttr(EItemAttributeType.Magic_Level_VALUE, String.valueOf(level));
				player.getItemBagMgr().updateItem(item);
			}
			player.getMagicMgr().wearMagic(item.getId());

			// 获取初始奖励的物品
			int[][] initItemArr = uniqueCfg.getInitItemArr();
			int len = 0;
			if (initItemArr != null && (len = initItemArr.length) > 0) {
				List<ItemInfo> list = new ArrayList<ItemInfo>(len);
				for (int i = 0; i < len; i++) {
					int[] rewardInfo = initItemArr[i];
//					player.getItemBagMgr().addItem(rewardInfo[0], rewardInfo[1]);
					list.add(new ItemInfo(rewardInfo[0], rewardInfo[1]));
				}
				player.getItemBagMgr().addItem(list);
			}

			// 初始化佣兵
			String[] initHeroArr = uniqueCfg.getInitHeroArr();
			if (initHeroArr != null) {
				for (int i = 0, len0 = initHeroArr.length; i < len0; i++) {
//					player.getHeroMgr().addHero(initHeroArr[i]);
					player.getHeroMgr().addHero(player, initHeroArr[i]);
				}
			}
		}
	}
	// private static void initMagic(Player player) {
	// int cfgId = 602101;
	// player.getItemBagMgr().addItem(cfgId, 1);// 增加一个法宝
	// ItemData item = player.getItemBagMgr().getItemListByCfgId(cfgId).get(0);
	// player.getMagicMgr().wearMagic(item.getId());
	// }
	//
	// private static void initItems(Player player) {
	// List<PlayerInitialItemCfg> list =
	// ItemCfgHelper.getPlayerInitialItemAllCfg();
	// if (list != null && list.size() > 0) {
	// for (PlayerInitialItemCfg playerInitialItem : list) {
	// player.getItemBagMgr().addItem(playerInitialItem.getId(),
	// playerInitialItem.getCount());
	// }
	// }
	// }
	//
	// private static void initHeros(Player player) {
	// // HeroMgr heroMgr = player.getHeroMgr();
	// // String[] arrHeroIds = { "202001" };// 需根据配置改变
	// // for (int i = 0; i < arrHeroIds.length; i++) {
	// // RoleCfg Herocfg = (RoleCfg)
	// RoleCfgDAO.getInstance().getCfgByModeID(arrHeroIds[i]);
	// // heroMgr.addHeroWhenCreatUser(Herocfg.getRoleId());
	// // }
	// }
}
