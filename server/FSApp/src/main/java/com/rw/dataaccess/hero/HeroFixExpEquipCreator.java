package com.rw.dataaccess.hero;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.playerdata.fixEquip.FixEquipHelper;
import com.playerdata.fixEquip.cfg.RoleFixEquipCfg;
import com.playerdata.fixEquip.cfg.RoleFixEquipCfgDAO;
import com.playerdata.fixEquip.exp.data.FixExpEquipDataItem;
import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyStore;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class HeroFixExpEquipCreator implements HeroExtPropertyCreator<FixExpEquipDataItem> {

	final private Comparator<FixExpEquipDataItem> comparator = new Comparator<FixExpEquipDataItem>() {

		@Override
		public int compare(FixExpEquipDataItem source, FixExpEquipDataItem target) {
			return source.getSlot() - target.getSlot();
		}

	};

	@Override
	public eOpenLevelType getOpenLevelType() {
		// do nothing
		return null;
	}

	@Override
	public List<FixExpEquipDataItem> firstCreate(HeroCreateParam params) {
		List<FixExpEquipDataItem> equipItemList = new ArrayList<FixExpEquipDataItem>();

		int modelId = params.getModelId();
		RoleFixEquipCfg roleFixEquipCfg = RoleFixEquipCfgDAO.getInstance().getCfgById(String.valueOf(modelId));
		if (roleFixEquipCfg == null) {
			throw (new RuntimeException("配置 roleFixEquipCfg 为空, modelId:" + modelId));
		}

		int slot = 4;
		for (String cfgId : roleFixEquipCfg.getExpCfgIdList()) {
			String ownerId = params.getHeroId();
			Integer id = FixEquipHelper.getExpItemId(ownerId, cfgId);

			FixExpEquipDataItem FixExpEquipDataItem = new FixExpEquipDataItem();
			FixExpEquipDataItem.setId(id);
			FixExpEquipDataItem.setCfgId(cfgId);
			FixExpEquipDataItem.setOwnerId(ownerId);
			FixExpEquipDataItem.setQuality(0);
			FixExpEquipDataItem.setLevel(1);
			FixExpEquipDataItem.setStar(0);
			FixExpEquipDataItem.setSlot(slot);

			equipItemList.add(FixExpEquipDataItem);
			slot++;

		}

		Collections.sort(equipItemList, comparator);
		return equipItemList;
	}

	@Override
	public List<FixExpEquipDataItem> checkAndCreate(PlayerExtPropertyStore<FixExpEquipDataItem> store, HeroCreateParam params) {
		// do nothing
		return null;
	}

	@Override
	public boolean requiredToPreload(HeroCreateParam params) {
		return true;
	}

}
