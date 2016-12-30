package com.playerdata;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.common.RefInt;
import com.rwbase.dao.chat.pojo.ChatIllegalData;
import com.rwbase.dao.chat.pojo.ChatIllegalDataDAO;
import com.rwbase.dao.chat.pojo.ChatTempAttribute;
import com.rwbase.dao.chat.pojo.cfg.ChatIllegalCfg;
import com.rwbase.dao.chat.pojo.cfg.TimeOfNotAllowedSpeechDAO;
import com.rwbase.dao.publicdata.PublicData;
import com.rwbase.dao.publicdata.PublicDataCfgDAO;
import com.rwproto.ChatServiceProtos.ChatAttachItem;
import com.rwproto.ChatServiceProtos.ChatMessageData;

/**
 * @Author HC
 * @date 2016年12月17日 上午11:25:16
 * @desc 聊天违规检查数据Mgr
 **/

public class ChatIllegalMgr {
	private static ChatIllegalMgr mgr = new ChatIllegalMgr();

	public static ChatIllegalMgr getMgr() {
		return mgr;
	}

	protected ChatIllegalMgr() {
	}

	/**
	 * 检查当前是否禁言
	 * 
	 * @param userId
	 * @param level
	 * @param vipLevel
	 * @param now
	 * @param cfg
	 * @return 返回当前禁言的时间段在哪个分段
	 */
	public int checkIsInNotAllowedSpeech(String userId, int level, int vipLevel, long now, ChatIllegalCfg cfg) {
		ChatIllegalDataDAO dao = ChatIllegalDataDAO.getDAO();
		ChatIllegalData chatIllegalData = dao.get(userId);
		if (chatIllegalData == null) {
			return 0;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		boolean hasDataUpdate = false;// 是否有数据刷新
		// 检查清理违规次数
		int illegalTimes = chatIllegalData.getIllegalTimes();// 当前已经犯规的次数
		if (illegalTimes > 0) {
			long lastIllegalTime = chatIllegalData.getLastIllegalTime();
			if (lastIllegalTime > 0) {
				int listenTime = PublicDataCfgDAO.getInstance().getPublicDataValueById(PublicData.THE_COOLING_TIME_OF_CLEAR_ILLEGAL_TIMES);
				long timeMillis = TimeUnit.SECONDS.toMillis(listenTime);
				long intervalMillis = TimeUnit.SECONDS.toMillis(cfg.getInterval());// 发言的间隔
				long clearTimeMillis = timeMillis + intervalMillis;

				System.err.println(String.format("上一次犯规的时间：%s，当前的时间：%s，发言间隔：%s，清除违规配置时间为：%s，已经过了%s秒", sdf.format(new Date(lastIllegalTime)), sdf.format(new Date(now)), cfg.getInterval(), listenTime, (now - lastIllegalTime) / 1000));

				if (now - lastIllegalTime >= clearTimeMillis) {// 在这个时间点上没有违规过，就把这个清除掉
					chatIllegalData.setIllegalTimes(0);
					chatIllegalData.setLastIllegalTime(0);
					hasDataUpdate = true;
				}
			}
		}

		int triggerTimes = chatIllegalData.getTriggerTimes();// 禁言的次数
		if (triggerTimes > 0) {
			// 检查禁言时间
			long lastNotAllowedSpeechTime = chatIllegalData.getLastNotAllowedSpeechTime();// 上次被禁言的时间
			if (lastNotAllowedSpeechTime > 0) {
				// 检查一下是否是永久禁言
				int timeOfNotAllowedSpeech = TimeOfNotAllowedSpeechDAO.getCfgDAO().getTimeOfNotAllowedSpeech(triggerTimes, vipLevel, null);// 获取禁言的时间

				System.err.println("当前要禁言的时间：" + timeOfNotAllowedSpeech);

				if (timeOfNotAllowedSpeech < 0) {// 是否是永久禁言，那么就直接返回True
					if (hasDataUpdate) {
						dao.update(userId);
					}
					return timeOfNotAllowedSpeech;
				} else if (timeOfNotAllowedSpeech == 0) {// 不禁言
					return timeOfNotAllowedSpeech;
				}

				// 检查禁言时间是否到了
				long nwSpeechTimeMillis = TimeUnit.SECONDS.toMillis(timeOfNotAllowedSpeech);

				System.err.println(String.format("上次禁言的时间：%s，当前的时间：%s，需要禁言时间：%s秒，禁言已经过了%s秒", sdf.format(new Date(lastNotAllowedSpeechTime)), sdf.format(new Date(now)), timeOfNotAllowedSpeech, (now - lastNotAllowedSpeechTime) / 1000));

				if (now - lastNotAllowedSpeechTime >= nwSpeechTimeMillis) {// 过了禁言时间
					chatIllegalData.setLastNotAllowedSpeechTime(0);
					hasDataUpdate = true;
				} else {
					if (hasDataUpdate) {
						dao.update(userId);
					}
					return timeOfNotAllowedSpeech;
				}

				int listenTime = PublicDataCfgDAO.getInstance().getPublicDataValueById(PublicData.NOT_ALLOWED_SPEECH_LISTEN_TIME);
				long timeMillis = TimeUnit.SECONDS.toMillis(listenTime);

				System.err.println(String.format("上次禁言的时间：%s，当前的时间：%s，禁言监听配置时间：%s秒，已经过了%s秒", sdf.format(new Date(lastNotAllowedSpeechTime)), sdf.format(new Date(now)), listenTime, (now - lastNotAllowedSpeechTime) / 1000));
				if (now - lastNotAllowedSpeechTime >= timeMillis) {// 过了24小时的冷却，中间没有犯规过，清除出发的所有数据
					chatIllegalData.setTriggerTimes(0);
					chatIllegalData.setLastNotAllowedSpeechTime(0);
					hasDataUpdate = true;
				}
			}
		}

		if (hasDataUpdate) {// 更新数据
			dao.update(userId);
		}

		return 0;
	}

	/**
	 * 检查是否可以触发禁言
	 * 
	 * @param player
	 */
	public void checkCanTriggerNotAllowedSpeech(Player player, ChatMessageData messageData, long now, ChatIllegalCfg cfg) {
		ChatIllegalDataDAO dao = ChatIllegalDataDAO.getDAO();
		String userId = player.getUserId();
		ChatIllegalData chatIllegalData = dao.get(userId);
		if (chatIllegalData == null) {
			return;
		}

		String msg = parseChatMessageData2String(messageData);// 转换消息

		ChatTempAttribute chatTempAttribute = player.getTempAttribute().getChatTempAttribute();
		long lastWorldSpeechTime = chatTempAttribute.getLastWorldSpeechTime();// 上次发送世界消息的时间
		if (lastWorldSpeechTime > 0) {
			String lastWorldMsg = chatTempAttribute.getLastWorldMsg();// 上次发送世界消息的内容
			int msgRepeatedTimes = chatTempAttribute.getMsgRepeatedTimes();// 之前已经重复的次数

			chatTempAttribute.setLastGroupSpeechTime(now);
			if (msg.equals(lastWorldMsg)) {// 是否跟上边的内容重复
				chatTempAttribute.setMsgRepeatedTimes(++msgRepeatedTimes);
			} else {
				chatTempAttribute.setLastWorldMsg(msg);
				chatTempAttribute.setMsgRepeatedTimes(0);
			}

			// 检查违规
			msgRepeatedTimes = chatTempAttribute.getMsgRepeatedTimes();
			long illegalIntervalMillis = TimeUnit.SECONDS.toMillis(cfg.getIllegalInterval());// 违规的间隔频率
			int repeatedTimes = cfg.getRepeatedTimes();// 发送重复消息违规的次数

			int newAddIllegalTimes = 0;
			if (msgRepeatedTimes >= repeatedTimes) {// 已经超出了违规次数
				newAddIllegalTimes++;
			}

			if (now - lastWorldSpeechTime <= illegalIntervalMillis) {// 频率上违规了
				newAddIllegalTimes++;
			}

			if (newAddIllegalTimes > 0) {// 增加了新的违规
				int hasNotAllowedSpeeckTimes = chatIllegalData.getTriggerTimes();
				int illegalTimes = chatIllegalData.getIllegalTimes();

				chatIllegalData.setLastIllegalTime(now);
				illegalTimes += newAddIllegalTimes;// 新的违规次数

				// 现在的总违规次数
				int times = hasNotAllowedSpeeckTimes + illegalTimes;

				System.err.println(String.format("之前触发禁言的次数：%s，这一次又触发犯规的次数：%s", hasNotAllowedSpeeckTimes, illegalTimes));

				RefInt refInt = new RefInt();// 返回触发的档次
				int timeOfNotAllowedSpeech = TimeOfNotAllowedSpeechDAO.getCfgDAO().getTimeOfNotAllowedSpeech(times, player.getVip(), refInt);

				System.err.println(String.format("之前触发禁言的次数：%s，Vip等级是：%s，这一次触发的禁言档次：%s，触发的禁言时间：%s", hasNotAllowedSpeeckTimes, player.getVip(), refInt.value, timeOfNotAllowedSpeech));
				if (timeOfNotAllowedSpeech == 0 || refInt.value <= hasNotAllowedSpeeckTimes) {// 不禁言，只刷新次数
					chatIllegalData.setIllegalTimes(illegalTimes);
				} else {// 跟上一档次一样
					chatIllegalData.setTriggerTimes(times);// 设置触发禁言的点
					chatIllegalData.setLastNotAllowedSpeechTime(now);// 设置禁言的时间点
					chatIllegalData.setIllegalTimes(0);
				}

				dao.update(userId);
			}
		} else {
			chatTempAttribute.setLastWorldSpeechTime(now);
			chatTempAttribute.setLastWorldMsg(msg);
		}
	}

	/**
	 * 解析消息转换成一个字符串
	 * 
	 * @param messageData
	 * @return
	 */
	public String parseChatMessageData2String(ChatMessageData messageData) {
		StringBuilder sb = new StringBuilder();
		sb.append(messageData.getMessage());

		List<ChatAttachItem> attachItemList = messageData.getAttachItemList();
		if (attachItemList != null && !attachItemList.isEmpty()) {
			for (int i = attachItemList.size(); --i >= 0;) {
				ChatAttachItem chatAttachItem = attachItemList.get(i);
				sb.append(chatAttachItem.getId());
				if (chatAttachItem.hasExtraInfo()) {
					sb.append(chatAttachItem.getExtraInfo());
				}
			}
		}
		return sb.toString();
	}
}