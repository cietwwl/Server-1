package com.bm.groupSecret.service;

import java.util.ArrayList;
import java.util.List;

import com.bm.groupSecret.GroupSecretBM;
import com.bm.groupSecret.GroupSecretComResult;
import com.bm.groupSecret.GroupSecretMgr;
import com.bm.groupSecret.GroupSecretType;
import com.bm.groupSecret.data.group.GroupSecretData;
import com.bm.groupSecret.data.user.UserGroupSecretMgr;
import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwproto.GroupSecretProto.CommonReqMsg;
import com.rwproto.GroupSecretProto.CommonRspMsg;
import com.rwproto.GroupSecretProto.GetDefRewardMsg;
import com.rwproto.GroupSecretProto.GetUserSecretsRspMsg;
import com.rwproto.GroupSecretProto.OpenReqMsg;
import com.rwproto.GroupSecretProto.OpenSecretRspMsg;

public class GroupSecretHandler {
	
	private static GroupSecretHandler instance = new GroupSecretHandler();
	
	public static GroupSecretHandler getInstance(){
		return instance;
	}

	public ByteString getUserSecrets(Player player, CommonReqMsg commonReq) {
		CommonRspMsg.Builder response = CommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		
		List<String> secretDataList = new ArrayList<String>();
		
		List<String> userSecretIds = UserGroupSecretMgr.getInstance().getUserSecretIds(player);
		for (String secretId : userSecretIds) {
			GroupSecretMgr secretMgr = GroupSecretBM.getInstance().getSecret(secretId);
			if(secretMgr!=null){
				GroupSecretData secretData = secretMgr.getSecretData();
				if(secretData!=null){					
					secretDataList.add(ClientDataSynMgr.toClientData(secretData));
				}
			}
		}
		
		GetUserSecretsRspMsg getUserSecretsRspMsg = GetUserSecretsRspMsg.newBuilder().addAllGroupSecretData(secretDataList).build();
		response.setGetUserSecretsRspMsg(getUserSecretsRspMsg);
		
		response.setIsSuccess(true);
		response.setTipMsg("");
		return response.build().toByteString();
	}
	
	public ByteString openSecret(Player player, CommonReqMsg commonReq) {
		CommonRspMsg.Builder response = CommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		
		OpenReqMsg openReqMsg = commonReq.getOpenReqMsg();
		int typeOrdinal = openReqMsg.getType();
		
		GroupSecretType groupSecretType = GroupSecretType.valueOf(typeOrdinal);
		
		GroupSecretMgr secretMgr = GroupSecretBM.getInstance().openSecret(player, groupSecretType);
		if(secretMgr!=null){
			String clientData = ClientDataSynMgr.toClientData(secretMgr.getSecretData());
			OpenSecretRspMsg openSecretRspMsg = OpenSecretRspMsg.newBuilder().setGroupSecretData(clientData).build();
			response.setOpenSecretRspMsg( openSecretRspMsg );
			
			response.setIsSuccess(true);
			
		}else{
			response.setIsSuccess(false);			
			response.setTipMsg("开启失败，请稍后尝试。");
		}
		return response.build().toByteString();
	}
	public ByteString getSecretReward(Player player, CommonReqMsg commonReq) {
		CommonRspMsg.Builder response = CommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		
		String secretId = commonReq.getGetSecretRewardMsg().getSecretId();
		GroupSecretMgr secretMgr = GroupSecretBM.getInstance().getSecret(secretId);
		
		GroupSecretComResult secretReward = secretMgr.getSecretReward(player);
		
		response.setIsSuccess(secretReward.isSuccess());			
		response.setTipMsg(secretReward.getReason());
		
		return response.build().toByteString();
	}
	public ByteString getDefReward(Player player, CommonReqMsg commonReq) {
		CommonRspMsg.Builder response = CommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		
		GetDefRewardMsg getDefRewardMsg = commonReq.getGetDefRewardMsg();
		String secretId = getDefRewardMsg.getSecretId();
		String defLogId = getDefRewardMsg.getDefLogId();
		
		GroupSecretMgr secretMgr = GroupSecretBM.getInstance().getSecret(secretId);
		
		GroupSecretComResult secretReward = secretMgr.getDefReward(player, secretId, defLogId);
		
		response.setIsSuccess(secretReward.isSuccess());			
		response.setTipMsg(secretReward.getReason());
		
		return response.build().toByteString();
	}


}
