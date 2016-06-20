package com.rw.service.hero;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.HeroServiceProtos.MsgHeroRequest;
import com.rwproto.HeroServiceProtos.eHeroType;
import com.rwproto.RequestProtos.Request;

public class HeroService implements FsService {

	// private static MainService instance = new MainService();
	private HeroHandler heroHandler = HeroHandler.getInstance();

	// private MainService(){}
	// public static MainService getInstance(){
	// return instance;
	// }
	public ByteString doTask(Request request, Player player) {
		ByteString result = null;
		// TODO Auto-generated method stub
		try {
			MsgHeroRequest msgHeroRequest = MsgHeroRequest.parseFrom(request.getBody().getSerializedContent());
			eHeroType eHeroType = msgHeroRequest.getHeroType();
			switch (eHeroType) {
			// case UPGRADE_QUALITY:
			// result = heroHandler.upgradeQuality(player, msgHeroRequest);
			// break;
			// case USE_EQUIPMENT:
			// result = heroHandler.useEquipMent(player, msgHeroRequest);
			// break;
			case SUMMON_HERO:
				result = heroHandler.summonHero(player, msgHeroRequest);
				break;
			case EVOLUTION_HERO:
				result = heroHandler.upgradeHeroStar(player, msgHeroRequest.getHeroId());
				break;
			// case EQUIP_STRENGTH:
			// result = heroHandler.strenghEquipMent(player, msgHeroRequest);
			// break;
			// case ONEKEY_STRENGTH:
			// result = heroHandler.oneKeytrenghEquipMent(player, msgHeroRequest);
			// break;

			case USE_EXP:
				result = heroHandler.useHeroExp(player, msgHeroRequest);
				break;
			case USE_EXP_MAX:// 一键升级到上限
				result = heroHandler.useHeroExpMax(player, msgHeroRequest);
				break;
			// case BUY_SKILL_POINT:
			// result = heroHandler.buyHeroSkill(player, msgHeroRequest);
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
