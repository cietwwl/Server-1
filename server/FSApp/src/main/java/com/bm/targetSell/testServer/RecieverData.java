package com.bm.targetSell.testServer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.timer.Timer;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.bm.targetSell.TargetSellManager;
import com.bm.targetSell.net.TargetSellOpType;
import com.bm.targetSell.param.TargetSellAbsArgs;
import com.bm.targetSell.param.TargetSellData;
import com.bm.targetSell.param.TargetSellSendRoleItems;
import com.rw.fsutil.util.fastjson.FastJsonUtil;
import com.rwbase.dao.targetSell.BenefitItems;
import com.rwbase.dao.user.User;
import com.rwbase.dao.user.UserDataDao;

public class RecieverData {

	private static RecieverData instance = new RecieverData();
	private ConcurrentHashMap<String, Integer> roleDataMap = new ConcurrentHashMap<String, Integer>();
	
	protected RecieverData(){
	}
	
	public static RecieverData getInstance(){
		return instance;
	}
	
	public String tranformData(String content){
		TargetSellData data = FastJsonUtil.deserialize(content, TargetSellData.class);
		String roleID = checkReturn(data);
		if(StringUtils.isBlank(roleID)){
			return null;
		}
		User user = UserDataDao.getInstance().getByUserId(roleID);
		TargetSellData returnData = TargetSellData.create(TargetSellOpType.OPTYPE_5004);
		TargetSellSendRoleItems items = new TargetSellSendRoleItems();
		items = TargetSellManager.getInstance().initCommonParam(items, user.getChannelId(), roleID, user.getAccount());
		items.setActionName("all");
		List<BenefitItems> itemList = new ArrayList<BenefitItems>();
		itemList.add(getBenefitItems(roleID));
		items.setItems(itemList);
		returnData.setArgs(TargetSellManager.getInstance().toJsonObj(items));
		return TargetSellManager.getInstance().toJsonString(returnData);
	}

	private BenefitItems getBenefitItems(String roleID){
		BenefitItems item = new BenefitItems();
		long finishTime = System.currentTimeMillis() + Timer.ONE_DAY * 5;
		item.setFinishTime((int) (finishTime/1000));
		item.setItemGroupId(roleDataMap.get(roleID));
		item.setItemIds("802003*2,804001*5,803002,801003*3");
		item.setPushCount(3);
		item.setRecharge(100);
		item.setTitle("顶戴礼物");
		item.setZlass(3);
		return item;
	}
	
	private String checkReturn(TargetSellData data) {
		String str = "";
		int opType = data.getOpType();
		if(opType == TargetSellOpType.OPTYPE_5006 || opType == TargetSellOpType.OPTYPE_5004){
			TargetSellAbsArgs args = JSONObject.toJavaObject(data.getArgs(), TargetSellAbsArgs.class);
			Integer count = roleDataMap.get(args.getRoleId());
			if(count % 10 != 0){
				str = args.getRoleId();
			}
		}
		return str;
	}
	
	
	
}
