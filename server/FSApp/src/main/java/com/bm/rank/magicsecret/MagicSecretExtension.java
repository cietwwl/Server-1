package com.bm.rank.magicsecret;

import com.bm.rank.RankingJacksonExtension;
import com.playerdata.Player;
import com.playerdata.mgcsecret.data.MSScoreDataItem;
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
		Player player = (Player)param;
		MSScoreDataItem toData = new MSScoreDataItem();
		toData.setUserId(player.getUserId());
		toData.setHeadImage(player.getHeadImage());
		toData.setJob(player.getCareer());
		toData.setLevel(player.getLevel());
		toData.setUserName(player.getUserName());
		toData.setTitle("NONE");
		return toData;
	}
}
