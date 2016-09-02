package com.rw.service.group;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.group.GroupDataVersionMgr;
import com.rw.service.FsService;
import com.rwproto.GroupCommonProto.RequestType;
import com.rwproto.GroupPersonalProto.GroupPersonalCommonReqMsg;
import com.rwproto.RequestProtos.Request;

/*
 * @author HC
 * @date 2016年1月20日 上午11:47:40
 * @Description 帮派协议处理Server
 */
public class GroupPersonalService implements FsService<GroupPersonalCommonReqMsg, RequestType> {

	@SuppressWarnings("finally")
	@Override
	public ByteString doTask(GroupPersonalCommonReqMsg request, Player player) {
		// TODO Auto-generated method stub
		GroupPersonalHandler personalHandler = GroupPersonalHandler.getHandler();
		ByteString byteString = null;
		try {
			RequestType reqType = request.getReqType();
			switch (reqType) {
			// ==============================帮派个人请求处理===========================
			case GET_GROUP_INFO_TYPE:// 获取帮派的信息
				byteString = personalHandler.getGrouoInfo(player);
				break;
			case GET_GROUP_RANK_INFO_TYPE:// 获取帮派排行榜信息
				byteString = personalHandler.getGroupRankInfo(player);
				break;
			case FIND_GROUP_TYPE:// 查找帮派
				byteString = personalHandler.findGroupHandler(player, request.getFindGroupReq());
				break;
			case APPLY_JOIN_GROUP_TYPE:// 申请加入帮派
				byteString = personalHandler.applyJoinGroupHandler(player, request.getApplyJoinGroupReq());
				break;
			case OPEN_DONATE_VIEW_TYPE:// 打开捐献界面
				byteString = personalHandler.openDonateViewHandler(player);
				break;
			case GROUP_DONATE_TYPE:// 帮派捐献
				byteString = personalHandler.groupDonateHandler(player, request.getGroupDonateReq());
				break;
			case TRANSFER_LEADER_POST_TYPE:// 转让帮主
				byteString = personalHandler.transferGroupLeaderPostHandler(player, request.getTransferLeaderPostReq());
				break;
			case STUDY_GROUP_SKILL_TYPE:// 学习技能
				break;
			case QUIT_GROUP_TYPE:// 退出帮派
				byteString = personalHandler.quitGroupHandler(player);
				break;
			case GROUP_RECOMMENT_TYPE:// 帮派推荐
				byteString = personalHandler.groupRecommendHandler(player, request.getGroupRecommentReq());
				break;
			// case CHECK_GROUP_DATA_TYPE:// 检查个人的帮派数据
			// byteString = personalHandler.checkGroupDataHandler(player);
			// break;
			default:
				GameLog.error("帮派模块", "分发协议Service", "接收到了一个Unknown的消息，无法处理");
				break;
			}
			GroupDataVersionMgr.synByVersion(player, request.getVersion());
		} catch (Exception e) {
			GameLog.error("帮派模块", "分发协议Service", "出现了Exception异常", e);
		} finally {
			return byteString;
		}
	}

	@Override
	public GroupPersonalCommonReqMsg parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		GroupPersonalCommonReqMsg commonReq = GroupPersonalCommonReqMsg.parseFrom(request.getBody().getSerializedContent());
		return commonReq;
	}

	@Override
	public RequestType getMsgType(GroupPersonalCommonReqMsg request) {
		// TODO Auto-generated method stub
		return request.getReqType();
	}
}