package com.rwbase.dao.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.setting.pojo.HeadCfg;
import com.rwbase.dao.setting.pojo.HeadType;

public class HeadCfgDAO  extends CfgCsvDao<HeadCfg>
{
	private static HeadCfgDAO m_instance = new HeadCfgDAO();
	public static HeadCfgDAO getInstance()
	{
		if(m_instance == null) 
		{
			m_instance = new HeadCfgDAO();
		}
		return m_instance;
	}
	
	@Override
	public Map<String, HeadCfg> initJsonCfg() 
	{
		cfgCacheMap = CfgCsvHelper.readCsv2Map("setting/headCfg.csv",HeadCfg.class);
		return cfgCacheMap;
	}
	
	/*
	 * 获取配置表里对应的职业头像
	 */
	public String getCareerHead(int career, int careerLevel, int sex)
	{
		String headName = null;
		List<HeadCfg> headCfgList = getAllCfg();
		for (HeadCfg headCfg : headCfgList) 
		{
			if(headCfg.getType() == HeadType.HEAD_CARRER)
			{
				if(headCfg.getCareer() == career/* && headCfg.getRank() == careerLevel*/ && headCfg.getSex() == sex)
				{
					headName = headCfg.getSpriteId();
					break;
				}
			}
		}
		return headName;
	}
	
	/*
	 * 按照类型获取配置表内所有的头像名
	 */
	public List<String> getHeadByType(int type)
	{
		List<String> baseHeadList = new ArrayList<String>();
		List<HeadCfg> headCfgList = getAllCfg();
		for (HeadCfg headCfg : headCfgList) 
		{
			if(headCfg.getType() == type)
			{
				baseHeadList.add(headCfg.getSpriteId());
			}
		}
		return baseHeadList;
	}
	
	/*
	 * 获取头像的类型
	 */
	public int getTypeOfHead(String headName)
	{
		int type = HeadType.NULL;
		List<HeadCfg> headCfgList = getAllCfg();
		for (HeadCfg headCfg : headCfgList) 
		{
			if(headCfg.getSpriteId().equals(headName))
			{
				type = headCfg.getType();
				break;
			}
		}
		return type;
	}
	
	
	public HeadCfg getCfg(String imageId){
		List<HeadCfg> headCfgList = getAllCfg();
		for (HeadCfg headCfg : headCfgList) 
		{
			if(headCfg.getSpriteId().equals(imageId))
			{
				return headCfg;
			}
		}
		return null;
	}
}