package com.bm.login;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.rwbase.dao.user.CfgBuyCoin;
import com.rwbase.dao.user.CfgBuyCoinDAO;
import com.rwbase.dao.user.CfgBuyPower;
import com.rwbase.dao.user.CfgBuyPowerDAO;
import com.rwbase.dao.user.UserGameData;
import com.rwbase.dao.user.UserGameDataDao;
import com.rwbase.dao.user.account.UserPurse;



public class UserBM {

	private static UserBM instance = new UserBM();
	private CfgBuyCoinDAO cfgBuyCoinDAO = CfgBuyCoinDAO.getInstance();
	private CfgBuyPowerDAO cfgBuyPowerDAO = CfgBuyPowerDAO.getInstance();
	private UserPurseBM userPurseBLL = UserPurseBM.getInstance();
	
	public static UserBM getInstance(){
		return instance;
	}
	
	private UserGameDataDao userDAO = UserGameDataDao.getInstance();
	

	public UserGameData getByUserId(String userId) {
		
		if (StringUtils.isBlank(userId)) {
			return null;
		}
		UserGameData user = userDAO.get(userId);
		return user;
	}
	
	public boolean update(UserGameData user) {
		boolean success = true;
		if (!userDAO.update(user)) {
			GameLog.info("userToken", user.getUserId(), "userToken 更新失败,res=-1", null);
			success = false;
		}
		return success;
	}
	
	public CfgBuyCoin getCfgBuyCoin(int times){
		CfgBuyCoin cfgBuyCoin = (CfgBuyCoin) cfgBuyCoinDAO.getCfgById(String.valueOf(times));
		return cfgBuyCoin;
	}
	
	public CfgBuyPower getCfgBuyPower(int times){
		CfgBuyPower cfgBuyPower = (CfgBuyPower) cfgBuyPowerDAO.getCfgById(String.valueOf(times));
		return cfgBuyPower;
	}
	
	
	public int buyPower(UserGameData user,int times){
		CfgBuyPower cfgBuyPower = getCfgBuyPower(times);
		UserPurse userPurse = userPurseBLL.getUserPurseById(user.getUserId());
		userPurse.setDiamond(userPurse.getDiamond() - cfgBuyPower.getNeedPurse());
		if(times == user.getBuyPowerTimes() + 1 && userPurseBLL.updateUserPurse(userPurse)){
			user.setPower(user.getPower() + cfgBuyPower.getPower());
			user.setBuyPowerTimes(user.getBuyPowerTimes() + 1);
			update(user);
		}
		return user.getPower();
	}
	
	
	
	public int decreaseCoin(UserGameData user,int decreaseCount){//扣除铜钱
		if(decreaseCount <= 0){
			return -1;//参数有误
		}
		if(user == null){
			return -2;//用户数据有误
		}
		if(user.getCoin() < decreaseCount){
			return -3;//铜钱不足
		}
		user.setCoin(user.getCoin()  - decreaseCount);
		int result = this.update(user) ? 0 : -4;
		if(result == 0){
			
			GameLog.info("user", user.getUserId(), "扣除铜钱更新 成功,res="+result, null);
		}else{
			
			GameLog.info("user", user.getUserId(), "扣除铜钱更新  失败,res="+result, null);
		}
		return result;
	}
	
	
}
