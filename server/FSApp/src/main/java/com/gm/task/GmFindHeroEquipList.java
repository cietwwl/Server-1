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
import com.playerdata.SkillMgr;
import com.rwbase.dao.equipment.EquipItem;
import com.rwbase.dao.hero.pojo.RoleBaseInfo;
import com.rwbase.dao.item.HeroEquipCfgDAO;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwbase.dao.skill.SkillCfgDAO;
import com.rwbase.dao.skill.pojo.Skill;
import com.rwbase.dao.skill.pojo.SkillCfg;

public class GmFindHeroEquipList implements IGmTask{

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
			

			
			EquipMgr equipMgr = hero.getEquipMgr();
			List<EquipItem> equipList = equipMgr.getEquipList();
			String[] itemName = new String[6];
			for(int i = 0 ;i < equipList.size() ;i++){
				int id = equipList.get(i).getModelId();
				itemName[i] = HeroEquipCfgDAO.getInstance().getCfgById(id+"").getName();				
			}
			
			
			
			map.put("itemName1",itemName[0]);
			map.put("itemName2",itemName[1]);
			map.put("itemName3",itemName[2]);
			map.put("itemName4",itemName[3]);
			map.put("itemName5",itemName[4]);
			map.put("itemName6",itemName[5]);
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
