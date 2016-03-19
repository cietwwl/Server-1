package com.bm.rank.anglearray;

import com.bm.rank.RankingJacksonExtension;
import com.rw.fsutil.ranking.RankingEntry;
import com.rwbase.dao.arena.pojo.TableArenaData;

/*
 * @author HC
 * @date 2016年3月18日 下午4:04:47
 * @Description 万仙阵扩展数据
 */
public class AngleArrayExtension extends RankingJacksonExtension<AngleArrayComparable, AngleArrayAttribute> {

	public AngleArrayExtension() {
		super(AngleArrayComparable.class, AngleArrayAttribute.class);
	}

	@Override
	public void notifyEntryEvicted(RankingEntry<AngleArrayComparable, AngleArrayAttribute> entry) {
	}

	@Override
	public <P> AngleArrayAttribute newEntryExtension(String key, P customParam) {
		TableArenaData arenaData = (TableArenaData) customParam;
		AngleArrayAttribute att = new AngleArrayAttribute();
		att.setUserId(arenaData.getUserId());
		return att;
	}
}