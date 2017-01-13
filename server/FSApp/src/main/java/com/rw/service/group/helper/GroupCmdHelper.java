package com.rw.service.group.helper;

import com.google.protobuf.ByteString;
import com.rwproto.GroupBaseMgrProto.GroupBaseMgrCommonRspMsg;
import com.rwproto.GroupMemberMgrProto.GroupMemberMgrCommonRspMsg;
import com.rwproto.GroupPersonalProto.GroupPersonalCommonRspMsg;
import com.rwproto.GroupPrayProto.GroupPrayCommonRspMsg;
import com.rwproto.GroupSkillServiceProto.GroupSkillCommonRspMsg;

/*
 * @author HC
 * @date 2016年3月5日 上午10:07:43
 * @Description 
 */
public class GroupCmdHelper {

	/**
	 * 填充失败的错误消息
	 * 
	 * @param commonRsp
	 * @param tipMsg
	 * @return
	 */
	public static ByteString groupBaseMgrFillFailMsg(GroupBaseMgrCommonRspMsg.Builder commonRsp, String tipMsg) {
		commonRsp.setIsSuccess(false);
		commonRsp.setTipMsg(tipMsg);
		return commonRsp.build().toByteString();
	}

	/**
	 * 填充失败的错误消息
	 * 
	 * @param commonRsp
	 * @param tipMsg
	 * @return
	 */
	public static ByteString groupMemberMgrFillFailMsg(GroupMemberMgrCommonRspMsg.Builder commonRsp, String tipMsg) {
		commonRsp.setIsSuccess(false);
		commonRsp.setTipMsg(tipMsg);
		return commonRsp.build().toByteString();
	}

	/**
	 * 填充失败的错误消息
	 * 
	 * @param commonRsp
	 * @param tipMsg
	 * @return
	 */
	public static ByteString groupPersonalFillFailMsg(GroupPersonalCommonRspMsg.Builder commonRsp, String tipMsg) {
		commonRsp.setIsSuccess(false);
		commonRsp.setTipMsg(tipMsg);
		return commonRsp.build().toByteString();
	}

	/**
	 * 填充失败的错误消息
	 * 
	 * @param commonRsp
	 * @param tipMsg
	 * @return
	 */
	public static ByteString groupSkillFillFailMsg(GroupSkillCommonRspMsg.Builder commonRsp, String tipMsg) {
		commonRsp.setIsSuccess(false);
		commonRsp.setTipMsg(tipMsg);
		return commonRsp.build().toByteString();
	}

	/**
	 * 填充帮派祈福的错误信息
	 * 
	 * @param commonRsp
	 * @param tipMsg
	 * @return
	 */
	public static ByteString groupPrayFillFailMsg(GroupPrayCommonRspMsg.Builder commonRsp, String tipMsg) {
		commonRsp.setIsSuccess(false);
		commonRsp.setTipMsg(tipMsg);
		return commonRsp.build().toByteString();
	}
}