package com.gm.task;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.GmResultStatusCode;
import com.gm.util.SocketHelper;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rw.service.gm.GMHandler;

/**
 * 调用原有的gm指令
 * @author lida
 *
 */
public class GmExecuteGMCommand implements IGmTask {

	private GMHandler gmHandler = GMHandler.getInstance();
	
	@Override
	public GmResponse doTask(GmRequest request) {
		GmResponse response = new GmResponse();
		try {
			String roleId = (String) request.getArgs().get("roleId");
			String content = (String) request.getArgs().get("command");

			if (StringUtils.isBlank(roleId) || StringUtils.isBlank(content)) {
				throw new Exception(String.valueOf(GmResultStatusCode.STATUS_ARGUMENT_ERROR.getStatus()));
			}

			Player player = PlayerMgr.getInstance().find(roleId);
			if (player == null) {
				throw new Exception(String.valueOf(GmResultStatusCode.STATUS_ARGUMENT_ERROR.getStatus()));
			}
			
			if(gmHandler.executeGMCommand(player, content)){
				response.setStatus(0);
				response.setCount(1);
				Map<String, Object> resultMap = new HashMap<String, Object>();
				response.addResult(resultMap);
			}else{
				throw new Exception(String.valueOf(GmResultStatusCode.STATUS_EXECUTE_GM_COMMAND_FAIL.getStatus()));
			}
			
			
		} catch (Exception ex) {
			SocketHelper.processException(ex, response);
		}
		return response;
	}

}
