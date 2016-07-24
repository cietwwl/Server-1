package com.playerdata;

import com.bm.arena.ArenaBM;
import com.common.TimeAction;
import com.common.TimeActionTask;
import com.playerdata.activity.VitalityType.ActivityVitalityTypeMgr;
import com.playerdata.activity.countType.ActivityCountTypeMgr;
import com.playerdata.activity.dailyCountType.ActivityDailyTypeMgr;
import com.playerdata.activity.dailyDiscountType.ActivityDailyDiscountTypeMgr;
import com.playerdata.activity.rankType.ActivityRankTypeMgr;
import com.playerdata.activity.rateType.ActivityRateTypeMgr;
import com.playerdata.activity.timeCardType.ActivityTimeCardTypeMgr;
import com.playerdata.groupsecret.UserGroupSecretBaseDataMgr;
import com.playerdata.mgcsecret.manager.MagicSecretMgr;
import com.rw.service.PeakArena.PeakArenaBM;
import com.rw.service.Privilege.MonthCardPrivilegeMgr;
import com.rwbase.dao.publicdata.PublicData;
import com.rwbase.dao.publicdata.PublicDataCfgDAO;

public class PlayerTimeActionHelper {

	/** 每秒执行 */
	public static TimeAction onSecond(final Player player) {
		TimeAction onSecondTimeAction = new TimeAction(player.getUserId());
		onSecondTimeAction.addTask(new TimeActionTask() {

			@Override
			public void doTask() {
				// 体力更新
				int level = player.getLevel();
				player.getUserGameDataMgr().addPowerByTime(level);
				// 秘境钥石恢复
				UserGroupSecretBaseDataMgr.getMgr().checkAndUpdateKeyData(player);
			}
		});
		return onSecondTimeAction;
	}

	/** 每分钟执行 */
	public static TimeAction onMinutes(final Player player) {
		TimeAction onMinutesTimeAction = new TimeAction(player.getUserId());
		// onMinutesTimeAction.addTask(new TimeActionTask() {
		// @Override
		// public void doTask() {
		//
		// // 体力更新
		// int level = player.getLevel();
		// player.getUserGameDataMgr().addPowerByTime(level);
		//
		// }
		// });
		onMinutesTimeAction.addTask(new TimeActionTask() {
			@Override
			public void doTask() {
				// 商店 判断是否是机器人
				if (!player.isRobot()) {
					player.getStoreMgr().onMinutes();
				}
			}
		});
		onMinutesTimeAction.addTask(new TimeActionTask() {
			@Override
			public void doTask() {
				// 时装
				player.getFashionMgr().onMinutes();
			}
		});
		return onMinutesTimeAction;

	}

	public static TimeAction onNewHour(final Player player) {

		TimeAction onNewHourTimeAction = new TimeAction(player.getUserId());
		onNewHourTimeAction.addTask(new TimeActionTask() {
			@Override
			public void doTask() {
				player.getDailyActivityMgr().resRed();
			}
		});
		onNewHourTimeAction.addTask(new TimeActionTask() {
			@Override
			public void doTask() {
				// TODO　HC 每个小时都检查一下是否需要重置万仙阵的匹配数据
				player.getTowerMgr().checkAndResetMatchData(player);
			}
		});
		onNewHourTimeAction.addTask(new TimeActionTask() {
			@Override
			public void doTask() {
				// 每个小时都检查一下活动的开启关闭状态
				ActivityCountTypeMgr.getInstance().checkActivityOpen(player);
				ActivityTimeCardTypeMgr.getInstance().checkActivityOpen(player);
				ActivityRateTypeMgr.getInstance().checkActivityOpen(player);
				ActivityDailyTypeMgr.getInstance().checkActivityOpen(player);
				ActivityVitalityTypeMgr.getInstance().checkActivityOpen(player);
				ActivityRankTypeMgr.getInstance().checkActivityOpen(player);
				ActivityDailyDiscountTypeMgr.getInstance().checkActivityOpen(player);
			}
		});
		return onNewHourTimeAction;

	}

	/** 0点刷新 */
	public static TimeAction onNewDayZero(final Player player) {

		TimeAction onNewDayZeroTimeAction = new TimeAction(player.getUserId());
		ActivityTimeCardTypeMgr.getInstance().checkActivityOpen(player);

		onNewDayZeroTimeAction.addTask(new TimeActionTask() {
			@Override
			public void doTask() {
				MonthCardPrivilegeMgr.getShareInstance().checkPrivilege(player);
			}
		});

		return onNewDayZeroTimeAction;
	}

	public static TimeAction onNewDay5ClockTimeAction(final Player player) {

		TimeAction onNewDay5ClockTimeAction = new TimeAction(player.getUserId());

		onNewDay5ClockTimeAction.addTask(new TimeActionTask() {
			@Override
			public void doTask() {
				player.getUserGameDataMgr().onNewDay5Clock();
			}
		});

		onNewDay5ClockTimeAction.addTask(new TimeActionTask() {
			@Override
			public void doTask() {
				player.getFriendMgr().onNewDay5Clock();
			}
		});
		onNewDay5ClockTimeAction.addTask(new TimeActionTask() {
			@Override
			public void doTask() {
				player.getDailyActivityMgr().ChangeRefreshVar();
			}
		});
		onNewDay5ClockTimeAction.addTask(new TimeActionTask() {
			@Override
			public void doTask() {
				player.getDailyActivityMgr().UpdateTaskList();
			}
		});
		onNewDay5ClockTimeAction.addTask(new TimeActionTask() {
			@Override
			public void doTask() {
				player.getEmailMgr().checkRemoveOverdue();
			}
		});
		onNewDay5ClockTimeAction.addTask(new TimeActionTask() {
			@Override
			public void doTask() {
				player.getGambleMgr().resetForNewDay();
			}
		});
		onNewDay5ClockTimeAction.addTask(new TimeActionTask() {
			@Override
			public void doTask() {
				player.getCopyRecordMgr().resetAllCopyRecord();
			}
		});
		onNewDay5ClockTimeAction.addTask(new TimeActionTask() {
			@Override
			public void doTask() {
				player.getUnendingWarMgr().refreshConst();
			}
		});
		onNewDay5ClockTimeAction.addTask(new TimeActionTask() {
			@Override
			public void doTask() {
				player.getDailyGifMgr().updataCount(player);
			}
		});
		onNewDay5ClockTimeAction.addTask(new TimeActionTask() {
			@Override
			public void doTask() {
				player.getStoreMgr().resetRefreshNum();
			}
		});

		onNewDay5ClockTimeAction.addTask(new TimeActionTask() {
			@Override
			public void doTask() {
				// 试练塔次数重置
				long now = System.currentTimeMillis();
				player.getBattleTowerMgr().resetBattleTowerResetTimes(now);
			}
		});

		// 0点更改成5点
		onNewDay5ClockTimeAction.addTask(new TimeActionTask() {
			@Override
			public void doTask() {
				player.getUserGameDataMgr().setFreeChat(PublicDataCfgDAO.getInstance().getPublicDataValueById(PublicData.ID_CHAT_FREE_COUNT));
			}
		});
		onNewDay5ClockTimeAction.addTask(new TimeActionTask() {
			@Override
			public void doTask() {
				player.getCopyDataMgr().resetDataInNewDay();
			}
		});
		onNewDay5ClockTimeAction.addTask(new TimeActionTask() {
			@Override
			public void doTask() {
				PeakArenaBM.getInstance().resetDataInNewDay(player);
			}
		});
		onNewDay5ClockTimeAction.addTask(new TimeActionTask() {
			@Override
			public void doTask() {
				ArenaBM.getInstance().resetDataInNewDay(player);
			}
		});

		onNewDay5ClockTimeAction.addTask(new TimeActionTask() {

			@Override
			public void doTask() {
				player.getTowerMgr().resetDataInNewDay();
			}
		});

		onNewDay5ClockTimeAction.addTask(new TimeActionTask() {

			@Override
			public void doTask() {
				MagicSecretMgr.getInstance().resetDailyMSInfo(player);
			}
		});

		return onNewDay5ClockTimeAction;
	}

}
