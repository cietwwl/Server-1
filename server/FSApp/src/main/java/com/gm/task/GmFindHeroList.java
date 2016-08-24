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
import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.embattle.EmBattlePositionKey;
import com.playerdata.embattle.EmbattleHeroPosition;
import com.playerdata.embattle.EmbattleInfoMgr;
import com.playerdata.embattle.EmbattlePositionInfo;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.RoleQualityCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwbase.dao.role.pojo.RoleQualityCfg;
import com.rwproto.BattleCommon.eBattlePositionType;

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
//		Enumeration<Hero> heroMap = heroMgr.getHerosEnumeration();
		Enumeration<? extends Hero> heroMap = heroMgr.getHerosEnumeration(player);
		while(heroMap.hasMoreElements()){
			Hero hero = heroMap.nextElement();
//			RoleBaseInfo roleBaseInfo = hero.getRoleBaseInfoMgr().getBaseInfo();
			Map<String, Object> map = new HashMap<String, Object>();
			
			RoleCfgDAO instance = RoleCfgDAO.getInstance();
//			RoleCfg heroCfg = instance.getCfgByModeID(roleBaseInfo.getModeId()+"");
			RoleCfg heroCfg = instance.getCfgByModeID(hero.getModeId()+"");
			String heroName = "";
			if(heroCfg == null){
				heroName = player.getUserName();
//				GameLog.error(LogModule.GmSender, player.getUserId(), "Gm指令查询用户阵容出现了异常佣兵模板id =" + roleBaseInfo.getId(), null);
			}else{
				heroName = heroCfg.getName();
			}
			RoleQualityCfg cfg = (RoleQualityCfg) RoleQualityCfgDAO.getInstance().getCfgById(hero.getQualityId());
			String quality = "品阶异常";
			if(cfg != null){
				quality = cfg.getQualityName();
			}
			EmbattlePositionInfo posInfo = EmbattleInfoMgr.getMgr().getEmbattlePositionInfo(player.getUserId(),eBattlePositionType.Normal_VALUE,EmBattlePositionKey.posCopy.getKey());
			List<EmbattleHeroPosition> posList = posInfo.getPos();
			boolean isfight = false;
			for(int i = 0 ; i < posList.size() ;i ++){
				if(StringUtils.equals(posList.get(i).getId(), hero.getId()+"")){
					isfight = true;
				}
			}
			int isFight = 1;
			if(isfight)isFight = 0;
			map.put("heroName",heroName);
			map.put("qualityLev", quality);
//			map.put("starLev", roleBaseInfo.getStarLevel());
			map.put("starLev", hero.getStarLevel());
			map.put("isFight", isFight);
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
