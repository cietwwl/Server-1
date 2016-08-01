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
import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.SkillMgr;
import com.rwbase.dao.hero.pojo.RoleBaseInfo;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwbase.dao.skill.SkillCfgDAO;
import com.rwbase.dao.skill.pojo.Skill;

public class GmFindHeroSkillList implements IGmTask{

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
				GameLog.error(LogModule.GmSender, player.getUserId(), "Gm指令查询用户阵容出现了异常佣兵模板id =" + roleBaseInfo.getId(), null);
			}else{
				heroName = heroCfg.getName();
			}
			
			SkillMgr skillMgr = hero.getSkillMgr();
			List<Skill> skillList = skillMgr.getSkillList();
			String name1= "";
			String name2= "";
			String name3= "";
			String name4= "";
			for(int i = 0; i< skillList.size() ;i ++){
				String nameTmp = SkillCfgDAO.getInstance().getCfgById(skillList.get(i).getId()).getName();
				if(i == 0){
					name1 = nameTmp + "" + skillList.get(i).getLevel();
				}
				if(i == 1){
					name2 =nameTmp +  "" + skillList.get(i).getLevel();
				}
				if(i == 2){
					name3 =nameTmp +  "" + skillList.get(i).getLevel();
				}
				if(i == 3){
					name4 = nameTmp + "" + skillList.get(i).getLevel();
				}
			}
			map.put("name1",name1);
			map.put("name2",name2);
			map.put("name3",name3);
			map.put("name4",name4);
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
