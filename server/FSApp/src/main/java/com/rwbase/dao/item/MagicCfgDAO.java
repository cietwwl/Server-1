package com.rwbase.dao.item;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.log.GameLog;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.item.pojo.MagicCfg;
import com.rwbase.dao.skill.SkillCfgDAO;

public class MagicCfgDAO extends CfgCsvDao<MagicCfg> {
	public static MagicCfgDAO getInstance() {
		return SpringContextUtil.getBean(MagicCfgDAO.class);
	}

	@Override
	public Map<String, MagicCfg> initJsonCfg() {
		Map<String, MagicCfg> readCsv2Map = CfgCsvHelper.readCsv2Map("item/Magic.csv", MagicCfg.class);

		if (readCsv2Map != null && !readCsv2Map.isEmpty()) {
			for (Entry<String, MagicCfg> e : readCsv2Map.entrySet()) {
				MagicCfg value = e.getValue();
				if (value == null) {
					continue;
				}

				value.initData();
				value.ExtraInitAfterLoad();
			}
		}

		return cfgCacheMap = readCsv2Map;
	}
	
	@Override
	public void CheckConfig() {
		super.CheckConfig();
		this.checkSkill();
		this.checkComposeTarget();
	}

	// TODO 所有表加载完成后，应该检查decomposeGoodList里面的模板ID是否存在于Consume表
	
	public void checkSkill() {
		SkillCfgDAO skillDao = SkillCfgDAO.getInstance();
		for (Iterator<String> keyItr = cfgCacheMap.keySet().iterator(); keyItr.hasNext();) {
			String key = keyItr.next();
			MagicCfg cfg = cfgCacheMap.get(key);
			if (!key.startsWith("603")) {
				continue;
			}
			if (cfg.getMagicType() == MagicCfg.TYPE_MAGIC) {
				Map<Integer, List<String>> unlockSkillList = cfg.getUnlockSkillIdList();
				if (unlockSkillList.size() > 0) {
					for (Iterator<Integer> unlockKeyItr = unlockSkillList.keySet().iterator(); unlockKeyItr.hasNext();) {
						Integer magicId = unlockKeyItr.next();
						if (!cfgCacheMap.containsKey(magicId.toString())) {
							GameLog.error("MagicCfgDAO#checkSkill", magicId.toString(), "解锁技能的目标法宝不存在！目标id：" + magicId + "，源id：" + cfg.getId());
						}
						List<String> skillIds = unlockSkillList.get(magicId);
						for (String skillId : skillIds) {
							if (skillDao.getCfg(skillId) == null) {
								GameLog.error("MagicCfgDAO#checkSkill", skillId, "解锁的技能不存在！技能id：" + skillId + "，法宝id：" + cfg.getId());
							}
						}
					}
				}
			}
		}
	}
	
	public void checkComposeTarget() {
		for (Iterator<String> keyItr = cfgCacheMap.keySet().iterator(); keyItr.hasNext();) {
			MagicCfg cfg = cfgCacheMap.get(keyItr.next());
			if (!String.valueOf(cfg.getId()).startsWith("6049")) {
				continue;
			}
			int itemId = cfg.getComposeItemID();
			if (itemId > 0) {
				String cfgId = String.valueOf(itemId);
				if (!cfgCacheMap.containsKey(String.valueOf(cfgId))) {
					GameLog.error("MagicCfgDAO#checkComposeTarget", cfgId, "合成的不存在！目标id：" + cfgId + "，碎片id：" + cfg.getId());
				}
			}
		}
	}
}
