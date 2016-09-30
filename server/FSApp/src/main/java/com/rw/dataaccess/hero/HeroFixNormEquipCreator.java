package com.rw.dataaccess.hero;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.playerdata.fixEquip.FixEquipHelper;
import com.playerdata.fixEquip.cfg.RoleFixEquipCfg;
import com.playerdata.fixEquip.cfg.RoleFixEquipCfgDAO;
import com.playerdata.fixEquip.norm.data.FixNormEquipDataItem;
import com.rw.fsutil.cacheDao.attachment.PlayerExtPropertyStore;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class HeroFixNormEquipCreator implements HeroExtPropertyCreator<FixNormEquipDataItem>{
	
	final private Comparator<FixNormEquipDataItem> comparator = new Comparator<FixNormEquipDataItem>() {

		@Override
		public int compare(FixNormEquipDataItem source, FixNormEquipDataItem target) {
			return source.getSlot() - target.getSlot();
		}

	};


	@Override
	public eOpenLevelType getOpenLevelType() {
		//do nothing
		return null;
	}

	@Override
	public boolean validateOpenTime(long currentTimeMillis) {
		return true;
	}

	@Override
	public List<FixNormEquipDataItem> firstCreate(HeroCreateParam params) {
		List<FixNormEquipDataItem> equipItemList = new ArrayList<FixNormEquipDataItem>();

		int modelId = params.getModelId();
		RoleFixEquipCfg roleFixEquipCfg = RoleFixEquipCfgDAO.getInstance().getCfgById(String.valueOf(modelId));
		if (roleFixEquipCfg == null) {			
			throw(new RuntimeException("配置 roleFixEquipCfg 为空, modelId:"+modelId));
		}

		int slot = 0;
		for (String cfgId : roleFixEquipCfg.getNormCfgIdList()) {

			String ownerId = params.getHeroId();
			Integer id = FixEquipHelper.getNormItemId(ownerId , cfgId);

			FixNormEquipDataItem FixNormEquipDataItem = new FixNormEquipDataItem();
			FixNormEquipDataItem.setId(id);
			FixNormEquipDataItem.setCfgId(cfgId);
			FixNormEquipDataItem.setOwnerId(ownerId);
			FixNormEquipDataItem.setQuality(0);
			FixNormEquipDataItem.setLevel(1);
			FixNormEquipDataItem.setStar(0);
			FixNormEquipDataItem.setSlot(slot);

			equipItemList.add(FixNormEquipDataItem);
			slot++;
		}

		Collections.sort(equipItemList, comparator);
		
		return equipItemList;
	}

	@Override
	public List<FixNormEquipDataItem> checkAndCreate(PlayerExtPropertyStore<FixNormEquipDataItem> store,HeroCreateParam params) {
		//do nothing
		return null;
	}


}
