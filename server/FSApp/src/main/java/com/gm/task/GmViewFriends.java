package com.gm.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.GmResultStatusCode;
import com.gm.util.SocketHelper;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rwbase.common.enu.ECareer;
import com.rwproto.FriendServiceProtos.FriendInfo;

public class GmViewFriends implements IGmTask{

	@Override
	public GmResponse doTask(GmRequest request) {
		// TODO Auto-generated method stub
				GmResponse response = new GmResponse();
				try {
					String roleId = (String) request.getArgs().get("roleId");
					int serverId = Integer.parseInt(request.getArgs().get("serverId").toString());

					if (StringUtils.isBlank(roleId) || serverId <= 0) {
						throw new Exception(String.valueOf(GmResultStatusCode.STATUS_ARGUMENT_ERROR.getStatus()));
					}
					Player player = PlayerMgr.getInstance().find(roleId);
					if (player == null) {
						throw new Exception(String.valueOf(GmResultStatusCode.STATUS_ARGUMENT_ERROR.getStatus()));
					}
					
					
					List<FriendInfo> friendList = player.getFriendMgr().getFriendList();
					for (FriendInfo friendInfo : friendList) {
						Map<String, Object> map = new HashMap<String, Object>();
						String userId = friendInfo.getUserId();
						
						map.put("roleId", userId);
						map.put("roleName", friendInfo.getUserName());
						map.put("level", friendInfo.getLevel());
						map.put("job", ECareer.getCarrer(friendInfo.getCareer()));
						response.addResult(map);
					}
					
					
					response.setStatus(0);
					response.setCount(friendList.size());

				} catch (Exception ex) {

					SocketHelper.processException(ex, response);
				}
				return response;
	}

}
