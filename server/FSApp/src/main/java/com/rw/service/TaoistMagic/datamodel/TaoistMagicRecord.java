package com.rw.service.TaoistMagic.datamodel;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaoistMagicRecord {
	@Id
	private String userId;
	private Map<Integer,Integer> levelMap;
	
	/**
	 * set方法只能用于Json库
	 */
	public TaoistMagicRecord(){
		levelMap = new HashMap<Integer, Integer>();
	}
	
	public Map<Integer, Integer> getLevelMap() {
		return levelMap;
	}

	public void setLevelMap(Map<Integer, Integer> levelMap) {
		this.levelMap = levelMap;
	}

	public TaoistMagicRecord(String uid) {
		this();
		userId = uid;
	}

	public Iterable<Entry<Integer, Integer>> getAll(){
		return levelMap.entrySet();
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}

	public static TaoistMagicRecord Create(String uid){
		TaoistMagicRecord data = new TaoistMagicRecord();
		data.userId = uid;
		return data;
	}
	
	protected boolean setLevel(int magicId,int level){
		if (level <= 0) {
			return false;
		}
		TaoistMagicCfg cfg = TaoistMagicCfgHelper.getInstance().getCfgById(String.valueOf(magicId));
		if (cfg == null){
			return false;
		}
		
		if (level > TaoistConsumeCfgHelper.getInstance().getMaxLevel(cfg.getConsumeId())){
			return false;
		}
		
		Player player = PlayerMgr.getInstance().find(userId);
		if (level > player.getLevel()){
			return false;
		}
		
		// signal update event
		Integer oldlvl = levelMap.put(magicId,level);
		if (oldlvl != null && oldlvl == level){
			return false;
		}
		return true;
	}

	public Integer getLevel(int tid) {
		return levelMap.get(tid);
	}
}
