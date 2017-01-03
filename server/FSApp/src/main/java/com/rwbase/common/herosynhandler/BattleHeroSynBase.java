package com.rwbase.common.herosynhandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.embattle.EmbattleHeroPosition;
import com.playerdata.hero.core.FSHeroHolder;
import com.playerdata.hero.core.FSHeroMgr;

public abstract class BattleHeroSynBase {

	protected void synHeros(Player player, List<EmbattleHeroPosition> posList) {
		Set<String> heroIds;
		boolean containsMain = false;
		if (posList.size() > 0) {
			heroIds = new HashSet<String>(posList.size(), 1.5f);
			for (EmbattleHeroPosition pos : posList) {
				String heroId = pos.getId();
				if (heroId != null && (heroId = heroId.trim()).length() > 0) {
					heroIds.add(heroId);
					if (heroId.equals(player.getUserId())) {
						containsMain = true;
					}
				}
			}
		} else {
			heroIds = Collections.emptySet();
		}
		List<Hero> allHeros;
		if (heroIds.size() > 0) {
			allHeros = FSHeroMgr.getInstance().getHeros(player, new ArrayList<String>(heroIds));
			if (!containsMain) {
				allHeros.add(FSHeroMgr.getInstance().getMainRoleHero(player));
			}
//			System.err.println("==========>>>>>>>>>> 下发英雄数据，列表：" + heroIds + " <<<<<<<<<<===");
		} else {
			allHeros = FSHeroMgr.getInstance().getAllHeros(player, null);
			containsMain = true;
//			System.err.println("==========>>>>>>>>>> 下发英雄数据，列表：" + heroIds + "，数量=0，改为下发所有英雄数据 <<<<<<<<<<===");
		}
		Hero h;
		for (int i = 0; i < allHeros.size(); i++) {
			h = allHeros.get(i);
			FSHeroHolder.getInstance().synAttributes(player, h, -1);
		}
	}
}
