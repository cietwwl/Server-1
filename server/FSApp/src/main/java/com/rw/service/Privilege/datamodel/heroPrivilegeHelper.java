package com.rw.service.Privilege.datamodel;

import java.util.List;
import java.util.Map;

import com.playerdata.VipMgr;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.service.Privilege.IPrivilegeProvider;
import com.rw.service.Privilege.IPrivilegeWare;
import com.rwproto.PrivilegeProtos.AllPrivilege;
import com.rwproto.PrivilegeProtos.AllPrivilege.Builder;
import com.rwproto.PrivilegeProtos.PrivilegeProperty;
import com.rwproto.PrivilegeProtos.HeroPrivilegeNames;

//	<bean class="com.rw.service.Privilege.datamodel.heroPrivilegeHelper"  init-method="init" />

public class heroPrivilegeHelper extends AbstractPrivilegeConfigHelper<HeroPrivilegeNames, heroPrivilege> {
	public static heroPrivilegeHelper getInstance() {
		return SpringContextUtil.getBean(heroPrivilegeHelper.class);
	}
	
	public int getDefaultSkillPoint(){
		Object val = this.cfgCacheMap.get(VipMgr.vipPrefix+"0").getValue(HeroPrivilegeNames.skillThreshold);
		Integer skillPoint = IntPropertyWriter.getShareInstance().extractVal(val, 15);
		return skillPoint;
	}

	@Override
	public Map<String, heroPrivilege> initJsonCfg() {
		return initJsonCfg("Privilege/heroPrivilege.csv",heroPrivilege.class, HeroPrivilegeNames.class);
	}
	
	@Override
	public void setValue(Builder holder, PrivilegeProperty.Builder value) {
		holder.setHero(value);
	}

	@Override
	public PrivilegeProperty.Builder getValue(Builder holder) {
		return holder.getHeroBuilder();
	}

	@Override
	protected IPrivilegeThreshold<HeroPrivilegeNames> getThresholder() {
		return heroPrivilegePropertiesHelper.getInstance();
	}

	@Override
	protected void putPrivilege(
			AbstractPrivilegeConfigHelper<HeroPrivilegeNames, heroPrivilege> abstractPrivilegeConfigHelper,
			IPrivilegeWare privilegeMgr,
			List<Pair<IPrivilegeProvider, PrivilegeProperty.Builder>> tmpMap) {
		privilegeMgr.putHeroPrivilege(this,tmpMap);
	}

	@Override
	public PrivilegeProperty getValue(AllPrivilege pri) {
		return pri.getHero();
	}
}