package com.gm.task;

import java.util.Map;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.util.GmUtils;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;

public class GmEditLevel implements IGmTask {

	@Override
	public GmResponse doTask(GmRequest request) {
		// TODO Auto-generated method stub
		GmResponse response = new GmResponse();

		Map<String, Object> args = request.getArgs();
		String userId = GmUtils.parseString(args, "roleId");
		int editlv = GmUtils.parseInt(args, "value");
		Player player = PlayerMgr.getInstance().find(userId);
		player.setLevelByGM(player.getLevel() + editlv);

		response.setStatus(0);
		response.setCount(1);

		return response;
	}

}
