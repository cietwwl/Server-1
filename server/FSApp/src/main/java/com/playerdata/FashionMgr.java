package com.playerdata;


import java.util.ArrayList;
import java.util.List;

import com.common.Action;
import com.playerdata.readonly.FashionMgrIF;
import com.rw.service.Email.EmailUtils;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.common.enu.ECareer;
import com.rwbase.common.enu.ESex;
import com.rwbase.dao.fashion.FashState;
import com.rwbase.dao.fashion.FashType;
import com.rwbase.dao.fashion.FashionBeingUsedHolder;
import com.rwbase.dao.fashion.FashionCfg;
import com.rwbase.dao.fashion.FashionCfgDao;
import com.rwbase.dao.fashion.FashionItem;
import com.rwbase.dao.fashion.FashionItem.FashionType;
import com.rwbase.dao.fashion.FashionItemHolder;
import com.rwbase.dao.fashion.FashionItemIF;

public class FashionMgr implements FashionMgrIF{

	private Player m_pPlayer = null;
	private FashionItemHolder fashionItemHolder;
	private FashionBeingUsedHolder fashionUsedHolder;
	private boolean isInited = false;
	private static ItemFilter swingOnItemPred = new ItemFilter() {
		
		@Override
		public boolean accept(FashionItemIF item) {
			
			return item != null && item.getState() == FashState.ON.ordinal()
					&& item.getType() == FashionType.Wing.ordinal();
		}
	};

	public ItemFilter getSwingOnItemPred(){
		return swingOnItemPred;
	}

	public void init(Player playerP){
		m_pPlayer = playerP;
		isInited = true;
		String userId = playerP.getUserId();
		fashionItemHolder = new FashionItemHolder(userId);
		fashionUsedHolder = new FashionBeingUsedHolder(userId);
	}
	
	public boolean isInited(){
		return isInited;
	}
	
	public void regChangeCallBack(Action callBack){
		fashionItemHolder.regChangeCallBack(callBack);
	}
	/**
	 * 购买或续费时装
	 * @param id
	 */
	public FashionItem buyFash(int id){
		return buyFash(String.valueOf(id));
	}
	
	public FashionItem buyFash(String id){
		FashionCfg cfg = FashionCfgDao.getInstance().getConfig(id);
		if(cfg == null || (cfg.getCareer() != m_pPlayer.getCareer() && cfg.getCareer() != ECareer.None.ordinal()
				&& cfg.getSex() != m_pPlayer.getSex() && cfg.getSex() != ESex.None.getOrder())){
			return null;
		}
		FashionItem item = getItem(id);
		if(item == null){
			item = newFash(cfg);
			fashionItemHolder.addItem(m_pPlayer, item);
			
		}else{
			item.setState(FashState.OFF.ordinal());
			item.setBuyTime(System.currentTimeMillis());
			fashionItemHolder.updateItem(m_pPlayer, item);
		}
		return item;
	}
	
	
	private FashionItem newFash(FashionCfg cfg) {
		FashionItem item = new FashionItem();
		item.setId(cfg.getId());
		item.setType(cfg.getType());
		item.setUserId(m_pPlayer.getUserId());
		item.setState(FashState.OFF.ordinal());
		item.setBuyTime(System.currentTimeMillis());
		return item;
	}
	
	public boolean save() {
		fashionItemHolder.flush();
		return true;
	}
	
	/**
	 * 修改时装状态
	 * @param id
	 * @param state
	 */
	public void changeFashState(int id,FashState state){
		changeFashState(String.valueOf(id),state);
	}
	
	public void changeFashState(String id,FashState state){	
		FashionItem item = fashionItemHolder.getItem(id);
		if(item != null){
			if(state == FashState.ON){
				List<FashionItem> list = fashionItemHolder.getItemList();
				for (FashionItem fasItem : list) {
					if(fasItem.getState() == FashState.ON.ordinal() &&
						fasItem.getType() == item.getType()){//将原来同类型的装备的先脱下
						fasItem.setState(FashState.OFF.ordinal());
						fashionItemHolder.updateItem(m_pPlayer, fasItem);
						break;
					}
				}
			}
			item.setState(state.ordinal());
			fashionItemHolder.updateItem(m_pPlayer, item);
		}
	}
//	/**
//	 * 属性修改
//	 * @param state
//	 * @param cfg
//	 */
//	private void setAttr(FashState state,FashionCfg cfg){
//		if(cfg == null || !StringUtils.isNotBlank(cfg.getAddAttr())){
//			return;
//		}
//		String[] attrList = cfg.getAddAttr().split(",");
//		for (String attrItem : attrList) {
//			int value = Integer.valueOf(attrItem.split("_")[1]);
//			value = state == FashState.ON ? value : -value;
//			eAttrIdDef def = eAttrIdDef.getDef(Integer.valueOf(attrItem.split("_")[0]));
//			m_pPlayer.getAttrMgr().addFashionAttr(def, value);
//		}
//	}
	
	public AttrData getAttrData(){
		AttrData attrData = new AttrData();
		List<FashionItem> list = fashionItemHolder.getItemList();
		for (FashionItem fasItem : list) {
			if(fasItem.getState() == FashState.ON.ordinal()){
				FashionCfg fashcfg = FashionCfgDao.getInstance().getConfig(fasItem.getId());
				if(fashcfg!=null){
					attrData.plus(AttrData.fromObject(fashcfg));
				}
				break;
			}
		}
		return attrData;
		
	}
	public AttrData getPercentAttrData(){
		AttrData attrData = new AttrData();
		List<FashionItem> list = fashionItemHolder.getItemList();
		for (FashionItem fasItem : list) {
			if(fasItem.getState() == FashState.ON.ordinal()){
				FashionCfg fashcfg = FashionCfgDao.getInstance().getConfig(fasItem.getId());
				if(fashcfg!=null){
					attrData.plus(AttrData.fromPercentObject(fashcfg));
				}
				break;
			}
		}
		return attrData;
		
	}
	
	public void onMinutes() {
		checkExpired();
	}
	/**
	 * 发送所有
	 */
	public void syncAll() {
		checkExpired();
		fashionItemHolder.synAllData(m_pPlayer, 0);
	}
	
	/**
	 * 过期判断
	 */
	private void checkExpired() {
		
		List<FashionItem> list = fashionItemHolder.getItemList();
		for (FashionItem fasItem : list) {
			if(fasItem.getState() == FashState.EXPIRED.ordinal()){
				continue;
			}
			FashionCfg fashcfg = FashionCfgDao.getInstance().getConfig(fasItem.getId());
			if(fashcfg == null){
				continue;
			}
			long val = fashcfg.getValidity();
			long buytime = val * 60 * 60 * 1000 + fasItem.getBuyTime();
			long now = System.currentTimeMillis();
			
			if(buytime < now){
				List<String> args = new ArrayList<String>();
				args.add(fashcfg.getName());
				EmailUtils.sendEmail(m_pPlayer.getUserId(), "10030",args);
				m_pPlayer.NotifyCommonMsg("您的时装" + fashcfg.getName() + "已过期，请到试衣间续费");
				fasItem.setState(FashState.EXPIRED.ordinal());
				fashionItemHolder.updateItem(m_pPlayer, fasItem);
			}
		}
	}
	
	public FashionItem getItem(String itemId){
		return fashionItemHolder.getItem(itemId);
	}
	
	public FashionItem getItem(int itemId){
		return getItem(String.valueOf(itemId));
	}
	
	/**
	 * 职业改变
	 */
	public void changeSuitCareer(){
		List<FashionItem> list = fashionItemHolder.getItemList();
		for (FashionItem fasItem : list) {
			FashionCfg fashcfg = FashionCfgDao.getInstance().getConfig(fasItem.getId());
			if(fashcfg == null){
				continue;
			}
			if(fasItem.getType() == FashType.suit.ordinal()){
				FashionCfg newcfg = FashionCfgDao.getInstance().getConfig(fashcfg.getSuitId(),m_pPlayer.getCareer(),m_pPlayer.getSex());
				FashionItem newitem =  newFash(newcfg);
				if(newitem != null){
					newitem.setState(fasItem.getState());
					fashionItemHolder.addItem(m_pPlayer, newitem);
				}
				fashionItemHolder.removeItem(m_pPlayer, fasItem);
			}
		}
	}
	
	@Override
	public List<FashionItemIF> search(ItemFilter predicate) {
		return fashionItemHolder.search(predicate);
	}

	
}
