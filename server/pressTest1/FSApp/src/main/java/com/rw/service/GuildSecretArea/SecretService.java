package com.rw.service.GuildSecretArea;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.RequestProtos.Request;
import com.rwproto.SecretAreaProtos.EReqSecretType;
import com.rwproto.SecretAreaProtos.MsgSecretRequest;


public class SecretService implements FsService{

	private SecretHandle secretHandler = SecretHandle.getInstance();
	
	public ByteString doTask(Request request, Player player) {
		ByteString result = null;
	
		try {
			MsgSecretRequest msgSecretRequest = MsgSecretRequest.parseFrom(request.getBody().getSerializedContent());
			EReqSecretType secretType = msgSecretRequest.getRequireType();
			switch (secretType) {
			case SECRET_PLAYER_BASE://请求玩家秘境基础信息
				result = secretHandler.getSecretMember(player, msgSecretRequest);
				break;
			case SECRET_ADD_AREA://请求探索秘境数据
				result = secretHandler.addSecretArea(player, msgSecretRequest);
				break;
			case SECRET_INFO://请求盟友单个面板秘境数据
				result = secretHandler.getGuildSecretArea(player, msgSecretRequest);
				break;	
			case SECRET_ROLE_INFO://请求对象佣兵及主角数据
				result=secretHandler.getEnenmyInfo(player, msgSecretRequest);
				break;	
			case UPDATE_SECRET_ROLE_INFO://更新对象佣兵及主角数据id
				result=secretHandler.updatePlayerHeroList(player, msgSecretRequest);
			break;
			case SECRET_END_FIGHT://请求进攻记录信息
				result = secretHandler.endFightSecret(player,msgSecretRequest);
				break;
			case SECRET_BEGAIN_FIGHT://请求开始战斗
				result = secretHandler.startFightSecret(player,msgSecretRequest);
				break;	
			case SECRET_ATTRACK_GIFT://请求进攻记录信息
				result = secretHandler.getAttackGift(player,msgSecretRequest);
				break;
			case SECRET_SECRET_GIFT://领取奖励
				result = secretHandler.getGift(player,msgSecretRequest);
				break;
			case SECRET_FIND_ENEMY://查找敌方驻点
				result = secretHandler.findEnemy(player,msgSecretRequest);
				break;	
			case SECRET_CHAT_INVITE://邀请驻守
				result = secretHandler.chatInvite(player,msgSecretRequest);
				break;
			case SECRET_BUY_KEY://购买密钥
				result=secretHandler.buySecretKey(player,msgSecretRequest);
				break;
			default:
				break;
			}
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

}