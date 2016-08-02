package com.playerdata.hero.core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.hero.HeroBaseInfo;
import com.playerdata.hero.IHeroCallbackAction;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class FSHeroHolder {

	private static final FSHeroHolder _INSTANCE = new FSHeroHolder();
	private static final List<String> _namesOfBaseInfoSyncFields;
	private static final com.rwproto.DataSynProtos.eSynType _syn_type_base_info = com.rwproto.DataSynProtos.eSynType.ROLE_BASE_ITEM;
	private static final com.rwproto.DataSynProtos.eSynType _syn_type_user_heros = com.rwproto.DataSynProtos.eSynType.USER_HEROS;
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
	
	private List<IHeroCallbackAction> _baseInfoChangeCallbackList = new ArrayList<IHeroCallbackAction>();
	
	public static FSHeroHolder getInstance() {
		return _INSTANCE;
	}
	
	private void notifyBaseInfoChange(FSHero hero) {
		for(IHeroCallbackAction action : _baseInfoChangeCallbackList) {
			action.doAction(hero);
		}
	}
	
	public void regBaseInfoChangeCallback(IHeroCallbackAction action) {
		_baseInfoChangeCallbackList.add(action);
	}
	
	public void syncAttributes(FSHero hero, int version) {
		Player player = PlayerMgr.getInstance().find(hero.getOwnerUserId());
		ClientDataSynMgr.synData(player, hero.getAttrMgr().getRoleAttrData(), eSynType.ROLE_ATTR_ITEM, eSynOpType.UPDATE_SINGLE, version);
	}
	
	public void synBaseInfo(Player player, FSHero hero) {
		FSHeroDAO.getInstance().notifyUpdate(hero.getOwnerUserId(), hero.getId());
		ClientDataSynMgr.synDataFiled(player, hero, _syn_type_base_info, eSynOpType.UPDATE_SINGLE, _namesOfBaseInfoSyncFields);
		this.notifyBaseInfoChange(hero);
	}
	
	public void syncUserHeros(Player player, List<String> heroIds) {
		FSTableUserHero userHero = new FSTableUserHero(player.getUserId(), heroIds);
		ClientDataSynMgr.updateData(player, userHero, _syn_type_user_heros, eSynOpType.UPDATE_SINGLE);
	}
}
