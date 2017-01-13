package com.playerdata.readonly;

import com.playerdata.AttrMgr;
import com.playerdata.EmailMgr;
import com.playerdata.GambleMgr;
import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.SignMgr;
import com.playerdata.TowerMgr;
import com.rw.service.TaoistMagic.ITaoistMgr;
import com.rw.service.magicEquipFetter.MagicEquipFetterMgr;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.fetters.pojo.SynFettersData;
import com.rwbase.dao.user.readonly.TableUserIF;
import com.rwbase.dao.user.readonly.TableUserOtherIF;

/**
 * Player接口，提供给外部只读访问(注释迟点补上)
 * 
 * @author Jamaz
 *
 */
public interface PlayerIF {

	// public ItemBagMgrIF getItemBagMgr();

	// public HeroMgrIF getHeroMgr();
	public HeroMgr getHeroMgr();

	public CopyRecordMgrIF getCopyRecordMgr();

	public CopyDataMgrIF getCopyDataMgr();

	public FriendMgrIF getFriendMgr();

	public SkillMgrIF getSkillMgr();

	public AttrMgr getAttrMgr();

	public EquipMgrIF getEquipMgr();

	public TaskMgrIF getTaskMgr();

	public StoreMgrIF getStoreMgr();

	public int getModelId();

	public int getSex();

	public int getCareer();

	// public int getLogoutTimer();

	public long getReward(eSpecialItemId id);

	public TableUserOtherIF getTableUserOther();

	public TableUserIF getTableUser();

	public SignMgr getSignMgr();

	public EmailMgr getEmailMgr();

	public TowerMgr getTowerMgr();

	public GambleMgr getGambleMgr();

	public FashionMgrIF getFashionMgr();

	public int getLevel();

	public String getTemplateId();

	public Hero getMainRoleHero();

	/**
	 * 获取法宝数据
	 * 
	 * @return
	 */
	public ItemDataIF getMagic();

	// public UserGroupAttributeDataMgr getUserGroupAttributeDataMgr();

	public String getUserName();

	public int getVip();

	public String getHeadImage();

	public String getHeadFrame();

	public String getUserId();

	public boolean isRobot();

	public ITaoistMgr getTaoistMgr();

	public SynFettersData getHeroFettersByModelId(int modelId);

	/**
	 * 获取上次登录时间
	 * 
	 * @return
	 */
	public long getLastLoginTime();

	public MagicEquipFetterMgr getMe_FetterMgr();
}