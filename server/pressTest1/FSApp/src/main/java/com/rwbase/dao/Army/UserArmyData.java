package com.rwbase.dao.Army;

import javax.persistence.Id;
import javax.persistence.Table;

import com.playerdata.dataSyn.annotation.SynClass;


/**
 * 装备信息
 * @author allen
 *
 */
@Table(name = "user_army_data")
public class UserArmyData
{
	@Id
	private String userId; 
	
	private ArmyItem arenaArmy;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public ArmyItem getArenaArmy() {
		return arenaArmy;
	}

	public void setArenaArmy(ArmyItem arenaArmy) {
		this.arenaArmy = arenaArmy;
	}
	
	

	
	
}
