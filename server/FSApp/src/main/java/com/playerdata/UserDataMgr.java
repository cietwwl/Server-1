package com.playerdata;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.common.PlayerEventListener;
import com.rw.service.log.infoPojo.ZoneLoginInfo;
import com.rw.service.log.infoPojo.ZoneRegInfo;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserDataHolder;
import com.rwbase.dao.user.readonly.TableUserIF;


public class UserDataMgr implements PlayerEventListener{
	
	private Player player;// 角色
	private UserDataHolder userDataHolder;
	private int entranceId;

	public UserDataMgr(Player player, String userId) {
		this.player = player;
		userDataHolder = new UserDataHolder(userId);
	}

	@Override
	public void notifyPlayerCreated(Player player) {
	}

	@Override
	public void notifyPlayerLogin(Player player) {
		
	}
	
	public void syn(int version) {
		userDataHolder.syn(player, version);
	}

	/**
	 * 保存数据
	 */
	public boolean flush() {
		userDataHolder.flush();
		return true;
	}
	
	public void setLevel(int newLevel){
		userDataHolder.get().setLevel(newLevel);
		userDataHolder.update(player);
	}
	
	public void setHeadId(String headImage) {
		if (StringUtils.isNotBlank(headImage)) {
			userDataHolder.get().setHeadImage(headImage);
			userDataHolder.update(player);
		}
	}
	
	public boolean setUserName(String nick) {
		
		if (StringUtils.isNotBlank(nick)) {
			User user = userDataHolder.get();
			String oldName = user.getUserName();
			if(oldName.equals(nick)){
				return false;
			}
			if(userDataHolder.updateUserName(player, nick)){
				user.setUserName(nick);
				userDataHolder.update(player);
				return true;
			}
		}
		return false;
	}	

	public void setSex(int sex) {
		userDataHolder.get().setSex(sex);
		userDataHolder.update(player);
	}
	
	public void setVip(int vip) {
		userDataHolder.get().setVip(vip);
		userDataHolder.update(player);
	}
	
	public int getZoneId() {
		return userDataHolder.get().getZoneId();
	}
	
	public TableUserIF getReadOnly(){
		return (TableUserIF)userDataHolder.get();
	}
	
	public int getVip() {
		return userDataHolder.get().getVip();
	}
	public String getAccount() {
		return userDataHolder.get().getAccount();
	}
	public String getUserName() {
		return userDataHolder.get().getUserName();
	}

	public String getHeadImage() {
		return userDataHolder.get().getHeadImageWithDefault();
	}
	public int getSex() {
		return userDataHolder.get().getSex();
	}
	public long getCreateTime() {
		return userDataHolder.get().getCreateTime();
	}

	public void setLastLoginTime(long time){
		userDataHolder.get().setLastLoginTime(time);
		userDataHolder.update(player);
	}
	public void setKickOffCoolTime(){
		final long coolTimeSpan = 5*60*1000;
		userDataHolder.get().setKickOffCoolTime(System.currentTimeMillis()+coolTimeSpan);
		userDataHolder.update(player);
	}
	
	public long getKickOffCoolTime(){
		return userDataHolder.get().getKickOffCoolTime();
	}
	
	public void block(String reason, long blockCoolTime){
		userDataHolder.get().block(reason, blockCoolTime);
		userDataHolder.update(player);
	}
	
	
	public void chatBan(String reason, long blockCoolTime){
		userDataHolder.get().chatBan(reason, blockCoolTime);
		userDataHolder.update(player);
	}
	
	public String getChatBanReason(){
		return userDataHolder.get().getChatBanReason();
	}
	
	public long getChatBanCoolTime(){
		return userDataHolder.get().getChatBanCoolTime();
	}
	
	
	public ZoneRegInfo getZoneRegInfo(){
		return userDataHolder.get().getZoneRegInfo();
	}
	
	public ZoneLoginInfo getZoneLoginInfo(){
		return userDataHolder.get().getZoneLoginInfo();
	}
	public boolean isBlocked(){
		return userDataHolder.get().isBlocked();
	}
	
	public boolean isChatBan(){
		return userDataHolder.get().isChatBan();
	}
	
	public User getUser(){
		return userDataHolder.get();
	}
	
	public long getUnblockTime() {

		long blockCoolTime = userDataHolder.get().getBlockCoolTime();

		// -1 表示永久封号，直接赋值
		long blockTimeDiffInSecond = blockCoolTime;
		if (blockCoolTime > 0) {
			blockTimeDiffInSecond = blockCoolTime / 1000;
		}

		return blockTimeDiffInSecond;
	}
	
	public long getUnbanTime() {
		long banCoolTime = userDataHolder.get().getChatBanCoolTime();
		if (banCoolTime > 0) {
			banCoolTime = banCoolTime / 1000;
		}
		return banCoolTime;
	}

	@Override
	public void init(Player player) {
		// TODO Auto-generated method stub
		
	}

	public int getEntranceId() {
		return entranceId;
	}

	public void setEntranceId(int entranceId) {
		this.entranceId = entranceId;
	}

	


}