package com.gm.task;

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

public class GmDeleteBag implements IGmTask {

	@Override
	public GmResponse doTask(GmRequest request) {
		// TODO Auto-generated method stub
		GmResponse response = new GmResponse();
		try{
			Map<String, Object> args = request.getArgs();
			String roleId = GmUtils.parseString(args, "roleId");
			int serverId = GmUtils.parseInt(args, "serverId");
			final String uniCode = GmUtils.parseString(args, "uniCode");
			final int amount =  GmUtils.parseInt(args, "amount");
			
			if (StringUtils.isBlank(roleId) || serverId <= 0 || StringUtils.isBlank(uniCode) || amount <= 0) {
				throw new Exception(String.valueOf(GmResultStatusCode.STATUS_ARGUMENT_ERROR.getStatus()));
			}
			
			Player player = PlayerMgr.getInstance().find(roleId);
			if (player == null) {
				throw new Exception(String.valueOf(GmResultStatusCode.STATUS_ARGUMENT_ERROR.getStatus()));
			}
			GameWorldFactory.getGameWorld().asyncExecute(roleId, new PlayerTask() {
				
				@Override
				public void run(Player e) {
					e.getItemBagMgr().useItemBySlotId(uniCode, amount);
				}
			});
			response.setStatus(0);
			response.setCount(1);
		}catch(Exception ex){
			SocketHelper.processException(ex, response);
		}
		return response;
	}

}
