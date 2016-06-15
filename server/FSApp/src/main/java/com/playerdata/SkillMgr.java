package com.playerdata;

import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;

import com.common.Action;
import com.log.GameLog;
import com.playerdata.readonly.SkillMgrIF;
import com.rwbase.common.enu.EPrivilegeDef;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwbase.dao.publicdata.PublicData;
import com.rwbase.dao.publicdata.PublicDataCfgDAO;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwbase.dao.skill.SkillCfgDAO;
import com.rwbase.dao.skill.SkillFeeCfgDAO;
import com.rwbase.dao.skill.pojo.Skill;
import com.rwbase.dao.skill.pojo.SkillCfg;
import com.rwbase.dao.skill.pojo.SkillFeeCfg;
import com.rwbase.dao.skill.pojo.SkillHelper;
import com.rwbase.dao.skill.pojo.SkillItemHolder;
import com.rwbase.dao.skill.pojo.TableSkill;
import com.rwbase.dao.user.CfgBuySkill;
import com.rwbase.dao.user.CfgBuySkillDAO;
import com.rwproto.PrivilegeProtos.HeroPrivilegeNames;
import com.rwproto.SkillServiceProtos.SkillResponse;
import com.rwproto.SkillServiceProtos.TagSkillData;

public class SkillMgr extends IDataMgr implements SkillMgrIF {

	public static final int MAX_SKILL_COUNT = 10;// 最大技能点数量
	public static final int DIE_SKILL_LEVEL = -2;

	private SkillItemHolder skillItemHolder;

	public void init(Hero pRole) {
		initPlayer(pRole);
		String ownerId = pRole.getUUId();
		skillItemHolder = new SkillItemHolder(ownerId);
	}

	public void regChangeCallBack(Action callBack) {
		skillItemHolder.regChangeCallBack(callBack);
	}

	@Override
	public boolean save() {
		skillItemHolder.flush(false);
		return true;
	}

	public boolean flush() {
		skillItemHolder.flush(true);
		return true;
	}

	public List<Skill> getSkillList() {
		return skillItemHolder.getItemList();
	}

	public boolean canUpgradeSkill() {
		List<Skill> list = getSkillList();
		for (Skill skill : list) {
			if (canUpgradeSkill(skill, false)) {
				return true;
			}
		}
		return false;
	}

	private boolean canUpgradeSkill(Skill skill, boolean showError) {
		if (!CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.SKILL_OPEN, m_pPlayer.getLevel())) {
			if (showError) {
				m_pPlayer.NotifyCommonMsg("角色等级不足！");
			}
			return false;
		}
		if (skill.getLevel() <= 0) {
			if (showError) {
				m_pPlayer.NotifyCommonMsg("技能未开放！");
			}
			return false;
		}
		if (skill.getLevel() >= m_pOwner.getLevel()) {
			if (showError) {
				// m_pPlayer.NotifyCommonMsg("技能等级不能超过人物等级");
			}
			return false;
		}
		SkillCfg skillCfg = (SkillCfg) SkillCfgDAO.getInstance().getCfgById(skill.getSkillId());
		if (StringUtils.isBlank(skillCfg.getNextSillId())) {
			if (showError) {
				m_pPlayer.NotifyCommonMsg("技能已是最高等级！");
			}
			return false;
		}
		int type = m_pOwner.getRoleType() == eRoleType.Hero ? 0 : 1;
		SkillFeeCfg skillFeeCfg = SkillFeeCfgDAO.getInstance().getSkillFeeCfg(type, skill.getOrder(), skill.getLevel());
		if (skillFeeCfg == null) {
			if (showError) {
				m_pPlayer.NotifyCommonMsg("skillFeeCfg表错误！");
			}
			return false;
		}
		// reshSkillPoint();
		// 用预期的方式代替真正回复技能点
		if (m_pPlayer.getUserGameDataMgr().getSkillPointCount() <= 0 && !m_pPlayer.getSkillMgr().expectSkillPointRecover()) {
			if (showError) {
				m_pPlayer.NotifyCommonMsg("技能点冷却中！");
			}
			return false;
		}
		if (skillFeeCfg.getCoin() > m_pPlayer.getUserGameDataMgr().getCoin()) {
			if (showError) {
				m_pPlayer.NotifyCommonMsg("金钱不足！");
			}
			return false;
		}
		return true;
	}

	public boolean updateSkill(String skillId, int addLevel) {
		Skill skill = null;
		List<Skill> skillList = skillItemHolder.getItemList();
		for (Skill current : skillList) {
			if (current.getSkillId().equals(skillId)) {
				skill = current;
				break;
			}
		}
		if (skill == null) {
			return false;
		}
		int level = skill.getLevel() + addLevel;
		StringTokenizer token = new StringTokenizer(skillId, "_");
		String newSkillId = token.nextToken() + "_" + level;
		SkillCfg newSkillCfg = (SkillCfg) SkillCfgDAO.getInstance().getCfgById(skill.getSkillId());
		if (newSkillCfg == null) {
			return false;
		}
		skill.setLevel(level);
		skill.setSkillId(newSkillId);
		updateMoreInfo(skill, null);
		skillItemHolder.updateItem(m_pPlayer, skill);
		return true;
	}

	/**
	 * vip升级增加技能点
	 * 
	 * @param type
	 * @param temp
	 */
	public void updateSkillPointTime(EPrivilegeDef type, int temp) {
		// for (int i = 1; i <= temp; i++) {
		// m_pPlayer.addSkillPointTime(getCoolTime());
		// }
	}

	public SkillResponse.Builder getSkillInfo(Player player) {
		SkillResponse.Builder response = SkillResponse.newBuilder();
		UserGameDataMgr userGameDataMgr = player.getUserGameDataMgr();
		reshSkillPoint();
		// int max = PrivilegeCfgDAO.getInstance().getDef(m_pPlayer.getVip(), EPrivilegeDef.SKILL_POINT_COUNT);
		int max = getMaxSkillCount();
		int count = userGameDataMgr.getSkillPointCount();
		response.setRemainSkillPoints(count);
		int rs = 0;
		if (count < max) {
			long currentTimeMillis = System.currentTimeMillis();
			long lastRecoverTimeMillis = userGameDataMgr.getLastRecoverSkillPointTime();
			long remainSeconds = TimeUnit.MILLISECONDS.toSeconds((getCoolTime() - (currentTimeMillis - lastRecoverTimeMillis)));
			if (remainSeconds > 0) {
				rs = (int) remainSeconds;
			}
		}
		response.setRemainSeconds(rs + 1);
		response.setRecoverSkillPoints(max - count);
		return response;
	}

	/**
	 * 激活佣兵技能
	 * 
	 * @param level
	 * @param quality
	 */
	public void activeSkill(int level, int quality) {
		// int maxOrder = -1;
		// modify by Jamaz 2015-11-23 抽取判断能否激活技能的方法
		for (Skill skill : skillItemHolder.getItemList()) {
			if (skill.getLevel() <= 0 && isSkillCanActive(skill, level, quality)) {
				skill.setLevel(1);
				updateMoreInfo(skill, null);
				// if (maxOrder < skill.getOrder()) {
				// maxOrder = skill.getOrder();
				// }
			}
		}
		skillItemHolder.synAllData(m_pPlayer, -1);
		// if (maxOrder > 0) {
		// openSkillOnClient(maxOrder);
		// }
	}

	// private void openSkillOnClient(int skillOrder) {
	// SkillResponse.Builder response = SkillResponse.newBuilder();
	// response.setRoleId(m_pOwner.getUUId());
	// response.setEventType(SkillEventType.Skill_Open);
	// response.setOrder(skillOrder);
	// response.setResultType(SkillResultType.succeed);
	// m_pPlayer.SendMsg(Command.MSG_SKILL, response.build().toByteString());
	// }

	/**
	 * 判断能否激活技能
	 * 
	 * @param skill
	 * @param heroLevel
	 * @param heroQuality
	 * @return
	 */
	public boolean isSkillCanActive(Skill skill, int heroLevel, int heroQuality) {
		SkillCfg cfg = SkillCfgDAO.getInstance().getCfg(skill.getSkillId());
		if (cfg == null) {
			return false;
		}
		return cfg.getRoleLevel() <= heroLevel && cfg.getRoleQuality() <= heroQuality;
	}

	/**
	 * 改变技能id
	 * 
	 * @param player
	 * @param playerId
	 */
	public void changeSkill(RoleCfg cfg) {
		List<Skill> skillList = skillItemHolder.getItemList();
		if (skillList.size() <= 0) {
			initSkill(cfg);
			return;
		}
		List<Skill> cfgList = RoleCfgDAO.getInstance().getSkill(cfg.getRoleId());
		for (int i = 0; i < skillList.size(); i++) {
			Skill pSkill = skillList.get(i);
			String oldId = cfgList.get(pSkill.getOrder()).getSkillId();
			String newId = oldId.split("_")[0] + "_" + pSkill.getSkillId().split("_")[1];
			pSkill.setSkillId(newId);
			updateMoreInfo(pSkill, null);
			skillItemHolder.updateItem(m_pPlayer, pSkill);
			System.out.println("skillchangeformplayerCareerChange:skillorder=" + pSkill.getOrder() + "oldSkillId=" + oldId + ",newid=" + newId);
		}
	}

	/**
	 * get 初始化技能
	 * 
	 * @param pRole
	 */
	public void initSkill(RoleCfg rolecfg) {
		// List<Skill> battleSkillList = initSkill(rolecfg, m_pOwner.getQualityId(), m_pOwner.getLevel());
		List<Skill> battleSkillList = SkillHelper.initSkill(rolecfg, m_pOwner.getQualityId(), m_pOwner.getLevel());
		skillItemHolder.addItem(m_pPlayer, battleSkillList);
	}

	// /**
	// * 初始化技能
	// *
	// * @param rolecfg
	// * @param qualityId
	// * @param playerLevel
	// * @return
	// */
	// public List<Skill> initSkill(RoleCfg rolecfg, String qualityId, int playerLevel) {
	// List<Skill> cfgSkillList = RoleCfgDAO.getInstance().getSkill(rolecfg.getRoleId());
	// // 技能buff有相关性，要先一次过加入到列表才行
	// List<Skill> battleSkillList = new ArrayList<Skill>();
	// for (Skill skilltmp : cfgSkillList) {
	// SkillCfg cfg = SkillCfgDAO.getInstance().getCfg(skilltmp.getSkillId());
	// if (cfg == null) {
	// if (skilltmp.getLevel() != DIE_SKILL_LEVEL) {
	// m_pPlayer.NotifyCommonMsg("配置表错误：没有skillID为" + skilltmp.getSkillId() + "的技能");
	// }
	// } else {
	// battleSkillList.add(skilltmp);
	// }
	// }
	//
	// for (Skill pSkill : battleSkillList) {
	// SkillCfg cfg = SkillCfgDAO.getInstance().getCfg(pSkill.getSkillId());
	//
	// int quality = RoleQualityCfgDAO.getInstance().getQuality(qualityId);
	// int level = cfg.getRoleLevel() <= playerLevel && cfg.getRoleQuality() <= quality ? 1 : 0;
	// pSkill.setLevel(level);
	//
	// updateMoreInfo(pSkill, cfgSkillList);
	// }
	//
	// return battleSkillList;
	// }

	// /**
	// * 重置掉技能伤害，技能对敌Buff和SelfBuff，技能额外附加伤害，以及附加百分比
	// *
	// * @param skillList
	// */
	// private void checkAllSkill(List<Skill> skillList) {
	// SkillCfgDAO cfgDAO = SkillCfgDAO.getInstance();
	// // 相互影响的伤害值
	// for (int i = skillList.size() - 1; i >= 0; --i) {
	// Skill skillInfo = skillList.get(i);
	// if (skillInfo.getLevel() <= 0) {
	// continue;
	// }
	//
	// SkillCfg skillCfg = cfgDAO.getCfg(skillInfo.getSkillId());
	// if (skillCfg == null) {
	// return;
	// }
	//
	// skillInfo.getBuffId().clear();
	// skillInfo.getSelfBuffId().clear();
	// skillInfo.setExtraDamage(0);
	// skillInfo.setSkillRate(0);
	//
	// // 设置技能伤害
	// skillInfo.setSkillDamage(skillCfg.getSkillDamage());
	//
	// // 检查控制技能
	// checkControl(skillInfo, skillList);
	//
	// // 检查技能所受的Buff
	// checkSkillBuffs(skillInfo, skillCfg, skillList);
	//
	// // 更新技能
	// if (skillItemHolder != null) {
	// skillItemHolder.updateItem(m_pPlayer, skillInfo);
	// }
	// }
	// }
	//
	// /**
	// * 检查受那些技能的影响
	// *
	// * @param skill
	// * @param skillList
	// */
	// private void checkControl(Skill skill, List<Skill> skillList) {
	// String updateSkillId = skill.getSkillId();
	// if (skill.getLevel() <= 0) {
	// return;
	// }
	//
	// SkillCfgDAO cfgDAO = SkillCfgDAO.getInstance();
	//
	// float skillRate = 0;
	// int extraDamage = 0;
	//
	// // 相互影响的伤害值
	// for (int i = skillList.size() - 1; i >= 0; --i) {
	// Skill skillInfo = skillList.get(i);
	// if (skillInfo.getLevel() <= 0) {
	// continue;
	// }
	//
	// String skillId = skillInfo.getSkillId();
	// SkillCfg cfg = cfgDAO.getCfg(skillId);
	// if (cfg == null) {
	// continue;
	// }
	//
	// String controlId = cfg.getControlId();
	// if (StringUtils.isEmpty(controlId)) {
	// continue;
	// }
	//
	// boolean hasControl = false;// 是否受此技能影响
	// String[] arr = controlId.split("_");
	// for (int j = 0, len = arr.length; j < len; j++) {
	// String controlSkillId = arr[j];// 控制的技能到的技能Id
	// if (updateSkillId.startsWith(controlSkillId)) {
	// hasControl = true;
	// break;
	// }
	// }
	//
	// if (hasControl) {
	// skillRate += cfg.getSkillRate();
	// extraDamage += cfg.getExtraDamage();
	// }
	// }
	//
	// skill.setExtraDamage(extraDamage);
	// skill.setSkillRate(skillRate);
	// }
	//
	// /**
	// * 检查技能所有的buff
	// *
	// * @param skill
	// * @param skillCfg
	// * @param skillList
	// */
	// private void checkSkillBuffs(Skill skill, SkillCfg skillCfg, List<Skill> skillList) {
	// String updateSkillId = skill.getSkillId();
	// if (skill.getLevel() <= 0) {
	// return;
	// }
	//
	// SkillCfgDAO cfgDAO = SkillCfgDAO.getInstance();
	//
	// List<Integer> buffList = new ArrayList<Integer>();
	// List<Integer> selfBuffList = new ArrayList<Integer>();
	//
	// // 相互影响的伤害值
	// for (int i = skillList.size() - 1; i >= 0; --i) {
	// Skill skillInfo = skillList.get(i);
	// if (skillInfo.getLevel() <= 0) {
	// continue;
	// }
	//
	// String skillId = skillInfo.getSkillId();
	// SkillCfg cfg = cfgDAO.getCfg(skillId);
	// if (cfg == null) {
	// continue;
	// }
	//
	// List<Integer> buffs = checkBuffer(updateSkillId, cfg.getBuffId(), buffList);
	// if (buffs != null && !buffs.isEmpty()) {
	// buffList.addAll(buffs);
	// }
	//
	// List<Integer> selfBuffs = checkBuffer(updateSkillId, cfg.getSelfBuffId(), selfBuffList);
	// if (selfBuffs != null && !selfBuffs.isEmpty()) {
	// selfBuffList.addAll(selfBuffs);
	// }
	// }
	//
	// skill.setBuffId(buffList);
	// skill.setSelfBuffId(selfBuffList);
	// }
	//
	// /**
	// * 检查被影响到的Buff列表
	// *
	// * @param updateSkillId
	// * @param buffIdStr
	// * @param hasBuff
	// * @return
	// */
	// private List<Integer> checkBuffer(String updateSkillId, String buffIdStr, List<Integer> hasBuff) {
	// if (StringUtils.isEmpty(buffIdStr)) {
	// return null;
	// }
	//
	// String[] split = buffIdStr.split(";");
	//
	// List<Integer> buffList = new ArrayList<Integer>();
	//
	// for (int i = 0, len = split.length; i < len; i++) {
	// String[] split1 = split[0].split("_");
	//
	// if (!updateSkillId.startsWith(split1[0])) {
	// return null;
	// }
	//
	// for (int j = 1, buffLen = split1.length; j < buffLen; j++) {
	// int buffId = Integer.parseInt(split1[j]);
	// if (buffId <= 0 || hasBuff.contains(buffId)) {
	// continue;
	// }
	//
	// buffList.add(buffId);
	// }
	// }
	//
	// return buffList;
	// }

	/**
	 * 更新技能其它信息
	 * 
	 * @param pSkill
	 */
	private void updateMoreInfo(Skill pSkill, List<Skill> skillList) {
		if (pSkill.getLevel() <= 0) {
			return;
		}

		String skillId = pSkill.getSkillId();
		SkillCfg pSkillCfg = (SkillCfg) SkillCfgDAO.getInstance().getCfgById(skillId);
		if (pSkillCfg == null) {
			if (pSkill.getLevel() != DIE_SKILL_LEVEL) {
				if (m_pPlayer != null) {
					m_pPlayer.NotifyCommonMsg("配置表错误：没有skillID为" + skillId + "的技能");
				}
			}
			return;
		}

		List<Skill> itemList = skillItemHolder.getItemList();

		SkillHelper.checkAllSkill(itemList);// 检查所有的技能

		StringBuilder sb = new StringBuilder();
		for (int i = 0, size = itemList.size(); i < size; i++) {
			Skill skill = itemList.get(i);
			if (skill == null) {
				continue;
			}

			skillItemHolder.updateItem(m_pPlayer, skill);

			sb.append(String.format("技能Order[%s],技能Id[%s],等级[%s],伤害[%s],额外[%s],系数[%s],buff{%s},selfBuff{%s}\n", skill.getOrder(), skill.getSkillId(), skill.getLevel(), skill.getSkillDamage(),
					skill.getExtraDamage(), skill.getSkillRate(), skill.getBuffId().toString(), skill.getSelfBuffId().toString()));
		}

		GameLog.info("升级技能模块", "升级后所有效果", sb.toString());

		// pSkill.setSkillRate();
		// pSkill.setExtraDamage();
		// pSkill.setSkillDamage(pSkillCfg.getSkillDamage());
		// pSkill.setSelfBuffId(new ArrayList<Integer>());

		// 查找控制技能的技能系数和额外伤害
		// if (StringUtils.isNotBlank(pSkillCfg.getControlId())) {
		// String[] relatedSkillCfgId = pSkillCfg.getControlId().split("_");
		// for (String cfgIdTmp : relatedSkillCfgId) {
		// Skill targetSkill = getSkill(cfgIdTmp, skillList);
		// if (targetSkill != null && targetSkill.getLevel() > 0) {
		// SkillCfg conSkillCfg = SkillCfgDAO.getInstance().getCfg(targetSkill.getSkillId());
		//
		// targetSkill.setSkillRate(conSkillCfg.getSkillRate() + pSkillCfg.getSkillRate());
		// targetSkill.setExtraDamage(conSkillCfg.getExtraDamage() + pSkillCfg.getExtraDamage());
		// }
		// }
		// }

		// checkSkillControlSkill(pSkill, skillList);// 改变我自己的技能
		//
		// pSkill.getBuffId().clear();
		// // 查找控制技能的buff
		// if (StringUtils.isNotBlank(pSkillCfg.getBuffId())) {
		// parseSkillBuffs(skillList, pSkillCfg.getBuffId(), false);
		// }
		//
		// pSkill.getSelfBuffId().clear();
		// if (StringUtils.isNotBlank(pSkillCfg.getSelfBuffId())) {
		// parseSkillBuffs(skillList, pSkillCfg.getSelfBuffId(), true);
		// }
		//
		// // 检查其他的
		// if (skillList != null) {
		// for (int i = skillList.size() - 1; i >= 0; --i) {
		// Skill skill = skillList.get(i);
		// if (skill.getSkillId().equals(skillId)) {
		// continue;
		// }
		//
		// checkSkillControlSkill(skill, skillList);
		// }
		// }
	}

	// /**
	// * 检查自己影响了那些技能
	// *
	// * @param pSkill
	// * @param skillList
	// */
	// private void checkSkillControlSkill(Skill pSkill, List<Skill> skillList) {
	// if (skillList == null) {
	// return;
	// }
	//
	// String skillId = pSkill.getSkillId();
	// SkillCfg pSkillCfg = SkillCfgDAO.getInstance().getCfgById(skillId);
	// if (pSkillCfg == null) {
	// return;
	// }
	//
	// float skillRate = pSkillCfg.getSkillRate();
	// int extraDamage = pSkillCfg.getExtraDamage();
	// int skillDamage = pSkillCfg.getSkillDamage();
	//
	// for (int i = skillList.size() - 1; i >= 0; --i) {
	// Skill skill = skillList.get(i);
	// if (skill.getSkillId().equals(skillId)) {
	// continue;
	// }
	//
	// if (skill.getLevel() <= 0) {
	// continue;
	// }
	//
	// SkillCfg conSkillCfg = SkillCfgDAO.getInstance().getCfg(skill.getSkillId());
	// if (conSkillCfg == null) {
	// continue;
	// }
	//
	// String controlId = conSkillCfg.getControlId();
	// if (StringUtils.isEmpty(controlId)) {
	// continue;
	// }
	//
	// String[] arr = controlId.split("_");
	// for (int j = 0, len = arr.length; j < len; j++) {
	// if (skillId.startsWith(arr[j])) {// 受到这个技能的影响了
	// skillRate += conSkillCfg.getSkillRate();
	// extraDamage += conSkillCfg.getExtraDamage();
	// break;
	// }
	// }
	// }
	//
	// pSkill.setSkillRate(skillRate);
	// pSkill.setExtraDamage(extraDamage);
	// pSkill.setSkillDamage(skillDamage);
	// }
	//
	// private void parseSkillBuffs(List<Skill> skillList, String id, boolean isSelf) {
	// String[] skillBufflist = id.split(";");
	// Skill targetSkill;
	// String[] targetBuffarr;
	// for (String skillBuff : skillBufflist) {
	// targetBuffarr = skillBuff.split("_");// skillBuff xxxx
	// targetSkill = getSkill(targetBuffarr[0], skillList);// 目标技能
	// if (targetSkill != null && targetSkill.getLevel() > 0) {
	// for (int j = 1; j < targetBuffarr.length; j++) {
	// String targetBuff = targetBuffarr[j];
	// if (StringUtils.isNotBlank(targetBuff) && !targetBuff.equals("0")) {
	// List<Integer> curBuffList = null;
	// if (isSelf) {
	// curBuffList = targetSkill.getSelfBuffId();
	// } else {
	// curBuffList = targetSkill.getBuffId();
	// }
	// if (curBuffList.size() > j - 1) {
	// curBuffList.set(j - 1, Integer.parseInt(targetBuff));
	// } else {
	// curBuffList.add(Integer.parseInt(targetBuff));
	// }
	// }
	// }
	// }
	// }
	// }

	/**
	 * 发送全部技能
	 */
	public void syncAllSkill(int version) {
		reshSkillPoint();
		skillItemHolder.synAllData(m_pPlayer, version);
	}

	/**
	 * 
	 * @param userId
	 * @return
	 */
	public List<TagSkillData> getSkillProtoList() {
		List<Skill> skillItemList = skillItemHolder.getItemList();
		List<TagSkillData> list = getSkillProtoList(skillItemList);
		return list;
	}

	/**
	 * 
	 * @param userId
	 * @return
	 */
	public List<TagSkillData> getSkillProtoList(List<Skill> skillLIst) {
		return SkillHelper.getSkillProtoList(skillLIst);
	}

	public void buySkillPoint() {
		int times = m_pPlayer.getUserGameDataMgr().getBuySkillTimes() + 1;
		CfgBuySkill cfgBuySkill = CfgBuySkillDAO.getInstance().getCfgBuySkill(times);
		if (m_pPlayer.getUserGameDataMgr().getSkillPointCount() == cfgBuySkill.getSkillpoint()) {
			return;
		}
		int skillTotalCount = m_pPlayer.getUserGameDataMgr().getSkillPointCount() + cfgBuySkill.getSkillpoint();
		int maxSkillCount = getMaxSkillCount();
		if (skillTotalCount > maxSkillCount) {
			skillTotalCount = maxSkillCount;
		}
		m_pPlayer.getUserGameDataMgr().addGold(-cfgBuySkill.getNeedPurse());
		m_pPlayer.getUserGameDataMgr().setBuySkillTimes(m_pPlayer.getUserGameDataMgr().getBuySkillTimes() + 1);
		m_pPlayer.getUserGameDataMgr().setSkillPointCount(skillTotalCount);

		return;
	}

	public int getAddSkillPoint() {
		long lastCountTime = m_pPlayer.getUserGameDataMgr().getLastRecoverSkillPointTime();
		long currentTimeMillis = System.currentTimeMillis();
		long timeSpan = currentTimeMillis - lastCountTime;
		long recoverSpanTime = getCoolTime();
		// 大于1分钟
		if (timeSpan > recoverSpanTime) {
			int recoverPoint = (int) (timeSpan / recoverSpanTime);
			long timeLeft = timeSpan % recoverSpanTime;
			lastCountTime = currentTimeMillis - timeLeft;
			m_pPlayer.getUserGameDataMgr().setLastRecoverSkillPointTime(lastCountTime);
			return recoverPoint;
		}
		return 0;
	}

	/**
	 * 预估能够时间恢复技能点
	 * 
	 * @return
	 */
	public boolean expectSkillPointRecover() {
		long lastCountTime = m_pPlayer.getUserGameDataMgr().getLastRecoverSkillPointTime();
		long currentTimeMillis = System.currentTimeMillis();
		long timeSpan = currentTimeMillis - lastCountTime;
		long recoverSpanTime = getCoolTime();
		return timeSpan > recoverSpanTime;
	}

	/**
	 * 重算技能点（技能点数量，技能点时间）
	 */
	public void reshSkillPoint() {
		int count = m_pPlayer.getUserGameDataMgr().getSkillPointCount();
		// int max = PrivilegeCfgDAO.getInstance().getDef(m_pPlayer.getVip(), EPrivilegeDef.SKILL_POINT_COUNT);
		int max = getMaxSkillCount();
		if (count >= max) {
			return;
		}
		int addSkillPoint = getAddSkillPoint();
		if (addSkillPoint <= 0) {
			return;
		}
		int total = addSkillPoint + count;
		if (total >= max) {
			total = max;
			m_pPlayer.getUserGameDataMgr().setLastRecoverSkillPointTime(0);
		}
		m_pPlayer.getUserGameDataMgr().setSkillPointCount(total);
	}

	/**
	 * 获取技能表
	 * 
	 * @return
	 */
	public TableSkill getTableSkill() {
		List<Skill> skillList = skillItemHolder.getItemList();

		TableSkill skillTable = new TableSkill();
		skillTable.setSkillList(skillList);
		skillTable.setUserId(this.m_pOwner.getUUId());
		return skillTable;
	}

	/**
	 * 根据order获取技能
	 * 
	 * @param order
	 * @return
	 */
	public Skill getSkill(int order) {
		return skillItemHolder.getByOrder(order);
	}

	// /**
	// * 根据id获取技能
	// *
	// * @param cfgId
	// * @return
	// */
	// private Skill getSkill(String cfgId, List<Skill> skillList) {
	// Skill targetItem = null;
	// if (skillList == null) {
	// skillList = skillItemHolder.getItemList();
	// }
	//
	// for (Skill skill : skillList) {
	// if (skill.getSkillId().startsWith(cfgId)) {
	// targetItem = skill;
	// break;
	// }
	// }
	//
	// return targetItem;
	// }

	public long getCoolTime() {
		long cooltime = PublicDataCfgDAO.getInstance().getPublicDataValueById(PublicData.SKILL_POINT_COOL_TIME);
		int decSecond = m_pPlayer.getPrivilegeMgr().getIntPrivilege(HeroPrivilegeNames.skillTimeDec);
		if (decSecond > 0) {
			cooltime -= decSecond;
		}
		cooltime = cooltime * 1000L;
		return cooltime > 0 ? cooltime : 0;
	}

	/**
	 * gm 修改技能
	 * 
	 * @param nSkillOrder
	 * @return
	 */
	public void gmUpgradeSkillLv(Skill skill) {
		updateMoreInfo(skill, null);
		skillItemHolder.updateItem(m_pPlayer, skill);
	}

	public int getMaxSkillCount() {
		return m_pPlayer.getPrivilegeMgr().getIntPrivilege(HeroPrivilegeNames.skillThreshold);
	}

}
