package com.rw.service.Privilege.datamodel;

import com.rwbase.common.enu.EPrivilegeDef;
import com.rwbase.dao.vip.PrivilegeCfgDAO;
import com.rwproto.PrivilegeProtos.ArenaPrivilegeNames;
import com.rwproto.PrivilegeProtos.CopyPrivilegeNames;
import com.rwproto.PrivilegeProtos.GeneralPrivilegeNames;
import com.rwproto.PrivilegeProtos.GroupPrivilegeNames;
import com.rwproto.PrivilegeProtos.HeroPrivilegeNames;
import com.rwproto.PrivilegeProtos.LoginPrivilegeNames;
import com.rwproto.PrivilegeProtos.PeakArenaPrivilegeNames;
import com.rwproto.PrivilegeProtos.PvePrivilegeNames;
import com.rwproto.PrivilegeProtos.StorePrivilegeNames;

public class VipPrivilegeHelper {
	private static VipPrivilegeHelper instance = new VipPrivilegeHelper();
	public static VipPrivilegeHelper getShareInstance(){
		if (instance == null){
			instance = new VipPrivilegeHelper();
		}
		return instance;
	}
	
	public int extractVipLevel(String chargeTy) {
		if (chargeTy == null || !chargeTy.startsWith(ChargeTypePriority.vipPrefix)) return -1;
		return Integer.parseInt(chargeTy.substring(chargeTy.indexOf(ChargeTypePriority.vipPrefix)+ChargeTypePriority.vipPrefix.length()));
	}

	public int getBestMatchCharge(String[] sources,int currentVip) {
		int result = -1;
		if (sources == null) return result;
		int bestMatchVipLevel = -1;
		for(int i =0;i<sources.length;i++){
			String chargeSource = sources[i];
			if (chargeSource == null) continue;
			int index = chargeSource.indexOf(ChargeTypePriority.vipPrefix);
			if (index!=-1){
				String vipLevelStr = chargeSource.substring(ChargeTypePriority.vipPrefix.length() + index);
				int lvl = -1;
				try{
					lvl = Integer.parseInt(vipLevelStr);
				}catch(Exception ex){
					System.out.println("无法解释VIP等级:"+chargeSource);
				}
				//取比当前vip等级要低（或者相等），并且比已有最佳匹配值要大的等级
				if (lvl > bestMatchVipLevel && lvl <= currentVip){
					bestMatchVipLevel = lvl;
					result = i;
				}
			}
		}
		return result;
	}
	
	public boolean reachChargeLevel(String chargeType,int currentVip) {
		int index = chargeType.indexOf(ChargeTypePriority.vipPrefix);
		if (index != -1) {
			String vipLevelStr = chargeType.substring(ChargeTypePriority.vipPrefix.length()+index);
			int lvl = -1;
			try {
				lvl = Integer.parseInt(vipLevelStr);
				return lvl <= currentVip;
			} catch (Exception ex) {
			}
		}
		return false;
	}

	public int getDef(int vip,EPrivilegeDef def){
		// 参考 PrivilegeCfgDAO
		PrivilegeConfigHelper helper = PrivilegeConfigHelper.getInstance();
		int value = PrivilegeCfgDAO.getInstance().getDef(vip, def);
		Enum<?> pname = null;
		switch (def) {
		case GOLD_MOPUP_OPEN:
			//没有对应特权控制点,
			value = 1;
			break;
		case SKILL_POINT_COUNT:
			pname = HeroPrivilegeNames.skillThreshold;
			break;
		case BUY_SKILL_POINT_OPEN:
			pname = HeroPrivilegeNames.isAllowBuySkillPoint;
			break;
		case ONEKEY_ADD_SPIRIT_OPEN:
			pname = HeroPrivilegeNames.isAllowAttach;
			break;
		case POWER_COUNT:
			pname = LoginPrivilegeNames.buyPowerCount;
			break;
		case MONEY_COUNT:
			pname = LoginPrivilegeNames.useCoinTransCount;
			break;
		case MOPUP_COUNT:
			pname = LoginPrivilegeNames.getSweepTicketNum;
			break;
		case SPORT_BUY_COUNT:
			pname = ArenaPrivilegeNames.arenaMaxCount;
			break;
		case ARENA_RESET_CD_OPEN:
			pname = ArenaPrivilegeNames.isAllowResetArena;
			break;
		case ARENA_RESET_TIMES:
			pname = ArenaPrivilegeNames.arenaMaxCount;
			break;
		case PEAK_SPORT_RESET_CD_OPEN:
			pname = PeakArenaPrivilegeNames.isAllowResetPeak;
			break;
		case PEAK_ARENA_RESET_TIMES:
			pname = PeakArenaPrivilegeNames.peakMaxCount;
			break;
		case COPY_COUNT:
			pname = CopyPrivilegeNames.eliteResetCnt;
			break;
		case ONEKEY_MOPUP_TEN_OPEN:
			pname = CopyPrivilegeNames.isAllowTenSweep;
			break;
		case EVER_SPECIAL_STORE1_OPEN:
			pname = StorePrivilegeNames.isOpenBlackmarketStore;
			break;
		case EVER_SPECIAL_STORE_2OPEN:
			pname = StorePrivilegeNames.isOpenMysteryStore;
			break;
		case THIRD_CHESTS_OPEN:
			pname = GeneralPrivilegeNames.isAllowSoulBox;
			break;
		case FASHION_BUY_OPEN:
			pname = GeneralPrivilegeNames.isAllowBuyFashion;
			break;
		case RESIGN_OPEN:
			pname = GeneralPrivilegeNames.isAllowReplenish;
			break;
		case SECRET_COPY_COUNT:
			pname = GroupPrivilegeNames.mysteryChallengeCount;
			break;
		case TRIAL1_COPY_RESET_TIMES:
			pname = PvePrivilegeNames.treasureResetCnt;
			break;
		case TRIAL2_COPY_RESET_TIMES:
			pname = PvePrivilegeNames.expResetCnt;
			break;
		case WARFARE_COPY_RESET_TIMES:
			pname = PvePrivilegeNames.warfareResetCnt;
			break;
		case COPY_CELESTAL:
			pname = PvePrivilegeNames.survivalResetCnt;
			break;
		case BATTLE_TOWER_TIMES:
			pname = PvePrivilegeNames.maxResetCount;
			break;
		case EXPEDITION_COUNT:
		case TOWER_RESET_TIMES:
			pname = PvePrivilegeNames.arrayMaxResetCnt;
			break;
		default:
			break;
		}
		
		if (pname != null){
			String[] sources = helper.getChargeSources(pname);
			if (sources != null){
				int index = getBestMatchCharge(sources ,vip);
				if (0 <= index && index < sources.length){
					Integer t = helper.getPrivilegeCast2Int(pname, sources[index]);
					if (t != null){
						value = t;
					}
				}
			}
		}
		return value;
	}
}
