package com.fy.version.activity;

import java.util.HashMap;
import java.util.Map;

import com.fy.utils.CfgCsvDao;
import com.fy.utils.CfgCsvHelper;

public class ChannelAddressCfgDao extends CfgCsvDao<ChannelAddressCfg> {
	
	
	private static ChannelAddressCfgDao instance = new ChannelAddressCfgDao();
	
	public static ChannelAddressCfgDao getInstance(){
		if(instance == null){
			instance = new ChannelAddressCfgDao();
		}
		return instance;
	}
	
	private ChannelAddressCfgDao(){
		initJsonCfg();
	}

	@Override
	public Map<String, ChannelAddressCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("channelAddress/channelAddress.csv",ChannelAddressCfg.class);
		return cfgCacheMap;
	}

	public ChannelAddressCfg getCfgByKey(String key) {
		ChannelAddressCfg cfg = (ChannelAddressCfg) getCfgById(key);
		return cfg;
	}
}
