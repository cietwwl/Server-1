package com.bm.saloon;

import com.bm.saloon.data.SaloonMagic;
import com.bm.saloon.data.SaloonPlayer;
import com.bm.saloon.data.SaloonPlayerFashion;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rwbase.dao.fashion.FashionBeingUsed;
import com.rwbase.dao.fashion.FashionBeingUsedHolder;
import com.rwbase.dao.item.pojo.ItemData;

public class SaloonHelper {
	
	public static SaloonHelper instance = new SaloonHelper();
	
	public static SaloonHelper getInstance(){
		return instance;
	}

	
	public SaloonPlayer getPlayer(String userId){
		
		Player player = PlayerMgr.getInstance().findPlayerFromMemory(userId);
		if(player == null){
			return null;
		}
		SaloonPlayer infoBuilder = new SaloonPlayer();
		infoBuilder.setUserId(userId);
		infoBuilder.setUserName(player.getUserName());
		infoBuilder.setLevel(player.getLevel());
		infoBuilder.setImageId(player.getHeadImage());
		infoBuilder.setCareer(player.getCareer());
		infoBuilder.setSex(player.getSex());
		infoBuilder.setCareerLevel(player.getStarLevel());
		infoBuilder.setFightingAll(player.getHeroMgr().getFightingAll(player));
		infoBuilder.setModelId(player.getModelId());
		infoBuilder.setStarLevel(player.getStarLevel());
		infoBuilder.setQualityId(player.getHeroMgr().getMainRoleHero(player).getQualityId());
		
		FashionBeingUsedHolder holder = FashionBeingUsedHolder.getInstance();
		FashionBeingUsed fashUsed = holder.get(userId);		
		
		if (fashUsed != null){
			SaloonPlayerFashion fashion = SaloonPlayerFashion.from(fashUsed);
			infoBuilder.setPlayerFashion(fashion);
		}
		ItemData magicItem = player.getMagic();
		if(null != magicItem){
			SaloonMagic magic = SaloonMagic.from(magicItem);
			infoBuilder.setMagic(magic);
		}
		return null;
	}
}
