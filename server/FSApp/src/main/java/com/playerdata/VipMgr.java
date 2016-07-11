package com.playerdata;

import java.util.Calendar;

import com.playerdata.common.PlayerEventListener;
import com.playerdata.readonly.VipMgrIF;
import com.rw.fsutil.common.stream.IStream;
import com.rw.fsutil.common.stream.StreamImpl;
import com.rw.fsutil.util.DateUtils;
import com.rw.service.Privilege.IPrivilegeProvider;
import com.rw.service.Privilege.datamodel.ChargeTypePriority;
import com.rw.service.Privilege.datamodel.VipPrivilegeHelper;
import com.rwbase.common.enu.EPrivilegeDef;
import com.rwbase.dao.vip.VipDataHolder;
import com.rwbase.dao.vip.pojo.TableVip;

public class VipMgr implements IPrivilegeProvider, VipMgrIF,PlayerEventListener{
	private VipDataHolder vipDataHolder;
	private int m_oldVip;
	
	private Player m_pPlayer;
	
	//by franky 实现特权系统与Vip的对接
	private StreamImpl<IPrivilegeProvider> vipPrivilegeProvider = new StreamImpl<IPrivilegeProvider>();
	@Override
	public IStream<IPrivilegeProvider> getPrivilegeProvider() {
		return vipPrivilegeProvider;
	}

	@Override
	public int getBestMatchCharge(String[] sources) {
		return VipPrivilegeHelper.getShareInstance().getBestMatchCharge(sources, m_pPlayer.getVip());
	}

	@Override
	public String getCurrentChargeType() {
		return ChargeTypePriority.vipPrefix+m_pPlayer.getVip();
	}

	@Override
	public boolean reachChargeLevel(String chargeType) {
		return VipPrivilegeHelper.getShareInstance().reachChargeLevel(chargeType, m_pPlayer.getVip());
	}
	
	@Override
	public boolean hasChargeType(String chargeType) {
		return VipPrivilegeHelper.getShareInstance().extractVipLevel(chargeType)>=0;
	}
	//by franky end

	@Override
	public void notifyPlayerCreated(Player player) {
	}
	
	@Override
	public void notifyPlayerLogin(Player player) {
		initVipPrivilege();
	}
	
	public void init(Player playerP) {
		m_pPlayer = playerP;
		vipDataHolder = new VipDataHolder(m_pPlayer.getUserId());
	}
	
	public void syn(int version){
		vipDataHolder.syn(m_pPlayer, version);
	}
	
	private void refreshConst(TableVip tableVip) {
		
		tableVip.addPrivilege(EPrivilegeDef.BUY_SKILL_POINT_OPEN,GetMaxPrivilege(EPrivilegeDef.BUY_SKILL_POINT_OPEN));
		tableVip.addPrivilege(EPrivilegeDef.EVER_SPECIAL_STORE1_OPEN,GetMaxPrivilege(EPrivilegeDef.EVER_SPECIAL_STORE1_OPEN));
		tableVip.addPrivilege(EPrivilegeDef.EVER_SPECIAL_STORE_2OPEN,GetMaxPrivilege(EPrivilegeDef.EVER_SPECIAL_STORE_2OPEN));
		tableVip.addPrivilege(EPrivilegeDef.GOLD_MOPUP_OPEN,GetMaxPrivilege(EPrivilegeDef.GOLD_MOPUP_OPEN));
		tableVip.addPrivilege(EPrivilegeDef.ONEKEY_ADD_SPIRIT_OPEN,GetMaxPrivilege(EPrivilegeDef.ONEKEY_ADD_SPIRIT_OPEN));
		tableVip.addPrivilege(EPrivilegeDef.ONEKEY_MOPUP_TEN_OPEN,GetMaxPrivilege(EPrivilegeDef.ONEKEY_MOPUP_TEN_OPEN));
		tableVip.addPrivilege(EPrivilegeDef.PEAK_SPORT_RESET_CD_OPEN,GetMaxPrivilege(EPrivilegeDef.PEAK_SPORT_RESET_CD_OPEN));
		tableVip.addPrivilege(EPrivilegeDef.THIRD_CHESTS_OPEN,GetMaxPrivilege(EPrivilegeDef.THIRD_CHESTS_OPEN));
		tableVip.addPrivilege(EPrivilegeDef.FASHION_BUY_OPEN,GetMaxPrivilege(EPrivilegeDef.FASHION_BUY_OPEN));
		tableVip.addPrivilege(EPrivilegeDef.RESIGN_OPEN,GetMaxPrivilege(EPrivilegeDef.RESIGN_OPEN));
		
	}
	private void refreshVar(TableVip tableVip){
		tableVip.addPrivilege(EPrivilegeDef.COPY_COUNT,GetMaxPrivilege(EPrivilegeDef.COPY_COUNT));
		tableVip.addPrivilege(EPrivilegeDef.EXPEDITION_COUNT,GetMaxPrivilege(EPrivilegeDef.EXPEDITION_COUNT));
		tableVip.addPrivilege(EPrivilegeDef.MONEY_COUNT,GetMaxPrivilege(EPrivilegeDef.MONEY_COUNT));
		tableVip.addPrivilege(EPrivilegeDef.MOPUP_COUNT,GetMaxPrivilege(EPrivilegeDef.MOPUP_COUNT));
		tableVip.addPrivilege(EPrivilegeDef.POWER_COUNT,GetMaxPrivilege(EPrivilegeDef.POWER_COUNT));
		tableVip.addPrivilege(EPrivilegeDef.SPORT_BUY_COUNT,GetMaxPrivilege(EPrivilegeDef.SPORT_BUY_COUNT));
		tableVip.addPrivilege(EPrivilegeDef.TRIAL1_COPY_RESET_TIMES,GetMaxPrivilege(EPrivilegeDef.TRIAL1_COPY_RESET_TIMES));
		tableVip.addPrivilege(EPrivilegeDef.TRIAL2_COPY_RESET_TIMES,GetMaxPrivilege(EPrivilegeDef.TRIAL2_COPY_RESET_TIMES));
		tableVip.addPrivilege(EPrivilegeDef.WARFARE_COPY_RESET_TIMES,GetMaxPrivilege(EPrivilegeDef.WARFARE_COPY_RESET_TIMES));
		tableVip.addPrivilege(EPrivilegeDef.SECRET_COPY_COUNT,GetMaxPrivilege(EPrivilegeDef.SECRET_COPY_COUNT));
		tableVip.addPrivilege(EPrivilegeDef.PEAK_ARENA_RESET_TIMES,GetMaxPrivilege(EPrivilegeDef.PEAK_ARENA_RESET_TIMES));
		tableVip.addPrivilege(EPrivilegeDef.ARENA_RESET_TIMES,GetMaxPrivilege(EPrivilegeDef.ARENA_RESET_TIMES));
		
		tableVip.addPrivilege(EPrivilegeDef.COPY_CELESTAL,GetMaxPrivilege(EPrivilegeDef.ARENA_RESET_TIMES));
		tableVip.addPrivilege(EPrivilegeDef.TOWER_RESET_TIMES,GetMaxPrivilege(EPrivilegeDef.ARENA_RESET_TIMES));
		tableVip.addPrivilege(EPrivilegeDef.BATTLE_TOWER_TIMES,GetMaxPrivilege(EPrivilegeDef.ARENA_RESET_TIMES));
		
	}
	
	public void upgradeVipRefreshPrivilege(int oldVip){
		TableVip vipTable = vipDataHolder.get();
		refreshConst(vipTable);
		m_oldVip = oldVip;
		getVipVar(vipTable);
		
		//by franky vip修改成功后发出通知
		vipPrivilegeProvider.fire(this);
	}
	
	public void initVipPrivilege(){
		TableVip tableVip = vipDataHolder.get();
		refreshConst(tableVip);
		getVipVar(tableVip);
	}
	
	public void getVipVar(TableVip vipTable){
		vipTable.addPrivilege(EPrivilegeDef.COPY_COUNT,getValue(EPrivilegeDef.COPY_COUNT, vipTable));
		vipTable.addPrivilege(EPrivilegeDef.EXPEDITION_COUNT,getValue(EPrivilegeDef.EXPEDITION_COUNT, vipTable));
		vipTable.addPrivilege(EPrivilegeDef.MONEY_COUNT,getValue(EPrivilegeDef.MONEY_COUNT, vipTable));
		vipTable.addPrivilege(EPrivilegeDef.MOPUP_COUNT,getValue(EPrivilegeDef.MOPUP_COUNT, vipTable));
		vipTable.addPrivilege(EPrivilegeDef.POWER_COUNT,getValue(EPrivilegeDef.POWER_COUNT, vipTable));
		vipTable.addPrivilege(EPrivilegeDef.SPORT_BUY_COUNT,getValue(EPrivilegeDef.SPORT_BUY_COUNT, vipTable));
		vipTable.addPrivilege(EPrivilegeDef.TRIAL1_COPY_RESET_TIMES,getValue(EPrivilegeDef.TRIAL1_COPY_RESET_TIMES, vipTable));
		vipTable.addPrivilege(EPrivilegeDef.TRIAL2_COPY_RESET_TIMES,getValue(EPrivilegeDef.TRIAL2_COPY_RESET_TIMES, vipTable));
		vipTable.addPrivilege(EPrivilegeDef.WARFARE_COPY_RESET_TIMES,getValue(EPrivilegeDef.WARFARE_COPY_RESET_TIMES, vipTable));
		vipTable.addPrivilege(EPrivilegeDef.SECRET_COPY_COUNT,getValue(EPrivilegeDef.SECRET_COPY_COUNT, vipTable));
		vipTable.addPrivilege(EPrivilegeDef.PEAK_ARENA_RESET_TIMES,getValue(EPrivilegeDef.PEAK_ARENA_RESET_TIMES, vipTable));
		vipTable.addPrivilege(EPrivilegeDef.ARENA_RESET_TIMES,getValue(EPrivilegeDef.ARENA_RESET_TIMES, vipTable));
		
		vipTable.addPrivilege(EPrivilegeDef.COPY_CELESTAL,GetMaxPrivilege(EPrivilegeDef.ARENA_RESET_TIMES));
		vipTable.addPrivilege(EPrivilegeDef.TOWER_RESET_TIMES,GetMaxPrivilege(EPrivilegeDef.ARENA_RESET_TIMES));
		vipTable.addPrivilege(EPrivilegeDef.BATTLE_TOWER_TIMES,GetMaxPrivilege(EPrivilegeDef.ARENA_RESET_TIMES));
		vipDataHolder.update(m_pPlayer);
		
		int value = GetMaxPrivilege(EPrivilegeDef.SKILL_POINT_COUNT) - GetPrivilegeInCfg(m_oldVip, EPrivilegeDef.SKILL_POINT_COUNT);
		if(value >0){
			m_pPlayer.getMainRoleHero().getSkillMgr().updateSkillPointTime(EPrivilegeDef.SKILL_POINT_COUNT,value);
		}
	}
	
	private int getValue(EPrivilegeDef type,TableVip tableVip){
		int value = GetMaxPrivilege(type) - GetPrivilegeInCfg(m_oldVip, type);
		if (tableVip.containPrivilege(type)) {
			value += tableVip.getPrivilege(type);
		} else {
			value = GetMaxPrivilege(type);
		}
		value = value > 0 ? value : 0;
		return value;
	}
	
	/**
	 * 添加当天特权次数
	 * @param count
	 * @param type
	 * @return 减掉成功与否
	 */
	public boolean subPrivilege(int count,EPrivilegeDef type){
		return addPrivilege(-count, type);
	}
	/**
	 * 添加当天特权次数
	 * @param count
	 * @param type
	 * @return 减掉成功与否
	 */
	public boolean addPrivilege(int count,EPrivilegeDef type){
		TableVip tableVip = vipDataHolder.get();
		timeRefresh(tableVip);
		int newValue = tableVip.getPrivilege(type) + count;
		if(newValue < 0)return false;
		tableVip.addPrivilege(type, newValue);
		vipDataHolder.update(m_pPlayer);
		return true;
	}
	
	private boolean timeRefresh(TableVip tableVip){

		Calendar today = Calendar.getInstance();
		Calendar lastDay = DateUtils.getCalendar(tableVip.getLastRefreshTime());
		boolean isVipDataChanged = false;
		if(DateUtils.dayChanged(lastDay) &&  today.get(Calendar.HOUR_OF_DAY) >= 5){
			tableVip.setLastRefreshTime(System.currentTimeMillis());
			refreshConst(tableVip);
			refreshVar(tableVip);
			isVipDataChanged = true;
		}
		return isVipDataChanged;
	}
	/**
	 * 获取主角VIP特权
	 * @param type
	 * @return
	 */
	public int GetPrivilege(EPrivilegeDef type)
	{
		TableVip tableVip = vipDataHolder.get();
		boolean isVipDataChanged  = timeRefresh(tableVip);
		if(isVipDataChanged){
			vipDataHolder.update(m_pPlayer);
		}
		return privilege(type);
	}
	public void flush() {
		vipDataHolder.flush();
	}

	/**
	 * 获取主角VIP配置表特权
	 * @param type
	 * @return
	 */
	public int GetMaxPrivilege(EPrivilegeDef type){
		return GetPrivilegeInCfg(m_pPlayer.getVip(), type);
	}

	private int GetPrivilegeInCfg(int vip,EPrivilegeDef type){
		//by franky 重新按照特权系统的数据进行映射！
		return VipPrivilegeHelper.getShareInstance().getDef(vip, type);
		//return PrivilegeCfgDAO.getInstance().getDef(vip, type);
	}
	
	/**
	 *  获取主角特权
	 * @param type
	 * @param vip
	 * @return
	 */
	private int privilege(EPrivilegeDef type){
		return vipDataHolder.get().getPrivilege(type);
	}
	
	public boolean isVipGiftTaken(int vipLevel){
		return vipDataHolder.get().isVipGiftTaken(vipLevel);
	}
	public void setVipGiftTaken(int vipLevel){
		vipDataHolder.get().setLevelVipGiftTaken(vipLevel);
		vipDataHolder.update(m_pPlayer);
	}

	public void failToBuyVipGift(int vipLevel){
		vipDataHolder.get().failToBuyVipGift(vipLevel);
		vipDataHolder.update(m_pPlayer);
	}
	
}