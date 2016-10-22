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

	
	public SaloonPlayer getSaloonPlayer(String userId){
		
		Player player = PlayerMgr.getInstance().findPlayerFromMemory(userId);
		if(player == null){
			return null;
		}
		
		return toPlayer( player);
	}


	public SaloonPlayer toPlayer(Player player) {
		String userId = player.getUserId();
		SaloonPlayer saloonPlayer = new SaloonPlayer();
		saloonPlayer.setId(userId);
		saloonPlayer.setUserName(player.getUserName());
		saloonPlayer.setLevel(player.getLevel());
		saloonPlayer.setImageId(player.getHeadImage());
		saloonPlayer.setCareer(player.getCareer());
		saloonPlayer.setSex(player.getSex());
		saloonPlayer.setCareerLevel(player.getStarLevel());
		saloonPlayer.setFightingAll(player.getHeroMgr().getFightingAll(player));
		saloonPlayer.setModelId(player.getModelId());
		saloonPlayer.setStarLevel(player.getStarLevel());
		saloonPlayer.setQualityId(player.getHeroMgr().getMainRoleHero(player).getQualityId());
		
		FashionBeingUsedHolder holder = FashionBeingUsedHolder.getInstance();
		FashionBeingUsed fashUsed = holder.get(userId);		
		
		if (fashUsed != null){
			SaloonPlayerFashion fashion = SaloonPlayerFashion.from(fashUsed);
			saloonPlayer.setPlayerFashion(fashion);
		}
		ItemData magicItem = player.getMagic();
		if(null != magicItem){
			SaloonMagic magic = SaloonMagic.from(magicItem);
			saloonPlayer.setMagic(magic);
		}
		return saloonPlayer;
	}
}
