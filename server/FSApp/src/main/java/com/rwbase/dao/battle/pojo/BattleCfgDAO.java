package com.rwbase.dao.battle.pojo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.battle.pojo.cfg.CopyMonsterInfoCfg;

/**
 * 关卡怪物信息，暂时只有关卡及怪物id,没有初始化怪物的位置
 * 后面如果有需求再增加
 * @author Alex
 * 2016年7月23日 下午4:52:48
 */
public class BattleCfgDAO extends CfgCsvDao<CopyMonsterInfoCfg>{

	/**关卡及对应的怪物信息 <key=关卡id, value=怪物列表>*/
	private Map<String, List<CopyMonsterInfoCfg>> copyMonsterMap = new HashMap<String, List<CopyMonsterInfoCfg>>();
	
	
	public static BattleCfgDAO getInstance(){
		return SpringContextUtil.getBean(BattleCfgDAO.class);
	}
	
	@Override
	protected Map<String, CopyMonsterInfoCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("battle/battle.csv", CopyMonsterInfoCfg.class);
		Collection<CopyMonsterInfoCfg> values = cfgCacheMap.values();
		for (CopyMonsterInfoCfg cfg : values) {
			cfg.formatData();
			List<CopyMonsterInfoCfg> list = copyMonsterMap.get(cfg.getCopyId());
			if(list == null){
				list = new ArrayList<CopyMonsterInfoCfg>();
				copyMonsterMap.put(cfg.getCopyId(), list);
			}
			list.add(cfg);
		}
		return cfgCacheMap;
	}

	/**
	 * 获取关卡里怪物的信息
	 * @param copyID
	 * @return
	 */
	public List<CopyMonsterInfoCfg> getCopyMonsterInfoByCopyID(String copyID){
		return copyMonsterMap.get(copyID);
	}

}
