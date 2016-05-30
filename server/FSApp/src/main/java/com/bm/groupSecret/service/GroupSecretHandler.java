//package com.bm.groupSecret.service;
//
//import com.bm.groupSecret.GroupSecretBM;
//import com.bm.groupSecret.GroupSecretComResult;
//import com.bm.groupSecret.GroupSecretMgr;
//import com.bm.groupSecret.GroupSecretType;
//import com.bm.groupSecret.data.user.UserGroupSecretMgr;
//import com.google.protobuf.ByteString;
//import com.playerdata.Player;
//import com.rwproto.GroupSecretProto.CommonReqMsg;
//import com.rwproto.GroupSecretProto.CommonRspMsg;
//import com.rwproto.GroupSecretProto.GetDefRewardMsg;
//import com.rwproto.GroupSecretProto.OpenReqMsg;
//
//public class GroupSecretHandler {
//	
//	private static GroupSecretHandler instance = new GroupSecretHandler();
//	
//	public static GroupSecretHandler getInstance(){
//		return instance;
//	}
//
//	public ByteString getUserSecrets(Player player, CommonReqMsg commonReq) {
//		CommonRspMsg.Builder response = CommonRspMsg.newBuilder();
//		response.setReqType(commonReq.getReqType());
//		
//		UserGroupSecretMgr.getInstance().synUserSecrets(player);
//		
//		response.setIsSuccess(true);
//		response.setTipMsg("");
//		return response.build().toByteString();
//	}
//	
////	private GroupSecretDataJson buildGroupSecretDataJson(GroupSecretMgr secretMgr) {
////
////		GroupSecretDataJson data = null;
////		GroupSecretData secretData = secretMgr.getSecretData();
////		if(secretData!=null){			
////			Builder dataBuilder = GroupSecretDataJson.newBuilder().setGroupSecretData(ClientDataSynMgr.toClientData(secretData));
////			
////			List<GroupSecretDefLog> defLogList = secretMgr.getDefLogList();
////			for (GroupSecretDefLog groupSecretDefLog : defLogList) {
////				dataBuilder.addGroupSecretDefLogData(ClientDataSynMgr.toClientData(groupSecretDefLog));
////			}
////			
////			data = dataBuilder.build();
////		}
////		
////		
////		return data;
////	}
//
//	public ByteString openSecret(Player player, CommonReqMsg commonReq) {
//		CommonRspMsg.Builder response = CommonRspMsg.newBuilder();
//		response.setReqType(commonReq.getReqType());
//		
//		OpenReqMsg openReqMsg = commonReq.getOpenReqMsg();
//		int typeOrdinal = openReqMsg.getType();
//		
//		GroupSecretType groupSecretType = GroupSecretType.valueOf(typeOrdinal);
//		
//		GroupSecretMgr secretMgr = GroupSecretBM.getInstance().openSecret(player, groupSecretType);
//		
//		if(secretMgr!=null){
//			secretMgr.synToClient(player);
//			response.setIsSuccess(true);
//			
//		}else{
//			response.setIsSuccess(false);			
//			response.setTipMsg("开启失败，请稍后尝试。");
//		}
//		return response.build().toByteString();
//	}
//	public ByteString getSecretReward(Player player, CommonReqMsg commonReq) {
//		CommonRspMsg.Builder response = CommonRspMsg.newBuilder();
//		response.setReqType(commonReq.getReqType());
//		
//		String secretId = commonReq.getGetSecretRewardMsg().getSecretId();
//		GroupSecretMgr secretMgr = GroupSecretBM.getInstance().getSecret(secretId);
//		
//		if(secretMgr!=null){
//			GroupSecretComResult secretReward = secretMgr.getSecretReward(player);
//			secretMgr.synToClient(player);			
//			
//			response.setIsSuccess(secretReward.isSuccess());			
//			response.setTipMsg(secretReward.getReason());
//		}else{
//			response.setIsSuccess(false);			
//			response.setTipMsg("秘境不存在");
//			
//		}
//		
//		
//		return response.build().toByteString();
//	}
//	public ByteString getDefReward(Player player, CommonReqMsg commonReq) {
//		CommonRspMsg.Builder response = CommonRspMsg.newBuilder();
//		response.setReqType(commonReq.getReqType());
//		
//		GetDefRewardMsg getDefRewardMsg = commonReq.getGetDefRewardMsg();
//		String secretId = getDefRewardMsg.getSecretId();
//		String defLogId = getDefRewardMsg.getDefLogId();
//		
//		GroupSecretMgr secretMgr = GroupSecretBM.getInstance().getSecret(secretId);
//		
//		if(secretMgr!=null){
//			GroupSecretComResult secretReward = secretMgr.getDefReward(player, secretId, defLogId);
//			secretMgr.synToClient(player);			
//			
//			response.setIsSuccess(secretReward.isSuccess());			
//			response.setTipMsg(secretReward.getReason());
//		}else{
//			response.setIsSuccess(false);			
//			response.setTipMsg("秘境不存在");
//		}
//		
//		return response.build().toByteString();
//	}
//
//
// }
