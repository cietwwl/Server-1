package com.rw.service.Privilege.datamodel;

import java.util.HashMap;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;

public abstract class AbstractPrivilegePropertiesHelper<PrivilegeNameEnum extends Enum<PrivilegeNameEnum>, ConfigClass extends IThresholdConfig>
		extends CfgCsvDao<ConfigClass> implements IPrivilegeThreshold<PrivilegeNameEnum> {
	private HashMap<PrivilegeNameEnum,ConfigClass> priMap;

	@Override
	public int getThreshold(PrivilegeNameEnum pname) {
		ConfigClass cfg = priMap.get(pname);
		if (cfg == null)
			return -1;
		int result = cfg.getThreshold();
		if (result <= 0) {
			return -1;
		}
		return result;
	}
	
	protected Map<String, ConfigClass> initJson(String csvFileName, 
			Class<ConfigClass> cfgCl,Class<PrivilegeNameEnum> priNameCl){
		cfgCacheMap = CfgCsvHelper.readCsv2Map(csvFileName,cfgCl);
		PrivilegeNameEnum[] nameEnums = priNameCl.getEnumConstants();
		priMap = new HashMap<PrivilegeNameEnum,ConfigClass>(nameEnums.length);
		for(int i = 0; i< nameEnums.length; i++){
			PrivilegeNameEnum privilegeNameEnum = nameEnums[i];
			ConfigClass cfg = cfgCacheMap.get(privilegeNameEnum.name());
			if (cfg == null){
				throw new RuntimeException("缺少特权属性配置:"+privilegeNameEnum);
			}
			priMap.put(privilegeNameEnum, cfg);
		}
		return cfgCacheMap;
	}
	
	public ConfigClass getByPrivilegeName(PrivilegeNameEnum pname){
		return priMap.get(pname);
	}
}
