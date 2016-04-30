package com.rw.service.Privilege.datamodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.service.Privilege.IPrivilegeProvider;
import com.rw.service.Privilege.IPrivilegeWare;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwproto.PrivilegeProtos.AllPrivilege;
import com.rwproto.PrivilegeProtos.ArenaPrivilege;

public class arenaPrivilegeHelper extends CfgCsvDao<arenaPrivilege> implements IPrivilegeConfigSourcer{
	public static arenaPrivilegeHelper getInstance() {
		return SpringContextUtil.getBean(arenaPrivilegeHelper.class);
	}

	private String[] sources;

	@Override
	public Map<String, arenaPrivilege> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Privilege/arenaPrivilege.csv",arenaPrivilege.class);
		Collection<arenaPrivilege> vals = cfgCacheMap.values();
		ArrayList<String> tmp = new ArrayList<String>(cfgCacheMap.size());
		String configCl = this.getClass().getName();
		for (arenaPrivilege cfg : vals) {
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
		List<Pair<IPrivilegeProvider, ArenaPrivilege.Builder>> tmpMap=new ArrayList<Pair<IPrivilegeProvider,ArenaPrivilege.Builder>>();
		for (IPrivilegeProvider pro : providers) {
			//TODO 从特权提供者获取可能的特权档次
			int sourceIndex = pro.getPrivilegeIndex(sources);
			if (0<=sourceIndex && sourceIndex < sources.length){
				String sourceName = sources[sourceIndex];
				arenaPrivilege priCfg = cfgCacheMap.get(sourceName);
				ArenaPrivilege.Builder pri = ArenaPrivilege.newBuilder();
				pri.setArenaChallengeDec(priCfg.getArenaChallengeDec());
				pri.setArenaMaxCount(priCfg.getArenaMaxCount());
				pri.setArenaRewardAdd(priCfg.getArenaRewardAdd());
				pri.setIsAllowResetArena(priCfg.getIsAllowResetArena());
				Pair<IPrivilegeProvider, ArenaPrivilege.Builder> pair=Pair.Create(pro, pri);
				tmpMap.add(pair);
			}else{
				GameLog.info("特权统计", pro.getClass().getName()+":当前特权:"+pro.getCurrentPrivilege(), "没有找到对应的特权属性", null);
			}
		}
		privilegeMgr.putArenaPrivilege(this,tmpMap);
	}

	@Override
	public AllPrivilege.Builder combine(AllPrivilege.Builder acc, AllPrivilege.Builder pri) {
		ArenaPrivilege.Builder accB = acc.getArenaBuilder();
		ArenaPrivilege added = pri.getArena();
		accB.setArenaChallengeDec(Math.max(accB.getArenaChallengeDec(), added.getArenaChallengeDec()));
		accB.setArenaMaxCount(Math.max(accB.getArenaMaxCount(), added.getArenaMaxCount()));
		accB.setArenaRewardAdd(Math.max(accB.getArenaRewardAdd(), added.getArenaRewardAdd()));
		accB.setIsAllowResetArena(accB.getIsAllowResetArena() || added.getIsAllowResetArena());
		//TODO 需要测试是否真的修改了
		return acc;
	}

}