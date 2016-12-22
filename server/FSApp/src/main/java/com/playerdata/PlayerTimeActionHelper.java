package com.playerdata;

import com.bm.arena.ArenaBM;
import com.bm.targetSell.TargetSellManager;
import com.common.TimeAction;
import com.common.TimeActionTask;
import com.playerdata.activity.countType.ActivityCountTypeMgr;
import com.playerdata.activity.timeCardType.ActivityTimeCardTypeMgr;
import com.playerdata.activityCommon.ActivityMgrHelper;
import com.playerdata.group.UserGroupAttributeDataMgr;
import com.playerdata.groupsecret.UserGroupSecretBaseDataMgr;
import com.playerdata.mgcsecret.manager.MagicSecretMgr;
import com.playerdata.teambattle.manager.UserTeamBattleDataMgr;
import com.rw.service.PeakArena.PeakArenaBM;
import com.rw.service.Privilege.MonthCardPrivilegeMgr;
import com.rwbase.dao.openLevelTiggerService.OpenLevelTiggerServiceMgr;
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
				OpenLevelTiggerServiceMgr.getInstance().oneSecondAction(player);
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
				// TODO　HC 每个小时都检查一下是否需要重置刷新数据
				ActivityCountTypeMgr.getInstance().checkActivity(player);
			}
		});
		onNewHourTimeAction.addTask(new TimeActionTask() {
			@Override
			public void doTask() {
				// TODO　HC 每个小时都检查一下是否需要重置万仙阵的匹配数据
				player.getTowerMgr().checkAndResetMatchData(player);
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
				// 个人帮派副本数据重置
				player.getUserGroupCopyRecordMgr().resetDataInNewDay();
				UserGroupAttributeDataMgr.getMgr().resetAllotGroupRewardCount(player.getUserId());
				TargetSellManager.getInstance().checkBenefitScoreAndSynData(player);
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

		onNewDay5ClockTimeAction.addTask(new TimeActionTask() {

			@Override
			public void doTask() {
				UserTeamBattleDataMgr.getInstance().dailyReset(player);
			}
		});

		onNewDay5ClockTimeAction.addTask(new TimeActionTask() {

			@Override
			public void doTask() {
				// ActivityDailyRechargeTypeMgr.getInstance().dailyRefreshNewDaySubActivity(player);
				ActivityMgrHelper.getInstance().dailyRefreshNewDaySubActivity(player);
			}
		});

		onNewDay5ClockTimeAction.addTask(new TimeActionTask() {

			@Override
			public void doTask() {
				ActivityCountTypeMgr.getInstance().checkActivity(player);
			}
		});

		return onNewDay5ClockTimeAction;
	}

}
