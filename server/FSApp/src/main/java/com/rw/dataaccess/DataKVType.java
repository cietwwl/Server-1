package com.rw.dataaccess;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.common.HPCUtil;
import com.rw.dataaccess.processor.BattleTowerCreator;
import com.rw.dataaccess.processor.CopyCreator;
import com.rw.dataaccess.processor.DailyActivityCreator;
import com.rw.dataaccess.processor.EmailCreator;
import com.rw.dataaccess.processor.FriendCreator;
import com.rw.dataaccess.processor.GambleCreator;
import com.rw.dataaccess.processor.GuideProgressCreator;
import com.rw.dataaccess.processor.PlotProgressCreator;
import com.rw.dataaccess.processor.RoleBaseInfoCreator;
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
import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rwbase.dao.battletower.pojo.db.dao.TableBattleTowerDao;
import com.rwbase.dao.business.SevenDayGifInfoDAO;
import com.rwbase.dao.copypve.TableCopyDataDAO;
import com.rwbase.dao.email.TableEmailDAO;
import com.rwbase.dao.friend.TableFriendDAO;
import com.rwbase.dao.gamble.TableGambleDAO;
import com.rwbase.dao.guide.GuideProgressDAO;
import com.rwbase.dao.guide.PlotProgressDAO;
import com.rwbase.dao.hero.UserHeroDAO;
import com.rwbase.dao.hero.pojo.RoleBaseInfoDAO;
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
//	ROLE_BASE_INFO(3,RoleBaseInfoDAO.class,RoleBaseInfoCreator.class),
	FRIEND(4, TableFriendDAO.class, FriendCreator.class), 
	SIGN(5,TableSignDataDAO.class, SignCreator.class), 
	VIP(6, TableVipDAO.class, VipCreator.class), 
	SETTING(7, TableSettingDataDAO.class, SettingProcessor.class), 
	//AGNEL_ARRAY_FLOOR(8,AngelArrayFloorDataDao.class, AngelArrayFloorCreator.class), 
	EMAIL(9, TableEmailDAO.class, EmailCreator.class), 
	GAMBLE(10, TableGambleDAO.class, GambleCreator.class), 
	STORE(11,TableStoreDao.class, StoreCreator.class), 
	DAILY_ACTIVITY(12, TableDailyActivityItemDAO.class, DailyActivityCreator.class), 
	SEVEN_DAY_GIF(13, SevenDayGifInfoDAO.class,SevenDayGifCreator.class), 
	UNENDING(14, UnendingWarDAO.class, UnendingWarCreator.class), 
	BATTLE_TOWER(15, TableBattleTowerDao.class, BattleTowerCreator.class), 
	PLOT_PROGRESS(16,PlotProgressDAO.class, PlotProgressCreator.class), 
	GUIDE__PROGRESS(17, GuideProgressDAO.class, GuideProgressCreator.class), 
	COPY(18, TableCopyDataDAO.class, CopyCreator.class);

	private DataKVType(int type, Class<? extends DataKVDao<?>> clazz, Class<? extends DataCreator<?, ?>> processorClass) {
		this.type = type;
		this.clazz = clazz;
		this.creatorClass = processorClass;
	}

	// 类型
	private int type;
	// DAO class
	private Class<? extends DataKVDao<?>> clazz;
	// processor class
	private Class<? extends DataCreator<?, ?>> creatorClass;

	public int getType() {
		return type;
	}

	public Class<? extends DataKVDao<?>> getClazz() {
		return clazz;
	}

	public Class<? extends DataCreator<?, ?>> getCreatorClass() {
		return creatorClass;
	}

	private static DataKVType[] array;

	static {
		DataKVType[] temp = DataKVType.values();
		for (DataKVType type : temp) {
			// 检查DataKVDao与UserGameDataProcessor的泛型是否一致
			if (getSuperclassGeneric(type.getClazz()) != getInterfacesGeneric(type.creatorClass)) {
				throw new ExceptionInInitializerError("DataKVDao与PlayerCreatedProcessor范型参数不一致:" + type.getClazz() + "," + type.creatorClass);
			}
		}
		Object[] copy = HPCUtil.toMappedArray(temp, "type");
		array = new DataKVType[copy.length];
		HPCUtil.copy(copy, array);
	}

	public static DataKVType getDataKVType(int type) {
		return array[type];
	}

	private static Class<?> getSuperclassGeneric(Class<?> clz) {
		Type type = clz.getGenericSuperclass();
		if (!(type instanceof ParameterizedType)) {
			throw new IllegalArgumentException("缺少父类的范型参数：" + clz);
		}
		ParameterizedType paramType = (ParameterizedType) type;
		return (Class<?>) paramType.getActualTypeArguments()[0];
	}

	private static Class<?> getInterfacesGeneric(Class<?> clz) {
		Class<?>[] interfaceClass = clz.getInterfaces();
		int index = -1;
		for (int i = 0; i < interfaceClass.length; i++) {
			Class<?> c = interfaceClass[i];
			if (c != DataExtensionCreator.class && c != PlayerCoreCreation.class) {
				continue;
			}
			index = i;
			break;
		}
		if (index == -1) {
			throw new IllegalArgumentException("缺少实现DataExtensionCreator or PlayerCoreCreation接口：" + clz);
		}
		Type[] typeArray = clz.getGenericInterfaces();
		Type type = typeArray[index];
		if (!(type instanceof ParameterizedType)) {
			throw new IllegalArgumentException("缺少实现接口的泛型参数：" + clz);
		}
		ParameterizedType paramType = (ParameterizedType) type;
		return (Class<?>) paramType.getActualTypeArguments()[0];
	}

}
