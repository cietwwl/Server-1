package com.rwbase.dao.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.log.GameLog;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.setting.pojo.HeadBoxCfg;
import com.rwbase.dao.setting.pojo.HeadBoxType;

public class HeadBoxCfgDAO extends CfgCsvDao<HeadBoxCfg>
{
	public static HeadBoxCfgDAO getInstance() {
		return SpringContextUtil.getBean(HeadBoxCfgDAO.class);
	}
	
	@Override
	public Map<String, HeadBoxCfg> initJsonCfg() 
	{
		cfgCacheMap = CfgCsvHelper.readCsv2Map("setting/headBoxCfg.csv",HeadBoxCfg.class);
		return cfgCacheMap;
	}
	
	/*
	 * 按照类型获取配置表内所有的头像框
	 */
	public List<String> getHeadBoxByType(int type)
	{
		List<String> baseHeadBoxList = new ArrayList<String>();
		List<HeadBoxCfg> headBoxCfgList = getAllCfg();
		for (HeadBoxCfg headBoxCfg : headBoxCfgList) 
		{
			if(headBoxCfg.getType() == type)
			{
				baseHeadBoxList.add(headBoxCfg.getSpriteId());
			}
		}
		if(baseHeadBoxList.size() == 0 || baseHeadBoxList.size() > 1)
		{
			GameLog.debug("头像框配置表出错，请确认是否有填写这个类型的头像框 ： " + type);
		}
		return baseHeadBoxList;
	}
	
	public int getTypeOfHeadBox(String headBoxName)
	{
		int type = HeadBoxType.NULL;
		List<HeadBoxCfg> headBoxCfgList = getAllCfg();
		for (HeadBoxCfg headBoxCfg : headBoxCfgList) 
		{
			if(headBoxCfg.getSpriteId().equals(headBoxName))
			{
				type = headBoxCfg.getType();
				break;
			}
		}
		return type;
	}
	
	public HeadBoxCfg getCfg(String imageId){
		List<HeadBoxCfg> headCfgList = getAllCfg();
		for (HeadBoxCfg headCfg : headCfgList) 
		{
			if(headCfg.getSpriteId().equals(imageId))
			{
				return headCfg;
			}
		}
		return null;
	}
}
