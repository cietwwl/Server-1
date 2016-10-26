package com.playerdata.activity.retrieve;

import java.util.List;

import com.playerdata.activity.retrieve.cfg.CostOrder;

public class ActivityRetrieveTypeHelper {

	public static String getItemId(String userId, ActivityRetrieveTypeEnum typeEnum) {
		return userId + "_" + typeEnum.getId();
	}

	public static int getCostByCountWithCostOrderList(List<CostOrder> list,int count){
		float cost = 0;
		int addWidth = 0;
		int widthTmp = 0;
		for(CostOrder costOrder : list){
			if(count <= widthTmp){
				break;
			}
			int tmp = count - costOrder.getWidth();
			tmp = tmp >= 0?costOrder.getWidth()- widthTmp:count-addWidth;
			cost += tmp*costOrder.getCost();
			addWidth +=tmp;			
			widthTmp = costOrder.getWidth();
		}		
		return (int)cost;
	}
	
	
}
