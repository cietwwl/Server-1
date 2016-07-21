package com.gm.task;

import java.util.Map;

import com.gm.GmRequest;
import com.gm.GmResponse;
import com.gm.GmResultStatusCode;
import com.gm.customer.QuestionReply;
import com.gm.util.GmUtils;
import com.gm.util.SocketHelper;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.PlayerQuestionMgr;

public class GmResponsePlayerQuestion implements IGmTask {

	@Override
	public GmResponse doTask(GmRequest request) {
		GmResponse response = new GmResponse();
		try {
			Map<String, Object> args = request.getArgs();
			int id = GmUtils.parseInt(args, "id");
			int serverId = GmUtils.parseInt(args, "serverId");
			String roleId = GmUtils.parseString(args, "roleId");
			String content = GmUtils.parseString(args, "content");
			String replyTime = GmUtils.parseString(args, "replyTime");

			Player player = PlayerMgr.getInstance().find(roleId);
			if (player == null) {
				throw new Exception(String.valueOf(GmResultStatusCode.STATUS_ARGUMENT_ERROR.getStatus()));
			}
			PlayerQuestionMgr playerQuestionMgr = player.getPlayerQuestionMgr();
			
			QuestionReply reply = new QuestionReply();
			reply.setContent(content);
			reply.setId(id);
			reply.setRoleId(roleId);
			reply.setServerId(serverId);
			reply.setReplyTime(replyTime);
			
			playerQuestionMgr.processNotifyReply(reply);
			
			response.setStatus(0);
			response.setCount(1);
		} catch (Exception ex) {
			SocketHelper.processException(ex, response);
		}
		return response;
	}

}
