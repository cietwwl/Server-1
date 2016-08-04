package com.gm.task;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.GmResultStatusCode;
import com.gm.util.GmUtils;
import com.gm.util.SocketHelper;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.EquipMgr;
import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.fixEquip.cfg.FixEquipCfgDAO;
import com.playerdata.fixEquip.exp.FixExpEquipMgr;
import com.playerdata.fixEquip.exp.data.FixExpEquipDataItem;
import com.playerdata.fixEquip.norm.FixNormEquipMgr;
import com.playerdata.fixEquip.norm.data.FixNormEquipDataItem;
import com.playerdata.team.HeroFixEquipInfo;
import com.rwbase.dao.equipment.EquipItem;
import com.rwbase.dao.hero.pojo.RoleBaseInfo;
import com.rwbase.dao.item.HeroEquipCfgDAO;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;

public class GmFindHeroNormalAndExpEquipList implements IGmTask{

	@Override
	public GmResponse doTask(GmRequest request) {
		GmResponse response = new GmResponse();
		try{
			
			String roleId = GmUtils.parseString(request.getArgs(), "roleId");
			Player player = getPlayer(roleId);
			if(player != null){
				setInfo(player,response);
				response.setStatus(0);
			}else{
				throw new Exception(String.valueOf(GmResultStatusCode.STATUS_ROLE_NOT_FOUND.getStatus()));
			}
					
		}catch (Exception e){
			SocketHelper.processException(e, response);
		}		
		return response;
	}
	
	private void setInfo(Player player, GmResponse response) {
		HeroMgr heroMgr = player.getHeroMgr();
		Enumeration<Hero> heroMap = heroMgr.getHerosEnumeration();
		while(heroMap.hasMoreElements()){
			Hero hero = heroMap.nextElement();
			RoleBaseInfo roleBaseInfo = hero.getRoleBaseInfoMgr().getBaseInfo();
			Map<String, Object> map = new HashMap<String, Object>();
			
			RoleCfgDAO instance = RoleCfgDAO.getInstance();
			RoleCfg heroCfg = instance.getCfgByModeID(roleBaseInfo.getModeId()+"");
			String heroName = "";
			if(heroCfg == null){
				heroName = player.getUserName();
				}else{
				heroName = heroCfg.getName();
			}					

			FixNormEquipMgr fixNormEquipMgr = hero.getFixNormEquipMgr();
			FixExpEquipMgr fixExpEquipMgr = hero.getFixExpEquipMgr();
			List<FixNormEquipDataItem> fixNormEquipList = fixNormEquipMgr.getFixNorEquipItemList(hero.getUUId());
			List<HeroFixEquipInfo> fixExpEquipList = fixExpEquipMgr.getHeroFixSimpleInfo(hero.getUUId());
			String[] names = new String[6];
			for(int i = 0;i < fixNormEquipList.size();i++){
				FixNormEquipDataItem normEquip = fixNormEquipList.get(i);
				String name = FixEquipCfgDAO.getInstance().getCfgById(normEquip.getCfgId()).getName();
				if(normEquip.getStar() > 0){
					name = FixEquipCfgDAO.getInstance().getCfgById(normEquip.getCfgId()).getAwakenName();
				}
				names[i] = name + "+" + normEquip.getLevel() + "+" + normEquip.getStar();				
			}
			
			for(int i = 0;i < fixExpEquipList.size();i++){
				HeroFixEquipInfo heroFixEquipInfo = fixExpEquipList.get(i);
				String name = FixEquipCfgDAO.getInstance().getCfgById(heroFixEquipInfo.getId()).getName();
				if(heroFixEquipInfo.getStar() > 0){
					name = FixEquipCfgDAO.getInstance().getCfgById(heroFixEquipInfo.getId()).getAwakenName();
				}
				names[i+4] = name + "+" + heroFixEquipInfo.getLevel() + "+" + heroFixEquipInfo.getStar();		
			}
			
			
			map.put("name1",names[0]);
			map.put("name2",names[1]);
			map.put("name3",names[2]);
			map.put("name4",names[3]);
			map.put("name5",names[4]);
			map.put("name6",names[5]);
			map.put("heroName",heroName);
			response.addResult(map);			
		}		
	}

	private Player getPlayer(String roleId) {
		Player player = null;
		if(StringUtils.isNotBlank(roleId)){
			player = PlayerMgr.getInstance().find(roleId);
		}		
		return player;
	}
}
