package com.bm.rank.secret;
import com.bm.rank.RankingJacksonExtension;
import com.rw.fsutil.ranking.RankingEntry;
import com.rwbase.dao.guildSecretArea.SecretAreaInfo;

public class SecretRankExtension extends RankingJacksonExtension<SecretInfoComp, SecretExtAttribute>{
	public SecretRankExtension(){
		super(SecretInfoComp.class, SecretExtAttribute.class);
	}
	
	@Override
	public void notifyEntryEvicted(RankingEntry<SecretInfoComp, SecretExtAttribute> entry) {
	}

	@Override
	public SecretExtAttribute newEntryExtension(String key, Object param) {
		SecretInfoComp areaInfoComp = (SecretInfoComp)param;
		SecretExtAttribute extAttr = new SecretExtAttribute();
		extAttr.setSecretId(areaInfoComp.getSecretId());
		return extAttr;
	}
	
}
