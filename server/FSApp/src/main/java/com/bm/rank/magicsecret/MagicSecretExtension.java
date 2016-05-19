package com.bm.rank.magicsecret;

import com.bm.rank.RankingJacksonExtension;
import com.playerdata.mgcsecret.data.MSScoreDataItem;
import com.playerdata.mgcsecret.data.UserMagicSecretData;
import com.rw.fsutil.ranking.RankingEntry;

public class MagicSecretExtension extends RankingJacksonExtension<MagicSecretComparable, MSScoreDataItem>{

	public MagicSecretExtension() {
		super(MagicSecretComparable.class, MSScoreDataItem.class);
	}

	@Override
	public void notifyEntryEvicted(RankingEntry<MagicSecretComparable, MSScoreDataItem> entry) {
	}

	@Override
	public <P> MSScoreDataItem newEntryExtension(String key, P param) {
		if(param instanceof MSScoreDataItem){
			return (MSScoreDataItem)param;
		}
		UserMagicSecretData magicData = (UserMagicSecretData)param;
		MSScoreDataItem toData = new MSScoreDataItem();
		toData.setUserId(magicData.getUserId());
		toData.setHistoryScore(magicData.getHistoryScore());
		toData.setTodayScore(magicData.getTodayScore());
		toData.setRecentScoreTime(magicData.getRecentScoreTime());
		return toData;
	}
}
