package com.rwbase.dao.fashion;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.setting.HeadBoxCfgDAO;
import com.rwbase.dao.setting.pojo.HeadBoxCfg;

public class FashionCommonCfgDao extends CfgCsvDao<FashionCommonCfg> {
	public static FashionCommonCfgDao getInstance() {
		return SpringContextUtil.getBean(FashionCommonCfgDao.class);
	}
	
	@Override
	public void CheckConfig() {
		HeadBoxCfgDAO helper = HeadBoxCfgDAO.getInstance();
		Collection<Entry<String,FashionCommonCfg>> values =  cfgCacheMap.entrySet();
		for (Entry<String,FashionCommonCfg> entry : values) {
			FashionCommonCfg cfg = entry.getValue();
			int unlockId = cfg.getFrameIconId();
			if (unlockId !=0){//允许不填解锁头像框
				HeadBoxCfg hcfg = helper.getCfgById(String.valueOf(unlockId));
				if (hcfg == null){
					throw new RuntimeException("HeadBoxCfg.csv缺少时装解锁头像框配置的ID:"+unlockId);
				}
			}
		}
	}

	@Override
	public Map<String, FashionCommonCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("fashion/FashionCommonCfg.csv", FashionCommonCfg.class);
		Collection<Entry<String,FashionCommonCfg>> values =  cfgCacheMap.entrySet();
		for (Entry<String,FashionCommonCfg> entry : values) {
			FashionCommonCfg cfg = entry.getValue();
			cfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
	
	public FashionCommonCfg getConfig(String id){
		FashionCommonCfg cfg = getCfgById(id);
		return cfg;
	}
	
	public FashionCommonCfg getConfig(int fashionID){
		FashionCommonCfg cfg = getCfgById(String.valueOf(fashionID));
		return cfg;
	}

}
