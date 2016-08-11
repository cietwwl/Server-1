package com.playerdata.battleVerify;

import com.google.protobuf.ByteString;
import com.monster.cfg.CopyMonsterCfg;
import com.monster.cfg.CopyMonsterCfgDao;
import com.playerdata.Player;
import com.playerdata.army.ArmyInfo;
import com.playerdata.dataEncode.DataEncoder;
import com.rwproto.BattleVerifyProto.BattleVerifyComReqMsg;
import com.rwproto.BattleVerifyProto.BattleVerifyComRspMsg;
import com.rwproto.BattleVerifyProto.CopyReqMsg;


public class BattleVerifyHandler {
	
	private static BattleVerifyHandler instance = new BattleVerifyHandler();
	
	public static BattleVerifyHandler getInstance(){
		return instance;
	}

	public ByteString verify(Player player, BattleVerifyComReqMsg commonReq) {
		BattleVerifyComRspMsg.Builder response = BattleVerifyComRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		
		CopyReqMsg copyReqMsg = commonReq.getCopyReqMsg();
		String copyId = copyReqMsg.getCopyId();
		String md5 = copyReqMsg.getCode();
		
		CopyMonsterCfg config = CopyMonsterCfgDao.getInstance().getConfig(copyId);
		ArmyInfo armyInfo = getArmyInfo(config);

		boolean success = DataEncoder.verify(armyInfo, md5);	
		
		response.setIsSuccess(success);	
		
		return response.build().toByteString();
	}

	private ArmyInfo getArmyInfo(CopyMonsterCfg config) {
		// TODO Auto-generated method stub
		return null;
	}
	
	


}
