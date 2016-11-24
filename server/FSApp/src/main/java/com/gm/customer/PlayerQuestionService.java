package com.gm.customer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.gm.customer.response.QueryBaseResponse;
import com.gm.gmsender.GmSender;
import com.gm.gmsender.GmSenderConfig;
import com.gm.gmsender.GmSenderPool;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.PlayerQuestionMgr;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.fsutil.util.fastjson.FastJsonUtil;
import com.rw.manager.ServerSwitch;
import com.rw.netty.ServerConfig;

public class PlayerQuestionService {
	// private static PlayerQuestionService instance = new PlayerQuestionService();
	private GmSenderConfig senderConfig;
	// private BlockingQueue<QuestionObject> Queue = new LinkedBlockingQueue<PlayerQuestionService.QuestionObject>();

	// private ExecutorService sendService;
	private ExecutorService submitService;

	private GmSenderPool giftSenderPool;

	public static PlayerQuestionService getInstance() {
		return SpringContextUtil.getBean(PlayerQuestionService.class);
	}

	public void init() {
		if (!ServerSwitch.isGiftCodeOpen()) {
			return;
		}
		String giftCodeServerIp = ServerConfig.getInstance().getServeZoneInfo().getGiftCodeServerIp();
		int giftCodeServerPort = ServerConfig.getInstance().getServeZoneInfo().getGiftCodeServerPort();
		senderConfig = new GmSenderConfig(giftCodeServerIp, giftCodeServerPort, 10000, (short) 10354);

		giftSenderPool = new GmSenderPool(senderConfig);
		submitService = Executors.newFixedThreadPool(10);

	}

	public <T> T submitRequestSync(Map<String, Object> content, int opType, String userId, boolean requireList, Class<T> responseClass) {
		GmSender borrowSender = giftSenderPool.borrowSender();
		if (borrowSender != null) {
			try {
				Player player = PlayerMgr.getInstance().find(userId);
				if (player == null) {
					return null;
				}
				QueryBaseResponse send = borrowSender.send(content, QueryBaseResponse.class, opType);
				String result = send.getResult();
				List<T> deserializeList = FastJsonUtil.deserializeList(result, responseClass);
				return deserializeList.get(0);

			} catch (Exception e) {
				borrowSender.setAvailable(false);// return pool之后会呗销毁。
				GameLog.error(LogModule.GmSender, "GiftCodeSenderBm[addSendTask]", "borrowSender.send error", e);
			} finally {
				giftSenderPool.returnSender(borrowSender);
			}
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T> void submitRequest(Map<String, Object> content, int opType, String userId, boolean requireList, Class<T> responseClass) {
		if (!ServerSwitch.isGiftCodeOpen()) {
			return;
		}
		final QuestionObject obj = new QuestionObject(content, opType, userId, requireList, responseClass);

		submitService.submit(new Runnable() {
			@Override
			public void run() {

				final GmSender borrowSender = giftSenderPool.borrowSender();
				if (borrowSender != null) {
					if (obj != null) {
						try {
							String userId = obj.getUserId();
							Player player = PlayerMgr.getInstance().find(userId);
							if (player == null) {
								return;
							}
							PlayerQuestionMgr playerQuestionMgr = player.getPlayerQuestionMgr();
							QueryBaseResponse send = borrowSender.send(obj.getContent(), QueryBaseResponse.class, obj.getOpType());
							String result = send.getResult();
							List deserializeList = FastJsonUtil.deserializeList(result, obj.getResponseClass());
							playerQuestionMgr.processResponse(deserializeList, obj.getOpType());

						} catch (Exception e) {
							borrowSender.setAvailable(false);// return pool之后会呗销毁。
							GameLog.error(LogModule.GmSender, "GiftCodeSenderBm[addSendTask]", "borrowSender.send error", e);
						} finally {
							giftSenderPool.returnSender(borrowSender);
						}
					}
				}

			}
		});

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public class QuestionObject<T> {
		Map<String, Object> content;
		int opType;
		String userId;
		Class<T> responseClass;
		boolean requireList = false;
		boolean blnSuccuess = false;

		public QuestionObject(Map<String, Object> content, int opType, String userId, boolean requireList, Class<T> responseClass) {
			this.content = content;
			this.opType = opType;
			this.userId = userId;
			this.requireList = requireList;
			this.responseClass = responseClass;
		}

		public Map<String, Object> getContent() {
			return content;
		}

		public int getOpType() {
			return opType;
		}

		public boolean isBlnSuccuess() {
			return blnSuccuess;
		}

		public void setBlnSuccuess(boolean blnSuccuess) {
			this.blnSuccuess = blnSuccuess;
		}

		public String getUserId() {
			return userId;
		}

		public Class<T> getResponseClass() {
			return responseClass;
		}
	}
}
