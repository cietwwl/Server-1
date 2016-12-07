package com.playerdata.groupcompetition.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.bm.group.GroupBM;
import com.bm.rank.groupCompetition.groupRank.GCompFightingItem;
import com.bm.rank.groupCompetition.groupRank.GCompFightingRankMgr;
import com.playerdata.groupcompetition.GroupCompetitionMgr;
import com.playerdata.groupcompetition.data.IGCGroup;
import com.playerdata.groupcompetition.holder.GCompDetailInfoMgr;
import com.playerdata.groupcompetition.stageimpl.GCGroup;
import com.playerdata.groupcompetition.stageimpl.GCompAgainst;
import com.rw.fsutil.common.IReadOnlyPair;
import com.rw.service.role.MainMsgHandler;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.cfg.GroupLevelCfg;
import com.rwbase.dao.group.pojo.cfg.dao.GroupLevelCfgDAO;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;
import com.rwbase.dao.groupcompetition.GroupCompetitionStageCfgDAO;
import com.rwbase.dao.groupcompetition.GroupCompetitionStageControlCfgDAO;
import com.rwbase.dao.groupcompetition.pojo.GroupCompetitionStageCfg;
import com.rwbase.dao.groupcompetition.pojo.GroupCompetitionStageControlCfg;

public class GCompUtil {

	private static final java.text.SimpleDateFormat _dateFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private static final Random matchingTimeoutRandom = new Random();

	/**
	 * 
	 * <pre>
	 * 计算赛事类型的开始索引
	 * 索引的计算规则如下图：
	 * 
	 * 帮派A------                      ------帮派E
	 *         1  |------        ------|  3
	 * 帮派B------       |   7  |       ------帮派F
	 *               5   |------|   6
	 * 帮派C------       |      |       ------帮派G
	 *         2  |------        ------|  4
	 * 帮派D------                      ------帮派H
	 * 
	 * 最初的索引统一从1开始。
	 * 当比赛是从8强开始，左上角为1号比赛，左下角为2号比赛，右上角为3号比赛，右下角为4号比赛
	 * 当比赛是从16强开始，左边从上至下依次为1、2、3、4，右边从上至下依次为5、6、7、8
	 * 晋级之后的索引计算，也是按照从左边开始，按照初赛是哪一种类型累加。
	 * 例如，初赛是8强，则晋级之后索引从5开始计算，16强则是从9开始
	 * </pre>
	 * 
	 * @param eventsType
	 * @return
	 */
	public static int computeBeginIndex(GCEventsType eventsType) {
		GCEventsType firstType = GroupCompetitionMgr.getInstance().getFisrtTypeOfCurrent();
		int usedIndex = 0;
		switch (eventsType) {
		case TOP_16:
			return 1; // 初赛从1开始
		case TOP_8:
			// 视乎是不是从16强开始而处理
			break;
		case QUATER:
			usedIndex = 4; // 最少会经历1/4 共4场比赛
			break;
		case FINAL:
			usedIndex = 4 + 2; // 最少会经历1/4和1/2决赛，共六场比赛
			break;
		}
		if (firstType == GCEventsType.TOP_16) {
			usedIndex += 8;
		}
		return usedIndex + 1;
	}

/**
	 * 
	 * 计算帮派战的开始时间
	 * 
	 * @param type 开始的时间类型，这个用于从{@link GroupCompetitionStageControlCfgDAO#getByType(int))
	 * @param relativeTime 以这个相对时间为基准进行偏移
	 * @return
	 */
	public static long calculateGroupCompetitionStartTime(GCompStartType type, long relativeTime) {
		Calendar instance = Calendar.getInstance();
		if (relativeTime > 0) {
			instance.setTimeInMillis(relativeTime);
		}
		GroupCompetitionStageControlCfg cfg = GroupCompetitionStageControlCfgDAO.getInstance().getByType(type.sign);
		GroupCompetitionStageCfg firstStage = GroupCompetitionStageCfgDAO.getInstance().getCfgById(String.valueOf(cfg.getStageDetailList().get(0)));
		IReadOnlyPair<Integer, Integer> time = firstStage.getStartTimeInfo();
		if (cfg.getStartWeeks() > 0) {
			instance.add(Calendar.WEEK_OF_YEAR, cfg.getStartWeeks());
			instance.set(Calendar.DAY_OF_WEEK, cfg.getStartDayOfWeek());
		} else {
			instance.set(Calendar.DAY_OF_WEEK, cfg.getStartDayOfWeek());
		}
		instance.set(Calendar.HOUR_OF_DAY, time.getT1());
		if (time.getT2() >= 0) {
			instance.set(Calendar.MINUTE, time.getT2());
		}
		instance.set(Calendar.SECOND, 0);
		if (instance.getTimeInMillis() < System.currentTimeMillis()) {
			if (type == GCompStartType.SERVER_TIME_OFFSET) {
				instance.setTimeInMillis(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1)); // 如果是服务器开服时间偏移，则直接偏移一分钟
			} else {
				instance.add(Calendar.WEEK_OF_YEAR, 1);
			}
		}
		return instance.getTimeInMillis();
	}

	public static long getNearTimeMillis(int hour, int minute, long relativeMillis) {
		Calendar instance = Calendar.getInstance();
		instance.setTimeInMillis(relativeMillis);
		int hourOfDay = 0;
		if ((hourOfDay = instance.get(Calendar.HOUR_OF_DAY)) > hour || (hourOfDay == hour && instance.get(Calendar.MINUTE) >= minute)) {
			instance.add(Calendar.DAY_OF_YEAR, 1);
		}
		instance.set(Calendar.HOUR_OF_DAY, hour);
		instance.set(Calendar.MINUTE, minute);
		instance.set(Calendar.SECOND, 0);
		return instance.getTimeInMillis();
	}

	public static long calculateEndTimeOfStage(String stageCfgId) {
		GroupCompetitionStageCfgDAO stageCfgDAO = GroupCompetitionStageCfgDAO.getInstance();
		GroupCompetitionStageCfg cfg = stageCfgDAO.getCfgById(String.valueOf(stageCfgId));
		Calendar currentDateTime = Calendar.getInstance();
		IReadOnlyPair<Integer, Integer> endTimeInfo = cfg.getEndTimeInfo();
		currentDateTime.add(Calendar.DAY_OF_YEAR, cfg.getLastDays());
		currentDateTime.set(Calendar.HOUR_OF_DAY, endTimeInfo.getT1());
		currentDateTime.set(Calendar.MINUTE, endTimeInfo.getT2());
		currentDateTime.set(Calendar.SECOND, 0);
		if (cfg.getStageType() == GCompStageType.REST.sign) {
			// 休整期的结束时间计算比较特别
			GroupCompetitionStageControlCfg controlCfg = GroupCompetitionStageControlCfgDAO.getInstance().getByType(GCompStartType.NUTRAL_TIME_OFFSET.sign);
			GroupCompetitionStageCfg nextFirst = stageCfgDAO.getCfgById(String.valueOf(controlCfg.getStageDetailList().get(0)));
			int startDay = controlCfg.getStartDayOfWeek();
			if (startDay != currentDateTime.get(Calendar.DAY_OF_WEEK)) {
				currentDateTime.set(Calendar.DAY_OF_WEEK, startDay);
			}
			endTimeInfo = nextFirst.getStartTimeInfo();
			currentDateTime.set(Calendar.HOUR_OF_DAY, endTimeInfo.getT1());
			currentDateTime.set(Calendar.MINUTE, endTimeInfo.getT2());
			currentDateTime.add(Calendar.SECOND, -30); // 下个开始前1秒结束
		}
		return currentDateTime.getTimeInMillis();
	}

	public static void sendMarquee(String msg) {
		MainMsgHandler.getInstance().sendPmdNotId(msg);
	}

	public static List<IGCGroup> getAllGroups(List<GCompAgainst> againstsList, Comparator<IGCGroup> comparator) {
		List<IGCGroup> allGroups = new ArrayList<IGCGroup>(againstsList.size() * 2);
		for (GCompAgainst against : againstsList) {
			if (against.getGroupA().getGroupId().length() > 0) {
				allGroups.add(against.getGroupA());
			}
			if (against.getGroupB().getGroupId().length() > 0) {
				allGroups.add(against.getGroupB());
			}
		}
		if (comparator != null) {
			Collections.sort(allGroups, comparator);
		}
		return allGroups;
	}

	/**
	 * 
	 * @param msg
	 * @param args
	 */
	public static void log(String msg, Object... args) {
		System.err.println(_dateFormatter.format(new java.util.Date()) + " " + MessageFormatter.arrayFormat(msg, args));
	}

	public static String format(String msg, Object... args) {
		return MessageFormatter.arrayFormat(msg, args);
	}

	public static int getMatchingTimeoutMillis() {
		int randomSecond = matchingTimeoutRandom.nextInt(5);
		int millis = GCompCommonConfig.getMachingTimeoutMillis();
		if (randomSecond != 0) {
			// 随机偏移一定的秒数
			millis -= TimeUnit.SECONDS.toMillis(randomSecond);
		}
		GCompUtil.log("---------- timeout millis : {} ----------", millis);
		return millis;
	}

	public static List<String> getTopCountGroupsFromRank() {
		// 从排行榜获取排名靠前的N个帮派数据
		List<GCompFightingItem> topGroups = GCompFightingRankMgr.getFightingRankList();
		List<String> groupIds = new ArrayList<String>(topGroups.size());
		int topCount = GCompCommonConfig.getTopCountGroups();
		Group group;
		String groupId;
		int minMembersCount = GCompCommonConfig.getMinMemberCountOfGroup();
		for (int i = 0, size = topGroups.size(); i < size; i++) {
			groupId = topGroups.get(i).getGroupId();
			group = GroupBM.getInstance().get(groupId);
			if (group == null) {
				GCompUtil.log("找不到帮派：{}", groupId);
				continue;
			}
			if (group.getGroupMemberMgr().getGroupMemberSize() < minMembersCount) {
				GCompUtil.log("帮派：{}，人数少于：{}，不能入围！", group.getGroupBaseDataMgr().getGroupData().getGroupName(), minMembersCount);
				continue;
			}
			groupIds.add(groupId);
			topCount--;
			if (topCount == 0) {
				break;
			}
		}
		GCompUtil.log("----------入围帮派id : " + groupIds + "----------");
		return groupIds;
	}

	public static void updateGroupInfo(List<GCompAgainst> list, Group targetGroup) {
		if (list.size() > 0) {
			GroupBaseDataIF baseData = targetGroup.getGroupBaseDataMgr().getGroupData();
			String groupId = baseData.getGroupId();
			List<GCGroup> groups = new ArrayList<GCGroup>();
			List<Integer> againstIds = new ArrayList<Integer>();
			for (int i = 0, size = list.size(); i < size; i++) {
				GCompAgainst against = list.get(i);
				if (against.getGroupA().getGroupId().equals(groupId)) {
					groups.add(against.getGroupA());
				} else if (against.getGroupB().getGroupId().equals(groupId)) {
					groups.add(against.getGroupB());
				} else {
					continue;
				}
				againstIds.add(against.getId());
			}
			if (groups.size() > 0) {
				GCGroup temp;
				String leaderName = targetGroup.getGroupMemberMgr().getGroupLeader().getName();
				int memberCount = targetGroup.getGroupMemberMgr().getGroupMemberSize();
				String groupName = baseData.getGroupName();
				int groupLv = baseData.getGroupLevel();
				GroupLevelCfg levelTemplate = GroupLevelCfgDAO.getDAO().getLevelCfg(groupLv);
				int maxMemberCount = levelTemplate.getMaxMemberLimit();
				String groupIcon = baseData.getIconId();
				for (int i = 0, size = groups.size(); i < size; i++) {
					temp = groups.get(i);
					temp.setGroupName(groupName);
					temp.setLeaderName(leaderName);
					temp.setGroupLv(groupLv);
					temp.setMemberNum(memberCount);
					temp.setMaxMemberNum(maxMemberCount);
					temp.setGroupIcon(groupIcon);
					GCompDetailInfoMgr.getInstance().updateDetailInfo(againstIds.get(i), groupId, groupName, groupIcon);
				}
			}
		}
	}

	final static public class MessageFormatter {
		static final char DELIM_START = '{';
		static final char DELIM_STOP = '}';
		static final String DELIM_STR = "{}";
		private static final char ESCAPE_CHAR = '\\';

		/**
		 * Performs single argument substitution for the 'messagePattern' passed as parameter.
		 * <p>
		 * For example,
		 * 
		 * <pre>
		 * MessageFormatter.format(&quot;Hi {}.&quot;, &quot;there&quot;);
		 * </pre>
		 * 
		 * will return the string "Hi there.".
		 * <p>
		 * 
		 * @param messagePattern The message pattern which will be parsed and formatted
		 * @param argument The argument to be substituted in place of the formatting anchor
		 * @return The formatted message
		 */
		final public static String format(String messagePattern, Object arg) {
			return arrayFormat(messagePattern, new Object[] { arg });
		}

		/**
		 * 
		 * Performs a two argument substitution for the 'messagePattern' passed as parameter.
		 * <p>
		 * For example,
		 * 
		 * <pre>
		 * MessageFormatter.format(&quot;Hi {}. My name is {}.&quot;, &quot;Alice&quot;, &quot;Bob&quot;);
		 * </pre>
		 * 
		 * will return the string "Hi Alice. My name is Bob.".
		 * 
		 * @param messagePattern The message pattern which will be parsed and formatted
		 * @param arg1 The argument to be substituted in place of the first formatting anchor
		 * @param arg2 The argument to be substituted in place of the second formatting anchor
		 * @return The formatted message
		 */
		final public static String format(final String messagePattern, Object arg1, Object arg2) {
			return arrayFormat(messagePattern, new Object[] { arg1, arg2 });
		}

		static final Throwable getThrowableCandidate(Object[] argArray) {
			if (argArray == null || argArray.length == 0) {
				return null;
			}

			final Object lastEntry = argArray[argArray.length - 1];
			if (lastEntry instanceof Throwable) {
				return (Throwable) lastEntry;
			}
			return null;
		}

		/**
		 * Same principle as the {@link #format(String, Object)} and {@link #format(String, Object, Object)} methods except that any number of arguments can be passed in an array.
		 * 
		 * @param messagePattern The message pattern which will be parsed and formatted
		 * @param argArray An array of arguments to be substituted in place of formatting anchors
		 * @return The formatted message
		 */
		final public static String arrayFormat(final String messagePattern, final Object[] argArray) {

			if (messagePattern == null) {
				return "";
			}

			if (argArray == null) {
				return messagePattern;
			}

			int i = 0;
			int j;
			StringBuilder sBuilder = new StringBuilder(messagePattern.length() + 50);

			int L;
			for (L = 0; L < argArray.length; L++) {

				j = messagePattern.indexOf(DELIM_STR, i);

				if (j == -1) {
					// no more variables
					if (i == 0) { // this is a simple string
						return "";
					} else { // add the tail string which contains no variables and return the result.
						sBuilder.append(messagePattern.substring(i, messagePattern.length()));
						return sBuilder.toString();
					}
				} else {
					if (isEscapedDelimeter(messagePattern, j)) {
						if (!isDoubleEscaped(messagePattern, j)) {
							L--; // DELIM_START was escaped, thus should not be incremented
							sBuilder.append(messagePattern.substring(i, j - 1));
							sBuilder.append(DELIM_START);
							i = j + 1;
						} else {
							// The escape character preceding the delimiter
							// start is itself escaped: "abc x:\\{}"
							// we have to consume one backward slash
							sBuilder.append(messagePattern.substring(i, j - 1));
							deeplyAppendParameter(sBuilder, argArray[L], new HashMap<Object, Object>());
							i = j + 2;
						}
					} else {
						// normal case
						sBuilder.append(messagePattern.substring(i, j));
						deeplyAppendParameter(sBuilder, argArray[L], new HashMap<Object, Object>());
						i = j + 2;
					}
				}
			}
			// append the characters following the last {} pair.
			sBuilder.append(messagePattern.substring(i, messagePattern.length()));
			// if (L < argArray.length - 1) {
			// return new FormattingTuple(sbuf.toString(), argArray, throwableCandidate);
			// } else {
			// return new FormattingTuple(sbuf.toString(), argArray, null);
			// }
			return sBuilder.toString();
		}

		final static boolean isEscapedDelimeter(String messagePattern, int delimeterStartIndex) {

			if (delimeterStartIndex == 0) {
				return false;
			}
			char potentialEscape = messagePattern.charAt(delimeterStartIndex - 1);
			if (potentialEscape == ESCAPE_CHAR) {
				return true;
			} else {
				return false;
			}
		}

		final static boolean isDoubleEscaped(String messagePattern, int delimeterStartIndex) {
			if (delimeterStartIndex >= 2 && messagePattern.charAt(delimeterStartIndex - 2) == ESCAPE_CHAR) {
				return true;
			} else {
				return false;
			}
		}

		// special treatment of array values was suggested by 'lizongbo'
		private static void deeplyAppendParameter(StringBuilder sbuf, Object o, Map<Object, Object> seenMap) {
			if (o == null) {
				sbuf.append("null");
				return;
			}
			if (!o.getClass().isArray()) {
				safeObjectAppend(sbuf, o);
			} else {
				// check for primitive array types because they
				// unfortunately cannot be cast to Object[]
				if (o instanceof boolean[]) {
					booleanArrayAppend(sbuf, (boolean[]) o);
				} else if (o instanceof byte[]) {
					byteArrayAppend(sbuf, (byte[]) o);
				} else if (o instanceof char[]) {
					charArrayAppend(sbuf, (char[]) o);
				} else if (o instanceof short[]) {
					shortArrayAppend(sbuf, (short[]) o);
				} else if (o instanceof int[]) {
					intArrayAppend(sbuf, (int[]) o);
				} else if (o instanceof long[]) {
					longArrayAppend(sbuf, (long[]) o);
				} else if (o instanceof float[]) {
					floatArrayAppend(sbuf, (float[]) o);
				} else if (o instanceof double[]) {
					doubleArrayAppend(sbuf, (double[]) o);
				} else {
					objectArrayAppend(sbuf, (Object[]) o, seenMap);
				}
			}
		}

		private static void safeObjectAppend(StringBuilder sbuf, Object o) {
			try {
				String oAsString = o.toString();
				sbuf.append(oAsString);
			} catch (Throwable t) {
				System.err.println("SLF4J: Failed toString() invocation on an object of type [" + o.getClass().getName() + "]");
				t.printStackTrace();
				sbuf.append("[FAILED toString()]");
			}

		}

		private static void objectArrayAppend(StringBuilder sbuf, Object[] a, Map<Object, Object> seenMap) {
			sbuf.append('[');
			if (!seenMap.containsKey(a)) {
				seenMap.put(a, null);
				final int len = a.length;
				for (int i = 0; i < len; i++) {
					deeplyAppendParameter(sbuf, a[i], seenMap);
					if (i != len - 1)
						sbuf.append(", ");
				}
				// allow repeats in siblings
				seenMap.remove(a);
			} else {
				sbuf.append("...");
			}
			sbuf.append(']');
		}

		private static void booleanArrayAppend(StringBuilder sbuf, boolean[] a) {
			sbuf.append('[');
			final int len = a.length;
			for (int i = 0; i < len; i++) {
				sbuf.append(a[i]);
				if (i != len - 1)
					sbuf.append(", ");
			}
			sbuf.append(']');
		}

		private static void byteArrayAppend(StringBuilder sbuf, byte[] a) {
			sbuf.append('[');
			final int len = a.length;
			for (int i = 0; i < len; i++) {
				sbuf.append(a[i]);
				if (i != len - 1)
					sbuf.append(", ");
			}
			sbuf.append(']');
		}

		private static void charArrayAppend(StringBuilder sbuf, char[] a) {
			sbuf.append('[');
			final int len = a.length;
			for (int i = 0; i < len; i++) {
				sbuf.append(a[i]);
				if (i != len - 1)
					sbuf.append(", ");
			}
			sbuf.append(']');
		}

		private static void shortArrayAppend(StringBuilder sbuf, short[] a) {
			sbuf.append('[');
			final int len = a.length;
			for (int i = 0; i < len; i++) {
				sbuf.append(a[i]);
				if (i != len - 1)
					sbuf.append(", ");
			}
			sbuf.append(']');
		}

		private static void intArrayAppend(StringBuilder sbuf, int[] a) {
			sbuf.append('[');
			final int len = a.length;
			for (int i = 0; i < len; i++) {
				sbuf.append(a[i]);
				if (i != len - 1)
					sbuf.append(", ");
			}
			sbuf.append(']');
		}

		private static void longArrayAppend(StringBuilder sbuf, long[] a) {
			sbuf.append('[');
			final int len = a.length;
			for (int i = 0; i < len; i++) {
				sbuf.append(a[i]);
				if (i != len - 1)
					sbuf.append(", ");
			}
			sbuf.append(']');
		}

		private static void floatArrayAppend(StringBuilder sbuf, float[] a) {
			sbuf.append('[');
			final int len = a.length;
			for (int i = 0; i < len; i++) {
				sbuf.append(a[i]);
				if (i != len - 1)
					sbuf.append(", ");
			}
			sbuf.append(']');
		}

		private static void doubleArrayAppend(StringBuilder sbuf, double[] a) {
			sbuf.append('[');
			final int len = a.length;
			for (int i = 0; i < len; i++) {
				sbuf.append(a[i]);
				if (i != len - 1)
					sbuf.append(", ");
			}
			sbuf.append(']');
		}
	}
}
