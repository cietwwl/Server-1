package com.rw.service.Privilege.datamodel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.common.RefParam;
import com.log.GameLog;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.service.Privilege.IPrivilegeProvider;
import com.rw.service.Privilege.IPrivilegeWare;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwproto.PrivilegeProtos.AllPrivilege;
import com.rwproto.PrivilegeProtos.AllPrivilege.Builder;
import com.rwproto.PrivilegeProtos.ArenaPrivilegeNames;
import com.rwproto.PrivilegeProtos.PrivilegeProperty;
import com.rwproto.PrivilegeProtos.PrivilegePropertyOrBuilder;
import com.rwproto.PrivilegeProtos.PrivilegeValue;

public class arenaPrivilegeHelper extends CfgCsvDao<arenaPrivilege> implements IPrivilegeConfigSourcer{
	public static arenaPrivilegeHelper getInstance() {
		return SpringContextUtil.getBean(arenaPrivilegeHelper.class);
	}

	private String[] headers;
	private String[] sources;
	private HashMap<String,PropertyWriter> combinatorMap;
	private HashMap<String,String[]> chargeSources;
	private HashMap<String,String> maxChargeType;

	@Override
	public Map<String, arenaPrivilege> initJsonCfg() {
		RefParam<String[]> outHeaders=new RefParam<String[]>();
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Privilege/arenaPrivilege.csv",arenaPrivilege.class,outHeaders);
		headers = new String[outHeaders.value.length - 2];
		combinatorMap = new HashMap<String,PropertyWriter>();
		Map<String, Field> fieldMap = CfgCsvHelper.getFieldMap(arenaPrivilege.class);
		
		for(int i = 0;i<headers.length;i++){
			PropertyWriter combinator;
			headers[i] = outHeaders.value[i-2];
			Field field = fieldMap.get(headers[i]);
			Class<?> fieldType = field.getType();
			if (fieldType == boolean.class || fieldType == Boolean.class) {
				combinator = BoolPropertyWriter.getShareInstance();
			}else if (fieldType == int.class || fieldType == Integer.class) {
				combinator = IntPropertyWriter.getShareInstance();
			} else if (fieldType == long.class || fieldType == Long.class) {
				combinator = LongPropertyWriter.getShareInstance();
			} else if (fieldType == float.class || fieldType == Float.class) {
				combinator = FloatPropertyWriter.getShareInstance();
			} else if (fieldType == double.class || fieldType == Double.class) {
				combinator = DoublePropertyWriter.getShareInstance();
			}else{
				throw new RuntimeException("无效属性类型，必须是整数或者布尔类型! "+headers[i]+":"+fieldType.getName());
			}
			combinatorMap.put(headers[i], combinator);
		}
		
		ArenaPrivilegeNames[] nameEnums = ArenaPrivilegeNames.values();
		for(int i = 0; i< nameEnums.length; i++){
			if (!combinatorMap.containsKey(nameEnums[i].name())){
				throw new RuntimeException("配置缺少了属性:"+nameEnums[i]);
			}
		}
		
		maxChargeType = new HashMap<String,String>(nameEnums.length);
		Collection<arenaPrivilege> vals = cfgCacheMap.values();
		ArrayList<String> tmp = new ArrayList<String>(cfgCacheMap.size());
		HashMap<String,List<String>> tmpPriMap = new HashMap<String,List<String>>();
		String configCl = this.getClass().getName();
		for (arenaPrivilege cfg : vals) {
			cfg.ExtraInitAfterLoad();
			String sourceName = cfg.getSource();
			if (StringUtils.isBlank(sourceName)){
				GameLog.error("特权", configCl+",key="+cfg.getSource(), "无效特权来源名称");
				continue;
			}
			
			tmp.add(sourceName);
			
			for(int i = 0; i< nameEnums.length; i++){
				String priName = nameEnums[i].name();
				Object privilegeValue = cfg.getValueByName(priName);
				String maxType = maxChargeType.get(priName);
				arenaPrivilege maxCfg = maxType != null ? cfgCacheMap.get(maxType) : null;
				Object maxVal = maxCfg.getValueByName(priName);
				PropertyWriter combinator = combinatorMap.get(priName);
				if (combinator.gt(privilegeValue,maxVal)){
					maxChargeType.put(priName, cfg.getSource());
				}
				
				if (StringUtils.isBlank(privilegeValue.toString())){
					GameLog.info("特权", configCl+",key="+cfg.getSource(), String.format("充值类型:%s,特权名称:%s,配置值空", cfg.getSource(),priName),null);
					continue;
				}

				List<String> chargeSrc = tmpPriMap.get(priName);
				if (chargeSrc == null){
					chargeSrc = new ArrayList<String>();
					tmpPriMap.put(priName, chargeSrc);
				}
				chargeSrc.add(sourceName);
			}
		}
		sources = new String[tmp.size()];
		sources = tmp.toArray(sources);
		
		for(int i = 0; i< nameEnums.length; i++){
			String priName = nameEnums[i].name();
			
			String maxType = maxChargeType.get(priName);
			if (StringUtils.isBlank(maxType)){
				throw new RuntimeException("无效特权配置:特权"+priName+"没有配置有效值");
			}

			List<String> chargeSrc = tmpPriMap.get(priName);
			String[] chargeSrcs;
			if (chargeSrc == null){
				chargeSrcs = new String[0];
				GameLog.info("特权", configCl, String.format("特权名称:%s,没有配置充值类型", priName),null);
			}else{
				chargeSrcs = new String[chargeSrc.size()];
				chargeSrcs = chargeSrc.toArray(chargeSrcs);
			}
			chargeSources.put(priName, chargeSrcs);
		}
		
		PrivilegeConfigHelper.getInstance().addOrReplace(configCl, this);
		return cfgCacheMap;
	}

	@Override
	public void putPrivilege(IPrivilegeWare privilegeMgr, List<IPrivilegeProvider> providers) {
		List<Pair<IPrivilegeProvider, PrivilegeProperty.Builder>> tmpMap=new ArrayList<Pair<IPrivilegeProvider,PrivilegeProperty.Builder>>();
		ArenaPrivilegeNames[] nameEnums = ArenaPrivilegeNames.values();
		
		for (IPrivilegeProvider pro : providers) {
			PrivilegeProperty.Builder pri = PrivilegeProperty.newBuilder();
			
			for (int i = 0; i < nameEnums.length; i++) {
				ArenaPrivilegeNames privilegeEnum = nameEnums[i];
				String pname = privilegeEnum.name();
				PrivilegeValue.Builder privilegeValues = PrivilegeValue.newBuilder();
				privilegeValues.setName(pname);
				String maxCharge = maxChargeType.get(pname);
				if (StringUtils.isNotBlank(maxCharge)){
					privilegeValues.setChargeType(maxCharge);
				}

				// 从特权提供者获取可能的特权档次
				// 对每个属性过滤无效sources
				int sourceIndex = pro.getBestMatchCharge(chargeSources.get(pname));
				if (0 <= sourceIndex && sourceIndex < sources.length) {
					String sourceName = sources[sourceIndex];
					arenaPrivilege priCfg = cfgCacheMap.get(sourceName);
					privilegeValues.setValue(priCfg.getValueByName(pname).toString());
				} else {
					GameLog.info("特权统计", pro.getClass().getName() + ":当前特权:" + pro.getCurrentChargeType(),
							"没有找到对应的特权属性", null);
					privilegeValues.setValue("");
				}
				
				pri.addKv(privilegeValues);//append!
			}

			Pair<IPrivilegeProvider, PrivilegeProperty.Builder> pair = Pair.Create(pro, pri);
			tmpMap.add(pair);
		}
		
		privilegeMgr.putArenaPrivilege(this,tmpMap);
	}

	@Override
	public AllPrivilege.Builder combine(AllPrivilege.Builder acc, AllPrivilege pri) {
		PrivilegeProperty.Builder accB = acc.getArenaBuilder();
		PrivilegePropertyOrBuilder added = pri.getArena();
		ArenaPrivilegeNames[] nameEnums = ArenaPrivilegeNames.values();
		if (accB.getKvCount() < nameEnums.length || added.getKvCount() < nameEnums.length){
			GameLog.error("特权", "putPrivilege或者combinePrivilege有bug", "特权名数量不足");
			return acc;
		}
		
		for(int i = 0; i< nameEnums.length; i++){
			ArenaPrivilegeNames privilegeEnum = nameEnums[i];
			String pname = privilegeEnum.name();
			PropertyWriter pwriter = combinatorMap.get(pname);
			PrivilegeValue.Builder accVal = accB.getKvBuilder(privilegeEnum.ordinal());
			PrivilegeValue right = added.getKv(privilegeEnum.ordinal());
			// 计算最大充值档次
			String chargeType;
			if (pwriter.gt(right.getValue(), accVal.getValue())){
				chargeType = right.getChargeType();
			}else{
				chargeType = accVal.getChargeType();
			}
			
			accVal = pwriter.combine(accVal,right, pname);
			if (StringUtils.isNotBlank(chargeType)){
				accVal.setChargeType(chargeType);
			}
			accB.setKv(privilegeEnum.ordinal(), accVal);
		}
		return acc;
	}

	@Override
	public void setValue(Builder holder, com.rwproto.PrivilegeProtos.PrivilegeProperty.Builder value) {
		holder.setArena(value);
	}

	@Override
	public com.rwproto.PrivilegeProtos.PrivilegeProperty.Builder getValue(Builder holder) {
		return holder.getArenaBuilder();
	}

}