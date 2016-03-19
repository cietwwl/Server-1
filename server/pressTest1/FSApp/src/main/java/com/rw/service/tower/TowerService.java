package com.rw.service.tower;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.RequestProtos.Request;
import com.rwproto.TowerServiceProtos.MsgTowerRequest;
import com.rwproto.TowerServiceProtos.eTowerType;

public class TowerService implements FsService {
	private TowerHandler towerHandler = TowerHandler.getInstance();
	public ByteString doTask(Request request, Player player) {
		ByteString result = null;
		try {
			MsgTowerRequest req = MsgTowerRequest.parseFrom(request.getBody().getSerializedContent());
			eTowerType towerType = req.getTowerType();
			switch (towerType) {	
				case TOWER_PANEL_INFO:
					result = towerHandler.getTowerPanelInfo(req,player);
					break;
				case TOWER_START_FIGHT:
					break;
				case TOWER_END_FIGHT:
					result = towerHandler.endFightTower(req,player);
					break;
				case TOWER_GET_REWARD:
					result = towerHandler.getAward(req,player);
					break;
				case TOWER_RESET_DATA:
					result = towerHandler.restTowerData(req,player);
					break;
				case TOWER_REQUIRE_ENEMY:
					result = towerHandler.getTowerEnemyInfo(req,player);
					break;
				default:
					break;
			}
			
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		
		return result;
	}

}


