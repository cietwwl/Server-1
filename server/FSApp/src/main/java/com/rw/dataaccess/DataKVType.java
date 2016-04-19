package com.rw.dataaccess;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.common.HPCUtil;
import com.rw.dataaccess.processor.AngelArrayFloorProcessor;
import com.rw.dataaccess.processor.BattleTowerProcessor;
import com.rw.dataaccess.processor.DailyActivityProcessor;
import com.rw.dataaccess.processor.EmailProcessor;
import com.rw.dataaccess.processor.FriendProcessor;
import com.rw.dataaccess.processor.GambleProcessor;
import com.rw.dataaccess.processor.GuideProgressProcessor;
import com.rw.dataaccess.processor.PlotProgressProcessor;
import com.rw.dataaccess.processor.SettingProcessor;
import com.rw.dataaccess.processor.SevenDayGifProcessor;
import com.rw.dataaccess.processor.SignProcessor;
import com.rw.dataaccess.processor.StoreProcessor;
import com.rw.dataaccess.processor.UnendingWarProcessor;
import com.rw.dataaccess.processor.UserGameDataProcessor;
import com.rw.dataaccess.processor.UserHeroProcessor;
import com.rw.dataaccess.processor.VipProcessor;
import com.rw.fsutil.cacheDao.DataKVDao;
import com.rwbase.dao.anglearray.pojo.db.dao.AngleArrayFloorDataDao;
import com.rwbase.dao.battletower.pojo.db.dao.TableBattleTowerDao;
import com.rwbase.dao.business.SevenDayGifInfoDAO;
import com.rwbase.dao.email.TableEmailDAO;
import com.rwbase.dao.friend.TableFriendDAO;
import com.rwbase.dao.gamble.TableGambleDAO;
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

	// USER(1, UserDataDao.class, UserProcessor.class),
	USER_GAME_DATA(1, UserGameDataDao.class, UserGameDataProcessor.class, LoadPolicy.PLAYER_QUERY), 
	USER_HERO(2, UserHeroDAO.class, UserHeroProcessor.class, LoadPolicy.PLAYER_QUERY), 
	//ROLE_BASE_INFO(3, RoleBaseInfoDAO.class, UserProcessor.class),
	FRIEND(4,TableFriendDAO.class,FriendProcessor.class, LoadPolicy.PLAYER_LOGIN),
	SIGN(5,TableSignDataDAO.class,SignProcessor.class, LoadPolicy.PLAYER_LOGIN),
	VIP(6,TableVipDAO.class,VipProcessor.class, LoadPolicy.PLAYER_LOGIN),
	SETTING(7,TableSettingDataDAO.class,SettingProcessor.class, LoadPolicy.PLAYER_LOGIN),
	AGNEL_ARRAY_FLOOR(8,AngleArrayFloorDataDao.class,AngelArrayFloorProcessor.class, LoadPolicy.PLAYER_QUERY),
	EMAIL(9,TableEmailDAO.class,EmailProcessor.class, LoadPolicy.PLAYER_LOGIN),
	GAMBLE(10,TableGambleDAO.class,GambleProcessor.class, LoadPolicy.PLAYER_QUERY),
	STORE(11,TableStoreDao.class,StoreProcessor.class, LoadPolicy.PLAYER_LOGIN),
	DAILY_ACTIVITY(12,TableDailyActivityItemDAO.class,DailyActivityProcessor.class, LoadPolicy.PLAYER_LOGIN),
	SEVEN_DAY_GIF(13,SevenDayGifInfoDAO.class,SevenDayGifProcessor.class, LoadPolicy.PLAYER_LOGIN),
	//USER_GROUP_ATTRIBUTE(14,UserGroupAttributeDataDAO.class,UserGroupProcessor.class),
	
	UNENDING(14,UnendingWarDAO.class,UnendingWarProcessor.class, LoadPolicy.PLAYER_QUERY),
	BATTLE_TOWER(15,TableBattleTowerDao.class,BattleTowerProcessor.class, LoadPolicy.PLAYER_QUERY),
	PLOT_PROGRESS(16,PlotProgressDAO.class,PlotProgressProcessor.class, LoadPolicy.PLAYER_LOGIN),
	GUIDE__PROGRESS(17,GuideProgressDAO.class,GuideProgressProcessor.class, LoadPolicy.PLAYER_LOGIN)
	;

	private DataKVType(int type, Class<? extends DataKVDao<?>> clazz, 
			Class<? extends PlayerCreatedProcessor<?>> processorClass,LoadPolicy loadPolicy) {
		this.type = type;
		this.clazz = clazz;
		this.processorClass = processorClass;
		this.loadPolicy = loadPolicy;
	}

	private int type;
	private Class<? extends DataKVDao<?>> clazz;
	private Class<? extends PlayerCreatedProcessor<?>> processorClass;
	private LoadPolicy loadPolicy;
	
	public int getType() {
		return type;
	}

	public Class<? extends PlayerCreatedProcessor<?>> getProcessorClass() {
		return processorClass;
	}

	public Class<? extends DataKVDao<?>> getClazz() {
		return clazz;
	}
	
	public LoadPolicy getLoadPolicy() {
		return loadPolicy;
	}

	private static DataKVType[] array;

	static {
		DataKVType[] temp = DataKVType.values();
		for (DataKVType type : temp) {
			if (getSuperclassGeneric(type.getClazz()) != getInterfacesGeneric(type.getProcessorClass())) {
				throw new ExceptionInInitializerError("DataKVDao与PlayerCreatedProcessor范型参数不一致:" + type.getClazz() + "," + type.getProcessorClass());
			}
		}
		Object[] copy = HPCUtil.toMappedArray(temp, "type");
		array = new DataKVType[copy.length];
		HPCUtil.copy(copy, array);
	}

	public static DataKVType getDataKVType(int type) {
		return array[type];
	}

	private static Class getSuperclassGeneric(Class clz) {
		Type type = clz.getGenericSuperclass();
		if (!(type instanceof ParameterizedType)) {
			throw new IllegalArgumentException("缺少父类的范型参数：" + clz);
		}
		ParameterizedType paramType = (ParameterizedType) type;
		return (Class) paramType.getActualTypeArguments()[0];
	}

	private static Class getInterfacesGeneric(Class clz) {
		Class[] interfaceClass = clz.getInterfaces();
		int index = -1;
		for (int i = 0; i < interfaceClass.length; i++) {
			Class c = interfaceClass[i];
			if (c != PlayerCreatedProcessor.class) {
				continue;
			}
			index = i;
			break;
		}
		if (index == -1) {
			throw new IllegalArgumentException("缺少实现PlayerCreatedProcessor接口：" + clz);
		}
		Type[] typeArray = clz.getGenericInterfaces();
		Type type = typeArray[index];
		if (!(type instanceof ParameterizedType)) {
			throw new IllegalArgumentException("缺少实现PlayerCreatedProcessor接口的泛型参数：" + clz);
		}
		ParameterizedType paramType = (ParameterizedType) type;
		return (Class) paramType.getActualTypeArguments()[0];
	}
}
