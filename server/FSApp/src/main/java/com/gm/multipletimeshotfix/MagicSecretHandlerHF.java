package com.gm.multipletimeshotfix;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.ByteString;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.embattle.EmbattleHeroPosition;
import com.playerdata.embattle.EmbattleInfoMgr;
import com.playerdata.embattle.EmbattlePositionInfo;
import com.playerdata.hero.core.FSHeroMgr;
import com.playerdata.mgcsecret.manager.MagicSecretMgr;
import com.playerdata.mgcsecret.service.MagicSecretHandler;
import com.rwproto.BattleCommon.eBattlePositionType;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;
import com.rwproto.MagicSecretProto.MagicSecretReqMsg;
import com.rwproto.MagicSecretProto.MagicSecretRspMsg;

public class MagicSecretHandlerHF extends MagicSecretHandler {

	private void synHeroInfo(Player player) {
		EmbattlePositionInfo positionInfo = EmbattleInfoMgr.getMgr().getEmbattlePositionInfo(player.getUserId(), eBattlePositionType.MagicSecret_VALUE, "");
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
	
	public ByteString enterMSFight(Player player, MagicSecretReqMsg msgMSRequest) {
		MagicSecretRspMsg.Builder msRsp = MagicSecretRspMsg.newBuilder();
		msRsp.setReqType(msgMSRequest.getReqType());
		MagicSecretMgr msMgr = MagicSecretMgr.getInstance();
		msMgr.enterMSFight(player, msRsp, msgMSRequest.getDungeonId());
		synHeroInfo(player);
		return msRsp.build().toByteString();
	}
}
