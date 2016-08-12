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
import com.rwbase.dao.skill.pojo.SkillCfg;

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
			boolean isCareer = true;
			if(player.getCareer()==0){
				isCareer = false;
			}
			if(heroCfg == null){
				
				heroName = player.getUserName();
			}else{
				heroName = heroCfg.getName();
			}
			
			SkillMgr skillMgr = hero.getSkillMgr();
			List<Skill> skillList = skillMgr.getSkillList();
			String[] names = new String[5];
			if(skillList.size() != 5){
				GameLog.error(LogModule.GmSender, player.getUserId(), "英雄技能数量异常,size=" + skillList.size(), null);
				continue;
			}
			for(int i = 0;i<skillList.size();i++){
				String skillId = skillList.get(i).getSkillId();
				String level = skillId.split("_")[1];
				SkillCfg skillCfg = SkillCfgDAO.getInstance().getCfgById(skillId);
				names[i] = skillCfg.getName()+"+"+level;				
			}
			String[] name = new String[4];
			int j = 0;
			if(!isCareer){
				for(int i = 0;i < names.length;i++){//未转职的主角，把暴雨斩替换一个未知
					
					if(i ==4){
						if(names[i].indexOf("暴雨") != -1){
							name[0] = names[i];
						}
						continue;
					}
					name[i] = names[i];
				}				
			}else{
				for(int i = 0 ;i < name.length ;i++){//去掉普攻
					if(names[j].indexOf("普攻") != -1){
						name[i] = names[j+1];
						j++;
					}else{
						name[i] = names[j];
					}
					j++;
				}
			}
			
			
			
			map.put("name1",name[0]);
			map.put("name2",name[1]);
			map.put("name3",name[2]);
			map.put("name4",name[3]);
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
