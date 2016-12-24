package com.gm.multipletimeshotfix;

import java.util.ArrayList;
import java.util.List;

import com.bm.groupCopy.GroupCopyResult;
import com.google.protobuf.ByteString;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.embattle.EmbattleHeroPosition;
import com.playerdata.embattle.EmbattleInfoMgr;
import com.playerdata.embattle.EmbattlePositionInfo;
import com.playerdata.hero.core.FSHeroMgr;
import com.rw.service.groupCopy.GroupCopyBattleHandler;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.groupCopy.cfg.GroupCopyLevelCfg;
import com.rwbase.dao.groupCopy.cfg.GroupCopyLevelCfgDao;
import com.rwbase.dao.groupCopy.cfg.GroupCopyMapCfg;
import com.rwbase.dao.groupCopy.cfg.GroupCopyMapCfgDao;
import com.rwproto.GroupCopyBattleProto;
import com.rwproto.BattleCommon.eBattlePositionType;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;
import com.rwproto.GroupCopyBattleProto.GroupCopyBattleComReqMsg;
import com.rwproto.GroupCopyBattleProto.GroupCopyBattleComRspMsg;
import com.rwproto.GroupCopyBattleProto.GroupCopyMonsterData;
import com.rwproto.GroupCopyBattleProto.RequestType;

public class GroupCopyBattleHandlerHF extends GroupCopyBattleHandler {

	private static void synHeroInfo(Player player) {
		EmbattlePositionInfo positionInfo = EmbattleInfoMgr.getMgr().getEmbattlePositionInfo(player.getUserId(), eBattlePositionType.GroupCopy_VALUE, "");
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
	 * TODO 协议有改动 详见 {@link GroupCopyBattleProto};
	 * @param player
	 * @param req
	 * @return
	 */
	public ByteString beginFight(Player player, GroupCopyBattleComReqMsg req) {
		GroupCopyBattleComRspMsg.Builder commonRsp = GroupCopyBattleComRspMsg.newBuilder();
		commonRsp.setReqType(RequestType.FIGHT_BEGIN);	
	
		Group group = com.rw.service.group.helper.GroupHelper.getGroup(player);
		boolean success = false;
		if(group!=null){
			String levelID = req.getLevel();
			int groupLevel = group.getGroupBaseDataMgr().getGroupData().getGroupLevel();
			GroupCopyLevelCfg levelCfg = GroupCopyLevelCfgDao.getInstance().getConfig(levelID);
			GroupCopyMapCfg mapCfg = GroupCopyMapCfgDao.getInstance().getCfgById(levelCfg.getChaterID());
			if(mapCfg.getUnLockLv() > groupLevel){
				commonRsp.setTipMsg("帮派等级不足，无法进入战斗！");
			}else{
				GroupCopyResult result = group.getGroupCopyMgr().beginFight(player, levelID);
				
				success = result.isSuccess();
				if(success){
					commonRsp.setMData((GroupCopyMonsterData.Builder) result.getItem());
				}
				commonRsp.setTipMsg(result.getTipMsg());
			}
		}	

		commonRsp.setIsSuccess(success);
		synHeroInfo(player);
		return commonRsp.build().toByteString();
	}
}
