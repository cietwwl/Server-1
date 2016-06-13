package com.groupCopy.rw.service.groupCopy;

import com.google.protobuf.ByteString;
import com.groupCopy.bm.GroupHelper;
import com.playerdata.Player;
import com.rwbase.dao.group.pojo.Group;
import com.rwproto.GroupCopyCmdProto.GroupCopyCmdReqMsg;
import com.rwproto.GroupCopyCmdProto.GroupCopyCmdRspMsg;
import com.rwproto.GroupCopyCmdProto.GroupCopyReqType;
import com.rwproto.GroupCopyCmdProto.ResultCode;

public class GroupCopyCmdHandler {

	private final static GroupCopyCmdHandler instance = new GroupCopyCmdHandler();
	
	
	public static GroupCopyCmdHandler getInstance() {
		return instance;
	}


	public ByteString getGroupCopyInfo(Player player, GroupCopyCmdReqMsg reqMsg) {
		GroupCopyCmdRspMsg.Builder rspCmd = GroupCopyCmdRspMsg.newBuilder();
		rspCmd.setReqType(GroupCopyReqType.GET_INFO);
		
		Group group = GroupHelper.getGroup(player);
		if(group != null){
			rspCmd.setResCode(ResultCode.CODE_SUC);
		}else{
			rspCmd.setResCode(ResultCode.CODE_FAIL);
		}
		return rspCmd.build().toByteString();
	}

}
