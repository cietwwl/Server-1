package com.playerdata.hero.core;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.playerdata.AttrMgr;
import com.playerdata.EquipMgr;
import com.playerdata.FightingCalculator;
import com.playerdata.Hero;
import com.playerdata.InlayMgr;
import com.playerdata.Player;
import com.playerdata.SkillMgr;
import com.playerdata.eRoleType;
import com.playerdata.dataSyn.annotation.IgnoreSynField;
import com.playerdata.dataSyn.annotation.SynClass;
import com.playerdata.fixEquip.exp.FixExpEquipMgr;
import com.playerdata.fixEquip.norm.FixNormEquipMgr;
import com.playerdata.hero.HeroBaseInfo;
import com.rw.fsutil.dao.annotation.CombineSave;
import com.rw.fsutil.dao.annotation.IgnoreUpdate;
import com.rw.fsutil.dao.annotation.NonSave;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.common.attrdata.RoleAttrData;
import com.rwbase.common.attribute.AttributeBM;
import com.rwbase.common.attribute.AttributeCalculator;
import com.rwbase.dao.role.pojo.RoleCfg;

/**
 * 
 * <pre>
 * 英雄的数据类
 * 历史遗漏问题：
 * 1、RoleBaseInfo同步同客户端的时候，必须按照原来的格式和字段去同步，原来需要的字段如下：
 *  {id, careerType, templateId, modeId, level, starLevel, qualityId, exp}
 *  由于不想维护两个对象的属性，所以引入了{@link com.playerdata.hero.HeroBaseInfo}标注，来标示原来的这些属性
 *  2、原来的一些逻辑方法已经迁移到以下两个类：
 *  {@link FSHeroMgr#getInstance()}
 *  {@link FSHeroBaseInfoMgr#getInstance()}
 * 
 * 注意事项：
 * 1、内部不能直接使用_userId字段，因为如果是主角类型的英雄，_userId会为""，应该使用{@link #getOwnerUserId()}来获取userId
 * </pre>
 * @author CHEN.P
 *
 */
@Table(name = "hero")
@SynClass
public class FSHero implements Hero, AttrMgr {
	
	@Transient
	@IgnoreSynField
	public static final int CURRENT_SYNC_ATTR_VERSION = -1;
	
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
	
	@IgnoreUpdate
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
	private long exp; // 英雄的当前经验值
	
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
		this.initAttrCalc();
	}
	
	private void initAttrCalc() {
		if (this._calc == null) {
			this._calc = AttributeBM.getAttributeCalculator(this.getOwnerUserId(), this.id);
		}
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
	
	private void calculateAttrsInternal(boolean syncToClient) {
		int preFighting = attr.getFighting();
		initAttrCalc();
		_calc.updateAttribute();
		attr.updateRoleBaseTotalData(_calc.getBaseResult());
		attr.updateTotalData(_calc.getResult());
		attr.updateFighting(FightingCalculator.calFighting(this, attr.getTotalData()));
		if (syncToClient) {
			FSHeroMgr.getInstance().syncFighting(this, preFighting);
		}
	}
	
	private void checkIfAttrInit() {
		if (this.attr.getFighting() == 0) {
			/* 
			 * 有可能是机器人和旧数据，现在计算属性是在syn的时候计算。
			 * 机器人不会触发syn，所以有可能属性为0
			 * 旧数据没有保存离线的attr，所以属性也有可能为0
			 */
			this.firstInit();
		}
	}
	
	/**
	 * 第一次init
	 */
	void firstInit() {
		if (_firstInited.compareAndSet(false, true)) {
			this.attr.setHeroId(id); // 从db读出来的时候，attr的id有可能未初始化
			FSHeroThirdPartyDataMgr.getInstance().notifyFirstInit(this); // 其他模块都加载完之后，然后计算属性
			this.calculateAttrsInternal(false);
		}
	}
	
	boolean hasBeenFirstInited() {
		return _firstInited.get();
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
	public int getCareerType() {
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
	public void save(boolean immediately) {
		FSHeroThirdPartyDataMgr.getInstance().notifySave(this, immediately);
	}

	@Override
	public void save() {
		this.save(false);
	}
	

	@Override
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	@Override
	public int getFighting() {
		if(attr.getFighting() == 0) {
			// 兼容旧数据
			this.firstInit();
		}
		return attr.getFighting();
	}
	
	@Override
	public void setStarLevel(int star) {
		this.starLevel = star;
	}

	@Override
	public void setQualityId(String pQualityId) {
		this.qualityId = pQualityId;
	}
	
	@Override
	public void setCareerType(int career) {
		this.careerType = career;
	}

	@Override
	public void setModelId(int pModelId) {
		this.modeId = pModelId;
	}

	@Override
	public void setLevel(int level) {
		this.level = level;
	}

	@Override
	public void setExp(long exp) {
		if(exp < 0) {
			return;
		}
		this.exp = exp;
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
		this.calculateAttrsInternal(true);
		return this.attr;
	}
	
	
}
