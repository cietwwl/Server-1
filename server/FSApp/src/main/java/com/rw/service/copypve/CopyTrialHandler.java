package com.rw.service.copypve;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.ByteString;
import com.playerdata.CopyDataMgr;
import com.playerdata.Player;
import com.playerdata.readonly.CopyDataIF;
import com.playerdata.readonly.CopyInfoCfgIF;
import com.playerdata.readonly.CopyLevelRecordIF;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.common.enu.EPrivilegeDef;
import com.rwbase.dao.copypve.CopyInfoCfgDAO;
import com.rwbase.dao.copypve.CopyType;
import com.rwbase.dao.copypve.TableCopyDataDAO;
import com.rwbase.dao.copypve.pojo.CopyData;
import com.rwbase.dao.copypve.pojo.CopyInfoCfg;
import com.rwbase.dao.copypve.pojo.TableCopyData;
import com.rwproto.CopyTrialServiceProtos.MsgTrialRequest;
import com.rwproto.CopyTrialServiceProtos.MsgTrialResponse;
import com.rwproto.CopyTrialServiceProtos.TrialData;
import com.rwproto.CopyTrialServiceProtos.eTrialResultType;

public class CopyTrialHandler {

	private static CopyTrialHandler instance;

	private CopyTrialHandler() {
	}

	public static CopyTrialHandler getInstance() {
		if (instance == null) {
			instance = new CopyTrialHandler();
		}
		return instance;
	}

	public ByteString copyTrial(Player player, MsgTrialRequest msgTrialRequest) {
		MsgTrialResponse.Builder response = MsgTrialResponse.newBuilder();
		response.setTrialType(msgTrialRequest.getTrialType());
		// 获取副本，聚宝之地，炼气出谷。当前开放的关卡配置
		List<CopyInfoCfgIF> infoCfgList = player.getCopyDataMgr().getTodayInfoCfg(CopyType.COPY_TYPE_TRIAL_LQSG);
		List<CopyInfoCfgIF> temps = player.getCopyDataMgr().getTodayInfoCfg(CopyType.COPY_TYPE_TRIAL_JBZD);
		infoCfgList.addAll(temps);
		if (infoCfgList.size() == 0) {
			System.out.println("can not find cfg file...");
			response.setTrialResultType(eTrialResultType.FAIL);
			return response.build().toByteString();
		}

		for (CopyInfoCfgIF infoCfg : infoCfgList) {
			CopyDataIF data = player.getCopyDataMgr().getByInfoId(infoCfg.getId());
			if (data == null) {
				System.out.println("CopyData is null...");
				response.setTrialResultType(eTrialResultType.FAIL);
				return response.build().toByteString();
			}
			// 根据关卡配置获取副本通关数据
			CopyLevelRecordIF copyRecord = player.getCopyRecordMgr().getLevelRecord(infoCfg.getId());
			TrialData.Builder trialData = TrialData.newBuilder();
			trialData.setInfoId(infoCfg.getId());
			trialData.setCopyCount(data.getCopyCount());
			trialData.setResetCount(data.getResetCount());
			trialData.setPassStar(0);
			if (copyRecord != null) {
				trialData.setPassStar(copyRecord.getPassStar());

			}

			response.addTrialData(trialData);
		}

		response.setTrialResultType(eTrialResultType.SUCCESS);
		return response.build().toByteString();
	}

	public ByteString copyCelestial(Player player, MsgTrialRequest msgTrialRequest)
	{
		MsgTrialResponse.Builder response = MsgTrialResponse.newBuilder().setTrialType(msgTrialRequest.getTrialType());
		response.setTrialType(msgTrialRequest.getTrialType());

		// 获取副本，生存幻镜。当前开放的关卡配置
		List<CopyInfoCfgIF> infoCfgList = player.getCopyDataMgr().getTodayInfoCfgList(CopyType.COPY_TYPE_CELESTIAL);// CopyDataMgr.getTodayInfoCfgList(CopyType.COPY_TYPE_CELESTIAL);
																													// //CopyDataMgr.getTodayInfoCfg(CopyType.COPY_TYPE_CELESTIAL);
		if (infoCfgList.size() == 0) 
		{
			System.out.println("can not find cfg file...");
			response.setTrialResultType(eTrialResultType.FAIL);
			return response.build().toByteString();
		}
		// infoCfgList.add(0, infoCfgList1.get(0));
		int dayOfWeek = CopyDataMgr.getDayOfWeek();
		for (CopyInfoCfgIF infoCfg : infoCfgList)
		{
			TrialData.Builder trialData = TrialData.newBuilder();
			// TODO 需要再优化，效率比分割好
			// 判断
			if (!infoCfg.getTime().contains(String.valueOf(dayOfWeek))) {
				trialData.setInfoId(infoCfg.getId());
				trialData.setCopyCount(0);
				trialData.setResetCount(0);
				trialData.setPassStar(-1);
				response.addTrialData(trialData);
				continue;
			}
			CopyData data = player.getCopyDataMgr().getByInfoId(infoCfg.getId());
			long lastResetTime = data.getLastFreeResetTime();
			if (DateUtils.isResetTime(0, 0, 0, lastResetTime)) {
				data.setCopyCount(infoCfg.getCount());
//				int resetCount = player.getVipMgr().GetMaxPrivilege(EPrivilegeDef.COPY_CELESTAL);
//				data.setResetCount(resetCount);
				data.setLastFreeResetTime(System.currentTimeMillis());
			}
			// 根据关卡配置获取副本通关数据
			CopyLevelRecordIF copyRecord = player.getCopyRecordMgr().getLevelRecord(infoCfg.getId());
			trialData.setInfoId(infoCfg.getId());
			trialData.setCopyCount(data.getCopyCount());
			trialData.setResetCount(data.getResetCount());
			if (copyRecord != null) {
				trialData.setPassStar(copyRecord.getPassStar());
			} else {
				trialData.setPassStar(0);
			}
			response.addTrialData(trialData);
		}

		response.setTrialResultType(eTrialResultType.SUCCESS);
		return response.build().toByteString();
	}

	// 重置pve副本数据
	public ByteString resetTrial(Player player, MsgTrialRequest msgTrialRequest) {
		MsgTrialResponse.Builder response = MsgTrialResponse.newBuilder();
		response.setTrialType(msgTrialRequest.getTrialType());
		int infoId = msgTrialRequest.getInfoId();
		CopyInfoCfg infoCfg = (CopyInfoCfg) CopyInfoCfgDAO.getInstance().getCfgById(String.valueOf(infoId));
		if (infoCfg == null) {
			System.out.println("can not find cfg file...");
			response.setTrialResultType(eTrialResultType.FAIL);
			return response.build().toByteString();
		}

		CopyDataIF data = player.getCopyDataMgr().resetCopyCount(infoId,infoCfg.getType());
		
		if (data == null)
		{
			response.setTrialResultType(eTrialResultType.FAIL);
			return response.build().toByteString();
		}
		TrialData.Builder trialData = TrialData.newBuilder();
		trialData.setInfoId(infoId);
		trialData.setCopyCount(data.getCopyCount());
		trialData.setResetCount(data.getResetCount());
		response.addTrialData(trialData);

		response.setTrialResultType(eTrialResultType.SUCCESS);
		return response.build().toByteString();
	}

}
