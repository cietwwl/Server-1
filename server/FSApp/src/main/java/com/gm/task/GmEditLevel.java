package com.gm.task;

import java.util.Map;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.GmResultStatusCode;
import com.gm.util.GmUtils;
import com.gm.util.SocketHelper;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rwbase.dao.user.User;

public class GmEditLevel implements IGmTask {

	@Override
	public GmResponse doTask(GmRequest request) {
		// TODO Auto-generated method stub
		GmResponse response = new GmResponse();

		try {
			Map<String, Object> args = request.getArgs();
			String userId = GmUtils.parseString(args, "roleId");
			int editlv = GmUtils.parseInt(args, "value");
			Player player = PlayerMgr.getInstance().find(userId);
			if (player != null) {
				User user = player.getUserDataMgr().getUser();
				player.setLevelByGM(user.getLevel() + editlv);
			} else {
				throw new Exception(String.valueOf(GmResultStatusCode.STATUS_ARGUMENT_ERROR.getStatus()));
			}
			response.setStatus(0);
			response.setCount(1);

		} catch (Exception ex) {
			SocketHelper.processException(ex, response);
		}

		return response;
	}

}
