package com.playerdata.mgcsecret;

import com.playerdata.Player;
import com.playerdata.mgcsecret.data.MagicChapterInfoHolder;
import com.playerdata.mgcsecret.data.UserMagicSecretHolder;
import com.rwproto.MagicSecretProto.msResultType;

// 有积分改变的时候通知排行榜

public class MagicSecretMgr{
	private MagicChapterInfoHolder mChapterHolder;
	private UserMagicSecretHolder userMSHolder;
	
	private Player m_pPlayer = null;
	private String userId;
	
	// 初始化
	public void init(Player playerP) {
		m_pPlayer = playerP;
		this.userId = playerP.getUserId();
		mChapterHolder = MagicChapterInfoHolder.getInstance();
		userMSHolder = new UserMagicSecretHolder(userId);
	}

	public msResultType enterMSFight() {

		return msResultType.SUCCESS;
	}
	
	
}