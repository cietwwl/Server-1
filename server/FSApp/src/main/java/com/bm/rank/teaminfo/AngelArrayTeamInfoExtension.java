package com.bm.rank.teaminfo;

import com.bm.rank.RankingJacksonExtension;
import com.bm.rank.angelarray.AngelArrayComparable;
import com.rw.fsutil.ranking.RankingEntry;

/*
 * @author HC
 * @date 2016年4月18日 下午2:15:39
 * @Description 万仙阵阵容排行榜记录信息
 */
public class AngelArrayTeamInfoExtension extends RankingJacksonExtension<AngelArrayComparable, AngelArrayTeamInfoAttribute> {

	public AngelArrayTeamInfoExtension() {
		super(AngelArrayComparable.class, AngelArrayTeamInfoAttribute.class);
	}

	@Override
	public void notifyEntryEvicted(RankingEntry<AngelArrayComparable, AngelArrayTeamInfoAttribute> entry) {
	}

	@Override
	public <P> AngelArrayTeamInfoAttribute newEntryExtension(String key, P customParam) {
		if (customParam instanceof AngelArrayTeamInfoAttribute) {
			return (AngelArrayTeamInfoAttribute) customParam;
		}

		return new AngelArrayTeamInfoAttribute();
	}
}