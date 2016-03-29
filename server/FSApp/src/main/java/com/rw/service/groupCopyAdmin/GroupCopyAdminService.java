package com.rw.service.groupCopyAdmin;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.group.GroupDataVersionMgr;
import com.rw.service.FsService;
import com.rwproto.GroupCopyAdminProto.GroupCopyAdminComReqMsg;
import com.rwproto.GroupCopyAdminProto.RequestType;
import com.rwproto.RequestProtos.Request;

/*
 * @author HC
 * @date 2016年3月1日 下午3:07:01
 * @Description 
 */
public class GroupCopyAdminService implements FsService {

	@SuppressWarnings("finally")
	@Override
	public ByteString doTask(Request request, Player player) {
		GroupCopyAdminHandler handler = GroupCopyAdminHandler.getInstance();
		ByteString byteString = null;
		try {
			GroupCopyAdminComReqMsg commonReq = GroupCopyAdminComReqMsg.parseFrom(request.getBody().getSerializedContent());
			RequestType reqType = commonReq.getReqType();
			switch (reqType) {
			// ==============================帮派管理处理===========================
			case GET_COPY_INFO:// 获取帮派副本信息
				byteString = handler.getInfo(player, commonReq);
				break;
			case OPEN_COPY:// 修改帮派公告
				byteString = handler.open(player, commonReq);
				break;
			case RESET_COPY:// 修改帮派名字
				byteString = handler.reset(player, commonReq);
				break;			
			default:
				
				GameLog.error(LogModule.COPY, "GroupCopyAdminService[doTask]", "接收到了一个Unknown的消息，无法处理", null);
				break;
			}
			GroupDataVersionMgr.synByVersion(player, commonReq.getVersion());
		} catch (Exception e) {
			GameLog.error(LogModule.COPY, "GroupCopyAdminService[doTask]", "出现了Exception异常", e);
		} finally {
			return byteString;
		}
	}

}