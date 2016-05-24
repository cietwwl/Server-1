package com.rw.manager;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.bm.guild.GuildGTSMgr;
import com.bm.rank.magicsecret.MSScoreRankMgr;
import com.gm.activity.RankingActivity;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.PlayerMgr;
import com.playerdata.RankingMgr;
import com.rw.fsutil.dao.cache.SimpleThreadFactory;
import com.rw.netty.UserChannelMgr;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.BIStatLogMgr;
import com.rw.service.log.eLog.eBILogRegSubChannelToClientPlatForm;
import com.rwbase.dao.Army.UserArmyDataDAO;
import com.rwbase.dao.anglearray.pojo.db.dao.AngelArrayTeamInfoDataHolder;
import com.rwbase.dao.group.GroupCheckDismissTask;
import com.rwbase.dao.gulid.faction.GuildDAO;

public class TimerManager {

	private static TimeSpanOpHelper biTimeMinuteOp;
	private static TimeSpanOpHelper biTime10MinuteOp;

	private static TimeSpanOpHelper timeMinuteOp;
	private static TimeSpanOpHelper time5MinuteOp;
	private static TimeSpanOpHelper timeHourOp;
	private static DayOpOnHour dayOpOnZero;
	private static DayOpOnHour dayOpOn5Am;
	private static DayOpOnHour dayOpOn9Pm;
	private static DayOpOnHour dayOpOn23h50m4Bilog;
	private static TimeSpanOpHelper timeSecondOp;// 秒时效

	private static ScheduledExecutorService timeService = Executors.newScheduledThreadPool(1, new SimpleThreadFactory("time_manager"));
	private static ScheduledExecutorService biTimeService = Executors.newScheduledThreadPool(1);

	public static void init() {
		final long SECOND = 1000;// 秒
		final long MINUTE = 60 * SECOND;
		final long MINUTE_5 = 5 * MINUTE;
		final long MINUTE_10 = 10 * MINUTE;
		final long HOUR = 60 * MINUTE;

		timeSecondOp = new TimeSpanOpHelper(new ITimeOp() {

			@Override
			public void doTask() {
				PlayerMgr.getInstance().secondFunc4AllPlayer();
			}
		}, SECOND);

		timeMinuteOp = new TimeSpanOpHelper(new ITimeOp() {
			@Override
			public void doTask() {
				minutesFun();
			}
		}, MINUTE);

		time5MinuteOp = new TimeSpanOpHelper(new ITimeOp() {
			@Override
			public void doTask() {
				// PlayerMgr.getInstance().saveAllPlayer();
				GuildDAO.getInstance().flush();
				// SecretAreaInfoDAO.getInstance().flush();
				UserArmyDataDAO.getInstance().flush();

			}
		}, MINUTE_5);

		timeHourOp = new TimeSpanOpHelper(new ITimeOp() {
			@Override
			public void doTask() {
				PlayerMgr.getInstance().hourFunc4AllPlayer();
				GuildGTSMgr.getInstance().checkAssignMent();
			}
		}, HOUR);

		dayOpOnZero = new DayOpOnHour(new ITimeOp() {
			@Override
			public void doTask() {
				PlayerMgr.getInstance().dayZero4Func4AllPlayer();
				RankingActivity.getInstance().notifyRecord();
			}
		}, 0);

		dayOpOn5Am = new DayOpOnHour(new ITimeOp() {

			@Override
			public void doTask() {
				RankingMgr.getInstance().resetUpdateState();
				PlayerMgr.getInstance().day5amFunc4AllPlayer();
				AngelArrayTeamInfoDataHolder.getHolder().resetAngelArrayTeamInfo();
				MSScoreRankMgr.dispatchMSDailyReward();
			}
		}, 5);

		dayOpOn9Pm = new DayOpOnHour(new ITimeOp() {

			@Override
			public void doTask() {
				RankingMgr.getInstance().arenaCalculate();
			}
		}, 21);

		timeService.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try {
					timeSecondOp.tryRun();
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
					time5MinuteOp.tryRun();
					timeHourOp.tryRun();
					dayOpOnZero.tryRun();
					dayOpOn5Am.tryRun();
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
					dayOpOn9Pm.tryRun();
				} catch (Throwable e) {
					GameLog.error(LogModule.COMMON.getName(), "TimerManager", "TimerManager[biTimeService]", e);
				}
			}
		}, 0, 1, TimeUnit.SECONDS);

	}

	/***** 每分刷新 *****/
	private static void minutesFun() {
		PlayerMgr.getInstance().minutesFunc4AllPlayer();
		/**** 排行 ***/
		// RankingMgr.getInstance().onTimeMinute();

		//GambleMgr.minutesUpdate();
		/*** 检查帮派 ***/
		GroupCheckDismissTask.check();
	}
}
