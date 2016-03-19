//package com.rwbase.dao.gamble.pojo;
//
//import java.util.Calendar;
//import java.util.HashMap;
//
//import com.rwbase.dao.gamble.pojo.cfg.GambleCfg;
//import com.rwbase.dao.gamble.pojo.cfg.GambleCfgDAO;
//import com.rwproto.GambleServiceProtos.EGambleType;
//import com.rwproto.GambleServiceProtos.ELotteryType;
//
//public class GambleItemVo {
//	private GambleItem item;
//	public GambleItemVo(GambleItem mItem){
//		item = mItem;
//	}
//	
//	/**重置次数*/
//	public void resetCount(){
//		item.setSurplusOrdinaryCount(0);
//	}
//	
//	/**添加一次(花了钱的)*/
//	public void addGoldCount(EGambleType type, ELotteryType temp){
//		if(!item.getGoldCount().containsKey(type)){
//			item.getGoldCount().put(type, new HashMap<ELotteryType, Integer>());
//		}
//		item.getGoldCount().get(type).put(temp, getGoldCount(type, temp) + 1);
//	}
//	
//	/**
//	 * 获取垂钓类型抽奖次数(花了钱的)
//	 * @param type 垂钓类型
//	 * @param temp 抽奖类型
//	 * @return
//	 */
//	public int getGoldCount(EGambleType type, ELotteryType temp){
//		if(item.getGoldCount().containsKey(type)){
//			if(item.getGoldCount().get(type).containsKey(temp)){
//				return item.getGoldCount().get(type).get(temp);
//			}			
//		}
//		return 0;
//	}
//	
//	/**添加一次(免费的)*/
//	public void addFreeCount(EGambleType type, ELotteryType temp){
//		if(!item.getFreeCount().containsKey(type)){
//			item.getFreeCount().put(type, new HashMap<ELotteryType, Integer>());
//		}
//		item.getFreeCount().get(type).put(temp, getFreeCount(type, temp) + 1);
//	}
//	
//	/**
//	 * 获取垂钓类型抽奖次数(免费的)
//	 * @param type 垂钓类型
//	 * @param temp 抽奖类型
//	 * @return
//	 */
//	public int getFreeCount(EGambleType type, ELotteryType temp){
//		if(item.getFreeCount().containsKey(type)){
//			if(item.getFreeCount().get(type).containsKey(temp)){
//				return item.getFreeCount().get(type).get(temp);
//			}
//		}
//		return 0;
//	}
//	
//	/**这次抽奖是否可免费*/
//	public boolean isCanFree(EGambleType type, ELotteryType lotterType){
//		switch(lotterType){
//			case ONE:
//				switch(type){
//					case PRIMARY:
//						if(gambleCfg(type).getDayFreeCount() - item.getSurplusOrdinaryCount() > 0 && ordinaryTime() - 2 <= 0){
//							return true;
//						}
//						break;
//					case MIDDLE:
//						if(prayTime() - 2 <= 0){//这里的-2是为了容错，让服务端比客户端快2秒
//							return true;
//						}
//						break;
//					default:
//						return false;
//				}
//			break;
//		}		
//		return false;
//	}
//	
//	/**添加一次免费使用次数*/
//	public void setOneConsumption(EGambleType type){
//		switch(type){
//			case PRIMARY:
//				item.setSurplusOrdinaryCount(item.getSurplusOrdinaryCount() + 1);;
//				item.setLastOrdinaryTime(Calendar.getInstance().getTimeInMillis());
//				break;
//			case MIDDLE:
//				item.setLastPrayTime(Calendar.getInstance().getTimeInMillis());
//				break;
//			default:
//				break;
//		}
//	}
//	
//	public int ordinaryTime(){
//		if(item.getLastOrdinaryTime() == 0){
//			return 0;
//		}
//		return (gambleCfg(EGambleType.PRIMARY).getRefreshTime() * 1000 - 
//				(int)(Calendar.getInstance().getTimeInMillis() - item.getLastOrdinaryTime())) / 1000;
//	}
//	
//	public int prayTime(){
//		if(item.getLastPrayTime() == 0){
//			return 0;
//		}
//		return (gambleCfg(EGambleType.MIDDLE).getRefreshTime() * 1000 - 
//				(int)(Calendar.getInstance().getTimeInMillis() - item.getLastPrayTime())) / 1000;
//	}
//	
//	public GambleCfg gambleCfg(EGambleType type){
//		return GambleCfgDAO.getInstance().getGambleCfg(type);
//	}
//	
//	public GambleItem getGambleItem(){
//		return item;
//	}
//}
