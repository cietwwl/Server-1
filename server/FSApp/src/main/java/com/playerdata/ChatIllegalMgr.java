package com.playerdata;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
				int timeOfNotAllowedSpeech = TimeOfNotAllowedSpeechDAO.getCfgDAO().getTimeOfNotAllowedSpeech(triggerTimes, vipLevel);// 获取禁言的时间
				if (timeOfNotAllowedSpeech < 0) {// 是否是永久禁言，那么就直接返回True
					if (hasDataUpdate) {
						dao.update(userId);
					}
					return timeOfNotAllowedSpeech;
				} else if (timeOfNotAllowedSpeech == 0) {// 不禁言
					return timeOfNotAllowedSpeech;
				}

				int listenTime = PublicDataCfgDAO.getInstance().getPublicDataValueById(PublicData.NOT_ALLOWED_SPEECH_LISTEN_TIME);
				// 检查禁言时间是否到了
				long nwSpeechTimeMillis = TimeUnit.SECONDS.toMillis(timeOfNotAllowedSpeech);
				long timeMillis = TimeUnit.SECONDS.toMillis(listenTime);
				if (now - lastNotAllowedSpeechTime >= nwSpeechTimeMillis) {// 过了禁言时间
					chatIllegalData.setLastNotAllowedSpeechTime(0);
					hasDataUpdate = true;
				} else if (now - lastNotAllowedSpeechTime >= timeMillis) {// 过了24小时的冷却，中间没有犯规过，清除出发的所有数据
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
				chatTempAttribute.setMsgRepeatedTimes(msgRepeatedTimes++);
			} else {
				chatTempAttribute.setLastWorldMsg(msg);
				chatTempAttribute.setMsgRepeatedTimes(0);
				return;
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
				int timeOfNotAllowedSpeech = TimeOfNotAllowedSpeechDAO.getCfgDAO().getTimeOfNotAllowedSpeech(times, player.getVip());
				if (timeOfNotAllowedSpeech == 0) {// 不禁言，只刷新次数
					chatIllegalData.setIllegalTimes(illegalTimes);
				} else {
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