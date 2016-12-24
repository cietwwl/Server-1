package com.gm.multipletimeshotfix;

import java.lang.reflect.Field;

import com.playerdata.FresherActivityMgr;
import com.playerdata.Player;
import com.rw.fsutil.cacheDao.FSUtilLogger;
import com.rw.fsutil.dao.optimize.DataValueAction;
import com.rw.service.FresherActivity.FresherActivityChecker;
import com.rw.service.FresherActivity.FresherActivityCheckerResult;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityItemHolder;

public class NewFreshActivityActionTask implements DataValueAction<Player> {

	@Override
	public void execute(Player p) {
		String userId = p.getUserId();
		FresherActivityMgr fresherActivityMgr = p.getFresherActivityMgr();
		FresherActivityItemHolder fresherActivityItemHolder = null;
		try {
			Field field = FresherActivityMgr.class.getDeclaredField("fresherActivityItemHolder");
			if (field != null) {
				field.setAccessible(true);
				Object object = field.get(fresherActivityMgr);
				if (object == null) {// 把值设置进去
					fresherActivityItemHolder = new FresherActivityItemHolder(userId);
					field.set(fresherActivityMgr, fresherActivityItemHolder);
					fresherActivityItemHolder.synAllData(p, -1);
					FSUtilLogger.info("执行角色开服活动检查，发现Mgr里的fresherActivityItemHolder=null，角色Id=" + userId);
				} else {
					fresherActivityItemHolder = (FresherActivityItemHolder) object;
				}
				field.setAccessible(false);
			} else {
				FSUtilLogger.info("执行角色开服活动检查，发现Mgr里的fresherActivityItemHolder这个字段用反射获取不到，角色Id=" + userId);
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		try {
			Field pField = fresherActivityMgr.getClass().getDeclaredField("m_Player");
			if (pField != null) {
				pField.setAccessible(true);
				Object object = pField.get(fresherActivityMgr);
				if (object == null) {
					FSUtilLogger.info("执行角色开服活动检查，发现Mgr里的m_Player=null，角色Id=" + userId);
					pField.set(fresherActivityMgr, p);
				}
				pField.setAccessible(false);
			} else {
				FSUtilLogger.info("执行角色开服活动检查，发现Mgr里的m_Player这个字段用反射获取不到，角色Id=" + userId);
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		// 检查开服活动
		if (fresherActivityItemHolder != null) {
			try {
				Field checkF = fresherActivityMgr.getClass().getDeclaredField("fresherActivityChecker");
				if (checkF != null) {
					checkF.setAccessible(true);
					Object object = checkF.get(fresherActivityMgr);
					if (object != null) {
						FresherActivityChecker fresherActivityChecker = (FresherActivityChecker) object;
						eActivityType[] values = eActivityType.values();
						for (int j = 0, len = values.length; j < len; j++) {
							FresherActivityCheckerResult returnResult = fresherActivityChecker.checkActivityCondition(p, values[j]);
							if (returnResult == null) {
								continue;
							}
							fresherActivityItemHolder.completeFresherActivity(p, returnResult);
						}
					} else {
						FSUtilLogger.info("执行角色开服活动检查，发现Mgr里的fresherActivityChecker=null，不能去检查数据了，角色Id=" + userId);
					}
					checkF.setAccessible(false);
				} else {
					FSUtilLogger.info("执行角色开服活动检查，发现Mgr里的fresherActivityChecker不能通过反射获取到，角色Id=" + userId);
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} else {
			FSUtilLogger.info("执行角色开服活动检查，发现Mgr里的fresherActivityItemHolder=null，心好累，检查的时候出现的，角色Id=" + userId);
		}
	}
}