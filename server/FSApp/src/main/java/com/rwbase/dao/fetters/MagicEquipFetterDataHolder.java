package com.rwbase.dao.fetters;

import java.util.ArrayList;
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
	 * @param version 版本
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
		if(item.getFetterIDs().isEmpty()){
			return;
		}
		
		SynMagicEquipFetterData synData = new SynMagicEquipFetterData(userID, item.getFetterIDs());
				
		ClientDataSynMgr.synData(player, synData, syType, eSynOpType.UPDATE_SINGLE);
		
	}
	
	private MapItemStore<MagicEquipFetterRecord> getItemStore(){
		MapItemStoreCache<MagicEquipFetterRecord> itemStoreCache = MapItemStoreFactory.getMagicEquipFetterCache();
		return itemStoreCache.getMapItemStore(userID, MagicEquipFetterRecord.class);
	}
	
	
	/**
	 * 检查数据库内记录是否与当前集合一致，如果没有则进行添加
	 * @param curCfgs
	 */
	public void compareRcord(Set<MagicEquipConditionCfg> curCfgs){
		MagicEquipFetterRecord item = getItemStore().getItem(userID);
		if(item == null){
			item = checkRecord();
		}
		
		Set<MagicEquipConditionCfg> combineRecord = new HashSet<MagicEquipConditionCfg>();
		Set<MagicEquipConditionCfg> remove = new HashSet<MagicEquipConditionCfg>();
		List<Integer> fetterIDs = item.getFetterIDs();
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
				if(cfg.recordOldData()){
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
		
		item.setFetterIDs(newList);
		getItemStore().updateItem(item);
	}
	
	
	
	/**
	 * 检查是否存在目标羁绊，如果没有则进行添加
	 * @param fetter
	 */
	public boolean checkOrAddFetter(MagicEquipConditionCfg fetter){
		MagicEquipFetterRecord item = getItemStore().getItem(userID);
		if(item == null){
			item = checkRecord();
		}

		
		
		//找出数据库里相同类型但等级低的羁绊，去掉,但如果是神器，就只能用当前的，因为有可能是降星操作，所以要把高级的也去掉
		List<Integer> fetterIDs = item.getFetterIDs();
		List<Integer> remove = new ArrayList<Integer>();
		for (Integer id : fetterIDs) {
			MagicEquipConditionCfg cfg = FetterMagicEquipCfgDao.getInstance().getCfgById(String.valueOf(id));
			if(cfg.getUniqueId() == fetter.getUniqueId()){//如果发现已经有相同的羁绊id，则直接返回
				return false;
			}
			
			if(cfg.recordOldData()
					&& cfg.getType() == fetter.getType() && cfg.getSubType() == fetter.getSubType() 
					&& cfg.getConditionLevel() < fetter.getConditionLevel()){
				remove.add(id);
			}else if(!cfg.recordOldData()
						&& cfg.getType() == fetter.getType() && cfg.getSubType() == fetter.getSubType() 
						&& cfg.getConditionLevel() != fetter.getConditionLevel()){
					remove.add(id);
			}
		}
		if( !remove.isEmpty()){
			fetterIDs.removeAll(remove);
		}
		
		fetterIDs.add(fetter.getUniqueId());
		getItemStore().updateItem(item);
		
		return true;
		
	}
	
	
	public int getVersion(){
		return dataVersion.get();
	}


	/**
	 * 获取英雄的神器羁绊列表
	 * @param modelId
	 * @return
	 */
	public List<MagicEquipConditionCfg> getFixEquipFetterByModelID(int modelId) {
		List<MagicEquipConditionCfg> returnList = new ArrayList<MagicEquipConditionCfg>();
		MagicEquipFetterRecord item = getItemStore().getItem(userID);
		List<Integer> fetterIDs = item.getFetterIDs();
		for (Integer id : fetterIDs) {
			
			MagicEquipConditionCfg cfg = FetterMagicEquipCfgDao.getInstance().getCfgById(String.valueOf(id));
			if(cfg.getModelIDList().contains(modelId)){
				returnList.add(cfg);
			}
		}
		return returnList;
	}


	/**
	 * 获取法宝的
	 * @return
	 */
	public List<MagicEquipConditionCfg> getMagicFetters() {
		List<MagicEquipConditionCfg> returnList = new ArrayList<MagicEquipConditionCfg>();
		MagicEquipFetterRecord item = getItemStore().getItem(userID);
		List<Integer> fetterIDs = item.getFetterIDs();
		for (Integer id : fetterIDs) {
			
			MagicEquipConditionCfg cfg = FetterMagicEquipCfgDao.getInstance().getCfgById(String.valueOf(id));
			if(cfg.getType() == FetterMagicEquipCfgDao.TYPE_MAGICWEAPON){
				returnList.add(cfg);
			}
		}
		return returnList;
	}
}
