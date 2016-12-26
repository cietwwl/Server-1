package com.rw.service.magicEquipFetter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.common.Action;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Hero;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.fixEquip.norm.data.FixNormEquipDataItem;
import com.rwbase.dao.fetters.FettersBM;
import com.rwbase.dao.fetters.FettersBM.SubConditionType;
import com.rwbase.dao.fetters.MagicEquipFetterDataHolder;
import com.rwbase.dao.fetters.pojo.cfg.MagicEquipConditionCfg;
import com.rwbase.dao.fetters.pojo.cfg.dao.FetterMagicEquipCfgDao;
import com.rwbase.dao.fetters.pojo.cfg.dao.MagicEquipConditionKey;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwproto.HeroFetterProto.HeroFetterType;
import com.rwproto.ItemBagProtos.EItemTypeDef;

import io.netty.util.collection.IntObjectHashMap;

/**
 * 法宝神器羁绊管理类
 * 
 * @author Alex
 *
 * 2016年7月18日 下午5:52:02
 */
public class MagicEquipFetterMgr {

	private MagicEquipFetterDataHolder holder;

	private final List<Action> actionListener = new ArrayList<Action>();

	public void init(Player player) {
		holder = new MagicEquipFetterDataHolder(player.getUserId());
	}

	public void loginNotify(Player player) {
		holder.synAllData(player, 0);
	}

	/**
	 * 检查角色数据
	 * 
	 * @param player
	 */
	public void checkPlayerData(Player player) {
		MagicEquipFetterLogic logic = MagicEquipFetterLogic.getInstance();
		logic.checkAndAddMagicFetter(player, false, holder);
		logic.checkAndAddEquipFetter(player, holder);
	}



	/**
	 * 法宝强化进阶通知
	 * 
	 * @param player
	 * @param itemData
	 */
	public void notifyMagicChange(Player player) {
		if (MagicEquipFetterLogic.getInstance().checkAndAddMagicFetter(player, true, holder)) {
			notifyListenerAction();
		}
	}

	/**
	 * 英雄变动通知
	 * 
	 * @param player
	 * @param hero
	 */
	public void notifyHeroChange(Player player, Hero hero) {
		if (hero.isMainRole()) {
			MagicEquipFetterLogic.getInstance().checkAndAddMagicFetter(player, false, holder);
		}
		MagicEquipFetterLogic.getInstance().checkOrAddTargetHeroEquipFetter(player, hero, false, holder);
		holder.synAllData(player, 0);
		notifyListenerAction();
	}

	/**
	 * 获取英雄的神器羁绊列表
	 * 
	 * @param modelId
	 */
	public List<Integer> getHeroFixEqiupFetter(int modelId) {
		return holder.getFixEquipFetterByModelID(modelId);
	}

	/**
	 * 获取法宝的羁绊列表
	 */
	public List<Integer> getMagicFetter() {
		return holder.getMagicFetters();
	}

	public void reChangeCallBack(Action action) {
		actionListener.add(action);
	}

	private void notifyListenerAction() {
		for (Action action : actionListener) {
			action.doAction();
		}
	}
}
