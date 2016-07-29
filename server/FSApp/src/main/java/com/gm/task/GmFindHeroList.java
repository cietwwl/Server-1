package com.gm.task;

import java.util.Enumeration;
import java.util.HashMap;
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
import com.rwbase.dao.hero.pojo.RoleBaseInfo;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;

public class GmFindHeroList implements IGmTask{
	@Override
	public GmResponse doTask(GmRequest request) {
		GmResponse response = new GmResponse();
		try{
			
			String roleId = GmUtils.parseString(request.getArgs(), "roleId");
			Player player = getPlayer(roleId);
			if(player != null){
				setInfo(player,response);
				response.setStatus(0);
				response.setStatus(1);
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
			RoleCfg heroCfg = instance.getCfgByModeID(roleBaseInfo.getId());
			String heroName = "";
			if(heroCfg != null){
				heroName = heroCfg.getName();
				GameLog.error(LogModule.GmSender, player.getUserId(), "Gm指令查询用户阵容出现了异常佣兵模板id =" + roleBaseInfo.getId(), null);
			}		
			map.put("heroName",heroName);
			map.put("qualityLev", roleBaseInfo.getQualityId());
			map.put("starLev", roleBaseInfo.getStarLevel());
			map.put("isFight", 0);
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
