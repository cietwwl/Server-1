package com.rwbase.dao.spriteattach;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.spriteattach.pojo.SpriteAttachCfg;

public class SpriteAttachCfgDAO extends CfgCsvDao<SpriteAttachCfg>{

	public static SpriteAttachCfgDAO getInstance() {
		return SpringContextUtil.getBean(SpriteAttachCfgDAO.class);
	}
	
	@Override
	protected Map<String, SpriteAttachCfg> initJsonCfg() {
		// TODO Auto-generated method stub
		cfgCacheMap = CfgCsvHelper.readCsv2Map("SpriteAttach/SpriteAttachCfg.csv",SpriteAttachCfg.class);
		return cfgCacheMap;
	}

}
