package com.rw.service.Privilege.datamodel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.common.RefParam;
import com.log.GameLog;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.common.Pair;
import com.rw.service.Privilege.IPrivilegeProvider;
import com.rw.service.Privilege.IPrivilegeWare;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwproto.PrivilegeProtos.AllPrivilege;
import com.rwproto.PrivilegeProtos.AllPrivilege.Builder;
import com.rwproto.PrivilegeProtos.PrivilegeProperty;
import com.rwproto.PrivilegeProtos.PrivilegeValue;

public abstract class AbstractPrivilegeConfigHelper<PrivilegeNameEnum extends Enum<PrivilegeNameEnum>, 
		ConfigClass extends IConfigChargeSource<PrivilegeNameEnum>>
		extends CfgCsvDao<ConfigClass> implements IPrivilegeConfigSourcer<PrivilegeNameEnum> {
	
	protected String[] headers;
	protected String[] sources;
	protected HashMap<PrivilegeNameEnum,PropertyWriter> combinatorMap;
	protected HashMap<PrivilegeNameEnum,String[]> chargeSources;
	protected HashMap<PrivilegeNameEnum,String> maxChargeType;
	protected PrivilegeNameEnum[] privilegeNameEnums;
	private Map<String, Field> fieldMap;

	abstract protected IPrivilegeThreshold<PrivilegeNameEnum> getThresholder();
	
	abstract protected void putPrivilege(
			AbstractPrivilegeConfigHelper<PrivilegeNameEnum, ConfigClass> abstractPrivilegeConfigHelper,
			IPrivilegeWare privilegeMgr, List<Pair<IPrivilegeProvider, PrivilegeProperty.Builder>> tmpMap);
	
	protected Map<String, ConfigClass> initJsonCfg(String csvFileName, 
			Class<ConfigClass> cfgCl,Class<PrivilegeNameEnum> priNameCl) {
		PrivilegeNameEnum[] nameEnums = priNameCl.getEnumConstants();
		privilegeNameEnums = nameEnums;
		RefParam<Map<String, Field>> outFieldMap = new RefParam<Map<String, Field>>();
		cfgCacheMap = CfgCsvHelper.readCsv2Map(csvFileName,cfgCl,outFieldMap);
		fieldMap = outFieldMap.value;
		Collection<ConfigClass> vals = cfgCacheMap.values();
		//转换为小写!
		for (ConfigClass cfg : vals) {
			cfg.toLowerCase(this);
		}
		//按照新的小写充值类型重建映射
		{
			HashMap<String, ConfigClass> tmp = new HashMap<String, ConfigClass>(cfgCacheMap.size());
			for (ConfigClass cfg : vals) {
				tmp.put(cfg.getSource(), cfg);
			}
			cfgCacheMap = tmp;
		}
		
		headers = new String[nameEnums.length];
		combinatorMap = new HashMap<PrivilegeNameEnum,PropertyWriter>();

		for(int i = 0;i<headers.length;i++){
			PropertyWriter combinator;
			headers[i] = nameEnums[i].name();
			Field field = fieldMap.get(headers[i]);
			if (field == null){
				throw new RuntimeException("缺少特权属性名对应的配置列:"+headers[i]);
			}
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
			PropertyWriter old = combinatorMap.put(nameEnums[i], combinator);
			if (old != null){
				throw new RuntimeException("特权属性名不能同名:"+headers[i]);
			}
		}

		maxChargeType = new HashMap<PrivilegeNameEnum,String>(nameEnums.length);
		ArrayList<String> tmp = new ArrayList<String>(cfgCacheMap.size());
		HashMap<PrivilegeNameEnum,List<String>> tmpPriMap = new HashMap<PrivilegeNameEnum,List<String>>();
		String configClName = this.getClass().getName();
		ChargeTypePriority chargePriorityHelper = ChargeTypePriority.getShareInstance();
		ChargeTypePriority chargePriority = chargePriorityHelper;
		for (ConfigClass cfg : vals) {
			cfg.ExtraInitAfterLoad(this);
			String sourceName = cfg.getSource();
			if (StringUtils.isBlank(sourceName)){
				GameLog.error("特权", configClName+",key="+cfg.getSource(), "无效特权来源名称");
				throw new RuntimeException("无效特权来源名称");
			}
			
			tmp.add(sourceName);
			
			for(int i = 0; i< nameEnums.length; i++){
				PrivilegeNameEnum privilegeNameEnum = nameEnums[i];
				Object privilegeValue = cfg.getValueByName(privilegeNameEnum);
				String maxType = maxChargeType.get(privilegeNameEnum);
				ConfigClass maxCfg = maxType != null ? cfgCacheMap.get(maxType) : null;
				Object maxVal = maxCfg != null? maxCfg.getValueByName(privilegeNameEnum) : null;
				PropertyWriter combinator = combinatorMap.get(privilegeNameEnum);
				if (combinator.gt(privilegeValue,maxVal)){
					maxChargeType.put(privilegeNameEnum, cfg.getSource());
				}else if (combinator.eq(privilegeValue,maxVal)){
					// 取等级最小的充值类型!
					if (chargePriority.gt(maxCfg.getSource(),cfg.getSource())){
						maxChargeType.put(privilegeNameEnum, cfg.getSource());
					}
				}
				
				if (StringUtils.isBlank(privilegeValue.toString())){
					GameLog.info("特权", configClName+",key="+cfg.getSource(), String.format("充值类型:%s,特权名称:%s,配置值空", cfg.getSource(),privilegeNameEnum.name()),null);
					continue;
				}

				List<String> chargeSrc = tmpPriMap.get(privilegeNameEnum);
				if (chargeSrc == null){
					chargeSrc = new ArrayList<String>();
					tmpPriMap.put(privilegeNameEnum, chargeSrc);
				}
				chargeSrc.add(sourceName);
			}
		}
		// 按优先级排序tmp
		Collections.sort(tmp,chargePriorityHelper);
		sources = new String[tmp.size()];
		sources = tmp.toArray(sources);
		
		//按优先级从小到大的顺序进行检查
		for (int i = 0; i < sources.length; i++) {
			ConfigClass cfg = cfgCacheMap.get(sources[i]);
			if ( i > 0){
				ConfigClass pre = cfgCacheMap.get(sources[i - 1]);
				String preChargeLvl = chargePriorityHelper.guessPreviousChargeLevel(cfg.getSource());
				if (!pre.getSource().equals(preChargeLvl)){
					int idx = chargePriorityHelper.getBestMatchCharge(sources, preChargeLvl);
					if (0 <= idx && idx < sources.length){
						pre = cfgCacheMap.get(sources[idx]);
					}else{
						pre = null;
					}
				}
				cfg.FixEmptyValue(this, pre);
			}else{
				cfg.FixEmptyValue(this, null);
			}
		}
		
		chargeSources = new HashMap<PrivilegeNameEnum,String[]>();
		for(int i = 0; i< nameEnums.length; i++){
			PrivilegeNameEnum privilegeNameEnum = nameEnums[i];
			
			String maxType = maxChargeType.get(privilegeNameEnum);
			if (StringUtils.isBlank(maxType)){
				throw new RuntimeException("无效特权配置:特权"+privilegeNameEnum+"没有配置有效值");
			}

			List<String> chargeSrc = tmpPriMap.get(privilegeNameEnum);
			String[] chargeSrcs;
			if (chargeSrc == null){
				chargeSrcs = new String[0];
				GameLog.info("特权", configClName, String.format("特权名称:%s,没有配置充值类型", privilegeNameEnum.name()),null);
			}else{
				Collections.sort(chargeSrc,chargePriorityHelper);
				//chargeSrc.sort(ChargeTypePriority.getShareInstance());
				chargeSrcs = new String[chargeSrc.size()];
				chargeSrcs = chargeSrc.toArray(chargeSrcs);
			}
			chargeSources.put(privilegeNameEnum, chargeSrcs);
		}
		
		PrivilegeConfigHelper.getInstance().update(priNameCl, this);
		return cfgCacheMap;
	}
	
	@Override
	public PropertyWriter getProWriter(PrivilegeNameEnum name){
		return combinatorMap.get(name);
	}
	
	@Override
	public IConfigChargeSource<PrivilegeNameEnum> getCfg(String chargetType){
		return cfgCacheMap.get(chargetType);
	}
	
	@Override
	public String[] getChargeSource(Enum<?> name){
		return chargeSources.get(name);
	}
	
	@Override
	public boolean eq(PrivilegeProperty oldValue, PrivilegeProperty.Builder newValue) {
		if (oldValue == null && newValue == null) {
			return true;
		}
		if (oldValue != null && newValue == null) {
			return false;
		}
		if (oldValue == null && newValue != null) {
			return false;
		}

		if (oldValue.getKvCount() != privilegeNameEnums.length || newValue.getKvCount() != privilegeNameEnums.length){
			return false;
		}
		
		//为了提高效率，暂时不用这个方法
		//return (oldValue.equals(newValue));
		
		for (int i =0;i<privilegeNameEnums.length;i++){
			PrivilegeNameEnum privilegeNameEnum = privilegeNameEnums[i];
			int ordinal = privilegeNameEnum.ordinal();
			String name = privilegeNameEnum.name();
			PrivilegeValue oldKv = oldValue.getKv(ordinal);
			PrivilegeValue newKv = newValue.getKv(ordinal);
			if (!oldKv.getName().equals(name) || !newKv.getName().equals(name)){
				return false;
			}
			if (!oldKv.getValue().equals(newKv.getValue())){
				return false;
			}
			String oldCharge = oldKv.hasChargeType() ? oldKv.getChargeType() : "";
			String newCharge = newKv.hasChargeType() ? newKv.getChargeType() : "";
			if ( !oldCharge.equals(newCharge)){
				return false;
			}
		}
		return true;
	}

	@Override
	public Field getConfigField(PrivilegeNameEnum name){
		return fieldMap.get(String.valueOf(name));
	}
	
	@Override
	public Field getConfigField(String name){
		return fieldMap.get(name);
	}
	
	@Override
	public void CheckConfig() {
		IPrivilegeThreshold<PrivilegeNameEnum> thresholdHelper = getThresholder();
		Collection<ConfigClass> vals = cfgCacheMap.values();
		for (ConfigClass cfg : vals) {
			cfg.checkThreshold(thresholdHelper,combinatorMap);
		}
	}

	@Override
	public void putPrivilege(IPrivilegeWare privilegeMgr, List<IPrivilegeProvider> providers) {
		List<Pair<IPrivilegeProvider, PrivilegeProperty.Builder>> tmpMap=new ArrayList<Pair<IPrivilegeProvider,PrivilegeProperty.Builder>>();
		
		for (IPrivilegeProvider pro : providers) {
			PrivilegeProperty.Builder pri = PrivilegeProperty.newBuilder();
			
			for (int i = 0; i < privilegeNameEnums.length; i++) {
				PrivilegeNameEnum privilegeEnum = privilegeNameEnums[i];
				String pname = privilegeEnum.name();
				PrivilegeValue.Builder privilegeValues = PrivilegeValue.newBuilder();
				privilegeValues.setName(pname);
				String maxCharge = maxChargeType.get(privilegeEnum);
				if (StringUtils.isNotBlank(maxCharge)){
					//判断最高充值类型是否已经达到或者超过了
					if (!pro.reachChargeLevel(maxCharge)){
						privilegeValues.setChargeType(maxCharge);
					}
				}

				// 从特权提供者获取可能的特权档次
				// 对每个属性过滤无效sources
				int sourceIndex = pro.getBestMatchCharge(chargeSources.get(privilegeEnum));
				if (0 <= sourceIndex && sourceIndex < sources.length) {
					String sourceName = sources[sourceIndex];
					ConfigClass priCfg = cfgCacheMap.get(sourceName);
					privilegeValues.setValue(priCfg.getValueByName(privilegeEnum).toString());
				} else {
					String currentChargeType = pro.getCurrentChargeType();
					if (!"monthnone".equals(currentChargeType)){
						GameLog.info("特权", pro.getClass().getName() + ":当前特权:" + currentChargeType,
								"没有找到对应的特权属性:"+privilegeEnum, null);
					}
					privilegeValues.setValue("");
				}
				
				pri.addKv(privilegeValues);//append!
			}

			Pair<IPrivilegeProvider, PrivilegeProperty.Builder> pair = Pair.Create(pro, pri);
			tmpMap.add(pair);
		}
		
		putPrivilege(this,privilegeMgr,tmpMap);
	}

	@Override
	public Builder combine(Builder acc, AllPrivilege pri) {
		if (pri == null) return acc;
		
		if (acc == null) {
			acc = AllPrivilege.newBuilder();
			PrivilegeProperty.Builder copy = PrivilegeProperty.newBuilder();
			PrivilegeProperty added = getValue(pri);
			copy.mergeFrom(added);
			setValue(acc, copy);
			return acc;
		}
		
		PrivilegeProperty.Builder accB = getValue(acc);
		PrivilegeProperty added = getValue(pri);
		if (accB.getKvCount() < privilegeNameEnums.length){
			PrivilegeProperty.Builder copy = PrivilegeProperty.newBuilder();
			copy.mergeFrom(added);
			setValue(acc, copy);
			//GameLog.error("特权", "putPrivilege或者combinePrivilege有bug", "特权名数量不足");
			return acc;
		}
		
		if (added.getKvCount() < privilegeNameEnums.length){
			return acc;
		}
		
		for(int i = 0; i< privilegeNameEnums.length; i++){
			PrivilegeNameEnum privilegeEnum = privilegeNameEnums[i];
			String pname = privilegeEnum.name();
			PropertyWriter pwriter = combinatorMap.get(privilegeEnum);
			PrivilegeValue.Builder accVal = accB.getKvBuilder(privilegeEnum.ordinal());
			PrivilegeValue right = added.getKv(privilegeEnum.ordinal());
			
			accVal = pwriter.combine(accVal,right, pname);
			accB.setKv(privilegeEnum.ordinal(), accVal);
		}
		return acc;
	}

	@Override
	public Object getValue(PrivilegeProperty currentPri, PrivilegeNameEnum pname) {
		if (currentPri == null || pname == null) {
			return null;
		}
		
		if (currentPri.getKvCount() < pname.ordinal()+1){
			return null;
		}
		
		String priName = pname.name();
		PrivilegeValue kv = currentPri.getKv(pname.ordinal());
		if (!priName.equals(kv.getName())){
			return null;
		}
		
		PropertyWriter pwriter = combinatorMap.get(pname);
		if (pwriter == null){
			return null;
		}
		
		return pwriter.extractValue(kv.getValue());
	}
}
