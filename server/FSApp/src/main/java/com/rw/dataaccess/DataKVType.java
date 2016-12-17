package com.rw.dataaccess;

import com.bm.worldBoss.data.WBUserDataCreator;
import com.bm.worldBoss.data.WBUserDataDao;
import com.common.HPCUtil;
import com.playerdata.charge.dao.ChargeInfoCreator;
import com.playerdata.charge.dao.ChargeInfoDao;
import com.playerdata.dailyreset.DailyResetReccordCreator;
import com.playerdata.dailyreset.DailyResetReccordDao;
import com.playerdata.groupFightOnline.data.UserGFightOnlineDAO;
import com.playerdata.mgcsecret.data.UserMagicSecretDao;
import com.playerdata.teambattle.data.UserTeamBattleDAO;
import com.rw.dataaccess.processor.BattleTowerCreator;
import com.rw.dataaccess.processor.CopyCreator;
import com.rw.dataaccess.processor.DailyActivityCreator;
import com.rw.dataaccess.processor.DropRecordCreator;
import com.rw.dataaccess.processor.EmailCreator;
import com.rw.dataaccess.processor.FSUserFightingGrowthDataCreator;
import com.rw.dataaccess.processor.FriendCreator;
import com.rw.dataaccess.processor.GuideProgressCreator;
import com.rw.dataaccess.processor.MagicSecretCreator;
import com.rw.dataaccess.processor.PlotProgressCreator;
import com.rw.dataaccess.processor.SettingProcessor;
import com.rw.dataaccess.processor.SevenDayGifCreator;
import com.rw.dataaccess.processor.SignCreator;
import com.rw.dataaccess.processor.StoreCreator;
import com.rw.dataaccess.processor.UnendingWarCreator;
import com.rw.dataaccess.processor.UserGFightDataCreator;
import com.rw.dataaccess.processor.UserGameDataProcessor;
import com.rw.dataaccess.processor.UserTeamBattleDataCreator;
import com.rw.dataaccess.processor.VipCreator;
import com.rw.fsutil.cacheDao.DataKVDao;
import com.rw.fsutil.cacheDao.loader.DataCreator;
import com.rw.service.TaoistMagic.datamodel.TaoistMagicDataCreator;
import com.rw.service.TaoistMagic.datamodel.TaoistMagicHolder;
import com.rw.service.gamble.datamodel.GambleCreator;
import com.rw.service.gamble.datamodel.GambleRecordDAO;
import com.rwbase.dao.battletower.pojo.db.dao.TableBattleTowerDao;
import com.rwbase.dao.business.SevenDayGifInfoDAO;
import com.rwbase.dao.chat.TableUserPrivateChatDao;
import com.rwbase.dao.chat.creator.UserChatCreator;
import com.rwbase.dao.chat.pojo.ChatIllegalDataCreator;
import com.rwbase.dao.chat.pojo.ChatIllegalDataDAO;
import com.rwbase.dao.copypve.TableCopyDataDAO;
import com.rwbase.dao.dropitem.DropRecordDAO;
import com.rwbase.dao.email.TableEmailDAO;
import com.rwbase.dao.fightinggrowth.FSUserFightingGrowthDataDAO;
import com.rwbase.dao.friend.TableFriendDAO;
import com.rwbase.dao.groupcompetition.UserGroupCompetitionDataCreator;
import com.rwbase.dao.groupcompetition.UserGroupCompetitionDataDAO;
import com.rwbase.dao.groupsecret.creator.GroupSecretDefendRecordDataCreator;
import com.rwbase.dao.groupsecret.creator.GroupSecretMatchEnemyDataCreator;
import com.rwbase.dao.groupsecret.creator.GroupSecretTeamDataCreator;
import com.rwbase.dao.groupsecret.creator.UserCreateGroupSecretDataCreator;
import com.rwbase.dao.groupsecret.creator.UserGroupSecretBaseDataCreator;
import com.rwbase.dao.groupsecret.pojo.db.dao.GroupSecretDefendRecordDataDAO;
import com.rwbase.dao.groupsecret.pojo.db.dao.GroupSecretMatchEnemyDataDAO;
import com.rwbase.dao.groupsecret.pojo.db.dao.GroupSecretTeamDataDAO;
import com.rwbase.dao.groupsecret.pojo.db.dao.UserCreateGroupSecretDataDAO;
import com.rwbase.dao.groupsecret.pojo.db.dao.UserGroupSecretBaseDataDAO;
import com.rwbase.dao.guide.GuideProgressDAO;
import com.rwbase.dao.guide.PlotProgressDAO;
import com.rwbase.dao.hero.FSUserHeroGlobalDataCreator;
import com.rwbase.dao.hero.FSUserHeroGlobalDataDAO;
import com.rwbase.dao.praise.PraiseCreator;
import com.rwbase.dao.praise.db.PraiseDAO;
import com.rwbase.dao.setting.TableSettingDataDAO;
import com.rwbase.dao.sign.TableSignDataDAO;
import com.rwbase.dao.store.TableStoreDao;
import com.rwbase.dao.targetSell.BenefitDataCreator;
import com.rwbase.dao.targetSell.BenefitDataDAO;
import com.rwbase.dao.task.TableDailyActivityItemDAO;
import com.rwbase.dao.unendingwar.UnendingWarDAO;
import com.rwbase.dao.user.UserGameDataDao;
import com.rwbase.dao.vip.TableVipDAO;

public enum DataKVType {

	USER_GAME_DATA(1, UserGameDataDao.class, UserGameDataProcessor.class),
	// USER_HERO(2, UserHeroDAO.class, UserHeroCreator.class),
	FRIEND(4, TableFriendDAO.class, FriendCreator.class),
	SIGN(5, TableSignDataDAO.class, SignCreator.class),
	VIP(6, TableVipDAO.class, VipCreator.class),
	SETTING(7, TableSettingDataDAO.class, SettingProcessor.class),
	EMAIL(9, TableEmailDAO.class, EmailCreator.class),
	GAMBLE(10, GambleRecordDAO.class, GambleCreator.class),
	STORE(11, TableStoreDao.class, StoreCreator.class),
	DAILY_ACTIVITY(12, TableDailyActivityItemDAO.class, DailyActivityCreator.class),
	SEVEN_DAY_GIF(13, SevenDayGifInfoDAO.class, SevenDayGifCreator.class),
	UNENDING(14, UnendingWarDAO.class, UnendingWarCreator.class),
	BATTLE_TOWER(15, TableBattleTowerDao.class, BattleTowerCreator.class),
	PLOT_PROGRESS(16, PlotProgressDAO.class, PlotProgressCreator.class),
	GUIDE_PROGRESS(17, GuideProgressDAO.class, GuideProgressCreator.class),
	COPY(18, TableCopyDataDAO.class, CopyCreator.class),
	TAOIST(19, TaoistMagicHolder.class, TaoistMagicDataCreator.class),
	USERMSDATA(20, UserMagicSecretDao.class, MagicSecretCreator.class),
	// 帮派秘境
	GROUP_SECRET_BASE(21, UserGroupSecretBaseDataDAO.class, UserGroupSecretBaseDataCreator.class),
	GROUP_SECRE_CREATE(22, UserCreateGroupSecretDataDAO.class, UserCreateGroupSecretDataCreator.class),
	GROUP_SECRE_TEAM(23, GroupSecretTeamDataDAO.class, GroupSecretTeamDataCreator.class),
	GROUP_SECRE_ENEMY(24, GroupSecretMatchEnemyDataDAO.class, GroupSecretMatchEnemyDataCreator.class),
	GROUP_SECRE_DEFEND_RECORD(25, GroupSecretDefendRecordDataDAO.class, GroupSecretDefendRecordDataCreator.class),
	// 私聊记录数据
	USER_CHAT(26, TableUserPrivateChatDao.class, UserChatCreator.class),
	USER_GFIGHT_DATA(27, UserGFightOnlineDAO.class, UserGFightDataCreator.class),
	USER_TEAMBATTLE_DATA(28, UserTeamBattleDAO.class, UserTeamBattleDataCreator.class),
	// 战力成长数据
	USER_FIGHT_GROWTH_DATA(29, FSUserFightingGrowthDataDAO.class, FSUserFightingGrowthDataCreator.class),

	// 精准营销数据
	USER_BENEFIT_SELL_DATA(30, BenefitDataDAO.class, BenefitDataCreator.class),
	// 首掉
	DROP_RECORD(31, DropRecordDAO.class, DropRecordCreator.class),
	// 帮战的个人数据
	USER_GROUP_COMPETITION(32, UserGroupCompetitionDataDAO.class, UserGroupCompetitionDataCreator.class),
	// 英雄模块的全局数据
	USER_HERO_GLOBAL_DATA(33, FSUserHeroGlobalDataDAO.class, FSUserHeroGlobalDataCreator.class),
	// 点赞的个人数据
	PRAISE_DATA(34, PraiseDAO.class, PraiseCreator.class),
	// 个人充值数据
	CHARGE_INFO(35, ChargeInfoDao.class, ChargeInfoCreator.class),

	WB_USER_DATA(36, WBUserDataDao.class, WBUserDataCreator.class),

	// 个人每日重置记录
	DAILY_RESET_RECORD(37, DailyResetReccordDao.class, DailyResetReccordCreator.class),
	// 聊天的违规数据
	CHAT_ILLEGAL_DATA(38, ChatIllegalDataDAO.class, ChatIllegalDataCreator.class);
	private DataKVType(int type, Class<? extends DataKVDao<?>> clazz, Class<? extends DataCreator<?, ?>> processorClass) {
		this.type = type;
		this.typeValue = type;
		this.daoClass = clazz;
		this.creatorClass = processorClass;
	}

	// 类型
	private final int type;

	private final Integer typeValue;
	// DAO class
	private final Class<? extends DataKVDao<?>> daoClass;
	// processor class
	private final Class<? extends DataCreator<?, ?>> creatorClass;

	public int getType() {
		return type;
	}

	public Integer getTypeValue() {
		return typeValue;
	}

	public Class<? extends DataKVDao<?>> getDaoClass() {
		return daoClass;
	}

	public Class<? extends DataCreator<?, ?>> getCreatorClass() {
		return creatorClass;
	}

	private static DataKVType[] array;

	static {
		DataKVType[] temp = DataKVType.values();
		Object[] copy = HPCUtil.toMappedArray(temp, "type");
		array = new DataKVType[copy.length];
		HPCUtil.copy(copy, array);
	}

	public static DataKVType getDataKVType(int type) {
		return array[type];
	}

}
