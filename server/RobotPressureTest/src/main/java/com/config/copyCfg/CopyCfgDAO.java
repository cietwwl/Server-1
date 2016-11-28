package com.config.copyCfg;

import java.util.HashMap;
import java.util.Map;

import com.config.cfgHelper.CfgCsvDao;
import com.config.cfgHelper.CfgCsvHelper;

public class CopyCfgDAO extends CfgCsvDao<CopyCfg>{

	private static CopyCfgDAO instance = new CopyCfgDAO();
	
	public static CopyCfgDAO getInstance() {
		return instance;
	}
	
	private final int NORMAL_COPY_START_ID = 110101;
	private final int ELITE_COPY_START_ID = 120101;
	
	private HashMap<Integer, CopyLevelNode> normalCopyMap;
	private HashMap<Integer, CopyLevelNode> eliteCopyMap;
	{
		cfgCacheMap = CfgCsvHelper.readCsv2Map("copy/copy.csv",CopyCfg.class);
		normalCopyMap = new HashMap<Integer, CopyLevelNode>();
		eliteCopyMap = new HashMap<Integer, CopyLevelNode>();
		
		for(Map.Entry<String, CopyCfg> entry:cfgCacheMap.entrySet()){
			CopyCfg cfg = entry.getValue();
			CopyLevelNode node = new CopyLevelNode();
			node.setId(cfg.getLevelID());
			node.setBefore(cfg.getPreviousLevelID());
			if(cfg.getLevelType() == 0){
				normalCopyMap.put(node.getId(), node);
			}else if(cfg.getLevelType() == 1){	
				eliteCopyMap.put(node.getId(), node);
			}
		}
		
		for(Map.Entry<String, CopyCfg> entry:cfgCacheMap.entrySet()){
			CopyCfg cfg = entry.getValue();
			if(cfg.getLevelType() == 0){
				CopyLevelNode preNode = normalCopyMap.get(cfg.getPreviousLevelID());
				if(null != preNode){
					preNode.setNext(cfg.getLevelID());
				}else{
					CopyLevelNode node = normalCopyMap.get(cfg.getLevelID());
					if(null != node){
						node.setBefore(0);
					}
				}
			}else if(cfg.getLevelType() == 1){	
				CopyLevelNode preNode = eliteCopyMap.get(cfg.getPreviousLevelID());
				if(null != preNode){
					preNode.setNext(cfg.getLevelID());
				}else{
					CopyLevelNode node = eliteCopyMap.get(cfg.getLevelID());
					if(null != node){
						node.setBefore(0);
					}
				}
			}
		}
	}
	
	public int getNextNormalCopyId(int id){
		return getNextId(id, NORMAL_COPY_START_ID, normalCopyMap);
	}
	
	public int getNextEliteCopyId(int id){
		return getNextId(id, ELITE_COPY_START_ID, eliteCopyMap);
	}
	
	private int getNextId(int id, final int startId, HashMap<Integer, CopyLevelNode> copyMap){
		int loopMaxCount = 1000;
		int checkId = startId;
		while(loopMaxCount-- > 0){
			CopyLevelNode node = copyMap.get(checkId);
			if(null == node) {
				return startId;
			}
			if(node.getBefore() == id){
				return node.getId();
			} else{
				checkId = node.getNext();
			}
		}
		return startId;
	}

	/**
	 * 是否是精英本或者普通本
	 * @param cfg
	 * @return
	 */
	public boolean isNormalOrElite(CopyCfg cfg){
		return cfg.getLevelType() == 0 || cfg.getLevelType() == 1;
	}

	@Override
	protected Map<String, CopyCfg> initJsonCfg() {
		// TODO Auto-generated method stub
		return null;
	}
}
