package com.playerdata.hero.core;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import com.log.GameLog;
import com.playerdata.AttrMgr;
import com.playerdata.EquipMgr;
import com.playerdata.FightingCalculator;
import com.playerdata.Hero;
import com.playerdata.InlayMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.RoleBaseInfoMgr;
import com.playerdata.SkillMgr;
import com.playerdata.eRoleType;
import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.fixEquip.exp.FixExpEquipMgr;
import com.playerdata.fixEquip.norm.FixNormEquipMgr;
import com.playerdata.hero.HeroBaseInfo;
import com.rw.fsutil.dao.annotation.CombineSave;
import com.rw.fsutil.dao.annotation.NonSave;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.common.attrdata.RoleAttrData;
import com.rwbase.common.attribute.AttributeBM;
import com.rwbase.common.attribute.AttributeCalculator;
import com.rwbase.dao.hero.pojo.RoleBaseInfo;
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
 * 注意事项：
 * 1、内部不能直接使用_userId字段，因为如果是主角类型的英雄，_userId会为""，应该使用{@link #getOwnerUserId()}来获取userId
 * 
 * @author CHEN.P
 *
 */
@Table(name = "hero")
@SynClass
public class FSHero implements Hero, RoleBaseInfoMgr, AttrMgr, RoleBaseInfo {
	
	@Transient
	@IgnoreSynField
	private static final int _CURRENT_SYNC_ATTR_VERSION = -1;
	
	@Transient
	@IgnoreSynField
	private static final String _COLUMN_ATTRIBUTE = "attribute";
	
	@Transient
	@IgnoreSynField
	private static final String _EMPTY_USER_ID = "";
	
	@Id
	@HeroBaseInfo
	private String id; // 唯一的id
	
	@Column(name="name")
	@IgnoreSynField
	private String _name; // 英雄的名字
	
	@Column(name="user_id")
	@IgnoreSynField
	private String _userId; // 英雄所属的玩家的userId
	
	@Column(name="template_id")
	@HeroBaseInfo
	@IgnoreSynField
	private String templateId; // 数据模板的id
	
	@Column(name="hero_type")
	@IgnoreSynField
	private int _heroType; // 英雄的类型（主英雄，一般英雄）
	
	@Column(name="exp")
	@HeroBaseInfo
	private int exp; // 英雄的当前经验值
	
	@Column(name="level")
	@HeroBaseInfo
	private int level; // 英雄的等级
	
	@Column(name="create_time")
	@IgnoreSynField
	private long _createTime; // 英雄的创建时间
	
	@CombineSave(Column=_COLUMN_ATTRIBUTE)
	@HeroBaseInfo
	private int starLevel; // 英雄的星级
	
	@CombineSave(Column=_COLUMN_ATTRIBUTE)
	@HeroBaseInfo
	private String qualityId; // 英雄的品质
	
	@CombineSave(Column=_COLUMN_ATTRIBUTE)
	@HeroBaseInfo
	private int careerType; // 英雄的职业
	
	@CombineSave(Column=_COLUMN_ATTRIBUTE)
	@HeroBaseInfo
	private int modeId; // 英雄的模型id
	
	@CombineSave(Column=_COLUMN_ATTRIBUTE)
	@IgnoreSynField
	private FSHeroAttr attr = new FSHeroAttr(); // 英雄的属性，總屬性會保存，角色上線，syn的時候才會重新計算屬性
	
	@NonSave
	@Transient
	@IgnoreSynField
	private AttributeCalculator<AttrData> _calc; // 属性计算器
	
	@NonSave
	@Transient
	@IgnoreSynField
	private AtomicBoolean _firstInited = new AtomicBoolean(); // 第一次加载是否完成（只有角色第一次登录的时候才会检查这个）
	
//	@CombineSave(Column=_COLUMN_ATTRIBUTE)
//	private FSHeroAttrDelegator _offlineAttr; // 离线战斗属性
	
	public FSHero() {
	}
	
	public FSHero(Player owner, eRoleType roleTypeP, RoleCfg heroCfg, String uuid) {
		this.id = uuid;
		boolean setName = true;
		if (roleTypeP == eRoleType.Player) {
			this._heroType = HERO_TYPE_MAIN;
			this._userId = _EMPTY_USER_ID;
			this._name = owner.getUserName();
			setName = false;
		} else {
			this._heroType = HERO_TYPE_COMMON;
			this._userId = owner.getUserId();
		}
		this._createTime = System.currentTimeMillis();
		this.attr.setHeroId(this.id);
		this.initFromCfg(heroCfg, setName);
	}
	
	private void getDataFromCfg(RoleCfg heroCfg) {
		this.modeId = heroCfg.getModelId();
		this.careerType = heroCfg.getCareerType();
		this.qualityId = heroCfg.getQualityId();
	}
	
	private void initFromCfg(RoleCfg heroCfg, boolean setName) {
		this.getDataFromCfg(heroCfg);
		this.templateId = heroCfg.getRoleId();
		this.level = 1;
		this.starLevel = heroCfg.getStarLevel();
		if (setName) {
			this._name = heroCfg.getName();
		}
	}
	
	private void updateLevelAndExp(Player owner, int toLv, int toExp) {
		int preLevel = this.level;
		this.exp = toExp;
		this.level = toLv;
		FSHeroHolder.getInstance().synBaseInfo(owner, this);
		if (preLevel != toLv) {
			this.calculateAttrsInternal(owner, true);
		}
		FSHeroThirdPartyDataMgr.getInstance().fireHeroLevelChangeEvent(owner, this, preLevel);
	}
	
	private void calculateAttrsInternal(Player owner, boolean syncToClient) {
		int preFighting = attr.getFighting();
		_calc.updateAttribute();
		attr.updateRoleBaseTotalData(_calc.getBaseResult());
		attr.updateTotalData(_calc.getResult());
		attr.updateFighting(FightingCalculator.calFighting(this, attr.getTotalData()));
		if (syncToClient) {
			FSHeroHolder.getInstance().syncAttributes(this, _CURRENT_SYNC_ATTR_VERSION);
			if (preFighting != attr.getFighting()) {
				// 保持那边的战斗力一致
				owner.getUserGameDataMgr().notifySingleFightingChange(attr.getFighting(), preFighting);
			}
		}
	}
	
	private void checkIfAttrInit() {
		if (this.attr.getFighting() == 0) {
			/* 
			 * 有可能是机器人和旧数据，现在计算属性是在syn的时候计算。
			 * 机器人不会触发syn，所以有可能属性为0
			 * 旧数据没有保存离线的attr，所以属性也有可能为0
			 */
			this.firstInit(getPlayer());
		}
	}
	
	/**
	 * 第一次init
	 */
	void firstInit(Player player) {
		if (_firstInited.compareAndSet(false, true)) {
			this.attr.setHeroId(id); // 从db读出来的时候，attr的id有可能未初始化
			FSHeroThirdPartyDataMgr.getInstance().notifyFirstInit(player, this); // 其他模块都加载完之后，然后计算属性
			this._calc = AttributeBM.getAttributeCalculator(player.getUserId(), this.id);
			this.calculateAttrsInternal(player, false);
		}
	}
	
	boolean hasBeenFirstInited() {
		return _firstInited.get();
	}
	
	@Override
	public Player getPlayer() {
		return PlayerMgr.getInstance().find(this.getOwnerUserId());
	}

	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public String getTemplateId() {
		return this.templateId;
	}
	
	@Override
	public int getLevel() {
		return level;
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
	public long getExp() {
		return exp;
	}
	
	@Override
	public int getModeId() {
		return modeId;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public String getOwnerUserId() {
		if(this._heroType == HERO_TYPE_COMMON) {
			return _userId;
		} else {
			return id;
		}
	}
	
	@Override
	public int getCareer() {
		return careerType;
	}

	@Override
	public String getUUId() {
		return this.id;
	}

	@Override
	public eRoleType getRoleType() {
		return this._heroType == HERO_TYPE_MAIN ? eRoleType.Player : eRoleType.Hero;
	}

	@Override
	public void syn(int version) {
		Player player = this.getPlayer();
		this.firstInit(player);
		FSHeroHolder.getInstance().synBaseInfo(player, this);
		FSHeroThirdPartyDataMgr.getInstance().notifySync(player, this, version);
		FSHeroHolder.getInstance().syncAttributes(this, version);
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
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
		FSHeroHolder.getInstance().synBaseInfo(getPlayer(), this);
	}

	@Override
	public int canUpgradeStar() {
		Player player = this.getPlayer();
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
	public int GetHeroQuality() {
		RoleQualityCfg cfg = (RoleQualityCfg) RoleQualityCfgDAO.getInstance().getCfgById(qualityId);
		if (cfg != null) {
			return cfg.getQuality();
		} else {
			return 0;
		}
	}

	@Override
	public void SetHeroLevel(int pLevel) {
		Player player = this.getPlayer();
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
//		if(pExp > this.exp) {
//			int subExp = (int)pExp - this.exp;
//			this.addHeroExp(subExp);
//		} else {
//			this.exp = (int)pExp;
//		}
		// 2016-08-05 by Perry 還是按照原來的邏輯 BEGIN >>>>>>
		this.exp = (int)pExp;
		FSHeroHolder.getInstance().synBaseInfo(getPlayer(), this);
	}

	@Override
	public int getFighting() {
		if(attr.getFighting() == 0) {
			// 兼容旧数据
			this.firstInit(getPlayer());
		}
		return attr.getFighting();
	}
	
	@Override
	public void setStarLevel(int star) {
		Player player = this.getPlayer();
		int preStarLv = this.starLevel;
		this.starLevel = star;
		FSHeroHolder.getInstance().synBaseInfo(player, this);
		FSHeroThirdPartyDataMgr.getInstance().fireStarLevelChangeEvent(player, this, preStarLv);
	}

	@Override
	public void setQualityId(String pQualityId) {
		Player player = this.getPlayer();
		String preQualityId = this.qualityId;
		this.qualityId = pQualityId;
		FSHeroHolder.getInstance().synBaseInfo(player, this);
		FSHeroThirdPartyDataMgr.getInstance().fireQualityChangeEvent(player, this, preQualityId);
	}

	@Override
	public void gmEditHeroLevel(int pLevel) {
		this.updateLevelAndExp(getPlayer(), pLevel, this.exp);
	}

	@Override
	public void gmCheckActiveSkill() {
		RoleQualityCfg cfg = (RoleQualityCfg) RoleQualityCfgDAO.getInstance().getCfgById(this.qualityId);
		FSHeroThirdPartyDataMgr.getInstance().activeSkill(this.getPlayer(), this.id, level, cfg.getQuality());
	}

	@Override
	public int addHeroExp(long heroExp) {
		Player player = PlayerMgr.getInstance().find(this.getOwnerUserId());
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
	public AttrMgr getAttrMgr() {
		return this;
	}
	
	@Override
	public RoleBaseInfoMgr getRoleBaseInfoMgr() {
		return this;
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

	@Override
	public void setCareerType(int career) {
		this.careerType = career;
		FSHeroHolder.getInstance().synBaseInfo(getPlayer(), this);
	}

	@Override
	public void setModelId(int pModelId) {
		this.modeId = pModelId;
		FSHeroHolder.getInstance().synBaseInfo(getPlayer(), this);
	}

	@Override
	public void setLevel(int level) {
		this.SetHeroLevel(level);
	}

	@Override
	public void setExp(long exp) {
		this.setHeroExp(exp);
	}

	@Override
	public void setLevelAndExp(int level, int exp) {
		this.updateLevelAndExp(getPlayer(), level, exp);
	}
	
	@Override
	public RoleBaseInfo getBaseInfo() {
		return this;
	}

	@Override
	public RoleAttrData getRoleAttrData() {
		this.checkIfAttrInit();
		return this.attr;
	}

	@Override
	public AttrData getTotalAttrData() {
		this.checkIfAttrInit();
		return this.attr.getTotalData();
	}

	@Override
	public RoleAttrData reCal() {
		this.calculateAttrsInternal(this.getPlayer(), true);
		return this.attr;
	}

//	@Override
//	public RoleBaseInfoIF getHeroData() {
//		return null;
//	}

//	@Override
//	public int GetHeroLevel() {
//		return 0;
//	}
	
//	public static void main(String[] args) throws Exception {
//		FSHero hero = new FSHero();
//		hero.id = java.util.UUID.randomUUID().toString();
//		hero.careerType = 1;
//		hero.modeId = 100001;
//		hero.templateId = "100001_1";
//		hero.level = 1;
//		hero.starLevel = 1;
//		hero.qualityId = "1000";
//		hero.exp = 1;
//		java.lang.reflect.Field fSyncFieldNameList = FSHeroHolder.class.getDeclaredField("_namesOfBaseInfoSyncFields");
//		fSyncFieldNameList.setAccessible(true);
//		@SuppressWarnings("unchecked")
//		java.util.List<String> fieldNameList = (java.util.List<String>)fSyncFieldNameList.get(null);
//		java.lang.reflect.Method mTransferToClientData = com.playerdata.dataSyn.ClientDataSynMgr.class.getDeclaredMethod("transferToClientData", Object.class, java.util.List.class);
//		mTransferToClientData.setAccessible(true);
//		com.rwproto.DataSynProtos.SynData.Builder builder = (com.rwproto.DataSynProtos.SynData.Builder)mTransferToClientData.invoke(null, hero, fieldNameList);
//		System.out.println(builder.build().toString());
//	}
}
