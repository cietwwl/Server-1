package com.playerdata;

import com.rwbase.dao.magicsecret.MagicChapterInfoHolder;
import com.rwbase.dao.magicsecret.UserMagicSecretHolder;

// 有积分改变的时候通知排行榜

public class MagicSecretMgr{
	private MagicChapterInfoHolder mChapterHolder;
	private UserMagicSecretHolder userMSHolder;
	
	Player m_pPlayer = null;
	String userId;
	
	// 初始化
	public void init(Player playerP) {
		m_pPlayer = playerP;
		this.userId = playerP.getUserId();
		mChapterHolder = new MagicChapterInfoHolder();
		userMSHolder = new UserMagicSecretHolder(userId);
	}
}