package com.playerdata.assistant;

import com.playerdata.Player;
import com.rwbase.dao.assistant.cfg.AssistantCfg.AssistantEventID;

public interface IAssistantCheck {

	public AssistantEventID doCheck(Player player);
	/**
	 * 当doCheck返回非空值的时候调用
	 * @return 返回空字符串表示没有额外参数需要传递
	 */
	public String getExtraParam();
}
