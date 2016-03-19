package com.rwbase.dao.Army;


public class UserArmyDataHolder{
	
	private static UserArmyDataHolder armyItemHolder = new UserArmyDataHolder();
	
	private UserArmyDataDAO armyItemDAO = UserArmyDataDAO.getInstance();
	
	public static UserArmyDataHolder getInstance() {
		return armyItemHolder;
	}


	
	public UserArmyData getItem(String userId){	
		UserArmyData userArmyData = armyItemDAO.get(userId);
		if(userArmyData == null){
			UserArmyData userArmyDataTmp = new UserArmyData();
			userArmyDataTmp.setUserId(userId);
			boolean addSuccess = armyItemDAO.add(userArmyDataTmp);
			if(addSuccess){
				userArmyData = userArmyDataTmp;
			}
		}
		
		return userArmyData;
	}
	
	public boolean update(UserArmyData item){
		
		return armyItemDAO.update(item);
	}

}
