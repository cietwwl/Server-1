package com.rw.dataaccess;

import com.common.HPCUtil;
import com.rw.dataaccess.processor.BattleTowerCreator;
import com.rw.dataaccess.processor.CopyCreator;
import com.rw.dataaccess.processor.DailyActivityCreator;
import com.rw.dataaccess.processor.EmailCreator;
import com.rw.dataaccess.processor.FriendCreator;
import com.rw.dataaccess.processor.GuideProgressCreator;
import com.rw.dataaccess.processor.PlotProgressCreator;
import com.rw.dataaccess.processor.SettingProcessor;
import com.rw.dataaccess.processor.SevenDayGifCreator;
import com.rw.dataaccess.processor.SignCreator;
import com.rw.dataaccess.processor.StoreCreator;
import com.rw.dataaccess.processor.UnendingWarCreator;
import com.rw.dataaccess.processor.UserGameDataProcessor;
import com.rw.dataaccess.processor.UserHeroCreator;
import com.rw.dataaccess.processor.VipCreator;
import com.rw.fsutil.cacheDao.DataKVDao;
import com.rw.fsutil.cacheDao.loader.DataCreator;
import com.rw.service.TaoistMagic.datamodel.TaoistMagicDataCreator;
import com.rw.service.TaoistMagic.datamodel.TaoistMagicHolder;
import com.rw.service.gamble.datamodel.GambleCreator;
import com.rw.service.gamble.datamodel.GambleRecordDAO;
import com.rwbase.dao.battletower.pojo.db.dao.TableBattleTowerDao;
import com.rwbase.dao.business.SevenDayGifInfoDAO;
import com.rwbase.dao.copypve.TableCopyDataDAO;
import com.rwbase.dao.email.TableEmailDAO;
import com.rwbase.dao.friend.TableFriendDAO;
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
import com.rwbase.dao.hero.UserHeroDAO;
import com.rwbase.dao.setting.TableSettingDataDAO;
import com.rwbase.dao.sign.TableSignDataDAO;
import com.rwbase.dao.store.TableStoreDao;
import com.rwbase.dao.task.TableDailyActivityItemDAO;
import com.rwbase.dao.unendingwar.UnendingWarDAO;
import com.rwbase.dao.user.UserGameDataDao;
import com.rwbase.dao.vip.TableVipDAO;

public enum DataKVType {

	
	USER_GAME_DATA(1, UserGameDataDao.class, UserGameDataProcessor.class), 
	USER_HERO(2, UserHeroDAO.class, UserHeroCreator.class), 
	FRIEND(4, TableFriendDAO.class, FriendCreator.class), 
	SIGN(5,TableSignDataDAO.class, SignCreator.class), 
	VIP(6, TableVipDAO.class, VipCreator.class), 
	SETTING(7, TableSettingDataDAO.class, SettingProcessor.class), 
	EMAIL(9, TableEmailDAO.class, EmailCreator.class), 
	GAMBLE(10, GambleRecordDAO.class, GambleCreator.class), 
	STORE(11,TableStoreDao.class, StoreCreator.class), 
	DAILY_ACTIVITY(12, TableDailyActivityItemDAO.class, DailyActivityCreator.class), 
	SEVEN_DAY_GIF(13, SevenDayGifInfoDAO.class, SevenDayGifCreator.class), 
	UNENDING(14, UnendingWarDAO.class, UnendingWarCreator.class), 
	BATTLE_TOWER(15, TableBattleTowerDao.class, BattleTowerCreator.class), 
	PLOT_PROGRESS(16,PlotProgressDAO.class, PlotProgressCreator.class), 
	GUIDE_PROGRESS(17, GuideProgressDAO.class, GuideProgressCreator.class), 
	COPY(18, TableCopyDataDAO.class, CopyCreator.class),
	TAOIST(19,TaoistMagicHolder.class,TaoistMagicDataCreator.class),
	
	
	
	GROUP_SECRET_BASE(20,UserGroupSecretBaseDataDAO.class,UserGroupSecretBaseDataCreator.class),
	GROUP_SECRE_CREATE(21,UserCreateGroupSecretDataDAO.class,UserCreateGroupSecretDataCreator.class),
	GROUP_SECRE_TEAM(22,GroupSecretTeamDataDAO.class,GroupSecretTeamDataCreator.class),
	GROUP_SECRE_ENEMY(23,GroupSecretMatchEnemyDataDAO.class,GroupSecretMatchEnemyDataCreator.class),
	GROUP_SECRE_DEFEND_RECORD(24,GroupSecretDefendRecordDataDAO.class,GroupSecretDefendRecordDataCreator.class);

	private DataKVType(int type, Class<? extends DataKVDao<?>> clazz, Class<? extends DataCreator<?, ?>> processorClass) {
		this.type = type;
		this.daoClass = clazz;
		this.creatorClass = processorClass;
	}

	// 类型
	private int type;
	// DAO class
	private Class<? extends DataKVDao<?>> daoClass;
	// processor class
	private Class<? extends DataCreator<?, ?>> creatorClass;

	public int getType() {
		return type;
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
