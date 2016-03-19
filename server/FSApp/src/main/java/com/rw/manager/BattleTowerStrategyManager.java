//package com.rw.manager;
//
//import java.util.List;
//import java.util.concurrent.ConcurrentHashMap;
//
//import com.rwbase.dao.battletower.pojo.cfg.dao.BattleTowerFloorCfgDao;
//import com.rwbase.dao.battletower.pojo.db.TableBattleTowerStrategy;
//import com.rwbase.dao.battletower.pojo.db.dao.TableBattleTowerStrategyDao;
//
///*
// * @author HC
// * @date 2015年9月3日 上午10:29:49
// * @Description 试练塔战略管理类
// */
//public class BattleTowerStrategyManager {
//	/** 战略数据集合 */
//	private static ConcurrentHashMap<Integer, TableBattleTowerStrategy> strategyMap;
//
//	/**
//	 * 打算在服务器启动的时候加载一次这个试练塔战略中的数据
//	 */
//	public static void init() {
//		List<String> groupList = BattleTowerFloorCfgDao.getCfgDao().getGroupList();
//		int size = groupList.size();
//
//		strategyMap = new ConcurrentHashMap<Integer, TableBattleTowerStrategy>(size);
//
//		TableBattleTowerStrategyDao dao = TableBattleTowerStrategyDao.getDao();
//		for (int i = 0; i < size; i++) {
//			String groupId = groupList.get(i);
//			TableBattleTowerStrategy tableBattleTowerStrategy = dao.get(groupId);
//			if (tableBattleTowerStrategy != null) {
//				strategyMap.put(Integer.valueOf(groupId), tableBattleTowerStrategy);
//			}
//		}
//	}
//
//	/**
//	 * 获取试练塔某个里程碑中的战略数据
//	 * 
//	 * @param groupId
//	 * @return
//	 */
//	public static TableBattleTowerStrategy getTableBattleTowerStrategy(int groupId) {
//		return strategyMap.get(groupId);
//	}
// }