package com.rw.service.hero;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.HeroServiceProtos.MsgHeroRequest;
import com.rwproto.HeroServiceProtos.eHeroType;
import com.rwproto.RequestProtos.Request;

public class HeroService implements FsService<MsgHeroRequest, eHeroType> {

	private HeroHandler heroHandler = HeroHandler.getInstance();


	@Override
	public ByteString doTask(MsgHeroRequest request, Player player) {
		// TODO Auto-generated method stub
		ByteString result = null;
		// TODO Auto-generated method stub
		try {
			eHeroType eHeroType = request.getHeroType();
			switch (eHeroType) {
			case SUMMON_HERO:
				result = heroHandler.summonHero(player, request);
				break;
			case EVOLUTION_HERO:
				result = heroHandler.upgradeHeroStar(player, request.getHeroId());
				break;
			case USE_EXP:
				result = heroHandler.useHeroExp(player, request);
				break;
			case USE_EXP_MAX:// 一键升级到上限
				result = heroHandler.useHeroExpMax(player, request);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public MsgHeroRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		MsgHeroRequest msgHeroRequest = MsgHeroRequest.parseFrom(request.getBody().getSerializedContent());
		return msgHeroRequest;
	}

	@Override
	public eHeroType getMsgType(MsgHeroRequest request) {
		// TODO Auto-generated method stub
		return request.getHeroType();
	}

}
