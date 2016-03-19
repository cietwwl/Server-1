package com.gm.task;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.UserDataMgr;
import com.rw.manager.GameManager;
import com.rw.service.log.infoPojo.ZoneRegInfo;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserDataDao;

public class GmUserInfo implements IGmTask{

	@Override
	public GmResponse doTask(GmRequest request) {
		GmResponse response = new GmResponse();
		response.setStatus(0);
		response.setCount(1);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		String roleId = (String)request.getArgs().get("roleId");
		String roleName = (String)request.getArgs().get("roleName");
		String account = (String)request.getArgs().get("account");
		Player player = null;
		if(StringUtils.isNotBlank(roleId)){
			player = PlayerMgr.getInstance().find(roleId);
			
		}else if(StringUtils.isNotBlank(roleName)){ 
			player = PlayerMgr.getInstance().findByName(roleName);
			
		}else if(StringUtils.isNotBlank(account)){ 
			
			User user = UserDataDao.getInstance().getByAccoutAndZoneId(account, GameManager.getZoneId());
			player = PlayerMgr.getInstance().find(user.getUserId());
		}
		if(player!=null){
			setInfo(player, resultMap);
		}

		response.addResult(resultMap );
		return response;
	}
	

	private void setInfo(Player player, Map<String, Object> resultMap){
		UserDataMgr userDataMgr = player.getUserDataMgr();
		resultMap.put("account", userDataMgr.getAccount());
		resultMap.put("roleId", player.getUserId());
		resultMap.put("roleName", player.getUserName());
		resultMap.put("level", player.getLevel());
		resultMap.put("exp", player.getExp());
		resultMap.put("coin", player.getUserGameDataMgr().getCoin());
		resultMap.put("gold", player.getUserGameDataMgr().getGold());
		ZoneRegInfo zoneRegInfo = userDataMgr.getZoneRegInfo();
		if(zoneRegInfo!=null){
			resultMap.put("channel", zoneRegInfo.getRegChannelId());
		}
		boolean blocked = userDataMgr.isBlocked();
		String blockStatus = blocked?"1":"0";
		resultMap.put("blockStatus", blockStatus);
		if(blocked){
			resultMap.put("blockTime", userDataMgr.getBlockTimeDiff());
		}
		
		int chatBanStatus = userDataMgr.isChatBan() ? 1 : 0;
		resultMap.put("talkStatus", chatBanStatus);
		 Player onlinePlayer = PlayerMgr.getInstance().findPlayerFromMemory(player.getUserId());
		 int onlineStatus = onlinePlayer == null ? 0 : 1;
		 resultMap.put("onlineStatus", onlineStatus);
	}

}
