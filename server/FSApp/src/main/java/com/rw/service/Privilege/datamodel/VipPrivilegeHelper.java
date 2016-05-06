package com.rw.service.Privilege.datamodel;

import com.rwbase.common.enu.EPrivilegeDef;
import com.rwbase.dao.vip.PrivilegeCfgDAO;

public class VipPrivilegeHelper {
	private static VipPrivilegeHelper instance = new VipPrivilegeHelper();
	public static VipPrivilegeHelper getShareInstance(){
		if (instance == null){
			instance = new VipPrivilegeHelper();
		}
		return instance;
	}
	
	public int getDef(int vip,EPrivilegeDef def){
		//TODO 参考 PrivilegeCfgDAO
		vip = getBestMatchVip(vip);
		int value = PrivilegeCfgDAO.getInstance().getDef(vip, def);
		switch (def) {
		case POWER_COUNT:
			break;
		case MONEY_COUNT:
			break;
		case COPY_COUNT:
			break;
		case MOPUP_COUNT:
			break;
		case SPORT_BUY_COUNT:
			break;
		case SKILL_POINT_COUNT:
			break;
		case GOLD_MOPUP_OPEN:
			break;
		case BUY_SKILL_POINT_OPEN:
			break;
		case PEAK_SPORT_RESET_CD_OPEN:
			break;
		case ONEKEY_MOPUP_TEN_OPEN:
			break;
		case ONEKEY_ADD_SPIRIT_OPEN:
			break;
		case EVER_SPECIAL_STORE1_OPEN:
			break;	
		case EVER_SPECIAL_STORE_2OPEN:
			break;
		case THIRD_CHESTS_OPEN:
			break;
		case ARENA_RESET_CD_OPEN:
			break;
		case EXPEDITION_COUNT:
			break;
		case FASHION_BUY_OPEN:
			break;
		case PEAK_ARENA_RESET_TIMES:
			break;
		case SECRET_COPY_COUNT:
			break;
		case ARENA_RESET_TIMES:
			break;
		case TRIAL1_COPY_RESET_TIMES:
			break;
		case TRIAL2_COPY_RESET_TIMES:
			break;
		case WARFARE_COPY_RESET_TIMES:
			break;
		case RESIGN_OPEN:
			break;
		case COPY_CELESTAL:
			break;
		case TOWER_RESET_TIMES:
			
			break;
		case BATTLE_TOWER_TIMES:
			break;
		default:
			break;
		}
		return value;
	}

	private int getBestMatchVip(int vip) {
		// TODO Auto-generated method stub
		return vip;
	}
}
