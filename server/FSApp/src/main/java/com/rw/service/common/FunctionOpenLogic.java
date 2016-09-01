package com.rw.service.common;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.common.RefParam;
import com.google.protobuf.ProtocolMessageEnum;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwbase.dao.openLevelLimit.pojo.CfgOpenLevelLimit;
import com.rwproto.MsgDef.Command;
import com.rwproto.RequestProtos.Request;

public class FunctionOpenLogic {
	private static FunctionOpenLogic instance = new FunctionOpenLogic();
	public static FunctionOpenLogic getInstance(){
		if (instance == null){
			instance = new FunctionOpenLogic();
		}
		return instance;
	}
	public boolean isOpen(ProtocolMessageEnum msgType, Request request,Player player){
		CfgOpenLevelLimitDAO helper = CfgOpenLevelLimitDAO.getInstance();
		Command cmd = request.getHeader().getCommand();
		List<CfgOpenLevelLimit> cfgLst = helper.getOpenCfg(cmd);
		if (cfgLst == null){
			return true;
		}
		
		eOpenLevelType openType = selectLevelType(msgType,request,player,cfgLst);
		if (openType == null){
			return true;
		}
		
		RefParam<String> outtip = new RefParam<String>();
		if (!helper.isOpen(openType, player,outtip)) {
			player.NotifyFunctionNotOpen(outtip.value);
			return false;
		}
		
		return true;
	}
	
	private eOpenLevelType selectLevelType(ProtocolMessageEnum msgType, Request request,Player player,List<CfgOpenLevelLimit> cfgLst){
		if (cfgLst == null){
			return null;
		}
		if (cfgLst.size() == 1){
			CfgOpenLevelLimit cfgOpenLevelLimit = cfgLst.get(0);
			if (StringUtils.isBlank(cfgOpenLevelLimit.getSubmoduleId())){
				return eOpenLevelType.getByOrder(cfgOpenLevelLimit.getType());
			}
		}
		return specialSelect(msgType,request,player,cfgLst);
	}
	
	/**
	 * 需要特殊处理的功能，都写在这里，特殊处理的功能需要配置非空的SubmoduleId，以示区别
	 * @param request
	 * @param player
	 * @param cfgLst 功能请求ID对应的配置（可能有多个）
	 * @return
	 */
	private eOpenLevelType specialSelect(ProtocolMessageEnum msgType, Request request,Player player,List<CfgOpenLevelLimit> cfgLst){
		String msgTypeStr = String.valueOf(msgType);
		for (CfgOpenLevelLimit cfg : cfgLst) {
			if (cfg.getSubmoduleId().equals(msgTypeStr)){
				return eOpenLevelType.getByOrder(cfg.getType());
			}
		}
		return null;
	}
}
