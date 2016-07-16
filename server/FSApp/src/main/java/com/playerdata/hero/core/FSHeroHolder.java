package com.playerdata.hero.core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.hero.HeroBaseInfo;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class FSHeroHolder {

	private static final FSHeroHolder _INSTANCE = new FSHeroHolder();
	private static final List<String> _namesOfBaseInfoSyncFields;
	private static final com.rwproto.DataSynProtos.eSynType _syn_type_base_info = com.rwproto.DataSynProtos.eSynType.ROLE_BASE_ITEM;
	
	static {
		Field[] allFields = FSHero.class.getDeclaredFields();
		Field tempField;
		List<String> fieldNames = new ArrayList<String>();
		for (int i = 0; i < allFields.length; i++) {
			tempField = allFields[i];
			if (tempField.isAnnotationPresent(HeroBaseInfo.class)) {
				fieldNames.add(tempField.getName());
			}
		}
		_namesOfBaseInfoSyncFields = Collections.unmodifiableList(fieldNames);
	}
	
	public static FSHeroHolder getInstance() {
		return _INSTANCE;
	}
	
	public void syncAttributes(FSHero hero, int version) {
		Player player = PlayerMgr.getInstance().find(hero.getOwnerUserId());
		ClientDataSynMgr.synData(player, hero.getAttr(), eSynType.ROLE_BASE_ITEM, eSynOpType.UPDATE_SINGLE, version);
	}
	
	public void updateBaseInfo(Player player, FSHero hero) {
		FSHeroDAO.getInstance().notifyUpdate(hero.getOwnerUserId(), hero.getId());
		ClientDataSynMgr.synDataFiled(player, this, _syn_type_base_info, _namesOfBaseInfoSyncFields);
	}
	
	public void updateUserHeros(Player player, List<String> heroIds) {
		
	}
}
