package com.rw.service.TaoistMagic.datamodel;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;

@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "taoist_record")
public class TaoistMagicRecord {
	@Id
	private String userId;
	private Map<Integer,Integer> levelMap;
	public TaoistMagicRecord(){
		levelMap = new HashMap<Integer, Integer>();
	}
	
	public Iterable<Entry<Integer, Integer>> getAll(){
		return levelMap.entrySet();
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
