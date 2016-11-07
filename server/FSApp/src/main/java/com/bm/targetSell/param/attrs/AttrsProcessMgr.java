package com.bm.targetSell.param.attrs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bm.targetSell.param.ERoleAttrs;
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
	
	private static BenefitAttrCfgDAO cfgDAO = BenefitAttrCfgDAO.getInstance();

	/**
	 * 打包所有的属性
	 * @param player
	 * @return
	 */
	public Map<String, Object> packAllAttrs(Player player) {
		ERoleAttrs[] all = ERoleAttrs.getAll();
		User user = UserDataDao.getInstance().getByUserId(player.getUserId());
		Map<String, Object> result = new HashMap<String, Object>();
		for (ERoleAttrs eRoleAttrs : all) {
			if(eRoleAttrs == null){
				continue;
			}
			String idStr = eRoleAttrs.getIdStr();
			BenefitAttrCfg cfg = cfgDAO.getCfgById(idStr);
			int processType = cfg.getProcessType();
			EAchieveType achieveType = EAchieveType.getAchieveType(processType);
			if(achieveType == null){
				continue;
			}
			AbsAchieveAttrValue instance = achieveType.getInstance();
			instance.achieveAttrValue(player, user, eRoleAttrs, null, result, cfgDAO);
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
		String idStr = ERoleAttrs.r_Level.getIdStr();
		BenefitAttrCfg cfg = cfgDAO.getCfgById(idStr);
		int processType = cfg.getProcessType();
		EAchieveType achieveType = EAchieveType.getAchieveType(processType);
		if(achieveType == null){
			return result;
		}
		AbsAchieveAttrValue instance = achieveType.getInstance();
		instance.achieveAttrValue(player, user, ERoleAttrs.r_Level, null, result, cfgDAO);
		return result;
	}

	/**
	 * 打包改变的属性
	 * @param player
	 * @param list
	 * @return
	 */
	public Map<String, Object> packChangeAttr(Player player, List<ERoleAttrs> list) {
		Map<String, Object> result = new HashMap<String, Object>();
		User user = UserDataDao.getInstance().getByUserId(player.getUserId());
		for (ERoleAttrs eRoleAttrs : list) {
			String idStr = eRoleAttrs.getIdStr();
			BenefitAttrCfg cfg = cfgDAO.getCfgById(idStr);
			int processType = cfg.getProcessType();
			EAchieveType achieveType = EAchieveType.getAchieveType(processType);
			if(achieveType == null){
				continue;
			}
			AbsAchieveAttrValue instance = achieveType.getInstance();
			instance.achieveAttrValue(player, user, eRoleAttrs, null, result, cfgDAO);
		}
		return result;
	}
}
