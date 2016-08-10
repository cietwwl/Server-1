package com.rw.manager;

import java.util.ArrayList;
import java.util.HashMap;

import com.playerdata.Player;
import com.playerdata.fixEquip.exp.data.FixExpEquipDataItem;
import com.playerdata.fixEquip.norm.data.FixNormEquipDataItem;
import com.rw.fsutil.dao.cache.DataCacheFactory;
import com.rw.fsutil.dao.cache.trace.DataValueParser;
import com.rw.trace.DropRecordParser;
import com.rw.trace.EquipItemParser;
import com.rw.trace.FixExpEquipDataItemParser;
import com.rw.trace.FixNormEquipDataItemParser;
import com.rw.trace.InlayItemParser;
import com.rw.trace.ItemDataParser;
import com.rw.trace.MajorDataParser;
import com.rw.trace.RoleBaseInfoParser;
import com.rw.trace.SkillParser;
import com.rw.trace.TableCopyDataParser;
import com.rw.trace.TableUserHeroParser;
import com.rw.trace.UserGameDataParser;
import com.rwbase.dao.chat.pojo.UserPrivateChat;
import com.rwbase.dao.copypve.pojo.TableCopyData;
import com.rwbase.dao.dropitem.DropRecord;
import com.rwbase.dao.equipment.EquipItem;
import com.rwbase.dao.gameNotice.TableGameNotice;
import com.rwbase.dao.group.pojo.db.GroupLogData;
import com.rwbase.dao.guide.pojo.UserGuideProgress;
import com.rwbase.dao.guide.pojo.UserPlotProgress;
import com.rwbase.dao.hero.pojo.RoleBaseInfo;
import com.rwbase.dao.hero.pojo.TableUserHero;
import com.rwbase.dao.inlay.InlayItem;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.majorDatas.pojo.MajorData;
import com.rwbase.dao.serverData.ServerData;
import com.rwbase.dao.skill.pojo.Skill;
import com.rwbase.dao.user.UserGameData;
import com.rwbase.dao.user.UserIdCache;
import com.rwbase.dao.zone.TableZoneInfo;

public class DataCacheInitialization {

	public static void init() {
		Class[] classArray = { ServerData.class, UserIdCache.class, TableZoneInfo.class, TableGameNotice.class, Player.class, GroupLogData.class, UserPrivateChat.class, UserPlotProgress.class, UserGuideProgress.class, GroupLogData.class };
		ArrayList<String> ignoreList = new ArrayList<String>(classArray.length);
		for (int i = 0; i < classArray.length; i++) {
			ignoreList.add(classArray[i].getName());
		}
		HashMap<Class<?>, DataValueParser<?>> map = new HashMap<Class<?>, DataValueParser<?>>();
		map.put(ItemData.class, new ItemDataParser());
		map.put(DropRecord.class,new DropRecordParser());
		map.put(EquipItem.class, new EquipItemParser());
		map.put(FixExpEquipDataItem.class, new FixExpEquipDataItemParser());
		map.put(FixNormEquipDataItem.class, new FixNormEquipDataItemParser());
		map.put(InlayItem.class, new InlayItemParser());
		map.put(MajorData.class, new MajorDataParser());
		map.put(RoleBaseInfo.class, new RoleBaseInfoParser());
		map.put(Skill.class, new SkillParser());
		map.put(TableCopyData.class, new TableCopyDataParser());
		map.put(TableUserHero.class, new TableUserHeroParser());
		map.put(UserGameData.class, new UserGameDataParser());
		DataCacheFactory.init(ignoreList,map);
	}
}
