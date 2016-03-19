package com.gm.task;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.GmResultStatusCode;
import com.gm.util.GmUtils;
import com.gm.util.SocketHelper;
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
		try {
			Map<String, Object> resultMap = new HashMap<String, Object>();

			String roleId = GmUtils.parseString(request.getArgs(), "roleId");
			String roleName = GmUtils.parseString(request.getArgs(), "roleName");
			String account = GmUtils.parseString(request.getArgs(), "account");
			Player player = getPlayer(roleId, roleName, account);

			if (player != null) {

				setInfo(player, resultMap);
				response.setStatus(0);
				response.setCount(1);
			} else {
				throw new Exception(String.valueOf(GmResultStatusCode.STATUS_ROLE_NOT_FOUND.getStatus()));
			}

			response.addResult(resultMap);
		} catch (Exception ex) {

			SocketHelper.processException(ex, response);
		}
		return response;
	}
	
	private Player getPlayer(String roleId, String roleName, String account) {
		Player player = null;
		if (StringUtils.isNotBlank(roleId)) {
			player = PlayerMgr.getInstance().find(roleId);
			return player;
		}

		if (StringUtils.isNotBlank(roleName)) {
			player = PlayerMgr.getInstance().findByName(roleName);
			return player;
		}
		if (StringUtils.isNotBlank(account)) {

			User user = UserDataDao.getInstance().getByAccoutAndZoneId(account, GameManager.getZoneId());
			player = PlayerMgr.getInstance().find(user.getUserId());
			return player;
		}
		return player;
	}

	private void setInfo(Player player, Map<String, Object> resultMap){
		UserDataMgr userDataMgr = player.getUserDataMgr();
		resultMap.put("account", userDataMgr.getAccount());
		resultMap.put("roleId", player.getUserId());
		resultMap.put("roleName", player.getUserName());
		resultMap.put("level", player.getLevel());
		resultMap.put("exp", player.getExp());
		resultMap.put("money", player.getUserGameDataMgr().getCoin());
		resultMap.put("coin", player.getUserGameDataMgr().getGold());
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
