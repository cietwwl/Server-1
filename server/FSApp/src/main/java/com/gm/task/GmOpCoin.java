package com.gm.task;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.util.GmUtils;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.UserGameDataMgr;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.PlayerTask;

public class GmOpCoin implements IGmTask{

	@Override
	public GmResponse doTask(GmRequest request) {		
		
		GmResponse response = new GmResponse();
		
		Map<String, Object> args = request.getArgs();
		
		String roleIdList = GmUtils.parseString(args, "roleId");
		final long value = GmUtils.parseLong(args, "value");
		if (StringUtils.isNotBlank(roleIdList)) {
			String[] roleIdArray = roleIdList.split(",");
			for (String roleId : roleIdArray) {
				Player target = PlayerMgr.getInstance().find(roleId);
				if (target != null) {
					GameWorldFactory.getGameWorld().asyncExecute(roleId, new PlayerTask() {

						@Override
						public void run(Player e) {
							UserGameDataMgr userGameDataMgr = e.getUserGameDataMgr();
							long currentCoin = userGameDataMgr.getCoin();
							long reult = value;
							if (currentCoin + reult < 0) {
								reult = -currentCoin;
							}

							userGameDataMgr.addCoin((int) reult);
						}
					});

				}
			}

		}


		response.setStatus(0);
		response.setCount(1);
		return response;
	}
	

}
