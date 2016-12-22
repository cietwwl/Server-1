package com.gm.multipletimeshotfix;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionException;

import com.playerdata.FresherActivityMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.activity.dailyDiscountType.cfg.ActivityDailyDiscountTypeCfgDAO;
import com.rw.manager.GameManager;
import com.rw.service.FresherActivity.FresherActivityChecker;
import com.rw.service.FresherActivity.FresherActivityCheckerResult;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.common.timer.IGameTimerTask;
import com.rwbase.common.timer.core.FSGameTimeSignal;
import com.rwbase.common.timer.core.FSGameTimerMgr;
import com.rwbase.common.timer.core.FSGameTimerTaskSubmitInfoImpl;
import com.rwbase.dao.fresherActivity.pojo.FresherActivityItemHolder;

/**
 * @Author HC
 * @date 2016年12月21日 下午12:36:18
 * @desc
 **/

public class FreshActivityCheckTask implements IGameTimerTask, Callable<Void> {

	public static volatile boolean hasInit = false;

	@Override
	public String getName() {
		return "CHECK_FRESH_ACT";// 检查开服活动
	}

	@Override
	public Object onTimeSignal(FSGameTimeSignal timeSignal) {
		// 检查当前角色的数据
		List<Player> onlinePlayers = PlayerMgr.getInstance().getOnlinePlayers();
		for (int i = 0, size = onlinePlayers.size(); i < size; i++) {
			Player p = onlinePlayers.get(i);
			FresherActivityMgr fresherActivityMgr = p.getFresherActivityMgr();
			FresherActivityItemHolder fresherActivityItemHolder = null;
			try {
				Field field = fresherActivityMgr.getClass().getDeclaredField("fresherActivityItemHolder");
				if (field != null) {
					field.setAccessible(true);
					Object object = field.get(fresherActivityMgr);
					if (object == null) {// 把值设置进去
						fresherActivityItemHolder = new FresherActivityItemHolder(p.getUserId());
						field.set(fresherActivityMgr, fresherActivityItemHolder);
						fresherActivityItemHolder.synAllData(p, -1);
					} else {
						fresherActivityItemHolder = (FresherActivityItemHolder) object;
					}
					field.setAccessible(false);
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
						pField.set(fresherActivityMgr, p);
					}
					pField.setAccessible(false);
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
						}
						checkF.setAccessible(false);
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
			}
		}
		return "SUCCESS";
	}

	@Override
	public void afterOneRoundExecuted(FSGameTimeSignal timeSignal) {
	}

	@Override
	public void rejected(RejectedExecutionException e) {
	}

	@Override
	public boolean isContinue() {
		return true;
	}

	@Override
	public List<FSGameTimerTaskSubmitInfoImpl> getChildTasks() {
		return null;
	}

	@Override
	public Void call() throws Exception {
		try {
			ActivityDailyDiscountTypeCfgDAO.getInstance().reload();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		if (!GameManager.getServerId().trim().equals("9001")) {
			return null;
		}
		if (!hasInit) {
			hasInit = true;
			FSGameTimerMgr.getInstance().submitSecondTask(this, 5);
		}
		return null;
	}
}