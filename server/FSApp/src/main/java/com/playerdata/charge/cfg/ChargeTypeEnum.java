package com.playerdata.charge.cfg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.charge.IChargeAction;
import com.playerdata.charge.action.CommonChargeAction;
import com.playerdata.charge.action.EmptyChargeAction;
import com.playerdata.charge.action.MonthCardChargeAction;

public enum ChargeTypeEnum {
	None("0", new EmptyChargeAction()),
	Normal("1", new CommonChargeAction()),//普通充值
	MonthCard("2", new MonthCardChargeAction()	),//月卡
	VipMonthCard("3", new MonthCardChargeAction()	);//至尊终身卡
	
	private String cfgId;
	private IChargeAction action;
	
	private static final Map<String, ChargeTypeEnum> _mapByCfgId ;
	private static final List<ChargeTypeEnum> _orderList;
	static {
		ChargeTypeEnum[] allValues = values();
		List<ChargeTypeEnum> orderList = new ArrayList<ChargeTypeEnum>(allValues.length);
		Map<String, ChargeTypeEnum> map = new HashMap<String, ChargeTypeEnum>(allValues.length, 1.5f);
		for (ChargeTypeEnum e : allValues) {
			map.put(e.getCfgId(), e);
			orderList.add(e);
		}
		_mapByCfgId = Collections.unmodifiableMap(map);
		_orderList = Collections.unmodifiableList(orderList);
	}
	
	private ChargeTypeEnum (String cfgId, IChargeAction pAction){
		this.cfgId = cfgId;
		this.action = pAction;
	}
	
	public String getCfgId(){
		return cfgId;
	}
	
	public IChargeAction getAction() {
		return action;
	}
	
	public String getName(){
		if(StringUtils.equals(cfgId, "2")){
			return "月卡";
		}else if(StringUtils.equals(cfgId, "3")){
			return "至尊终身卡";
		}
		return "";
	}
	
	public static List<ChargeTypeEnum> getOrderList() {
		return _orderList;
	}
	
	public static ChargeTypeEnum getById(String cfgId) {
		return _mapByCfgId.get(cfgId);
	}
}

