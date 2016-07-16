package com.rw.service.gamble.datamodel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.common.HPCUtil;
import com.playerdata.EquipMgr;
import com.playerdata.Hero;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.rwbase.dao.equipment.EquipItem;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.role.RoleQualityCfgDAO;

public class DropMissingLogic {
	private static DropMissingLogic instance = new DropMissingLogic();

	public static DropMissingLogic getInstance() {
		return instance;
	}
	
	private static Comparator<Hero> comparator = new Comparator<Hero>() {
		@Override
		public int compare(Hero o1, Hero o2) {
			RoleQualityCfgDAO helper = RoleQualityCfgDAO.getInstance();
			if (o1 != null && o2 != null){
				int q1 = helper.getQuality(o1.getQualityId());
				int q2 = helper.getQuality(o1.getQualityId());
				return q1 - q2;
			}
			return 0;
		}
	};

	/**
	 * 假设player,cfg非空
	 * @param player
	 * @param cfg
	 * @return
	 */
	public String searchMissingItem(Player player,DropMissingCfg cfg){
		RoleQualityCfgDAO qualityHelper = RoleQualityCfgDAO.getInstance();
		ItemBagMgr itemBagMgr = player.getItemBagMgr();
		Random r = HPCUtil.getRandom();
		
		List<Hero> heroList = player.getHeroMgr().getAllHeros(comparator);
		for (int i = 0; i < heroList.size(); i++) {
			Hero hero = heroList.get(i);
			List<Integer> equipCandidates = searchOneHero(hero,itemBagMgr,qualityHelper,cfg);
			int max = equipCandidates.size();
			if (max <= 0){
				continue;
			}
			return String.valueOf(equipCandidates.get(r.nextInt(max)));
		}
		return null;
	}
	
	/**
	 * 搜索一个玩家缺少的装备，需要考虑配置的排除列表
	 * @param hero
	 * @param itemBagMgr
	 * @param qualityHelper
	 * @param cfg
	 * @return
	 */
	private List<Integer> searchOneHero(Hero hero,ItemBagMgr itemBagMgr,
			RoleQualityCfgDAO qualityHelper,DropMissingCfg cfg){
		ArrayList<Integer> result=new ArrayList<Integer>();
		if (hero == null) {
			return result;
		}
		String qualityId = hero.getQualityId();
		int quality = qualityHelper.getQuality(qualityId);
		quality = cfg.checkQualityRange(quality);
		qualityId = hero.getModelId()+"_"+(quality+1);
			//配置的装备列表
			ArrayList<Integer> equipCfgList = qualityHelper.getEquipList(qualityId,cfg.getExcludeEquipPosition());
			//已装备列表
			ArrayList<Integer> wearEquipIdList = new ArrayList<Integer>();
			
			EquipMgr equipMgr = hero.getEquipMgr();
			if (equipMgr != null){
				List<EquipItem> hasEquipList = equipMgr.getEquipList(hero.getUUId());
				for (EquipItem item : hasEquipList) {
					if (item != null){
						wearEquipIdList.add(item.getModelId());
					}
				}
			}
			
			for (Integer equipCfgId : equipCfgList) {//每个位置对应的装备ID
				if (wearEquipIdList.contains(equipCfgId)){//已穿戴
					continue;
				}
				//搜索背包
				if (!isBagContain(itemBagMgr,equipCfgId)){
					result.add(equipCfgId);
				}
			}
			
			//装备空缺组容错规则：如果没有空缺，则用非空缺的位置补上
			if(result.size() <= 0){
				result = equipCfgList;
			}
		
		return result;
	}
	
	private boolean isBagContain(ItemBagMgr itemBagMgr,Integer equipCfgId){
		if (itemBagMgr == null){
			return false;
		}
		List<ItemData> itemDataList = itemBagMgr.getItemListByCfgId(equipCfgId);
		if (itemDataList == null || itemDataList.isEmpty()) {
			return false;
		}
		return true;
	}
}
