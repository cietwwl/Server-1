package com.playerdata;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.common.Action;
import com.common.BeanCopyer;
import com.common.OutLong;
import com.common.OutString;
import com.log.GameLog;
import com.playerdata.readonly.FashionMgrIF;
import com.rw.service.Email.EmailUtils;
import com.rwbase.common.INotifyChange;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.common.attrdata.AttrDataIF;
import com.rwbase.dao.fashion.FashionBeingUsed;
import com.rwbase.dao.fashion.FashionBeingUsedHolder;
import com.rwbase.dao.fashion.FashionBuyRenewCfg;
import com.rwbase.dao.fashion.FashionCommonCfg;
import com.rwbase.dao.fashion.FashionCommonCfgDao;
import com.rwbase.dao.fashion.FashionEffectCfgDao;
import com.rwbase.dao.fashion.FashionItem;
import com.rwbase.dao.fashion.FashionItemHolder;
import com.rwbase.dao.fashion.FashionItemIF;
import com.rwbase.dao.fashion.FashionQuantityEffectCfgDao;
import com.rwbase.dao.fashion.FashionUsedIF;
import com.rwbase.dao.fashion.IEffectCfg;
import com.rwproto.ErrorService.ErrorType;
import com.rwproto.FashionServiceProtos;
import com.rwproto.FashionServiceProtos.FashionCommon;
import com.rwproto.FashionServiceProtos.FashionEventType;
import com.rwproto.FashionServiceProtos.FashionResponse;
import com.rwproto.FashionServiceProtos.FashionUsed;
import com.rwproto.MsgDef;

public class FashionMgr implements FashionMgrIF,INotifyChange{

	private static String ExpiredEMailID = "10030";
	private static String GiveEMailID ="10036";
	private static String ExpiredNotifycation = "您的时装%s已过期，请到试衣间续费";
	private Player m_player = null;
	private FashionItemHolder fashionItemHolder;
	private FashionBeingUsedHolder fashionUsedHolder;
	private boolean isInited = false;

	//缓存的增益数据，清空会重新计算
	private AttrData addedValueAttr = null;
	private AttrData addedPercentAttr = null;
	
	public void init(Player playerP){
		m_player = playerP;
		isInited = true;
		String userId = playerP.getUserId();
		fashionItemHolder = new FashionItemHolder(userId);
		fashionUsedHolder = new FashionBeingUsedHolder(userId);
	}
	
	public boolean isInited(){
		return isInited;
	}
	
	public void regChangeCallBack(final Action callBack){
		Action hook = new Action() {
			
			@Override
			public void doAction() {
				RecomputeBattleAdded();
				callBack.doAction();
			}
		};
		fashionItemHolder.regChangeCallBack(hook);
		fashionUsedHolder.regChangeCallBack(hook);
	}
	
	/**
	 * 增加时装
	 * @param cfg 不能为空
	 * @param buyCfg 不能为空
	 * @return
	 */
	public boolean buyFashionItemNotCheck(FashionCommonCfg cfg, FashionBuyRenewCfg buyCfg){
		FashionItem item = newFashionItem(cfg,buyCfg);
		fashionItemHolder.addItem(m_player, item);
		return item != null;
	}
	
	/**
	 * 延长时装有效期
	 * @param item 不能为空
	 * @param renewDay
	 */
	public void renewFashion(FashionItem item, int renewDay) {
		long now = System.currentTimeMillis();
		long expiredTime = item.getExpiredTime();
		if (expiredTime < now){
			//过期了，重新设置
			expiredTime = now;
		}
		// 更新购买/续费时间，和有效期
		item.setBuyTime(now);
		//在上次有效期内延长对应的时间，如果已经过期，使用当前时间作为基数
		expiredTime +=  TimeUnit.DAYS.toMillis(renewDay);
		item.setExpiredTime(expiredTime);
		// 更新时装，特殊效果并推送
		if (!updateFashionItem(item)){
			GameLog.error("时装", m_player.getUserId(), "更新续费后的时装失败,ID="+item.getId());
		}
	}
	
	/**
	 * 没有穿在身上的不能脱
	 * 不负责向客户端同步穿着数据，调用着根据需要进行同步
	 * @param fashionId
	 * @return
	 */
	public boolean takeOffFashion(int fashionId){
		FashionBeingUsed fashionUsed = getFashionBeingUsed();
		if (takeOff(fashionId,fashionUsed)){
			fashionUsedHolder.update(fashionUsed,true);
			// 兼容旧的逻辑，相当于调用changeFashState(fashionId, FashState.OFF)
			// 当删除FashionItem 的 state字段，这段逻辑就不再需要
			return true;
		}
		return false;
	}
	
	/**
	 * 不检查是否过期，调用者自行检查
	 * 不负责同步时装使用数据，调用着根据需要向客户端发送（机器人是不需要的！）
	 * 不能是已经穿在身上的，如果想换，必须先调用takeOffFashion脱了再穿
	 * @param fashionId
	 * @param tip
	 * @return
	 */
	public boolean putOnFashion(int fashionId,OutString tip){
		FashionBeingUsed fashionUsed = getFashionBeingUsed();
		if (isFashionBeingUsed(fashionId,fashionUsed)){
			LogError(tip,"时装已经穿上",",fashionId="+fashionId);
			return false;
		}
		
		FashionItem item = fashionItemHolder.getItem(fashionId);
		if (item == null){
			LogError(tip,"时装未购买",",fashionId="+fashionId);
			return false;
		}
		
		if (fashionUsed == null){
			//首次穿时装，初始化FashionBeingUsed
			fashionUsed = fashionUsedHolder.newFashion(m_player.getUserId());
		}
		
		if (putOn(item,fashionUsed)){
			fashionUsedHolder.update(fashionUsed,true);
			// 兼容旧的逻辑，相当于调用changeFashState(fashionId, FashState.ON)
			// 当删除FashionItem 的 state字段，这段逻辑就不再需要
			return true;
		}
		
		LogError(tip,"无法穿上时装",",类型不对,fashionId="+fashionId);
		return false;
	}

	private AttrDataIF lensOnEffect(IEffectCfg cfg,int lensNum){
		if (cfg == null) return null;
		if (lensNum == 0){
			return cfg.getAddedValues();
		}
		if (lensNum == 1){
			return cfg.getAddedPercentages();
		}
		return null;
	}
	private AttrData computeAttr(int lensNum){
		AttrData result = new AttrData();
		FashionBeingUsed used = getFashionBeingUsed();
		if (used!= null){
			int career = m_player.getCareer();
			int[] list = used.getUsingList();
			for (int i = 0; i < list.length; i++) {
				int fashionId = list[i];
				AttrDataIF data = lensOnEffect(FashionEffectCfgDao.getInstance().getConfig(fashionId,career),lensNum);
				if (data != null){
					result.plus(data);
				}
			}
		}
		AttrDataIF data = lensOnEffect(FashionQuantityEffectCfgDao.getInstance().searchOption(fashionItemHolder.getItemCount()),lensNum);
		if (data != null){
			result.plus(data);
		}
		return result;
	}
	public AttrData getAttrData(){
		if (addedValueAttr == null){
			AttrData attrData = computeAttr(0);
			/*
			AttrData attrData = new AttrData();
			FashionBeingUsed used = getFashionBeingUsed();
			if (used!= null){
				int career = m_pPlayer.getCareer();
				int[] list = used.getUsingList();
				for (int i = 0; i < list.length; i++) {
					int fashionId = list[i];
					IEffectCfg cfg = FashionEffectCfgDao.getInstance().getConfig(fashionId,career);
					if (cfg != null){
						attrData.plus(cfg.getAddedValues());
					}
				}
			}
			IEffectCfg cfg = FashionQuantityEffectCfgDao.getInstance().searchOption(fashionItemHolder.getItemCount());
			if (cfg != null){
				attrData.plus(cfg.getAddedValues());
			}
			*/
			addedValueAttr = attrData;
		}
		
		AttrData result=new AttrData();
		BeanCopyer.copy(addedValueAttr, result);
		return result;
	}
	
	public AttrData getPercentAttrData() {
		if (addedPercentAttr == null) {
			AttrData attrData = computeAttr(1);
			/*
			AttrData attrData = new AttrData();
			FashionBeingUsed used = getFashionBeingUsed();
			if (used != null) {
				int career = m_pPlayer.getCareer();
				int[] list = used.getUsingList();
				for (int i = 0; i < list.length; i++) {
					int fashionId = list[i];
					IEffectCfg cfg = FashionEffectCfgDao.getInstance().getConfig(fashionId, career);
					if (cfg != null) {
						attrData.plus(cfg.getAddedPercentages());
						// AttrData.fromPercentObject(cfg);
					}
				}
			}
			IEffectCfg cfg = FashionQuantityEffectCfgDao.getInstance().searchOption(fashionItemHolder.getItemCount());
			if (cfg != null){
				attrData.plus(cfg.getAddedPercentages());
			}
			*/
			addedPercentAttr = attrData;
		}
		
		AttrData result = new AttrData();
		BeanCopyer.copy(addedPercentAttr, result);
		return addedPercentAttr;
	}
	
	public boolean save() {
		fashionItemHolder.flush();
		return true;
	}
	
	public void onMinutes() {
		checkExpired();
	}
	/**
	 * 发送所有
	 */
	public void syncAll() {
		checkExpired();
		// 过期不会影响fashionItem存储的值
		//fashionItemHolder.synAllData(m_pPlayer, 0);
	}
	
	/**
	 * 过期判断
	 * @param fashionId
	 * @param tip 不能为空
	 * @return
	 */
	public boolean isExpired(int fashionId,OutString tip) {
		FashionItem item = fashionItemHolder.getItem(fashionId);
		return isExpired(fashionId,tip,item);
	}
	
	public FashionItem getItem(String itemId){
		return fashionItemHolder.getItem(itemId);
	}
	
	public FashionItem getItem(int itemId){
		return getItem(String.valueOf(itemId));
	}
	
	public FashionUsedIF getFashionUsed(String userId){
		return fashionUsedHolder.get(userId);
	}
	
	public FashionUsedIF getFashionUsed(){
		return fashionUsedHolder.get(m_player.getUserId());
	}
	
	public FashionUsed.Builder getFashionUsedBuilder(){
		return getFashionUsedBuilder(m_player.getUserId());
	}
	
	public FashionUsed.Builder getFashionUsedBuilder(String userId){
		FashionUsed.Builder fashionUsed = FashionUsed.newBuilder();
		FashionUsedIF fashion = getFashionUsed(userId);
		if (fashion  != null){
			if (fashion.getWingId() != -1)
				fashionUsed.setWingId(fashion.getWingId());
			if (fashion.getSuitId() != -1)
				fashionUsed.setSuitId(fashion.getSuitId());
			if (fashion.getPetId() != -1)
				fashionUsed.setPetId(fashion.getPetId());
			if (fashion.getTotalEffectPlanId() != -1)
				fashionUsed.setSpecialEffectId(fashion.getTotalEffectPlanId());
		}
		return fashionUsed;
	}
	
	/**
	 * 用于机器人的调用！
	 * @param fashionId
	 * @return
	 */
	public FashionItem giveFashionItem(int fashionId) {
		return giveFashionItem(fashionId,-1,null,false);
	}
	
	/**
	 * 赠送时装
	 * 有效期day设置为－1表示永久有效
	 * @param fashionId
	 * @param day
	 * @param userId
	 * @param sendEmail
	 */
	public FashionItem giveFashionItem(int fashionId,int day,String userId,boolean sendEmail){
		FashionCommonCfg fashionCfg = FashionCommonCfgDao.getInstance().getConfig(fashionId);
		if (fashionCfg == null) {
			return null;
		}
		FashionItem item = newFashionItem(fashionCfg,day);
		fashionItemHolder.addItem(PlayerMgr.getInstance().find(userId), item);
		
		if (sendEmail){
			List<String> args = new ArrayList<String>();
			args.add(fashionCfg.getName());
			EmailUtils.sendEmail(userId, GiveEMailID,args);
		}
		return item;
	}
	
	/**
	 * 向客户端发送时装穿着数据
	 */
	public void notifyFashionBeingUsedChanged() {
		// notify client that Fashion Being Used Changed!
		FashionResponse.Builder response = FashionResponse.newBuilder();
		response.setEventType(FashionEventType.getFashiondata);
		FashionCommon.Builder common = FashionCommon.newBuilder();
		FashionUsed.Builder fashion = m_player.getFashionMgr().getFashionUsedBuilder(m_player.getUserId());
		common.setUsedFashion(fashion);
		response.setFashionCommon(common);
		response.setError(ErrorType.SUCCESS);
		m_player.SendMsg(MsgDef.Command.MSG_FASHION, response.build().toByteString());
	}

	/**
	 * 职业改变
	 */
	public void changeSuitCareer(){
		// 修改设计之后，时装穿戴的数据与职业性别无关，因此无需修改！
		// 但是战斗属性增益跟职业有关系，需要重新计算增益
		RecomputeBattleAdded();
		
		/*
		List<FashionItem> list = fashionItemHolder.getItemList();
		for (FashionItem fasItem : list) {
			FashionCfg fashcfg = FashionCfgDao.getInstance().getConfig(fasItem.getId());
			if(fashcfg == null){
				continue;
			}
			if(fasItem.getType() == FashType.suit.ordinal()){
				FashionCfg newcfg = FashionCfgDao.getInstance().getConfig(fashcfg.getSuitId(),m_pPlayer.getCareer(),m_pPlayer.getSex());
				FashionItem newitem = newFash(newcfg);
				if(newitem != null){
					newitem.setState(fasItem.getState());
					fashionItemHolder.addItem(m_pPlayer, newitem);
				}
				fashionItemHolder.removeItem(m_pPlayer, fasItem);
			}
		}
		*/
	}
	
	@Override
	public List<FashionItemIF> search(ItemFilter predicate) {
		return fashionItemHolder.search(predicate);
	}

	private FashionItem newFashionItem(FashionCommonCfg cfg, FashionBuyRenewCfg buyCfg) {
		return newFashionItem(cfg,buyCfg.getDay());
	}
	
	private FashionItem newFashionItem(FashionCommonCfg cfg, int day) {
		FashionItem item = new FashionItem();
		item.setFashionId(cfg.getId());
		item.setType(cfg.getFashionType().ordinal());
		item.setUserId(m_player.getUserId());
		long now = System.currentTimeMillis();
		item.setBuyTime(now);
		if (day > 0) {
			item.setExpiredTime(now + TimeUnit.DAYS.toMillis(day));
		}else{
			item.setExpiredTime(-1);
		}
		return item;
	}
	
	private boolean updateFashionItem(FashionItem item){
		if (item != null){
			fashionItemHolder.updateItem(m_player, item);
			return true;
		}
		return false;
	}
	
	private boolean takeOff(int fashionId,FashionBeingUsed fashionUsed){
		if (fashionUsed != null){
			if (fashionUsed.getWingId() == fashionId){
				fashionUsed.setWingId(-1);
				return true;
			}
			if (fashionUsed.getSuitId() == fashionId) {
				fashionUsed.setSuitId(-1);
				return true;
			}
			if (fashionUsed.getPetId() == fashionId) {
				fashionUsed.setPetId(-1);
				return true;
			}
		}
		return false;
	}
	
	private boolean isFashionBeingUsed(int fashionId,FashionUsedIF fashionUsed){
		if (fashionUsed != null){
			if (fashionUsed.getWingId() == fashionId)
				return true;
			if (fashionUsed.getSuitId() == fashionId)
				return true;
			if (fashionUsed.getPetId() == fashionId)
				return true;
		}
		return false;
	}
	
	/**
	 * 传入的两个参数都不能为空！
	 * @param item
	 * @param fashionUsed
	 * @return
	 */
	private boolean putOn(FashionItem item,FashionBeingUsed fashionUsed){
		int fashionId = item.getFashionId();
		int typeInt = item.getType();
		
		if (FashionServiceProtos.FashionType.Wing_VALUE == typeInt) {
			fashionUsed.setWingId(fashionId);
			return true;
		}
		if (FashionServiceProtos.FashionType.Suit_VALUE == typeInt) {
			fashionUsed.setSuitId(fashionId);
			return true;
		}
		if (FashionServiceProtos.FashionType.Pet_VALUE == typeInt) {
			fashionUsed.setPetId(fashionId);
			return true;
		}
		return false;
	}
	
	private boolean isExpired(int fashionId,OutString tip,FashionItem item){
		OutLong expired=new OutLong();
		if (getExpiredTime(fashionId,tip,item,expired)){
			long now = System.currentTimeMillis();
			return (expired.value <= now);
		}
		return false;
	}
	
	/**
	 * getExpiredTime返回负数或零表示永久时装
	 * @param fashionId
	 * @param tip
	 * @param item
	 * @param time
	 * @return
	 */
	private boolean isExpired(int fashionId,OutString tip,FashionItem item,long time){
		OutLong expired=new OutLong();
		if (getExpiredTime(fashionId,tip,item,expired)){
			return (expired.value >0 && expired.value <= time);
		}
		return false;
	}
	
	/**
	 * 返回负数或零表示永久时装
	 * @param fashionId
	 * @param tip
	 * @param item
	 * @param expiredTime
	 * @return
	 */
	private boolean getExpiredTime(int fashionId,OutString tip,FashionItem item,OutLong expiredTime){
		if (item == null){
			LogError(tip,"时装未购买",",fashionId="+fashionId);
			return false;
		}
		long buyTime = item.getBuyTime();
		long expired = item.getExpiredTime();
		if (buyTime > expired){
			LogError(tip,"时装数据异常",",购买时间比到期时间迟！fashionId="+fashionId);
			return false;
		}
		expiredTime.value = expired;
		return true;
	}
	
	private void checkExpired() {
		List<FashionItem> list = fashionItemHolder.getItemList();
		if (list.isEmpty()) return;
		long now = System.currentTimeMillis();
		FashionBeingUsed fashionUsed = getFashionBeingUsed();
		boolean isBeingUsedChanged = false;
		OutString tip = new OutString();
		for (FashionItem fasItem : list) {
			int fashionId = fasItem.getFashionId();
			if (isExpired(fashionId,tip,fasItem,now)){
				if (takeOff(fashionId,fashionUsed)){
					isBeingUsedChanged = true;					
				}
				FashionCommonCfg fashcfg = FashionCommonCfgDao.getInstance().getConfig(fashionId);
				String fashionName = fashcfg.getName();
				if (fashcfg != null && !StringUtils.isBlank(fashionName)){
					List<String> args = new ArrayList<String>();
					args.add(fashionName);
					EmailUtils.sendEmail(m_player.getUserId(), ExpiredEMailID,args);
					m_player.NotifyCommonMsg(String.format(ExpiredNotifycation,fashionName));
				}
			}
		}
		if (isBeingUsedChanged){
			notifyFashionBeingUsedChanged();
		}
	}
	
	/**
	 * 重新计算战斗增益
	 * 与时装穿戴数据的改变有关(FashionBeingUsedHolder负责存储)
	 * 时装总数带来的增益(FashionItemHolder负责存储)
	 * 以及职业变更有关系
	 * 时装的穿戴，脱下和过期会导致改变
	 */
	private void RecomputeBattleAdded() {
		addedValueAttr = null;
		addedPercentAttr = null;
	}

	private void LogError(OutString tip,String userTip,String addedLog){
		tip.str = userTip;
		if (addedLog != null){
			GameLog.error("时装", m_player.getUserId(), tip.str+addedLog);
		}
	}

	private FashionBeingUsed getFashionBeingUsed(){
		return fashionUsedHolder.get(m_player.getUserId());
	}
}
