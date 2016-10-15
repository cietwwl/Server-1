package com.rw.handler.groupCompetition.util;

import java.util.ArrayList;
import java.util.List;

import com.rw.Client;
import com.rw.handler.hero.TableUserHero;

public class GCompUtil {

	public static List<String> getTeamHeroIds(Client client) {
		TableUserHero tableUserHero = client.getUserHerosDataHolder().getTableUserHero();
		List<String> ownHeroIds = tableUserHero.getHeroIds();
		List<String> heroIds = new ArrayList<String>();
		String userId = client.getUserId();
		String heroId;
		heroIds.add(client.getUserId());
		for (int i = 0, size = ownHeroIds.size(); i < size; i++) {
			heroId = ownHeroIds.get(i);
			if(heroId.equals(userId)) {
				continue;
			}
			heroIds.add(heroId);
			if(heroIds.size() == 5) {
				break;
			}
		}
		return heroIds;
	}
}
