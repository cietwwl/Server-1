package com.bm.rank.groupCompetition.groupRank.groupRankStatic;

import java.util.ArrayList;
import java.util.List;

import com.bm.rank.groupCompetition.groupRank.GCompFightingItem;
import com.bm.rank.groupCompetition.groupRank.GCompFightingRankMgr;
import com.rw.fsutil.json.JSONArray;
import com.rw.fsutil.json.JSONException;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.GameWorldKey;

/**
 * 帮派战力排行榜的静态榜
 * @author aken
 */
public class GroupStaticRankMgr {
	
	private static GroupStaticRankMgr instance = new GroupStaticRankMgr();
	
	public static GroupStaticRankMgr getInstance(){
		return instance;
	}

	private int MAX_RANK_COUNT = 16;

	public void saveStaticRankData(){
		List<GCompFightingItem> rankItems = GCompFightingRankMgr.getFightingRankList(MAX_RANK_COUNT);
		if(rankItems.isEmpty()){
			return;
		}
		JSONArray array = new JSONArray();
		for(GCompFightingItem item : rankItems){
			array.put(JsonUtil.writeValue(item));
		}
		GameWorldFactory.getGameWorld().updateAttribute(GameWorldKey.GROUP_STATIC_RANK, array.toString());		
	}
	
	public List<GCompFightingItem> getStaticGroupRank(){
		List<GCompFightingItem> rankItems = new ArrayList<GCompFightingItem>();
		String jstring = GameWorldFactory.getGameWorld().getAttribute(GameWorldKey.GROUP_STATIC_RANK);
		try {
			JSONArray jsArray = new JSONArray(jstring);
			for(int i = 0, size = jsArray.length(); i < size; i++){
				rankItems.add(JsonUtil.readValue(jsArray.get(i).toString(), GCompFightingItem.class));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} 
		return rankItems;
	}
}