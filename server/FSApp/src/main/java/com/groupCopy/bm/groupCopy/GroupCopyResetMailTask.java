package com.groupCopy.bm.groupCopy;

import com.playerdata.Player;
import com.rwbase.gameworld.PlayerTask;

/**
 * 重置副本邮件时效
 * @author Alex
 * 2016年7月5日 下午2:22:48
 */
public class GroupCopyResetMailTask implements PlayerTask{

	private String recieverID;
	private String roleName;
	private String chaterName;
	
	
	
	public GroupCopyResetMailTask(String recieverID, String roleName,
			String chaterName) {
		this.recieverID = recieverID;
		this.roleName = roleName;
		this.chaterName = chaterName;
	}



	@Override
	public void run(Player e) {
		GroupCopyMailHelper.getInstance().sendResetGroupCopyMail(recieverID, roleName, chaterName);
	}

}
