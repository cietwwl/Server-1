package com.playerdata.hero.core;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.playerdata.EquipMgr;
import com.playerdata.InlayMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.SkillMgr;
import com.playerdata.eRoleType;
import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.fixEquip.exp.FixExpEquipMgr;
import com.playerdata.fixEquip.norm.FixNormEquipMgr;
import com.playerdata.hero.HeroBaseInfo;
import com.playerdata.hero.IHero;
import com.rw.fsutil.dao.annotation.CombineSave;
import com.rw.fsutil.dao.annotation.NonSave;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.common.attribute.AttributeBM;
import com.rwbase.common.attribute.AttributeCalculator;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.RoleQualityCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwbase.dao.role.pojo.RoleQualityCfg;
import com.rwbase.dao.user.LevelCfgDAO;
import com.rwbase.dao.user.pojo.LevelCfg;

/**
 * 
 * 英雄的数据类
 * 历史遗漏问题：
 * 1、RoleBaseInfo同步同客户端的时候，必须按照原来的格式和字段去同步，原来需要的字段如下：
 *  {id, careerType, templateId, modeId, level, starLevel, qualityId, exp}
 *  由于不想维护两个对象的属性，所以引入了{@link com.playerdata.hero.HeroBaseInfo}标注，来标示原来的这些属性 
 * 
 * @author CHEN.P
 *
 */
@Table(name = "hero")
@SynClass
public class FSHero implements IHero {
	
	private static final int _CURRENT_SYNC_ATTR_VERSION = -1;
	
	private static final String _COLUMN_ATTRIBUTE = "attribute";
	
	@Id
	@HeroBaseInfo
	private String id; // 唯一的id
	
	@Column(name="name")
	private String _name; // 英雄的名字
	
	@Column(name="user_id")
	private String _userId; // 英雄所属的玩家的userId
	
	@Column(name="template_id")
	@HeroBaseInfo
	private String templateId; // 数据模板的id
	
	@Column(name="hero_type")
	private int _heroType; // 英雄的类型（主英雄，一般英雄）
	
	@Column(name="exp")
	@HeroBaseInfo
	private int exp; // 英雄的当前经验值
	
	@Column(name="level")
	@HeroBaseInfo
	private int level; // 英雄的等级
	
	@Column(name="create_time")
	private long _createTime; // 英雄的创建时间
	
	@CombineSave(Column=_COLUMN_ATTRIBUTE)
	@HeroBaseInfo
	private int starLevel; // 英雄的星级
	
	@CombineSave(Column=_COLUMN_ATTRIBUTE)
	@HeroBaseInfo
	private String qualityId; // 英雄的品质
	
	@NonSave
	@HeroBaseInfo
	private int careerType; // 英雄的职业（不用保存）
	@NonSave
	@HeroBaseInfo
	private int modeId; // 英雄的模型id
	
	@NonSave
	private FSHeroAttr _attr; // 英雄的属性
	@NonSave
	private AttributeCalculator<AttrData> _calc; // 属性计算器
	@NonSave
	private boolean _firstInited; // 第一次加载是否完成（只有角色第一次登录的时候才会检查这个）
	
	@CombineSave(Column=_COLUMN_ATTRIBUTE)
	private FSHeroAttrDelegator _offlineAttr; // 离线战斗属性
	
	public FSHero() {
	}
	
	public FSHero(Player owner, eRoleType roleTypeP, RoleCfg heroCfg, String uuid) {
		this._userId = owner.getUserId();
		this.id = uuid;
		this._offlineAttr = new FSHeroAttrDelegator();
		this.initFromCfg(heroCfg);
		FSHeroThirdPartyDataMgr.getInstance().getSkillMgr().initSkill(owner, uuid, heroCfg);
		this.firstInit(owner, true);
		owner.getUserTmpGameDataFlag().setSynFightingAll(true);
	}
	
	private Player getOwner() {
		return PlayerMgr.getInstance().find(this._userId);
	}
	
	private void getDataFromCfg(RoleCfg heroCfg) {
		this.modeId = heroCfg.getModelId();
		this.careerType = heroCfg.getCareerType();
		this.qualityId = heroCfg.getQualityId();
	}
	
	private void initFromCfg(RoleCfg heroCfg) {
		this.getDataFromCfg(heroCfg);
		this.templateId = heroCfg.getRoleId();
		this.level = 1;
		this.starLevel = heroCfg.getStarLevel();
	}
	
	private void updateLevelAndExp(Player player, int toLv, int toExp) {
		int preLevel = this.level;
		this.exp = toExp;
		this.level = toLv;
		FSHeroHolder.getInstance().updateBaseInfo(player, this);
		if (preLevel != toLv) {
			this.calculateAttrs(true);
			FSHeroThirdPartyDataMgr.getInstance().fireLevelUpEvent(player, this, preLevel);
		}
	}
	
	/**
	 * 第一次init
	 */
	void firstInit(Player player, boolean fresh) {
		if (!_firstInited) {
			this._firstInited = true;
			FSHeroThirdPartyDataMgr.getInstance().notifyFirstInit(player, this, fresh); // 其他模块都加载完之后，然后计算属性
			this._calc = AttributeBM.getAttributeCalculator(this._userId, this.id);
			this.calculateAttrs(false);
		}
	}
	
	FSHeroAttr getAttr() {
		return _attr;
	}
	
	boolean hasBeenFirstInited() {
		return _firstInited;
	}
	
	public void calculateAttrs(boolean syncToClient) {
		_calc.updateAttribute();
		_attr.updateRoleBaseTotalData(_calc.getBaseResult());
		_attr.updateTotalData(_calc.getResult());
		_offlineAttr.update(_attr.getTotalData());
		if (syncToClient) {
			FSHeroHolder.getInstance().syncAttributes(this, _CURRENT_SYNC_ATTR_VERSION);
		}
	}
	
	public void playerLogin(Player player) {
		this.firstInit(player, false);
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public String getOwnerUserId() {
		return _userId;
	}

	@Override
	public int getLevel() {
		return level;
	}

	@Override
	public int getExp() {
		return exp;
	}
	
	@Override
	public int getCareerType() {
		return careerType;
	}

	@Override
	public int getStarLevel() {
		return starLevel;
	}

	@Override
	public String getQualityId() {
		return qualityId;
	}

	@Override
	public String getUUId() {
		return this.id;
	}

	@Override
	public eRoleType getRoleType() {
		switch (this._heroType) {
		case HERO_TYPE_MAIN:
			return eRoleType.Player;
		default:
			return eRoleType.Hero;
		}
	}

	@Override
	public void sync(int version) {
		Player player = this.getOwner();
		FSHeroHolder.getInstance().updateBaseInfo(player, this);
		FSHeroThirdPartyDataMgr.getInstance().notifySync(player, this, version);
	}

	@Override
	public void save(boolean immediately) {
		FSHeroThirdPartyDataMgr.getInstance().notifySave(this, immediately);
	}

	@Override
	public void save() {
		this.save(false);
	}

	@Override
	public RoleCfg getHeroCfg() {
		return (RoleCfg) RoleCfgDAO.getInstance().getCfgById(this.templateId);
	}

	@Override
	public LevelCfg getLevelCfg() {
		return (LevelCfg) LevelCfgDAO.getInstance().getCfgById(String.valueOf(level));
	}

	@Override
	public int getModelId() {
		return this.modeId;
	}

	@Override
	public String getTemplateId() {
		return this.templateId;
	}

	@Override
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
		FSHeroHolder.getInstance().updateBaseInfo(getOwner(), this);
	}

	@Override
	public int canUpgradeStar() {
		Player player = this.getOwner();
		int result = 0;
		RoleCfg rolecfg = getHeroCfg();
		int soulStoneCount = player.getItemBagMgr().getItemCountByModelId(rolecfg.getSoulStoneId());
		if (soulStoneCount < rolecfg.getRisingNumber()) {
			result = -1;
		} else if (player.getUserGameDataMgr().getCoin() < rolecfg.getUpNeedCoin()) {
			result = -2;
		} else if (!StringUtils.isNotBlank(rolecfg.getNextRoleId())) {
			result = -3;
		}

		return result;
	}

	@Override
	public int getHeroQuality() {
		RoleQualityCfg cfg = (RoleQualityCfg) RoleQualityCfgDAO.getInstance().getCfgById(qualityId);
		if (cfg != null) {
			return cfg.getQuality();
		} else {
			return 0;
		}
	}

	@Override
	public void SetHeroLevel(int pLevel) {
		Player player = this.getOwner();
		int preLevel = this.level;
		this.updateLevelAndExp(player, pLevel, this.exp);
		if(preLevel < pLevel) {
			RoleQualityCfg cfg = (RoleQualityCfg)RoleQualityCfgDAO.getInstance().getCfgById(this.qualityId);
			FSHeroThirdPartyDataMgr.getInstance().activeSkill(player, id, this.level, cfg.getQuality());
		}
	}

	@Override
	public void setHeroExp(long pExp) {
		if(pExp < 0) {
			return;
		}
		if(pExp > this.exp) {
			int subExp = (int)pExp - this.exp;
			this.addHeroExp(subExp);
		} else {
			this.exp = (int)pExp;
		}
	}

	@Override
	public int getFighting() {
		return _attr.getFighting();
	}
	
	@Override
	public void setStarLevel(int star) {
		Player player = this.getOwner();
		int preStarLv = this.starLevel;
		this.starLevel = star;
		FSHeroHolder.getInstance().updateBaseInfo(player, this);
		if (preStarLv != starLevel) {
			FSHeroThirdPartyDataMgr.getInstance().fireStarLevelChangeEvent(player, this, preStarLv);
		}
	}

	@Override
	public void setQualityId(String pQualityId) {
		Player player = this.getOwner();
		String preQualityId = this.qualityId;
		this.qualityId = pQualityId;
		FSHeroHolder.getInstance().updateBaseInfo(player, this);
		FSHeroThirdPartyDataMgr.getInstance().fireQualityChangeEvent(player, this, preQualityId);
	}

	@Override
	public void gmEditHeroLevel(int pLevel) {
		this.updateLevelAndExp(getOwner(), pLevel, this.exp);
	}

	@Override
	public void gmCheckActiveSkill() {
		RoleQualityCfg cfg = (RoleQualityCfg) RoleQualityCfgDAO.getInstance().getCfgById(this.qualityId);
		FSHeroThirdPartyDataMgr.getInstance().activeSkill(this.getOwner(), this.id, level, cfg.getQuality());
	}

	@Override
	public int addHeroExp(long heroExp) {
		Player player = PlayerMgr.getInstance().find(_userId);
		int maxLevel = player.getLevel();
		int currentLevel = this.level;
		int currentExp = this.exp;
		int oldLevel = currentLevel;
		LevelCfgDAO levelCfgDAO = LevelCfgDAO.getInstance();
		for (;;) {
			LevelCfg currentCfg = levelCfgDAO.getByLevel(currentLevel);
			if (currentCfg == null) {
				GameLog.error("hero", "addExp", "获取等级配置失败：" + currentLevel, null);
				break;
			}
			int upgradeExp = currentCfg.getHeroUpgradeExp();
			if (upgradeExp > currentExp) {
				int needExp = upgradeExp - currentExp;
				if (heroExp >= needExp) {
					// 升级并消耗部分经验
					heroExp -= needExp;
					currentExp = upgradeExp;
				} else {
					// 不能升级，把剩余经验用完
					currentExp += (int) heroExp;
					heroExp = 0;
					break;
				}
			}
			// 对等级进行判断
			if (currentLevel >= maxLevel) {
				currentExp = upgradeExp;
				break;
			}
			currentLevel++;
			currentExp = 0;
		}
		this.updateLevelAndExp(player, currentLevel, currentExp);
		return oldLevel == currentLevel ? 0 : 1;
	}

	@Override
	public boolean isMainRole() {
		return this._heroType == HERO_TYPE_MAIN;
	}
	
	@Override
	public InlayMgr getInlayMgr() {
		return FSHeroThirdPartyDataMgr.getInstance().getInlayMgr();
	}
	
	@Override
	public SkillMgr getSkillMgr() {
		return FSHeroThirdPartyDataMgr.getInstance().getSkillMgr();
	}
	
	@Override
	public EquipMgr getEquipMgr() {
		return FSHeroThirdPartyDataMgr.getInstance().getEquipMgr();
	}
	
	@Override
	public FixNormEquipMgr getFixNormEquipMgr() {
		return FSHeroThirdPartyDataMgr.getInstance().getFixNormEquipMgr();
	}
	
	@Override
	public FixExpEquipMgr getFixExpEquipMgr() {
		return FSHeroThirdPartyDataMgr.getInstance().getFixExpEquipMgr();
	}
	
	public static void main(String[] args) throws Exception {
		FSHero hero = new FSHero();
		hero.id = java.util.UUID.randomUUID().toString();
		hero.careerType = 1;
		hero.modeId = 100001;
		hero.templateId = "100001_1";
		hero.level = 1;
		hero.starLevel = 1;
		hero.qualityId = "1000";
		hero.exp = 1;
		java.lang.reflect.Field fSyncFieldNameList = FSHeroHolder.class.getDeclaredField("_namesOfBaseInfoSyncFields");
		fSyncFieldNameList.setAccessible(true);
		@SuppressWarnings("unchecked")
		java.util.List<String> fieldNameList = (java.util.List<String>)fSyncFieldNameList.get(null);
		java.lang.reflect.Method mTransferToClientData = com.playerdata.dataSyn.ClientDataSynMgr.class.getDeclaredMethod("transferToClientData", Object.class, java.util.List.class);
		mTransferToClientData.setAccessible(true);
		com.rwproto.DataSynProtos.SynData.Builder builder = (com.rwproto.DataSynProtos.SynData.Builder)mTransferToClientData.invoke(null, hero, fieldNameList);
		System.out.println(builder.build().toString());
	}
}
