package com.gm.task;

import org.apache.commons.lang3.StringUtils;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.GmResultStatusCode;
import com.gm.util.SocketHelper;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;

public class GmDeleteBag implements IGmTask {

	@Override
	public GmResponse doTask(GmRequest request) {
		// TODO Auto-generated method stub
		GmResponse response = new GmResponse();
		try{
			String roleId = (String) request.getArgs().get("roleId");
			int serverId = Integer.parseInt(request.getArgs().get("serverId").toString());
			String uniCode = request.getArgs().get("uniCode").toString();
			int amount = Integer.parseInt(request.getArgs().get("amount").toString());
			
			if (StringUtils.isBlank(roleId) || serverId <= 0 || StringUtils.isBlank(uniCode) || amount <= 0) {
				throw new Exception(String.valueOf(GmResultStatusCode.STATUS_ARGUMENT_ERROR.getStatus()));
			}
			
			Player player = PlayerMgr.getInstance().find(roleId);
			if (player == null) {
				throw new Exception(String.valueOf(GmResultStatusCode.STATUS_ARGUMENT_ERROR.getStatus()));
			}
			boolean useItemBySlotId = player.getItemBagMgr().useItemBySlotId(uniCode, amount);
			if(!useItemBySlotId){
				throw new Exception(String.valueOf(GmResultStatusCode.STATUS_DELETE_ITEM_FAIL.getStatus()));
			}
			response.setStatus(0);
			response.setCount(1);
		}catch(Exception ex){
			SocketHelper.processException(ex, response);
		}
		return response;
	}

}
