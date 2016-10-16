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
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.PlayerTask;

public class GmOpExp implements IGmTask{

	@Override
	public GmResponse doTask(GmRequest request) {		
		
		GmResponse response = new GmResponse();
		try {

			Map<String, Object> args = request.getArgs();
			String roleIdList = GmUtils.parseString(args, "roleId");
			final long value = GmUtils.parseLong(args, "value");
			if (StringUtils.isNotBlank(roleIdList)) {
				String[] roleIdArray = roleIdList.split(",");
				for (String roleId : roleIdArray) {
					Player target = PlayerMgr.getInstance().find(roleId);
					if (target == null) {
						throw new Exception(String.valueOf(GmResultStatusCode.STATUS_ARGUMENT_ERROR.getStatus()));
					}
					GameWorldFactory.getGameWorld().asyncExecute(roleId, new PlayerTask() {
						
						@Override
						public void run(Player e) {
							e.addUserExp(value);
						}
					});
					
				}

			}

			response.setStatus(0);
			response.setCount(1);
		} catch (Exception ex) {
			SocketHelper.processException(ex, response);
		}
		return response;
	}
}
