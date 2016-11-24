package com.bm.targetSell.param.attrs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bm.targetSell.param.ERoleAttrs;
import com.bm.targetSell.param.TargetSellRoleChange;
import com.playerdata.Player;
import com.rwbase.dao.targetSell.BenefitAttrCfg;
import com.rwbase.dao.targetSell.BenefitAttrCfgDAO;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserDataDao;

public class AttrsProcessMgr {
	
	private static AttrsProcessMgr instance = new AttrsProcessMgr();
	
	
	public static AttrsProcessMgr getInstance(){
		if(instance == null){
			instance = new AttrsProcessMgr();
		}
		return instance;
	}
	

	/**
	 * 打包所有的属性
	 * @param player
	 * @return
	 */
	public Map<String, Object> packAllAttrs(Player player) {
		BenefitAttrCfgDAO cfgDAO = BenefitAttrCfgDAO.getInstance();
		User user = UserDataDao.getInstance().getByUserId(player.getUserId());
		List<BenefitAttrCfg> allCfg = cfgDAO.getAllCfg();
		Map<String, Object> result = new HashMap<String, Object>();
		
		for (BenefitAttrCfg cfg : allCfg) {
			int processType = cfg.getProcessType();
			EAchieveType achieveType = EAchieveType.getAchieveType(processType);
			if(achieveType == null){
				continue;
			}
			AbsAchieveAttrValue instance = achieveType.getInstance();
			instance.achieveAttrValue(player, user, cfg, result);
			
		}
		
		return result;
	}
	
	/**
	 * 打包登陆的属性
	 * @param player
	 * @return
	 */
	public Map<String, Object> packLoginAttr(Player player) {
		Map<String, Object> result = new HashMap<String, Object>();
		User user = UserDataDao.getInstance().getByUserId(player.getUserId());
		String idStr = ERoleAttrs.r_LastLoginTime.getId();
		BenefitAttrCfgDAO cfgDAO = BenefitAttrCfgDAO.getInstance();
		BenefitAttrCfg cfg = cfgDAO.getCfgById(idStr);
		int processType = cfg.getProcessType();
		EAchieveType achieveType = EAchieveType.getAchieveType(processType);
		if(achieveType == null){
			return result;
		}
		AbsAchieveAttrValue instance = achieveType.getInstance();
		instance.achieveAttrValue(player, user, cfg, result);
		return result;
	}

	/**
	 * 打包改变的属性
	 * @param player
	 * @param list
	 * @return
	 */
	public Map<String, Object> packChangeAttr(Player player, List<String> list) {
		Map<String, Object> result = new HashMap<String, Object>();
		BenefitAttrCfgDAO cfgDAO = BenefitAttrCfgDAO.getInstance();
		User user = UserDataDao.getInstance().getByUserId(player.getUserId());
		for (String id : list) {
			BenefitAttrCfg cfg = cfgDAO.getCfgById(id);
			int processType = cfg.getProcessType();
			EAchieveType achieveType = EAchieveType.getAchieveType(processType);
			if(achieveType == null){
				continue;
			}
			AbsAchieveAttrValue instance = achieveType.getInstance();
			instance.achieveAttrValue(player, user, cfg, result);
		}
		return result;
	}





	public void addHeroChangeAttrs(String userID, String heroID, List<EAchieveType> change, TargetSellRoleChange value) {
		for (EAchieveType eAchieveType : change) {
			AbsAchieveAttrValue ins = eAchieveType.getInstance();
			if(ins == null){
				continue;
			}
			ins.addHeroAttrs(userID, heroID, eAchieveType, value);
		}
	
		
	}
}
