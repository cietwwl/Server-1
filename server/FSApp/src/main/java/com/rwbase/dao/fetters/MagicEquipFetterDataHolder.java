package com.rwbase.dao.fetters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rw.fsutil.cacheDao.MapItemStoreCache;
import com.rw.fsutil.cacheDao.mapItem.MapItemStore;
import com.rwbase.common.MapItemStoreFactory;
import com.rwbase.dao.fetters.pojo.MagicEquipFetterRecord;
import com.rwbase.dao.fetters.pojo.SynMagicEquipFetterData;
import com.rwbase.dao.fetters.pojo.cfg.MagicEquipConditionCfg;
import com.rwbase.dao.fetters.pojo.cfg.dao.FetterMagicEquipCfgDao;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;


/**
 * 法宝神器羁绊holder
 * @author Alex
 *
 * 2016年7月18日 上午9:54:58
 */
public class MagicEquipFetterDataHolder {

	
	private static final eSynType syType = eSynType.MAGICEQUIP_FETTER;
	
	private AtomicInteger dataVersion = new AtomicInteger(0);
	
	private final String userID;

	
	
	public MagicEquipFetterDataHolder(String userID) {
		this.userID = userID;
		checkRecord();
	}
	

	/**
	 * 检查是否有记录，如果没有就新生成
	 */
	private MagicEquipFetterRecord checkRecord() {
		MagicEquipFetterRecord item = getItemStore().getItem(userID);
		if(item == null){
			item = new MagicEquipFetterRecord();
			item.setId(userID);
			item.setUserId(userID);
			getItemStore().addItem(item);
		}
		return item;
	}


	/**
	 * 同步所有法宝神器羁绊数据
	 * @param player
	 * @param version 版本 0表示强制同步
	 */
	public void synAllData(Player player, int version){
		if(version != 0 && version == dataVersion.get()){
			return;
		}
		MagicEquipFetterRecord item = getItemStore().getItem(userID);
		if(item == null){
			item = checkRecord();
			return;
		}
		if(item.isEmpty()){
			return;
		}
//		StringBuffer sb = new StringBuffer("同步羁绊数据：");
//		for (Integer id : item.getAllFetters()) {
//			sb.append("[").append(id).append("]");
//		}
//		System.out.println(sb.toString());
		SynMagicEquipFetterData synData = new SynMagicEquipFetterData(userID, item.getAllFetters());
				
		ClientDataSynMgr.synData(player, synData, syType, eSynOpType.UPDATE_SINGLE);
		
	}
	
	private MapItemStore<MagicEquipFetterRecord> getItemStore(){
		MapItemStoreCache<MagicEquipFetterRecord> itemStoreCache = MapItemStoreFactory.getMagicEquipFetterCache();
		return itemStoreCache.getMapItemStore(userID, MagicEquipFetterRecord.class);
	}
	
	/**
	 * 检查英雄羁绊数据
	 * @param tempSet
	 * @param modelId
	 */
	public void checkFixEquipFetterRecord(Set<MagicEquipConditionCfg> tempSet, int modelId) {
		MagicEquipFetterRecord item = getItemStore().getItem(userID);
		if(item == null){
			item = checkRecord();
		}
		
		List<Integer> fetterIDs = item.getFixEquipFetters();


		List<MagicEquipConditionCfg> existType = new ArrayList<MagicEquipConditionCfg>();
		for (MagicEquipConditionCfg cfg : tempSet) {
			if(fetterIDs.contains(cfg.getUniqueId())){
				existType.add(cfg);
			}
		}
		
		//去掉数据库里已经存在的
		tempSet.removeAll(existType);
		if(tempSet.isEmpty()){
			return;
		}
		
		existType.clear();
		List<Integer> clearOld = new ArrayList<Integer>();
		//检查数据库里有没有相同类型的旧数据
		for (Integer id : fetterIDs) {
			MagicEquipConditionCfg cfg = FetterMagicEquipCfgDao.getInstance().getCfgById(String.valueOf(id));
			
			for (MagicEquipConditionCfg fetter : tempSet) {
				if(fetter.getUniqueId() != id && cfg.getType() == fetter.getType() && cfg.getSubType() == fetter.getSubType()){
					if(cfg.recordOldData()){
						//要保留的旧记录，可能是降星之前的, 判断一下哪个等级高
						if(cfg.getConditionLevel() >= fetter.getConditionLevel()){
							//新的记录没有超过旧的，保留旧记录
							existType.add(fetter);
						}else{
							//超过了，去掉旧的
							clearOld.add(id);
							break;
						}
					}else{
						//不用保留，直接清理
						clearOld.add(id);
						break;
					}
				}
			}
		}
		
		
		tempSet.removeAll(existType);
		fetterIDs.removeAll(clearOld);
		if(tempSet.isEmpty() && clearOld.isEmpty()){
			return;
		}
		for (MagicEquipConditionCfg cfg : tempSet) {
			fetterIDs.add(cfg.getUniqueId());
		}
		item.setFixEquipFetters(fetterIDs);
		getItemStore().updateItem(item);
		dataVersion.incrementAndGet();
		
	}
	
	/**
	 * 检查数据库内法宝羁绊记录是否与当前集合一致，如果没有则进行添加
	 * @param curCfgs
	 * @param modelID TODO 英雄modelID
	 */
	public void compareMagicFetterRcord(Set<MagicEquipConditionCfg> curCfgs, int modelID){
		MagicEquipFetterRecord item = getItemStore().getItem(userID);
		if(item == null){
			item = checkRecord();
		}
		
		Set<MagicEquipConditionCfg> combineRecord = new HashSet<MagicEquipConditionCfg>();
		Set<MagicEquipConditionCfg> remove = new HashSet<MagicEquipConditionCfg>();
		

		List<Integer> fetterIDs;
		fetterIDs = item.getMagicFetters();
		List<MagicEquipConditionCfg> sameType = new ArrayList<MagicEquipConditionCfg>();
		//先找出数据库里多出来的记录，判断是否要保留
		for (Integer id : fetterIDs) {
			MagicEquipConditionCfg cfg = FetterMagicEquipCfgDao.getInstance().getCfgById(String.valueOf(id));
			sameType.clear();
			boolean exist = false;
			for (MagicEquipConditionCfg fetter : curCfgs) {
				if(fetter.getUniqueId() == id){
					exist = true;
				}
				
				//记录一下相同类型的新集合
				if(fetter.getUniqueId() != id && cfg.getType() == fetter.getType() && cfg.getSubType() == fetter.getSubType()){
					sameType.add(fetter);
				}
			}
			
			if(!exist){
				//检查是否要保留
				if(cfg.recordOldData() && Integer.parseInt(cfg.getHeroModelID()) == modelID){
					//如果是要保留，则判断新集合内是否有相同类型记录，比较两个等级，保留最高
					boolean del = false;
					for (MagicEquipConditionCfg temp : sameType) {
						if(temp.getConditionLevel() <= cfg.getConditionLevel()){
							remove.add(temp);
						}else{
							del = true;
						}
					}
					if(!del){
						combineRecord.add(cfg);
					}
				}
			}
		}
		
		//添加入当前的羁绊
		combineRecord.addAll(curCfgs);
		//删除等级低的
		combineRecord.removeAll(remove);
		
		
		List<Integer> newList = new ArrayList<Integer>();
		for (MagicEquipConditionCfg m : combineRecord) {
			newList.add(m.getUniqueId());
		}

		
		item.setMagicFetters(newList);
		getItemStore().updateItem(item);
		dataVersion.incrementAndGet();
	}
	
	
	
	
	public int getVersion(){
		return dataVersion.get();
	}


	/**
	 * 获取英雄的神器羁绊列表
	 * @param modelId
	 * @return
	 */
	public List<Integer> getFixEquipFetterByModelID(int modelId) {
		List<Integer> temp = new ArrayList<Integer>();
		MagicEquipFetterRecord item = getItemStore().getItem(userID);
		List<Integer> fetterIDs = item.getFixEquipFetters();
		for (Integer id : fetterIDs) {
			MagicEquipConditionCfg cfg = FetterMagicEquipCfgDao.getInstance().getCfgById(String.valueOf(id));
			if(Integer.parseInt(cfg.getHeroModelID()) == modelId){
				temp.add(id);
			}
		}
		return temp;
	}


	/**
	 * 获取法宝的
	 * @return
	 */
	public List<Integer> getMagicFetters() {
		List<Integer> temp = new ArrayList<Integer>();
		MagicEquipFetterRecord item = getItemStore().getItem(userID);
		List<Integer> fetterIDs = item.getMagicFetters();
		temp.addAll(fetterIDs);
		return temp;
	}


	
}
