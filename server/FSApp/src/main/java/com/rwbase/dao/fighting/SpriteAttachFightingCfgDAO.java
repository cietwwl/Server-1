package com.rwbase.dao.fighting;

import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.dao.fighting.pojo.SpriteAttachFightingCfg;

public class SpriteAttachFightingCfgDAO extends FightingCfgCsvDAOBase<SpriteAttachFightingCfg>{

	public static SpriteAttachFightingCfgDAO getInstance(){
		return SpringContextUtil.getBean(SpriteAttachFightingCfgDAO.class);
	}
	
	@Override
	protected String getFileName() {
		return "SpriteAttachFighting.csv";
	}

	@Override
	protected Class<SpriteAttachFightingCfg> getCfgClazz() {
		// TODO Auto-generated method stub
		return SpriteAttachFightingCfg.class;
	}

}
