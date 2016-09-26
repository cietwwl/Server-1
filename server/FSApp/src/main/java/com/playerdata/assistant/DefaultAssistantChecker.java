package com.playerdata.assistant;

import com.playerdata.Player;
import com.rwbase.dao.assistant.cfg.AssistantCfg.AssistantEventID;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class DefaultAssistantChecker implements IAssistantCheck {

	/**
	 * 清空了额外参数param，方便重新计算
	 * @param player
	 * @return
	 */
	public AssistantEventID doCheck(Player player) {
		param = null;
		return null;
	}

	protected String param;
	@Override
	public String getExtraParam() {
		return param;
	}
	
	@Override
	public eOpenLevelType getOpenType(){
		//TODO 每个业务模块需要重写这个函数，才可以做统一的处理
		return null;
	}

}
