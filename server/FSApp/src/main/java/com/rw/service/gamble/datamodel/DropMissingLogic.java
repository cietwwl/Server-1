package com.rw.service.gamble.datamodel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.common.HPCUtil;
import com.common.Utils;
import com.playerdata.EquipMgr;
import com.playerdata.Hero;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.hero.core.FSHeroMgr;
import com.rw.fsutil.common.IReadOnlyPair;
import com.rw.fsutil.common.Pair;
import com.rwbase.dao.equipment.EquipItem;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.role.RoleQualityCfgDAO;

public class DropMissingLogic {
	private static DropMissingLogic instance = new DropMissingLogic();

	public static DropMissingLogic getInstance() {
		return instance;
	}
	
	private static final IDropMissingRecord _defaultRecord = new DropMissingRecordDefaultImpl();

	private static Comparator<Hero> comparator = new Comparator<Hero>() {
		@Override
		public int compare(Hero o1, Hero o2) {
			RoleQualityCfgDAO helper = RoleQualityCfgDAO.getInstance();
			if (o1 != null && o2 != null) {
				int q1 = helper.getQuality(o1.getQualityId());
				int q2 = helper.getQuality(o1.getQualityId());
				return q1 - q2;
			}
			return 0;
		}
	};

	/**
	 * 假设player,cfg非空
	 * 
	 * @param player
	 * @param cfg
	 * @return
	 */
	public String searchMissingItem(Player player, DropMissingCfg cfg) {
//		RoleQualityCfgDAO qualityHelper = RoleQualityCfgDAO.getInstance();
//		Random r = HPCUtil.getRandom();
//
//		String userId = player.getUserId();
//		List<Hero> heroList = FSHeroMgr.getInstance().getAllHeros(player, comparator);
//		for (int i = 0; i < heroList.size(); i++) {
//			Hero hero = heroList.get(i);
//			List<Integer> equipCandidates = searchOneHero(hero, userId, qualityHelper, cfg);
//			int max = equipCandidates.size();
//			if (max <= 0) {
//				continue;
//			}
//			return String.valueOf(equipCandidates.get(r.nextInt(max)));
//		}
//		return null;
		return this.searchMissingItem(player, cfg, _defaultRecord);
	}
	
	/**
	 * 假设player,cfg非空
	 * 
	 * @param player
	 * @param cfg
	 * @return
	 */
	public String searchMissingItem(Player player, DropMissingCfg cfg, IDropMissingRecord dropMissingRecord) {
		RoleQualityCfgDAO qualityHelper = RoleQualityCfgDAO.getInstance();
		Random r = HPCUtil.getRandom();

		String userId = player.getUserId();
		List<Hero> heroList = FSHeroMgr.getInstance().getAllHeros(player, comparator);
		List<Integer> allEquipCfgIds = new ArrayList<Integer>();
		for (int i = 0; i < heroList.size(); i++) {
			Hero hero = heroList.get(i);
			IReadOnlyPair<Boolean, List<Integer>> dropResult = searchOneHero(hero, userId, qualityHelper, cfg, dropMissingRecord);
			if (dropResult.getT1()) {
				allEquipCfgIds.addAll(dropResult.getT2());
				continue;
			} else {
				List<Integer> equipCandidates = dropResult.getT2();
				Integer dropItemId = equipCandidates.get(r.nextInt(equipCandidates.size()));
				dropMissingRecord.addToRecord(hero.getId(), dropItemId);
				return String.valueOf(dropItemId);
			}
		}
		return String.valueOf(allEquipCfgIds.get(r.nextInt(allEquipCfgIds.size())));
	}

	/**
	 * 搜索一个玩家缺少的装备，需要考虑配置的排除列表
	 * 
	 * @param hero
	 * @param itemBagMgr
	 * @param qualityHelper
	 * @param cfg
	 * @return {@link Pair#getT1()}表示是否有missingItem，
	 */
//	private List<Integer> searchOneHero(Hero hero, String userId, RoleQualityCfgDAO qualityHelper, DropMissingCfg cfg) {
	private IReadOnlyPair<Boolean, List<Integer>> searchOneHero(Hero hero, String userId, RoleQualityCfgDAO qualityHelper, DropMissingCfg cfg, IDropMissingRecord recorder) {
		List<Integer> result = new ArrayList<Integer>();
		Pair<Boolean, List<Integer>> pairResult = Pair.Create(false, result);
		if (hero == null) {
			return pairResult;
		}
		String qualityId = hero.getQualityId();
		int quality = qualityHelper.getQuality(qualityId);
		quality = cfg.checkQualityRange(quality);
		// qualityId = hero.getModeId()+"_"+(quality+1);
		qualityId = Utils.computeQualityId(hero.getModeId(), (quality + 1));
		// 配置的装备列表
		ArrayList<Integer> equipCfgList = qualityHelper.getEquipList(qualityId, cfg.getExcludeEquipPosition());
		// 已装备列表
		ArrayList<Integer> wearEquipIdList = new ArrayList<Integer>();

		EquipMgr equipMgr = hero.getEquipMgr();
		if (equipMgr != null) {
			List<EquipItem> hasEquipList = equipMgr.getEquipList(hero.getUUId());
			for (EquipItem item : hasEquipList) {
				if (item != null) {
					wearEquipIdList.add(item.getModelId());
				}
			}
		}

		String heroId = hero.getId();
		for (Integer equipCfgId : equipCfgList) {// 每个位置对应的装备ID
			if (wearEquipIdList.contains(equipCfgId)) {// 已穿戴
				continue;
			}
			// 搜索背包
//			if (!isBagContain(userId, equipCfgId)) {
			if (!isBagContain(userId, equipCfgId) && !recorder.containsRecord(heroId, equipCfgId)) {
				result.add(equipCfgId);
			}
		}

		// 装备空缺组容错规则：如果没有空缺，则用非空缺的位置补上
		if (result.size() <= 0) {
			result.addAll(equipCfgList);
			pairResult.setT1(true);
		}

		return pairResult;
	}

	private boolean isBagContain(String userId, Integer equipCfgId) {
		List<ItemData> itemDataList = ItemBagMgr.getInstance().getItemListByCfgId(userId, equipCfgId);
		if (itemDataList == null || itemDataList.isEmpty()) {
			return false;
		}
		return true;
	}
}
