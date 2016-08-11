package com.rw.handler.GroupCopy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.common.Utils;
import com.rw.Client;
import com.rw.common.RobotLog;
import com.rw.handler.GroupCopy.data.GroupCopyMapRecord;
import com.rw.mutiTest.MutiTestAccount;
import com.rwbase.common.RandomUtil;
import com.rwproto.GroupCopyAdminProto.RequestType;
import com.rwproto.GroupCopyCmdProto.GroupCopyMapStatus;


public class GroupCopyMgr {
	
	private static GroupCopyMgr instance = new GroupCopyMgr();

	private ExecutorService executorService = Executors.newFixedThreadPool(MutiTestAccount.threadCount);
	
	public static GroupCopyMgr getInstance(){
		return instance;
	}
	
	public boolean playGroupCopy(Client client){
		//同步地图关卡数据
		GroupCopyHandler.getInstance().applyCopyInfo(client);

		//获取所有还没有开放的副本 发送开放请求
		List<GroupCopyMapRecord> list = getAllNotOpenChaters(client);
		for (GroupCopyMapRecord record : list) {
			GroupCopyHandler.getInstance().openLevel(client, record.getChaterID(), RequestType.OPEN_COPY);
		}
		GroupCopyHandler handler = GroupCopyHandler.getInstance();
		GroupCopyMapRecord record = getRandomOpenChater(client);
		if(record == null){
			RobotLog.info("当前机器人没有可进入的帮派副本");
			return true;
		}
		handler.clientApplyDistRewardLog(client);
		handler.clientApplyDropData(client, record.getChaterID());
		handler.clientApplyGroupDamageRank(client);
		handler.clientApplyServerRank(client, record.getCurLevelID());
		handler.getAllRewardApplyInfo(client);
		GroupCopyHandler.getInstance().applyCopyInfo(client);
		handler.try2EnterBattle(client, record.getCurLevelID());
		handler.clientBeginFight(client, record.getCurLevelID());
		return true;
	}
	
	/**
	 * 随机一个开启状态下的副本
	 * @param client
	 * @return
	 */
	public GroupCopyMapRecord getRandomOpenChater(Client client){
		List<GroupCopyMapRecord> list = getAllOnGoingChaters(client);
		int size = list.size();
		int index = RandomUtil.getRandonIndexWithoutProb(size);
		return list.get(index);
	}
	
	public List<GroupCopyMapRecord> getAllNotOpenChaters(Client client){
		List<GroupCopyMapRecord> list = client.getGroupCopyHolder().getMapRecordList();
		List<GroupCopyMapRecord> temp = new ArrayList<GroupCopyMapRecord>();
		for (GroupCopyMapRecord record : list) {
			if(record.getStatus() != GroupCopyMapStatus.ONGOING){
				temp.add(record);
			}
		}
		
		return temp;
	}
	
	public List<GroupCopyMapRecord> getAllOnGoingChaters(Client client){
		List<GroupCopyMapRecord> list = client.getGroupCopyHolder().getMapRecordList();
		List<GroupCopyMapRecord> temp = new ArrayList<GroupCopyMapRecord>();
		for (GroupCopyMapRecord record : list) {
			if(record.getStatus() == GroupCopyMapStatus.ONGOING){
				temp.add(record);
			}
		}
		
		return temp;
	}
	
	/**
	 * 添加结束战斗通知
	 * @param task
	 */
	public void submitGroupCopyEndFihgtTask(Runnable task){
		executorService.submit(task);
	}
}
