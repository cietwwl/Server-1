package com.playerdata.battleVerify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.Hero;
//import com.monster.cfg.CopyMonsterCfg;
//import com.monster.cfg.CopyMonsterCfgDao;
import com.playerdata.Player;
import com.playerdata.dataEncode.DataEncoder;
import com.playerdata.hero.core.FSHeroMgr;
import com.rwproto.BattleVerifyProto.BattleVerifyComReqMsg;
import com.rwproto.BattleVerifyProto.BattleVerifyComRspMsg;
import com.rwproto.BattleVerifyProto.BattleVerifyMsg;
import com.rwproto.BattleVerifyProto.HeroVerifyData;
import com.rwproto.BattleVerifyProto.TeamVerifyData;


public class BattleVerifyHandler {
	
	private static BattleVerifyHandler instance = new BattleVerifyHandler();
	
	public static BattleVerifyHandler getInstance(){
		return instance;
	}

	public ByteString verify(Player player, BattleVerifyComReqMsg commonReq) {
		BattleVerifyComRspMsg.Builder response = BattleVerifyComRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		
//		CopyReqMsg copyReqMsg = commonReq.getCopyReqMsg();
//		String copyId = copyReqMsg.getCopyId();
//		String md5 = copyReqMsg.getCode();
		
//		CopyMonsterCfg config = CopyMonsterCfgDao.getInstance().getConfig(copyId);
//		ArmyInfo armyInfo = getArmyInfo(config);

//		boolean success = DataEncoder.verify(armyInfo, md5);	
//		
//		response.setIsSuccess(success);	
		
		return response.build().toByteString();
	}

//	private ArmyInfo getArmyInfo(CopyMonsterCfg config) {
//		return null;
//	}
	
	private void verifyTeamData(Player player, TeamVerifyData teamData) {
		List<HeroVerifyData> dataList = teamData.getVerifyDataList();
		List<String> allHeroIds = new ArrayList<String>();
		Map<String, String> md5Map = new HashMap<String, String>();
		String userId = teamData.getUserId(); // 第一个是userId
		for (HeroVerifyData data : dataList) {
			allHeroIds.add(data.getUuid());
			md5Map.put(data.getUuid(), data.getMd5());
		}
		List<Hero> heros = FSHeroMgr.getInstance().getHeros(userId, allHeroIds);
		for (Hero hero : heros) {
			String attrMd5 = DataEncoder.encodeAttrData(hero.getAttrMgr().getTotalAttrData());
			String clientMd5 = md5Map.get(hero.getUUId());
			if (!attrMd5.equals(clientMd5)) {
				GameLog.error("BattleVerifyHandler", player.getUserId(), "==========校验不通过，heroId=" + hero.getId() + "，服务器的md5：" + attrMd5 + "，客户端md5：" + clientMd5 + "，队伍所属的user：" + userId + "=========");
			}
		}
	}
	
	public ByteString verifyArmyInfo(Player player, BattleVerifyMsg msg) {
		List<TeamVerifyData> teamDataList = msg.getVerifyTeamDataList();
		for (TeamVerifyData teamData : teamDataList) {
			verifyTeamData(player, teamData);
		}
		return ByteString.EMPTY;
	}

}
