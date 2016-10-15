package com.rwbase.dao.fetters.pojo.cfg.dao;

import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.fetters.pojo.cfg.MagicEquipConditionCfg;

public class FetterMagicEquipCfgDao extends CfgCsvDao<MagicEquipConditionCfg> {

	public static int TYPE_MAGICWEAPON = 1;

	public static int TYPE_FIXEQUIP = 2;

	/** key=modelID, 优化检索速度 */
	private Map<Integer, List<MagicEquipConditionCfg>> modelData;

	/**
	 * <pre>
	 * 第一层key 对应 {@link MagicEquipConditionCfg#getType()}
	 * 第二层key 对应 {@link MagicEquipConditionCfg#getHeroModelID()}
	 * 加入{@link MagicEquipConditionKey}作为类型筛选，加快剔除符合玩家条件{@link MagicEquipConditionCfg}
	 * 因为相同的{@link MagicEquipConditionKey}只生效一条记录{@link MagicEquipConditionCfg}
	 * </pre>
	 **/
	private Map<Integer, IntObjectHashMap<List<MagicEquipConditionCfg>>> typeData;

	private IntObjectHashMap<MagicEquipConditionCfg> cfgMap;

	public static FetterMagicEquipCfgDao getInstance() {
		return SpringContextUtil.getBean(FetterMagicEquipCfgDao.class);
	}

	@Override
	protected Map<String, MagicEquipConditionCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("HeroFetters/magicEquipConditionCfg.csv", MagicEquipConditionCfg.class);
		return cfgCacheMap;
	}

	@Override
	public void CheckConfig() {
		List<MagicEquipConditionCfg> list = getAllCfg();
		Map<Integer, IntObjectHashMap<List<MagicEquipConditionCfg>>> typeData = new HashMap<Integer, IntObjectHashMap<List<MagicEquipConditionCfg>>>();
		Map<Integer, List<MagicEquipConditionCfg>> modelData = new HashMap<Integer, List<MagicEquipConditionCfg>>();
		IntObjectHashMap<MagicEquipConditionCfg> cfgMap = new IntObjectHashMap<MagicEquipConditionCfg>();
		HashSet<Integer> uniqueIdCheck = new HashSet<Integer>();
		for (MagicEquipConditionCfg cfg : list) {
			cfg.formateData();
			if (!uniqueIdCheck.add(cfg.getUniqueId())) {
				throw new ExceptionInInitializerError("存在重复羁绊id=" + cfg.getUniqueId() + "," + cfg.getType() + "," + cfg.getSubType());
			}
			cfgMap.put(cfg.getUniqueId(), cfg);
			List<Integer> modelIDList = cfg.getModelIDList();
			for (Integer modelID : modelIDList) {
				if (modelData.containsKey(modelID)) {
					List<MagicEquipConditionCfg> modelList = modelData.get(modelID);
					modelList.add(cfg);
				} else {
					ArrayList<MagicEquipConditionCfg> temp = new ArrayList<MagicEquipConditionCfg>();
					temp.add(cfg);
					modelData.put(modelID, temp);
				}
			}
			IntObjectHashMap<List<MagicEquipConditionCfg>> heroModelMap = typeData.get(cfg.getType());
			if (heroModelMap == null) {
				heroModelMap = new IntObjectHashMap<List<MagicEquipConditionCfg>>();
				typeData.put(cfg.getType(), heroModelMap);
			}
			Integer heroModelId = cfg.getHeroModelID();
			List<MagicEquipConditionCfg> typeList = heroModelMap.get(heroModelId);
			if (typeList == null) {
				typeList = new ArrayList<MagicEquipConditionCfg>();
				heroModelMap.put(heroModelId, typeList);
			}
			typeList.add(cfg);
		}
		for (Map.Entry<Integer, IntObjectHashMap<List<MagicEquipConditionCfg>>> entry : typeData.entrySet()) {
			for (IntObjectMap.Entry<List<MagicEquipConditionCfg>> heroModelEntry : entry.getValue().entries()) {
				ArrayList<MagicEquipConditionCfg> typeList = (ArrayList<MagicEquipConditionCfg>) heroModelEntry.value();
				typeList.trimToSize();
				heroModelEntry.setValue(Collections.unmodifiableList(typeList));
			}
		}
		this.typeData = typeData;
		this.modelData = modelData;
		this.cfgMap = cfgMap;
	}

	/**
	 * 找出目标类型的配置
	 * 
	 * @param type
	 *            法宝或神器
	 * @return
	 */
	public List<MagicEquipConditionCfg> getCfgByType(int type, int heroModelId) {
		IntObjectHashMap<List<MagicEquipConditionCfg>> heroModelMap = typeData.get(type);
		if (heroModelMap == null) {
			return Collections.emptyList();
		}
		List<MagicEquipConditionCfg> list = heroModelMap.get(heroModelId);
		if (list == null) {
			return Collections.emptyList();
		} else {
			return list;
		}
	}

	/**
	 * 根据modelID获取配置列表
	 * 
	 * @param modelId
	 * @return
	 */
	public List<MagicEquipConditionCfg> getCfgListByModelID(int modelId) {
		if (modelData.containsKey(modelId)) {
			return Collections.unmodifiableList(modelData.get(modelId));
		}
		return Collections.emptyList();

	}

	/**
	 * 根据唯一id获取配置对象
	 * 
	 * @param uniqueId
	 * @return
	 */
	public MagicEquipConditionCfg get(int uniqueId) {
		return this.cfgMap.get(uniqueId);
	}

}
