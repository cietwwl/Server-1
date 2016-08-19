package com.playerdata;

import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;

import com.common.IHeroAction;
import com.playerdata.hero.core.FSHeroMgr;
import com.playerdata.readonly.SkillMgrIF;
import com.playerdata.refactor.IDataMgrSingletone;
import com.rw.service.skill.SkillConstant;
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

//public class SkillMgr extends IDataMgr implements SkillMgrIF {
public class SkillMgr  implements SkillMgrIF, IDataMgrSingletone {

	public static final int MAX_SKILL_COUNT = 10;// 最大技能点数量
	public static final int DIE_SKILL_LEVEL = -2;
	
	private static final SkillMgr _INSTANCE = new SkillMgr();
	
	public static final SkillMgr getInstance() {
		return _INSTANCE;
	}
	
	protected SkillMgr() {}

//	private SkillItemHolder skillItemHolder;
	private final SkillItemHolder skillItemHolder = SkillItemHolder.getSkillItemHolder();

	public void init(Hero pRole) {
//		initPlayer(pRole);
//		String ownerId = pRole.getUUId();
//		skillItemHolder = new SkillItemHolder(ownerId);
	}
	
	public void regDataChangeCallback(IHeroAction callback) {
		skillItemHolder.regDataChangeCallback(callback);
	}
	
	@Override
	public boolean load(String heroId) {
		return false;
	}

	@Override
	public boolean save(String heroId) {
		skillItemHolder.flush(heroId, false);
		return true;
	}

	public boolean flush(String heroId) {
		skillItemHolder.flush(heroId, true);
		return true;
	}

	public List<Skill> getSkillList(String heroId) {
		return skillItemHolder.getItemList(heroId);
	}
	
	public boolean canUpgradeSkill(Player player, String heroId) {
		List<Skill> list = getSkillList(heroId);
		for (Skill skill : list) {
			if (canUpgradeSkill(player, heroId, skill, false)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean canUpgradeSkill(Player player, String heroId, Skill skill, boolean showError) {
		Hero hero = FSHeroMgr.getInstance().getHeroById(player, heroId);
		if (!CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.SKILL_OPEN, player.getLevel())) {
			if (showError) {
				player.NotifyCommonMsg("角色等级不足！");
			}
			return false;
		}
		if (skill.getLevel() <= 0) {
			if (showError) {
				player.NotifyCommonMsg("技能未开放！");
			}
			return false;
		}
		if (skill.getLevel() >= hero.getLevel()) {
			if (showError) {
				// m_pPlayer.NotifyCommonMsg("技能等级不能超过人物等级");
			}
			return false;
		}
		SkillCfg skillCfg = (SkillCfg) SkillCfgDAO.getInstance().getCfgById(skill.getSkillId());
		if (StringUtils.isBlank(skillCfg.getNextSillId())) {
			if (showError) {
				player.NotifyCommonMsg("技能已是最高等级！");
			}
			return false;
		}
		int type = hero.getRoleType() == eRoleType.Hero ? 0 : 1;
		SkillFeeCfg skillFeeCfg = SkillFeeCfgDAO.getInstance().getSkillFeeCfg(type, skill.getOrder(), skill.getLevel());
		if (skillFeeCfg == null) {
			if (showError) {
				player.NotifyCommonMsg("skillFeeCfg表错误！");
			}
			return false;
		}
		// reshSkillPoint();
		// 用预期的方式代替真正回复技能点
		if (player.getUserGameDataMgr().getSkillPointCount() <= 0 && !player.getSkillMgr().expectSkillPointRecover(player)) {
			if (showError) {
				player.NotifyCommonMsg("技能点冷却中！");
			}
			return false;
		}
		if (skillFeeCfg.getCoin() > player.getUserGameDataMgr().getCoin()) {
			if (showError) {
				player.NotifyCommonMsg("金钱不足！");
			}
			return false;
		}
		return true;
	}

//	public boolean updateSkill(String skillId, int addLevel) {
//		Skill skill = null;
//		List<Skill> skillList = skillItemHolder.getItemList();
//		for (Skill current : skillList) {
//			if (current.getSkillId().equals(skillId)) {
//				skill = current;
//				break;
//			}
//		}
//		if (skill == null) {
//			return false;
//		}
//		int level = skill.getLevel() + addLevel;
//		StringTokenizer token = new StringTokenizer(skillId, "_");
//		String newSkillId = token.nextToken() + "_" + level;
//		SkillCfg newSkillCfg = (SkillCfg) SkillCfgDAO.getInstance().getCfgById(skill.getSkillId());
//		if (newSkillCfg == null) {
//			return false;
//		}
//		skill.setLevel(level);
//		skill.setSkillId(newSkillId);
//		updateMoreInfo(skill, null);
//		skillItemHolder.updateItem(m_pPlayer, skill);
//		return true;
//	}
	
	public boolean updateSkill(Player player, String heroId, String skillId, int addLevel) {
		Skill skill = null;
		List<Skill> skillList = skillItemHolder.getItemList(heroId);
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
//		SkillCfg newSkillCfg = (SkillCfg) SkillCfgDAO.getInstance().getCfgById(skill.getSkillId());
		SkillCfg newSkillCfg = (SkillCfg) SkillCfgDAO.getInstance().getCfgById(newSkillId); // PERRY 2016-08-19，上面的本意应该是要获取下个技能
		if (newSkillCfg == null) {
			return false;
		}
		skill.setLevel(level);
		skill.setSkillId(newSkillId);
		updateMoreInfo(player, heroId, skill, null);
		skillItemHolder.updateItem(player, heroId, skill);
		return true;
	}

	/**
	 * vip升级增加技能点
	 * 
	 * @param type
	 * @param temp
	 */
	public void updateSkillPointTime(Player player, EPrivilegeDef type, int temp) {
		// for (int i = 1; i <= temp; i++) {
		// m_pPlayer.addSkillPointTime(getCoolTime());
		// }
	}

	public SkillResponse.Builder getSkillInfo(Player player) {
		SkillResponse.Builder response = SkillResponse.newBuilder();
		UserGameDataMgr userGameDataMgr = player.getUserGameDataMgr();
		reshSkillPoint(player);
		// int max = PrivilegeCfgDAO.getInstance().getDef(m_pPlayer.getVip(), EPrivilegeDef.SKILL_POINT_COUNT);
		int max = getMaxSkillCount(player);
		int count = userGameDataMgr.getSkillPointCount();
		response.setRemainSkillPoints(count);
		int rs = 0;
		if (count < max) {
			long currentTimeMillis = System.currentTimeMillis();
			long lastRecoverTimeMillis = userGameDataMgr.getLastRecoverSkillPointTime();
			long remainSeconds = TimeUnit.MILLISECONDS.toSeconds((getCoolTime(player) - (currentTimeMillis - lastRecoverTimeMillis)));
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
	public void activeSkill(Player player, String heroId, int level, int quality) {
		// int maxOrder = -1;
		// modify by Jamaz 2015-11-23 抽取判断能否激活技能的方法
		for (Skill skill : skillItemHolder.getItemList(heroId)) {
			if (skill.getLevel() <= 0 && isSkillCanActive(skill, level, quality)) {
				skill.setLevel(1);
				updateMoreInfo(player, heroId, skill, null);
				// if (maxOrder < skill.getOrder()) {
				// maxOrder = skill.getOrder();
				// }
			}
		}
		skillItemHolder.synAllData(player, heroId, -1);
		// if (maxOrder > 0) {
		// openSkillOnClient(maxOrder);
		// }
	}

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
	 * @param heroId
	 * @param cfg
	 */
	public void changeSkill(Player player, String heroId, RoleCfg cfg) {
		// List<Skill> skillList = skillItemHolder.getItemList();
		// if (skillList.isEmpty()) {
		// initSkill(cfg);
		// return;
		// }
		List<Skill> cfgList = RoleCfgDAO.getInstance().getSkill(cfg.getRoleId());
		for (int i = cfgList.size() - 1; i >= 0; --i) {
			Skill newSkill = cfgList.get(i);
			if (newSkill == null) {
				continue;
			}

			Skill oldSkill = getSkill(heroId, newSkill.getOrder());
			if (oldSkill == null) {
				continue;
			}

			String oldSkillId = oldSkill.getSkillId();
			String newSkillId = newSkill.getSkillId().split("_")[0] + "_" + oldSkillId.split("_")[1];
			oldSkill.setSkillId(newSkillId);
		}

		// 检查所有的技能
		updateMoreInfo(player, heroId, null, skillItemHolder.getItemList(heroId));

		// for (int i = 0; i < skillList.size(); i++) {
		// Skill pSkill = skillList.get(i);
		// String oldId = cfgList.get(pSkill.getOrder()).getSkillId();
		// String newId = oldId.split("_")[0] + "_" + pSkill.getSkillId().split("_")[1];
		// pSkill.setSkillId(newId);
		// updateMoreInfo(pSkill, null);
		// skillItemHolder.updateItem(m_pPlayer, pSkill);
		// System.out.println("skillchangeformplayerCareerChange:skillorder=" + pSkill.getOrder() + "oldSkillId=" + oldId + ",newid=" + newId);
		// }
	}
	
	/**
	 * get 初始化技能
	 * 
	 * @param pRole
	 */
	public void initSkill(Player player, Hero m_pOwner, RoleCfg rolecfg) {
		List<Skill> battleSkillList = SkillHelper.initSkill(rolecfg, m_pOwner.getQualityId(), m_pOwner.getLevel());
		skillItemHolder.addItem(player, m_pOwner.getId(), battleSkillList);
	}

//	/**
//	 * 更新技能其它信息
//	 * 
//	 * @param pSkill
//	 */
//	private void updateMoreInfo(Skill pSkill, List<Skill> skillList) {
//		if (pSkill != null) {
//			if (pSkill.getLevel() <= 0) {
//				return;
//			}
//
//			String skillId = pSkill.getSkillId();
//			SkillCfg pSkillCfg = (SkillCfg) SkillCfgDAO.getInstance().getCfgById(skillId);
//			if (pSkillCfg == null) {
//				if (pSkill.getLevel() != DIE_SKILL_LEVEL) {
//					if (m_pPlayer != null) {
//						m_pPlayer.NotifyCommonMsg("配置表错误：没有skillID为" + skillId + "的技能");
//					}
//				}
//				return;
//			}
//		}
//
//		List<Skill> itemList = skillItemHolder.getItemList();
//
//		SkillHelper.checkAllSkill(itemList);// 检查所有的技能
//
//		// StringBuilder sb = new StringBuilder();
//		for (int i = 0, size = itemList.size(); i < size; i++) {
//			Skill skill = itemList.get(i);
//			if (skill == null) {
//				continue;
//			}
//
//			skillItemHolder.updateItem(m_pPlayer, skill);
//
//			// sb.append(String.format("技能Order[%s],技能Id[%s],等级[%s],伤害[%s],额外[%s],系数[%s],buff{%s},selfBuff{%s}\n", skill.getOrder(),
//			// skill.getSkillId(), skill.getLevel(), skill.getSkillDamage(),
//			// skill.getExtraDamage(), skill.getSkillRate(), skill.getBuffId().toString(), skill.getSelfBuffId().toString()));
//		}
//
//		// GameLog.info("升级技能模块", "升级后所有效果", sb.toString());
//	}
	
	/**
	 * 更新技能其它信息
	 * 
	 * @param pSkill
	 */
	private void updateMoreInfo(Player player, String heroId, Skill pSkill, List<Skill> skillList) {
		if (pSkill != null) {
			if (pSkill.getLevel() <= 0) {
				return;
			}

			String skillId = pSkill.getSkillId();
			SkillCfg pSkillCfg = (SkillCfg) SkillCfgDAO.getInstance().getCfgById(skillId);
			if (pSkillCfg == null) {
				if (pSkill.getLevel() != DIE_SKILL_LEVEL) {
					if (player != null) {
						player.NotifyCommonMsg("配置表错误：没有skillID为" + skillId + "的技能");
					}
				}
				return;
			}
		}

		List<Skill> itemList = skillItemHolder.getItemList(heroId);

		SkillHelper.checkAllSkill(itemList);// 检查所有的技能

		// StringBuilder sb = new StringBuilder();
		for (int i = 0, size = itemList.size(); i < size; i++) {
			Skill skill = itemList.get(i);
			if (skill == null) {
				continue;
			}

			skillItemHolder.updateItem(player, heroId, skill);

			// sb.append(String.format("技能Order[%s],技能Id[%s],等级[%s],伤害[%s],额外[%s],系数[%s],buff{%s},selfBuff{%s}\n", skill.getOrder(),
			// skill.getSkillId(), skill.getLevel(), skill.getSkillDamage(),
			// skill.getExtraDamage(), skill.getSkillRate(), skill.getBuffId().toString(), skill.getSelfBuffId().toString()));
		}

		// GameLog.info("升级技能模块", "升级后所有效果", sb.toString());
	}

	/**
	 * 发送全部技能
	 */
	public void syncAllSkill(Player player, String heroId, int version) {
		reshSkillPoint(player);
		skillItemHolder.synAllData(player, heroId, version);
	}
	
	/**
	 * 
	 * 获取英雄技能列表的proto表示
	 * 
	 * @param heroId
	 * @return
	 */
	public List<TagSkillData> getSkillProtoList(String heroId) {
		List<Skill> skillItemList = skillItemHolder.getItemList(heroId);
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
	
	/**
	 * 
	 * 玩家购买技能点
	 * 
	 * @param player
	 */
	public void buySkillPoint(Player player) {
		int times = player.getUserGameDataMgr().getBuySkillTimes() + 1;
		CfgBuySkill cfgBuySkill = CfgBuySkillDAO.getInstance().getCfgBuySkill(times);
		if (player.getUserGameDataMgr().getSkillPointCount() == cfgBuySkill.getSkillpoint()) {
			return;
		}
		int skillTotalCount = player.getUserGameDataMgr().getSkillPointCount() + cfgBuySkill.getSkillpoint();
		int maxSkillCount = getMaxSkillCount(player);
		if (skillTotalCount > maxSkillCount) {
			skillTotalCount = maxSkillCount;
		}
		player.getUserGameDataMgr().addGold(-cfgBuySkill.getNeedPurse());
		player.getUserGameDataMgr().setBuySkillTimes(player.getUserGameDataMgr().getBuySkillTimes() + 1);
		player.getUserGameDataMgr().setSkillPointCount(skillTotalCount);

		return;
	}
	
	/**
	 * 
	 * 获取上次增加技能点到当前的时间之间所能增加的技能点数量
	 * 
	 * @param player
	 * @return
	 */
	public int getAddSkillPoint(Player player) {
		long lastCountTime = player.getUserGameDataMgr().getLastRecoverSkillPointTime();
		long currentTimeMillis = System.currentTimeMillis();
		long timeSpan = currentTimeMillis - lastCountTime;
		long recoverSpanTime = getCoolTime(player);
		// 大于1分钟
		if (timeSpan > recoverSpanTime) {
			int recoverPoint = (int) (timeSpan / recoverSpanTime);
			long timeLeft = timeSpan % recoverSpanTime;
			lastCountTime = currentTimeMillis - timeLeft;
			player.getUserGameDataMgr().setLastRecoverSkillPointTime(lastCountTime);
			return recoverPoint;
		}
		return 0;
	}
	
	/**
	 * 预估能够时间恢复技能点
	 * 
	 * @return
	 */
	public boolean expectSkillPointRecover(Player player) {
		long lastCountTime = player.getUserGameDataMgr().getLastRecoverSkillPointTime();
		long currentTimeMillis = System.currentTimeMillis();
		long timeSpan = currentTimeMillis - lastCountTime;
		long recoverSpanTime = getCoolTime(player);
		return timeSpan > recoverSpanTime;
	}
	
	/**
	 * 重算技能点（技能点数量，技能点时间）
	 */
	public void reshSkillPoint(Player player) {
		int count = player.getUserGameDataMgr().getSkillPointCount();
		int max = getMaxSkillCount(player);
		if (count >= max) {
			return;
		}
		int addSkillPoint = getAddSkillPoint(player);
		if (addSkillPoint <= 0) {
			return;
		}
		int total = addSkillPoint + count;
		if (total >= max) {
			total = max;
			player.getUserGameDataMgr().setLastRecoverSkillPointTime(0);
		}
		player.getUserGameDataMgr().setSkillPointCount(total);
	}
	
	/**
	 * 获取技能表
	 * 
	 * @return
	 */
	public TableSkill getTableSkill(Player player, String heroId) {
		List<Skill> skillList = skillItemHolder.getItemList(heroId);

		TableSkill skillTable = new TableSkill();
		skillTable.setSkillList(skillList);
		skillTable.setUserId(heroId);
		return skillTable;
	}
	
	/**
	 * 根据order获取技能
	 * 
	 * @param order
	 * @return
	 */
	public Skill getSkill(String heroId, int order) {
		return skillItemHolder.getByOrder(heroId, order);
	}
	
	/**
	 * 
	 * 获取技能点冷却的剩余时间
	 * 
	 * @param player
	 * @return
	 */
	public long getCoolTime(Player player) {
		long cooltime = PublicDataCfgDAO.getInstance().getPublicDataValueById(PublicData.SKILL_POINT_COOL_TIME);
		int decSecond = player.getPrivilegeMgr().getIntPrivilege(HeroPrivilegeNames.skillTimeDec);
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
	public void gmUpgradeSkillLv(Player player, String heroId, Skill skill) {
		updateMoreInfo(player, heroId, skill, null);
		skillItemHolder.updateItem(player, heroId, skill);
	}
	
	/**
	 * 
	 * 获取玩家能拥有的最大技能数量
	 * 
	 * @param player
	 * @return
	 */
	public int getMaxSkillCount(Player player) {
		return player.getPrivilegeMgr().getIntPrivilege(HeroPrivilegeNames.skillThreshold);
	}
	
	/**
	 * 检查英雄的技能数据，主要是想做数据普攻的数据兼容，以及能从配置表中读取的数据，就不存储到数据库
	 * 
	 * @param heroTemplateId
	 */
	public void checkSkill(Player player, String heroId, String heroTemplateId) {
		// 检查是否增加了普攻数据
		String attackId = RoleCfgDAO.getInstance().getAttackId(heroTemplateId);
		if (StringUtils.isNotBlank(attackId)) {// 有没有普攻
			List<Skill> skillList = getSkillList(heroId);
			boolean hasNormalSkill = false;
			for (int i = skillList.size() - 1; i >= 0; --i) {
				Skill skill = skillList.get(i);
				if (skill == null) {
					continue;
				}

				if (skill.getSkillId().equals(attackId)) {
					hasNormalSkill = true;
					break;
				}
			}

			if (!hasNormalSkill) {
				Skill normalSkill = new Skill();
				normalSkill.setSkillId(attackId);
				normalSkill.setOrder(SkillConstant.NORMAL_SKILL_ORDER);
				normalSkill.setLevel(1);
				skillItemHolder.addItem(player, heroId, normalSkill, false);
			}
		}

		// 初始化各个技能之间的影响的buffer等
		updateMoreInfo(player, heroId, null, skillItemHolder.getItemList(heroId));
	}
}