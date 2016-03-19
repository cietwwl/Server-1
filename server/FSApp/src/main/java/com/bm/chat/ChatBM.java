package com.bm.chat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rw.fsutil.common.SimpleThreadFactory;
import com.rw.netty.UserChannelMgr;
import com.rw.service.chat.ChatHandler;
import com.rwbase.dao.friend.FriendUtils;
import com.rwproto.ChatServiceProtos.ChatMessageData;
import com.rwproto.ChatServiceProtos.MsgChatResponse;
import com.rwproto.ChatServiceProtos.eChatResultType;
import com.rwproto.ChatServiceProtos.eChatType;
import com.rwproto.MsgDef;

// 聊天缓存

public class ChatBM {

	private static ChatBM instance = new ChatBM();
	// private static List<ChatMessageData.Builder> listWorld = Collections.synchronizedList(new
	// ArrayList<ChatMessageData.Builder>(ChatHandler.MAX_CACHE_MSG_SIZE));// 多线程保护
	private static ConcurrentHashMap<String, List<ChatMessageData.Builder>> familyChatMap = new ConcurrentHashMap<String, List<ChatMessageData.Builder>>();

	private static List<ChatInfo> worldMessageList = new ArrayList<ChatInfo>(ChatHandler.MAX_CACHE_MSG_SIZE);
	private AtomicInteger messageId = new AtomicInteger();// 当前最新的消息Id
	private AtomicInteger checkMessageId = new AtomicInteger();// 上次检查的版本号
	private ScheduledExecutorService ses = Executors.newScheduledThreadPool(1,new SimpleThreadFactory("chat_broadcast"));// 线程池

	private class ChatRun implements Runnable {

		@Override
		public void run() {
			try {
				// System.err.println("检查的版本Id：" + checkMessageId.get() + ",消息版本：" + messageId.get());
				if (checkMessageId.get() >= messageId.get()) {// 上次服务器发送版本大于当前
					return;
				}

				checkMessageId.set(messageId.get());// 检查的版本更新

				Set<String> playerIdList = UserChannelMgr.getOnlinePlayerIdSet();
				if (playerIdList == null || playerIdList.isEmpty()) {
					return;
				}

				if (worldMessageList.isEmpty()) {
					return;
				}

				List<ChatInfo> list = ChatBM.getInstance().getWorldList();
				int size = list.size();

				Iterator<String> itr = playerIdList.iterator();
				while (itr.hasNext()) {
					String playerId = itr.next();
					Player player = PlayerMgr.getInstance().find(playerId);
					if (player == null) {
						continue;
					}

					int lastWorldChatId = player.getLastWorldChatId();
					if (lastWorldChatId >= messageId.get()) {
						continue;
					}

					MsgChatResponse.Builder msgChatResponse = MsgChatResponse.newBuilder();
					msgChatResponse.setChatType(eChatType.CHAT_WORLD);

					// msgChatResponse.setOnLogin(false);
					for (int i = 0; i < size; i++) {
						ChatInfo chatInfo = list.get(i);
						ChatMessageData chatMsg = chatInfo.getMessage().build();
						String userId = chatMsg.getSendMessageUserInfo().getUserId();
						if (userId == null || userId.isEmpty()) {
							continue;
						}

						if (userId.equals(playerId)) {
							continue;
						}

						if (player.getLastWorldChatId() >= chatInfo.getId()) {
							continue;
						}

						if (!FriendUtils.isBlack(player, userId)) {// 不在黑名单
							msgChatResponse.addListMessage(chatMsg);
						}
					}

					msgChatResponse.setChatResultType(eChatResultType.SUCCESS);
					player.SendMsg(MsgDef.Command.MSG_CHAT, msgChatResponse.build().toByteString());
					player.setLastWorldChatId(messageId.get());// 缓存版本号
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private ChatBM() {
		ses.scheduleAtFixedRate(new ChatRun(), 0, 1500, TimeUnit.MILLISECONDS);
	}

	public static ChatBM getInstance() {
		return instance;
	}

	/**
	 * 增加一条聊天
	 * 
	 * @param data
	 */
	public synchronized int updateWroldList(ChatMessageData.Builder data) {
		int andIncrement = messageId.incrementAndGet();// 增加一个消息Id版本
		if (worldMessageList.size() > ChatHandler.MAX_CACHE_MSG_SIZE) {
			worldMessageList.remove(0);
		}
		worldMessageList.add(new ChatInfo(andIncrement, data));

		// System.err.println("增加一个迭代版本：" + messageId.get());
		return andIncrement;
	}

	public synchronized List<ChatInfo> getWorldList() {
		return new ArrayList<ChatInfo>(worldMessageList);
	}

	/**
	 * 获取当前的消息版本
	 * 
	 * @return
	 */
	public int getChatVersion() {
		return this.messageId.get();
	}

	/**
	 * 获取帮派聊天的列表
	 * 
	 * @param familyId
	 * @return
	 */
	public List<ChatMessageData.Builder> getFamilyChatList(String familyId) {
		List<ChatMessageData.Builder> list = familyChatMap.get(familyId);
		return (list == null || list.isEmpty()) ? new ArrayList<ChatMessageData.Builder>() : new ArrayList<ChatMessageData.Builder>(list);
	}

	/**
	 * 增加帮派聊天
	 * 
	 * @param familyId 帮派Id
	 * @param chatMsg 聊天消息
	 */
	public void addFamilyChat(String familyId, ChatMessageData.Builder chatMsg) {
		List<ChatMessageData.Builder> list = familyChatMap.get(familyId);
		if (list == null) {
			list = new ArrayList<ChatMessageData.Builder>();
			familyChatMap.put(familyId, list);
		}

		list.add(chatMsg);
		if (list.size() > ChatHandler.MAX_CACHE_MSG_SIZE) {
			list.remove(0);
		}
	}
}