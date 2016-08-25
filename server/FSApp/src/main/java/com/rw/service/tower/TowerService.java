package com.rw.service.tower;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.RequestProtos.Request;
import com.rwproto.TowerServiceProtos.MsgTowerRequest;
import com.rwproto.TowerServiceProtos.eTowerType;

public class TowerService implements FsService<MsgTowerRequest, eTowerType> {
	private TowerHandler towerHandler = TowerHandler.getInstance();

	@Override
	public ByteString doTask(MsgTowerRequest request, Player player) {
		// TODO Auto-generated method stub
		ByteString result = null;
		try {
			eTowerType towerType = request.getTowerType();
			switch (towerType) {
			case TOWER_PANEL_INFO:
				result = towerHandler.getTowerPanelInfo(request, player);
				break;
			case TOWER_START_FIGHT:// TODO HC 至少要在开战之前加个数据验证吧。。
				break;
			case TOWER_END_FIGHT:
				result = towerHandler.endFightTower(request, player);
				break;
			case TOWER_GET_REWARD:
				result = towerHandler.getAward(request, player);
				break;
			case TOWER_RESET_DATA:
				result = towerHandler.restTowerData(request, player);
				break;
			case TOWER_REQUIRE_ENEMY:
				result = towerHandler.getTowerEnemyInfo(request, player);
				break;
			default:
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public MsgTowerRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		MsgTowerRequest req = MsgTowerRequest.parseFrom(request.getBody().getSerializedContent());
		return req;
	}

	@Override
	public eTowerType getMsgType(MsgTowerRequest request) {
		// TODO Auto-generated method stub
		return request.getTowerType();
	}

}
