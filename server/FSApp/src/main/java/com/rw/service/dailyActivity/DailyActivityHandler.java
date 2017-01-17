package com.rw.service.dailyActivity;

import java.util.HashMap;
import java.util.List;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.DailyActivityMgr;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.activity.retrieve.userFeatures.UserFeatruesMgr;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.template.BIActivityCode;
import com.rw.service.log.template.BILogTemplateHelper;
import com.rw.service.log.template.BilogItemInfo;
import com.rwbase.dao.copy.pojo.ItemInfo;
import com.rwbase.dao.task.DailyActivityCfgDAO;
import com.rwbase.dao.task.pojo.DailyActivityCfgEntity;
import com.rwbase.dao.task.pojo.DailyActivityData;
import com.rwproto.DailyActivityProtos.DailyActivityInfo;
import com.rwproto.DailyActivityProtos.EDailyActivityRequestType;
import com.rwproto.DailyActivityProtos.MsgDailyActivityRequest;
import com.rwproto.DailyActivityProtos.MsgDailyActivityResponse;
import com.rwproto.DailyActivityProtos.eDailyActivityResultType;
import com.rwproto.MsgDef.Command;
import com.rwproto.TaskProtos.OneKeyResultType;

public class DailyActivityHandler {
	private static DailyActivityHandler m_instance = new DailyActivityHandler();

	public static DailyActivityHandler getInstance() {
		return m_instance;
	}

	// 获得任务列表
	public ByteString getTaskList(Player player, MsgDailyActivityRequest request) {
		MsgDailyActivityResponse.Builder response = MsgDailyActivityResponse.newBuilder();
		response.setResponseType(request.getRequestType());
		response.setResultType(eDailyActivityResultType.SUCCESS);
		List<DailyActivityData> taskList = player.getDailyActivityMgr().getAllTask();
		for (DailyActivityData td : taskList) {
			response.addTaskList(toTaskInfo(td));
		}
		return response.build().toByteString();
	}

	public void sendTaskList(Player player) {
		MsgDailyActivityResponse.Builder response = MsgDailyActivityResponse.newBuilder();
		response.setResponseType(EDailyActivityRequestType.Task_List);
		response.setResultType(eDailyActivityResultType.SUCCESS);
		List<DailyActivityData> taskList = player.getDailyActivityMgr().getAllTask();
		StringBuilder tmp = new StringBuilder();
		for (DailyActivityData td : taskList) {
			tmp.append("    id =").append(td.getTaskId());
			response.addTaskList(toTaskInfo(td));
		}
		GameLog.info("任务触发", player.getUserId(), tmp.toString(), null);
		player.SendMsg(Command.MSG_DAILY_ACTIVITY, response.build().toByteString());
	}

	// 将TaskData转化为TaskInfo
	private DailyActivityInfo.Builder toTaskInfo(DailyActivityData td) {
		DailyActivityInfo.Builder taskInfo = DailyActivityInfo.newBuilder();
		taskInfo.setTaskId(td.getTaskId());
		taskInfo.setCanGetReward(td.getCanGetReward());
		taskInfo.setCurrentProgress(td.getCurrentProgress());
		return taskInfo;
	}

	// 任务完成的回应
	public ByteString taskFinish(Player player, MsgDailyActivityRequest request) {
		MsgDailyActivityResponse.Builder response = MsgDailyActivityResponse.newBuilder();
		response.setResponseType(request.getRequestType());
		response.setResultType(eDailyActivityResultType.SUCCESS);
		int taskId = request.getTaskId();
		DailyActivityMgr activityMgr = player.getDailyActivityMgr();
		DailyActivityCfgEntity entity = DailyActivityCfgDAO.getInstance().getCfgEntity(taskId);
		if (entity == null) {
			GameLog.error("daily", "takeFinish", player + "领取配置不存在的日常任务：" + taskId, null);
			return returnFailResponse(response);
		}

		// BIActivityCode activitycode = BILogTemplateHelper.getByDailyTaskId(taskId);
		BILogMgr.getInstance().logActivityBegin(player, null, BIActivityCode.DAILY_TASK, 0, entity.getCfg().getId());

		// 从任务列表中删除该任务
		if (activityMgr.RemoveTaskById(taskId)) {
			List<ItemInfo> rewardList = entity.getReward();
			// for(ItemInfo info:rewardList){
			// player.getItemBagMgr().addItem(info.getItemID(), info.getItemNum());
			// }
			ItemBagMgr.getInstance().addItem(player, rewardList);
			response.setTaskId(request.getTaskId());

			List<BilogItemInfo> rewardslist = BilogItemInfo.fromItemList(rewardList);
			String rewardInfoActivity = BILogTemplateHelper.getString(rewardslist);
			BILogMgr.getInstance().logActivityEnd(player, null, BIActivityCode.DAILY_TASK, 0, true, 0, rewardInfoActivity, entity.getCfg().getId());
			UserFeatruesMgr.getInstance().checkDailyTask(player, entity.getCfg().getId());
		} else {
			GameLog.error("daily", "takeFinish", player + "重复领取的日常任务：" + taskId, null);
			response.setResultType(eDailyActivityResultType.FAIL);
		}

		// 返回任务列表
		List<DailyActivityData> taskList = activityMgr.getAllTask();
		for (DailyActivityData td : taskList) {
			DailyActivityCfgEntity e = DailyActivityCfgDAO.getInstance().getCfgEntity(td.getTaskId());
//			System.out.println("task id:" + td.getTaskId() + ",desc:" + e.getCfg().getDescription() + ",titel:" + e.getCfg().getTitle());
			response.addTaskList(toTaskInfo(td));
		}
		return response.build().toByteString();
	}

	// 领取所有已经完成的日常
	public OneKeyResultType taskAllFinish(Player player, HashMap<Integer, Integer> rewardMap) {
		DailyActivityMgr activityMgr = player.getDailyActivityMgr();
		List<DailyActivityData> dailyList = activityMgr.getAllTask();
		if (null == dailyList || dailyList.isEmpty())
			return OneKeyResultType.NO_REWARD;
		boolean haveReward = false;
		for (DailyActivityData data : dailyList) {
			if (data.getCanGetReward() == 1) {
				DailyActivityCfgEntity entity = DailyActivityCfgDAO.getInstance().getCfgEntity(data.getTaskId());
				if (entity == null) {
					GameLog.error("daily", "takeFinish", player + "领取配置不存在的日常任务：" + data.getTaskId(), null);
					continue;
				}
				BILogMgr.getInstance().logActivityBegin(player, null, BIActivityCode.DAILY_TASK,0,entity.getCfg().getId());
				// 从任务列表中删除该任务
				if (activityMgr.RemoveTaskById(data.getTaskId())) {
					haveReward = true;
					List<ItemInfo> rewardList = entity.getReward();
					for (ItemInfo info : rewardList) {
						Integer haveCount = rewardMap.get(info.getItemID());
						if (null == haveCount)
							haveCount = info.getItemNum();
						else
							haveCount += info.getItemNum();
						rewardMap.put(info.getItemID(), haveCount);
					}
					List<BilogItemInfo> rewardslist = BilogItemInfo.fromItemList(rewardList);
					String rewardInfoActivity = BILogTemplateHelper.getString(rewardslist);
					BILogMgr.getInstance().logActivityEnd(player, null, BIActivityCode.DAILY_TASK, 0, true, 0, rewardInfoActivity, entity.getCfg().getId());
					UserFeatruesMgr.getInstance().checkDailyTask(player, entity.getCfg().getId());

				} else {
					GameLog.error("daily", "takeFinish", player + "重复领取的日常任务：" + data.getTaskId(), null);
					continue;
				}
			}
		}
		if (!haveReward)
			return OneKeyResultType.NO_REWARD;
		activityMgr.resRed();
		return OneKeyResultType.OneKey_SUCCESS;
	}

	private ByteString returnFailResponse(MsgDailyActivityResponse.Builder response) {
		response.setResultType(eDailyActivityResultType.FAIL);
		return response.build().toByteString();
	}
}
