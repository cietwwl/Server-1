package com.bm.rank.angelarray;

import com.bm.rank.RankingJacksonExtension;
import com.rw.fsutil.ranking.RankingEntry;
import com.rwbase.dao.arena.pojo.TableArenaData;

/*
 * @author HC
 * @date 2016年3月18日 下午4:04:47
 * @Description 万仙阵扩展数据
 */
public class AngelArrayExtension extends RankingJacksonExtension<AngelArrayComparable, AngelArrayAttribute> {

	public AngelArrayExtension() {
		super(AngelArrayComparable.class, AngelArrayAttribute.class);
	}

	@Override
	public void notifyEntryEvicted(RankingEntry<AngelArrayComparable, AngelArrayAttribute> entry) {
	}

	@Override
	public <P> AngelArrayAttribute newEntryExtension(String key, P customParam) {
		TableArenaData arenaData = (TableArenaData) customParam;
		AngelArrayAttribute att = new AngelArrayAttribute();
		att.setUserId(arenaData.getUserId());
		return att;
	}
}