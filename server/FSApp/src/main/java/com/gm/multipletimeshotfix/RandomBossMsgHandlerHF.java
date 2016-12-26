package com.gm.multipletimeshotfix;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.bm.randomBoss.RandomBossMgr;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.embattle.EmbattleHeroPosition;
import com.playerdata.embattle.EmbattleInfoMgr;
import com.playerdata.embattle.EmbattlePositionInfo;
import com.playerdata.hero.core.FSHeroMgr;
import com.rw.service.ranodmBoss.RandomBossMsgHandler;
import com.rwproto.BattleCommon.eBattlePositionType;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;
import com.rwproto.RandomBossProto.MsgType;
import com.rwproto.RandomBossProto.RandomBossMsgResponse;
import com.rwproto.RandomBossProto.RandomMsgRequest;

public class RandomBossMsgHandlerHF extends RandomBossMsgHandler {

	private void synHeroInfo(Player player) {
		EmbattlePositionInfo positionInfo = EmbattleInfoMgr.getMgr().getEmbattlePositionInfo(player.getUserId(), eBattlePositionType.Normal_VALUE, "");
		if (positionInfo != null) {
			List<EmbattleHeroPosition> posList = positionInfo.getPos();
			List<String> heroIds = new ArrayList<String>(posList.size());
			for (EmbattleHeroPosition heroPos : posList) {
				heroIds.add(heroPos.getId());
			}
			List<Hero> heros = FSHeroMgr.getInstance().getHeros(player, heroIds);
			for (Hero h : heros) {
				ClientDataSynMgr.synData(player, h.getAttrMgr().getRoleAttrData(), eSynType.ROLE_ATTR_ITEM, eSynOpType.UPDATE_SINGLE, -1);
			}
		} else {
			List<Hero> allHeros = FSHeroMgr.getInstance().getAllHeros(player, null);
			for (Hero h : allHeros) {
				ClientDataSynMgr.synData(player, h.getAttrMgr().getRoleAttrData(), eSynType.ROLE_ATTR_ITEM, eSynOpType.UPDATE_SINGLE, -1);
			}
		}
	}
	
	/**
	 * 请求进入boss战斗
	 * @param player
	 * @param request
	 * @return
	 */
	public ByteString applyEnterBattle(Player player, RandomMsgRequest request) {
		
		String bossID = request.getBossID();
		RandomBossMsgResponse.Builder response = RandomBossMsgResponse.newBuilder();
		response.setMsgType(MsgType.APPLY_BATTLE);
		if(StringUtils.isBlank(bossID)){
			response.setIsSuccess(false);
			response.setTips("参数错误!");
			return response.build().toByteString();
		}
		try {
			
			RandomBossMgr.getInstance().applyEnterBattle(player,bossID, response);
			
		} catch (Exception e) {
			GameLog.error("RandomBoss", "RandomBossMsgHandler[applyEnterBattle]", "请求进入boss战时出现异常", e);
			response.setIsSuccess(false);
			response.setTips("服务器繁忙");
		}
		
		synHeroInfo(player);
		return response.build().toByteString();
	}
}
