package com.common.beanCopy.bak;

import com.playerdata.army.ArmyInfo;

public class ArmyInfoCopyer implements ICopy<ArmyInfo,ArmyInfo>{

	@Override
	public void copy(ArmyInfo source, ArmyInfo target) {
		
		source.setHeroList(target.getHeroList());
		source.setPlayer(target.getPlayer());
		source.setArmyMagic(target.getArmyMagic());
		source.setPlayerName(target.getPlayerName());
		source.setPlayerHeadImage(target.getPlayerHeadImage());
		source.setGuildName(target.getGuildName());
		source.setArmyFashion(target.getArmyFashion());


		
		
	}

	
	
}
