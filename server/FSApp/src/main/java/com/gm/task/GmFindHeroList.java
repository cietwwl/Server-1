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
import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rwbase.dao.hero.pojo.RoleBaseInfo;

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
			map.put("heroName",roleBaseInfo.getId() );
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
