package com.rw.manager;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.bm.group.GroupBM;
import com.gm.activity.RankingActivity;
import com.groupCopy.bm.groupCopy.GroupCopyMailHelper;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.PlayerMgr;
import com.playerdata.RankingMgr;
import com.playerdata.activity.rankType.ActivityRankTypeMgr;
import com.playerdata.groupFightOnline.state.GFightStateTransfer;
import com.rw.fsutil.common.SimpleThreadFactory;
import com.rw.netty.UserChannelMgr;
import com.rw.service.gamble.GambleHandler;
import com.rw.service.gamble.datamodel.GambleHotHeroPlan;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.BIStatLogMgr;
import com.rw.service.log.eLog.eBILogRegSubChannelToClientPlatForm;
import com.rwbase.dao.angelarray.pojo.db.dao.AngelArrayTeamInfoDataHolder;
import com.rwbase.dao.group.GroupCheckDismissTask;

public class TimerManager {

	private static TimeSpanOpHelper biTimeMinuteOp;
	private static TimeSpanOpHelper biTime10MinuteOp;

	private static TimeSpanOpHelper timeMinuteOp;
	// private static TimeSpanOpHelper time5MinuteOp;
	private static TimeSpanOpHelper timeHourOp;
	private static DayOpOnHour dayOpOnZero;
	private static DayOpOnHour dayOpOn5Am;
	private static DayOpOnHour dayOpOn9Pm;
	private static DayOpOnHour dayOpOn23h50m4Bilog;
	private static TimeSpanOpHelper timeSecondOp;// 秒时效
	private static TimeSpanOpHelper time10SecondOp;// 10秒时效

	private static ScheduledExecutorService timeService = Executors.newScheduledThreadPool(1, new SimpleThreadFactory("timer"));
	private static ScheduledExecutorService biTimeService = Executors.newScheduledThreadPool(1, new SimpleThreadFactory("biTimer"));
	private static ExecutorService heavyWeightsExecturos = Executors.newFixedThreadPool(4);

	public static void init() {
		final long SECOND = 1000;// 秒
		final long MINUTE = 60 * SECOND;
		final long MINUTE_10 = 10 * MINUTE;
		final long HOUR = 60 * MINUTE;

		timeSecondOp = new TimeSpanOpHelper(new ITimeOp() {

			@Override
			public void doTask() {
				PlayerMgr.getInstance().secondFunc4AllPlayer();
			}
		}, SECOND);

		time10SecondOp = new TimeSpanOpHelper(new ITimeOp() {

			@Override
			public void doTask() {
				GFightStateTransfer.getInstance().checkTransfer();
			}

		}, SECOND * 10);

		timeMinuteOp = new TimeSpanOpHelper(new ITimeOp() {
			@Override
			public void doTask() {
				minutesFun();
			}
		}, MINUTE);

		timeHourOp = new TimeSpanOpHelper(new ITimeOp() {
			@Override
			public void doTask() {
				PlayerMgr.getInstance().hourFunc4AllPlayer();
				// 帮派副本定时发奖
				GroupCopyMailHelper.getInstance().dispatchGroupWarPrice();
				ActivityRankTypeMgr.getInstance().changeMap();
			}
		}, HOUR);

		dayOpOnZero = new DayOpOnHour(new ITimeOp() {
			@Override
			public void doTask() {
				PlayerMgr.getInstance().dayZero4Func4AllPlayer();
				heavyWeightsExecturos.execute(new Runnable() {

					@Override
					public void run() {
						RankingActivity.getInstance().notifyRecord();
					}
				});
			}
		}, 0);

		dayOpOn5Am = new DayOpOnHour(new ITimeOp() {

			@Override
			public void doTask() {
				// TODO 与allen沟通后临时解决慢速任务阻塞时效问题问题，时效需要整理
				heavyWeightsExecturos.execute(new Runnable() {
					@Override
					public void run() {
						GambleHotHeroPlan.resetHotHeroList(GambleHandler.getInstance().getRandom());
					}
				});

				heavyWeightsExecturos.execute(new Runnable() {

					@Override
					public void run() {
						RankingMgr.getInstance().resetUpdateState();
					}
				});

				PlayerMgr.getInstance().day5amFunc4AllPlayer();
				heavyWeightsExecturos.execute(new Runnable() {

					@Override
					public void run() {
						AngelArrayTeamInfoDataHolder.getHolder().resetAngelArrayTeamInfo();
					}
				});
				heavyWeightsExecturos.execute(new Runnable() {

					@Override
					public void run() {
						GroupBM.checkOrAllGroupDayLimit();
					}
				});
			}
		}, 5);

		dayOpOn9Pm = new DayOpOnHour(new ITimeOp() {

			@Override
			public void doTask() {
				heavyWeightsExecturos.execute(new Runnable() {

					@Override
					public void run() {
						RankingMgr.getInstance().arenaCalculate();
					}
				});
			}
		}, 21);

		timeService.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try {
					timeSecondOp.tryRun();
					time10SecondOp.tryRun();
				} catch (Throwable e) {
					GameLog.error(LogModule.COMMON.getName(), "TimerManager", "TimerManager[init]用户数据保存错误", e);
				}
			}
		}, 0, 1, TimeUnit.SECONDS);

		timeService.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try {
					timeMinuteOp.tryRun();
					// time5MinuteOp.tryRun();
					timeHourOp.tryRun();
					dayOpOnZero.tryRun();
					dayOpOn5Am.tryRun();
					dayOpOn9Pm.tryRun();
				} catch (Throwable e) {
					GameLog.error(LogModule.COMMON.getName(), "TimerManager", "TimerManager[init]用户数据保存错误", e);
				}
			}
		}, 0, 10, TimeUnit.SECONDS);

		biTimeMinuteOp = new TimeSpanOpHelper(new ITimeOp() {
			@Override
			public void doTask() {
				BILogMgr.getInstance().doLogoutLog();
			}
		}, MINUTE);
		biTime10MinuteOp = new TimeSpanOpHelper(new ITimeOp() {
			@Override
			public void doTask() {
				Map<String, eBILogRegSubChannelToClientPlatForm> subChannelCount = UserChannelMgr.getSubChannelCount();
				if (subChannelCount.keySet().size() == 0) {
					// BILogMgr.getInstance().logOnlineCount(null,null);没人不打印
				} else {
					for (String regSubChannelIdandclientPlayForm : subChannelCount.keySet()) {
						BILogMgr.getInstance().logOnlineCount(subChannelCount.get(regSubChannelIdandclientPlayForm), regSubChannelIdandclientPlayForm);
					}
				}

			}
		}, MINUTE_10);

		dayOpOn23h50m4Bilog = new DayOpOnHour(new ITimeOp() {

			@Override
			public void doTask() {
				BIStatLogMgr.getInstance().doStat();
			}
		}, 23, 50);

		biTimeService.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try {
					dayOpOn23h50m4Bilog.tryRun();
					biTime10MinuteOp.tryRun();
					biTimeMinuteOp.tryRun();
				} catch (Throwable e) {
					GameLog.error(LogModule.COMMON.getName(), "TimerManager", "TimerManager[biTimeService]", e);
				}
			}
		}, 0, 1, TimeUnit.SECONDS);
	}

	/***** 每分刷新 *****/
	private static void minutesFun() {
		PlayerMgr.getInstance().minutesFunc4AllPlayer();
		/**** 排行 榜奖励 ***/
		ActivityRankTypeMgr.getInstance().sendGift();

		// GambleMgr.minutesUpdate();

		/*** 检查帮派 ***/
		GroupCheckDismissTask.check();
	}
}
