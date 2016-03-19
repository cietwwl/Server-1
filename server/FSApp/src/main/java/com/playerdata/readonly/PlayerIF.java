package com.playerdata.readonly;

import com.playerdata.AttrMgr;
import com.playerdata.EmailMgr;
import com.playerdata.GambleMgr;
import com.playerdata.GuildUserMgr;
import com.playerdata.SignMgr;
import com.playerdata.TowerMgr;
import com.playerdata.group.UserGroupAttributeDataMgr;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.user.readonly.TableUserIF;
import com.rwbase.dao.user.readonly.TableUserOtherIF;

/**
 * Player接口，提供给外部只读访问(注释迟点补上)
 * 
 * @author Jamaz
 *
 */
public interface PlayerIF {

	public ItemBagMgrIF getItemBagMgr();

	public HeroMgrIF getHeroMgr();

	public CopyRecordMgrIF getCopyRecordMgr();

	public CopyDataMgrIF getCopyDataMgr();

	public FriendMgrIF getFriendMgr();

	public SkillMgrIF getSkillMgr();

	public AttrMgr getAttrMgr();

	public EquipMgrIF getEquipMgr();

	public TaskMgrIF getTaskMgr();

	public StoreMgrIF getStoreMgr();

	public int getModelId();

	// public int getLogoutTimer();

	public long getReward(eSpecialItemId id);

	public TableUserOtherIF getTableUserOther();

	public TableUserIF getTableUser();

	public SignMgr getSignMgr();

	public EmailMgr getEmailMgr();

	public TowerMgr getTowerMgr();

	public GambleMgr getGambleMgr();

	public FashionMgrIF getFashionMgr();

	public GuildUserMgr getGuildUserMgr();

	public int getLevel();

	public String getTemplateId();

	public HeroIF getMainRoleHero();

	/**
	 * 获取法宝数据
	 * 
	 * @return
	 */
	public ItemDataIF getMagic();

	public UserGroupAttributeDataMgr getUserGroupAttributeDataMgr();
}