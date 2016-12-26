package com.gm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.gm.task.GMRuntimeUpdate;
import com.gm.task.GMWorldBossState;
import com.gm.task.GmAddVipExp;
import com.gm.task.GmBlockPlayer;
import com.gm.task.GmBlockRelease;
import com.gm.task.GmChargeSwitch;
import com.gm.task.GmChatBanPlayer;
import com.gm.task.GmChatBanRelease;
import com.gm.task.GmCheckBag;
import com.gm.task.GmCheckDataOpProgress;
import com.gm.task.GmDeleteBag;
import com.gm.task.GmDeleteGameNotice;
import com.gm.task.GmEditCloseTips;
import com.gm.task.GmEditGameNotice;
import com.gm.task.GmEditGmNotice;
import com.gm.task.GmEditLevel;
import com.gm.task.GmEditPlatformNotice;
import com.gm.task.GmEmailAll;
import com.gm.task.GmEmailSingleCheck;
import com.gm.task.GmEmailSingleDelete;
import com.gm.task.GmEmailSingleSend;
import com.gm.task.GmEmailWhiteList;
import com.gm.task.GmExecuteGM;
import com.gm.task.GmExecuteGMCommand;
import com.gm.task.GmFindDaoistList;
import com.gm.task.GmFindFishionSwingPetList;
import com.gm.task.GmFindHeroEquipList;
import com.gm.task.GmFindHeroList;
import com.gm.task.GmFindHeroNormalAndExpEquipList;
import com.gm.task.GmFindHeroSkillList;
import com.gm.task.GmFindMagicList;
import com.gm.task.GmForClassLoad;
import com.gm.task.GmGetRankList;
import com.gm.task.GmHotUpdate;
import com.gm.task.GmKickOffPlayer;
import com.gm.task.GmRemoteMsgSenderState;
import com.gm.task.GmMessageServiceRemoved;
import com.gm.task.GmNotifyGenerateGiftPackage;
import com.gm.task.GmOnlineCount;
import com.gm.task.GmOnlineLimitModify;
import com.gm.task.GmOpCoin;
import com.gm.task.GmOpExp;
import com.gm.task.GmOpGold;
import com.gm.task.GmQueryGmNotice;
import com.gm.task.GmQueryGroupInfo;
import com.gm.task.GmQueryPlayerRanking;
import com.gm.task.GmQueryGroupRankingInfo;
import com.gm.task.GmReloadCfg;
import com.gm.task.GmRemoveGmNotice;
import com.gm.task.GmResponsePlayerQuestion;
import com.gm.task.GmSavePlayer;
import com.gm.task.GmServerInfo;
import com.gm.task.GmServerStatus;
import com.gm.task.GmServerSwitch;
import com.gm.task.GmStartRobotCreation;
import com.gm.task.GmSwitchBIGm;
import com.gm.task.GmUpdateCacheSwitch;
import com.gm.task.GmUserDetailInfo;
import com.gm.task.GmUserInfo;
import com.gm.task.GmViewEmailList;
import com.gm.task.GmViewEquipments;
import com.gm.task.GmViewFriends;
import com.gm.task.GmViewGameNotice;
import com.gm.task.GmViewGroupMember;
import com.gm.task.GmViewPlatformNotice;
import com.gm.task.GmWhiteListModify;
import com.gm.task.GmWhiteListSwitch;
import com.gm.task.IGmTask;
import com.gm.util.SocketHelper;
import com.log.GameLog;
import com.log.LogModule;
import com.rw.fsutil.log.GmLog;

public class GmHandler {

	private String gmAccount = "gm";

	private String gmPassword = "passwd";

	private Map<Integer, IGmTask> taskMap = new HashMap<Integer, IGmTask>();

	public GmHandler(String account, String password) {
		this.gmAccount = account;
		this.gmPassword = password;

		// 运维功能
		taskMap.put(1001, new GmServerSwitch());
		taskMap.put(1002, new GmKickOffPlayer());
		taskMap.put(1003, new GmSavePlayer());
		taskMap.put(1004, new GmOnlineCount());
		taskMap.put(1005, new GmWhiteListSwitch());
		taskMap.put(1006, new GmWhiteListModify());
		taskMap.put(1007, new GmOnlineLimitModify());
		taskMap.put(1008, new GmChargeSwitch());
		taskMap.put(1009, new GmServerInfo());
		taskMap.put(1010, new GmCheckDataOpProgress());
		taskMap.put(1011, new GmHotUpdate());
		taskMap.put(1012, new GMRuntimeUpdate());
		// 机器人
		taskMap.put(3001, new GmStartRobotCreation());
		// 开启和关闭游戏内的gm指令
		taskMap.put(4001, new GmSwitchBIGm());
		// for class load by classname
		taskMap.put(888888, new GmForClassLoad());

		// 运营功能
		taskMap.put(20001, new GmUserInfo());
		taskMap.put(20002, new GmServerStatus());
		taskMap.put(20003, new GmEditPlatformNotice());
		taskMap.put(20004, new GmViewPlatformNotice());
		taskMap.put(20005, new GmEditGameNotice());
		taskMap.put(20006, new GmViewGameNotice());
		taskMap.put(20007, new GmDeleteGameNotice());
		taskMap.put(20008, new GmEditGmNotice());
		taskMap.put(20009, new GmQueryGmNotice());
		taskMap.put(20010, new GmRemoveGmNotice());
		taskMap.put(20014, new GmEmailWhiteList());
		taskMap.put(20015, new GmEmailAll());
		taskMap.put(20016, new GmEmailSingleSend());
		taskMap.put(20019, new GmResponsePlayerQuestion());
		taskMap.put(20020, new GmExecuteGM());
		taskMap.put(20021, new GmEmailSingleCheck());
		taskMap.put(20022, new GmEmailSingleDelete());

		taskMap.put(20023, new GmBlockPlayer());
		taskMap.put(20024, new GmBlockRelease());
		taskMap.put(20025, new GmChatBanPlayer());
		taskMap.put(20026, new GmChatBanRelease());
		taskMap.put(20027, new GmOpGold());
		taskMap.put(20028, new GmOpCoin());
		taskMap.put(20029, new GmOpExp());
		taskMap.put(20030, new GmUserDetailInfo());
		taskMap.put(20032, new GmViewFriends());

		taskMap.put(20035, new GmCheckBag());
		taskMap.put(20036, new GmDeleteBag());
		taskMap.put(20037, new GmViewEquipments());
		taskMap.put(20038, new GmNotifyGenerateGiftPackage());
		taskMap.put(20040, new GmViewEmailList());
		taskMap.put(20055, new GmFindHeroList());
		taskMap.put(20056, new GmFindMagicList());
		taskMap.put(20057, new GmFindDaoistList());
		taskMap.put(20058, new GmFindFishionSwingPetList());
		taskMap.put(20059, new GmFindHeroSkillList());
		taskMap.put(20060, new GmFindHeroEquipList());
		taskMap.put(20061, new GmFindHeroNormalAndExpEquipList());
		taskMap.put(20071, new GmQueryPlayerRanking());
		taskMap.put(20072, new GmQueryGroupRankingInfo());
		taskMap.put(20073, new GmQueryGroupInfo());
		taskMap.put(20074, new GmViewGroupMember());
		taskMap.put(20075, new GmEditLevel());
		// 移除消息处理器(屏蔽指定功能消息入口)
		taskMap.put(20076, new GmMessageServiceRemoved());
		taskMap.put(20077, new GmRemoteMsgSenderState());
		//GM开启世界boss
		taskMap.put(20078, new GMWorldBossState());
		//VIP经验
		taskMap.put(20079, new GmAddVipExp());

		taskMap.put(99999, new GmExecuteGMCommand());

		// 获取各种排名的用户id列表
		taskMap.put(77777, new GmGetRankList());
		// 修改服务器关闭提示语
		taskMap.put(99998, new GmEditCloseTips());
		// 更新缓存记录开关
		taskMap.put(99997, new GmUpdateCacheSwitch());
		// 重新加载配置表
		taskMap.put(99996, new GmReloadCfg());
	}

	public void handle(Socket socket) {
		try {
			// 读取客户端数据
			DataOutputStream output = new DataOutputStream(socket.getOutputStream());
			DataInputStream input = new DataInputStream(socket.getInputStream());

			GmRequest gmRequest = SocketHelper.read(input, GmRequest.class);
			GmResponse gmResponse = null;
			if (gmRequest == null) {
				gmResponse = new GmResponse();
				gmResponse.setStatus(2);
				gmResponse.setCount(0);
				Map<String, Object> resultMap = new HashMap<String, Object>();
				resultMap.put("value", "Can not find the gm command.");
				gmResponse.addResult(resultMap);
			} else {
				if (unAuthorize(gmRequest)) {

					gmResponse = new GmResponse();
					gmResponse.setStatus(1);
					gmResponse.setCount(0);
					Map<String, Object> resultMap = new HashMap<String, Object>();
					resultMap.put("value", "account unauthorized.");
					gmResponse.addResult(resultMap);
					GameLog.info(LogModule.GM.getName(), "gmId", "GmHandler[handle] 账户或密码不对", null);

				} else if (gmRequest != null) {
					IGmTask gmTask = taskMap.get(gmRequest.getOpType());
					if (gmTask != null) {
						GmLog.info("GmHandler[handle] GmTask found:" + gmTask.getClass() + " optype:" + gmRequest.getOpType());
						gmResponse = gmTask.doTask(gmRequest);
					} else {
						GmLog.info("GmHandler[handle] GmTask not found," + " optype:" + gmRequest.getOpType());

					}
				}
			}

			SocketHelper.write(output, gmResponse);
			output.flush();
			GameLog.info(LogModule.GM.getName(), "gmId", "GmHandler[handle] 消息发送成功", null);

			output.close();
			input.close();

		} catch (Exception e) {
			GameLog.error(LogModule.GM.getName(), "gmId", "GmHandler[handle] gm处理异常", e);
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (Exception e) {
					socket = null;
					GameLog.error(LogModule.GM.getName(), "gmId", "GmHandler[handle] finally socket关闭异常", e);
				}
			}
		}
	}

	private boolean unAuthorize(GmRequest gmRequest) {
		String account = gmRequest.getAccount();
		String password = gmRequest.getPassword();
		return !StringUtils.equals(gmAccount, account) || !StringUtils.equals(gmPassword, password);
	}

	public void setGmAccount(String gmAccount) {
		this.gmAccount = gmAccount;
	}

	public void setGmPassword(String gmPassword) {
		this.gmPassword = gmPassword;
	}

}
