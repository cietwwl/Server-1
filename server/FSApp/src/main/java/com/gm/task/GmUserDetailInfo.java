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
import com.playerdata.UserGameDataMgr;
import com.rw.service.log.infoPojo.ZoneRegInfo;
import com.rwbase.common.enu.ECareer;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserDataDao;

public class GmUserDetailInfo implements IGmTask {

	@Override
	public GmResponse doTask(GmRequest request) {
		GmResponse response = new GmResponse();
		try {

			Map<String, Object> resultMap = new HashMap<String, Object>();

			String roleId = GmUtils.parseString(request.getArgs(), "roleId");
			int serverId = GmUtils.parseInt(request.getArgs(), "serverId");

			if (StringUtils.isBlank(roleId) || serverId <= 0) {
				throw new Exception(String.valueOf(GmResultStatusCode.STATUS_ARGUMENT_ERROR.getStatus()));
			}

			Player player = PlayerMgr.getInstance().find(roleId);
			if (player == null) {
				throw new Exception(String.valueOf(GmResultStatusCode.STATUS_ARGUMENT_ERROR.getStatus()));
			}
			
			getUserDetailInfo(resultMap, player);
			response.addResult(resultMap);

			response.setStatus(0);
			response.setCount(1);
		} catch (Exception ex) {
			SocketHelper.processException(ex, response);
		}
		return response;
	}

	
	public void getUserDetailInfo(Map<String, Object> map, Player player){
		
		String account = player.getUserDataMgr().getAccount();
		User user = UserDataDao.getInstance().getByUserId(player.getUserId());
		
		
		map.put("account", account);
		map.put("roleId", user.getUserId());
		map.put("roleName", user.getUserName());
		map.put("level", user.getLevel());
		map.put("exp", player.getExp());
		UserGameDataMgr userGameDataMgr = player.getUserGameDataMgr();
		UserDataMgr userDataMgr = player.getUserDataMgr();
		map.put("coin", userGameDataMgr.getGold());
		map.put("money", userGameDataMgr.getCoin());
		ZoneRegInfo zoneRegInfo = userDataMgr.getZoneRegInfo();
		if(zoneRegInfo!=null){
			map.put("channel", zoneRegInfo.getRegChannelId());
		}
		map.put("vipLevel", player.getVip());
//		map.put("fight", player.getHeroMgr().getFightingAll());
		map.put("fight", player.getHeroMgr().getFightingAll(player));
		//map.put("faction_id", player.getGuildUserMgr().getGuildId());
		map.put("faction_id", "");
		String carrer = ECareer.getCarrer(player.getCareer());
		map.put("job", carrer);
		
	}
}
