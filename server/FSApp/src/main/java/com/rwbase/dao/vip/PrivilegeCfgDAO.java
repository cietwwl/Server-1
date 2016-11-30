package com.rwbase.dao.vip;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.common.enu.EPrivilegeDef;
import com.rwbase.dao.version.VersionConfigDAO;
import com.rwbase.dao.vip.pojo.PrivilegeCfg;

public class PrivilegeCfgDAO extends CfgCsvDao<PrivilegeCfg>{

	public static PrivilegeCfgDAO getInstance() {
		return SpringContextUtil.getBean(PrivilegeCfgDAO.class);
	}
	@Override
	public Map<String, PrivilegeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("vip/PrivilegeCfg.csv",PrivilegeCfg.class);
		return cfgCacheMap;
	}
	
	public PrivilegeCfg getCfg(int vipLevel){
    	PrivilegeCfg privilege = (PrivilegeCfg) getCfgById(String.valueOf(vipLevel));
    	return privilege;
    }
	
	public int getMaxVip(){
		List<PrivilegeCfg>  list = sortCfg();
		return list.get(list.size() - 1).getVip();
	}
	
	public int getMinVip(){
		List<PrivilegeCfg>  list = sortCfg();
		return list.get(0).getVip();
	}
	
	private List<PrivilegeCfg> sortCfg() {
		List<PrivilegeCfg> allCfgs = super.getAllCfg();
		Collections.sort(allCfgs,new Comparator<PrivilegeCfg>()
		{
			public int compare(PrivilegeCfg o1, PrivilegeCfg o2) {
				if(o1.getVip() < o2.getVip()) return -1;
				if(o1.getVip() > o2.getVip()) return 1;
				return 0;
			}});
		return allCfgs;
	}
	
	public int getDef(int vip,EPrivilegeDef def){
		int value = 0;
		PrivilegeCfg cfg = getCfg(vip);
		if (cfg == null)return value;
		switch (def) {
		case POWER_COUNT:
			value = cfg.getPowerCount();
			break;
		case MONEY_COUNT:
			value = cfg.getMoneyCount();
			break;
		case COPY_COUNT:
			value = cfg.getCopyCount();
			break;
		case MOPUP_COUNT:
			value = cfg.getMopupCount();
			break;
		case SPORT_BUY_COUNT:
			value = cfg.getSportBuyCount();
			break;
		case SKILL_POINT_COUNT:
			value = cfg.getSkillPointCount();
			break;
		case GOLD_MOPUP_OPEN:
			value = cfg.getGoldMopupOpen();
			break;
		case BUY_SKILL_POINT_OPEN:
			value = cfg.getBuySkillPointOpen();
			break;
		case PEAK_SPORT_RESET_CD_OPEN:
			value = cfg.getPeakSportResetCDOpen();
			break;
		case ONEKEY_MOPUP_TEN_OPEN:
			value = cfg.getOnekeyMopupTenOpen();
			break;
		case ONEKEY_ADD_SPIRIT_OPEN:
			value = cfg.getOnekeyAddSpiritOpen();
			break;
		case EVER_SPECIAL_STORE1_OPEN:
			value = cfg.getEverSpecialStore1Open();
			break;	
		case EVER_SPECIAL_STORE_2OPEN:
			value = cfg.getEverSpecialStore2Open();
			break;
		case THIRD_CHESTS_OPEN:
			value = cfg.getThirdChestsOpen();
			break;
		case ARENA_RESET_CD_OPEN:
			value = cfg.getArenaResetCDOpen();
			break;
		case EXPEDITION_COUNT:
			value = cfg.getExpeditionCount();
			break;
		case FASHION_BUY_OPEN:
			value = cfg.getFashionBuyOpen();
			break;
		case PEAK_ARENA_RESET_TIMES:
			value = cfg.getPeakArenaResetTimes();
			break;
		case SECRET_COPY_COUNT:
			value = cfg.getSecretCopyCount();
			break;
		case ARENA_RESET_TIMES:
			value = cfg.getArenaResetTimes();
			break;
		case TRIAL1_COPY_RESET_TIMES:
			value = cfg.getTrial1CopyResetTimes();
			break;
		case TRIAL2_COPY_RESET_TIMES:
			value = cfg.getTrial2CopyResetTimes();
			break;
		case WARFARE_COPY_RESET_TIMES:
			value = cfg.getWarfareCopyResetTimes();
			break;
		case RESIGN_OPEN:
			value = cfg.getResignOpen();
			break;
		case COPY_CELESTAL:
			value = cfg.getCopyCelestal();
			break;
		case TOWER_RESET_TIMES:
			value = cfg.getTowerResetTime();
			break;
		case BATTLE_TOWER_TIMES:
			value = cfg.getBattleTowerResetTimes();
			break;
		case TEAMBATTLE_TIMES:
			value = cfg.getTeamBattleTimes();
		default:
			break;
		}
		return value;
	}
}
