package com.rw.service.Privilege.datamodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.service.Privilege.IPrivilegeProvider;
import com.rw.service.Privilege.IPrivilegeWare;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwproto.PrivilegeProtos.AllPrivilege.Builder;

public class peakArenaPrivilegeHelper extends CfgCsvDao<peakArenaPrivilege> implements IPrivilegeConfigSourcer{
	public static peakArenaPrivilegeHelper getInstance() {
		return SpringContextUtil.getBean(peakArenaPrivilegeHelper.class);
	}

	private String[] sources;

	@Override
	public Map<String, peakArenaPrivilege> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Privilege/peakArenaPrivilege.csv",peakArenaPrivilege.class);
		Collection<peakArenaPrivilege> vals = cfgCacheMap.values();
		ArrayList<String> tmp = new ArrayList<String>(cfgCacheMap.size());
		String configCl = this.getClass().getName();
		for (peakArenaPrivilege cfg : vals) {
			cfg.ExtraInitAfterLoad();
			String sourceName = cfg.getSource();
			if (StringUtils.isNotBlank(sourceName)){
				tmp.add(sourceName);
			}else{
				GameLog.error("特权", configCl+",key="+cfg.getKey(), "无效特权来源名称");
			}
		}
		sources = new String[tmp.size()];
		sources = tmp.toArray(sources);
		PrivilegeConfigHelper.getInstance().addOrReplace(configCl, this);
		return cfgCacheMap;
	}

	@Override
	public void putPrivilege(IPrivilegeWare privilegeMgr, List<IPrivilegeProvider> providers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Builder combine(Builder acc, Builder pri) {
		// TODO Auto-generated method stub
		return null;
	}

}