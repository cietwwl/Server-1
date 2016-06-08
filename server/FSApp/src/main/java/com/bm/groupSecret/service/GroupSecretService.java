//package com.bm.groupSecret.service;
//
//import com.google.protobuf.ByteString;
//import com.log.GameLog;
//import com.log.LogModule;
//import com.playerdata.Player;
//import com.rw.service.FsService;
//import com.rwproto.GroupSecretProto.CommonReqMsg;
//import com.rwproto.GroupSecretProto.RequestType;
//import com.rwproto.RequestProtos.Request;
//
//
//public class GroupSecretService implements FsService {
//
//
//	@Override
//	public ByteString doTask(Request request, Player player) {
//		
//		GroupSecretHandler handler = GroupSecretHandler.getInstance();
//		
//		ByteString byteString = null;
//		try {
//			CommonReqMsg commonReq = CommonReqMsg.parseFrom(request.getBody().getSerializedContent());
//			
//			RequestType reqType = commonReq.getReqType();
//			switch (reqType) {	
//			case GET_USER_SECRETS:// 获取用户的秘境
//				byteString = handler.getUserSecrets(player, commonReq);
//				break;
//			case OPEN_SECRET:// 开启秘境
//				byteString = handler.openSecret(player, commonReq);
//				break;
//			case GET_SECRET_REWARD:// 获取奖励
//				byteString = handler.getSecretReward(player, commonReq);
//				break;
//			case GET_DEF_REWARD:// 获取奖励
//				byteString = handler.getSecretReward(player, commonReq);
//				break;
//			default:
//				GameLog.error(LogModule.GroupSecret, player.getUserId(), "接收到了一个Unknown的消息，无法处理", null);
//				break;
//			}
//			
//		} catch (Exception e) {
//			GameLog.error(LogModule.GroupSecret, player.getUserId(), "出现了Exception异常", e);
//		}
//		return byteString;
//		
//	}
// }