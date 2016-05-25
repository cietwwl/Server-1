package com.gm.customer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.Spring;

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
import com.rw.manager.GameManager;
import com.rw.manager.ServerSwitch;

public class PlayerQuestionService {
	private static PlayerQuestionService instance = new PlayerQuestionService();
	private GmSenderConfig senderConfig; 
	private BlockingQueue<QuestionObject> Queue = new LinkedBlockingQueue<PlayerQuestionService.QuestionObject>();
	
	private ExecutorService sendService;

	private ExecutorService submitService;

	private GmSenderPool giftSenderPool;
	
	public static PlayerQuestionService getInstance(){
		return SpringContextUtil.getBean(PlayerQuestionService.class);
	}
	
	public void init(){
		if (!ServerSwitch.isGiftCodeOpen()) {
			return;
		}
		senderConfig = new GmSenderConfig(GameManager.getGiftCodeServerIp(), GameManager.getGiftCodeServerPort(), 10000, (short)10354);
		

		giftSenderPool = new GmSenderPool(senderConfig);

		sendService = Executors.newSingleThreadExecutor();
		submitService = Executors.newFixedThreadPool(10);
//		submitService.submit(new Runnable() {
//			@Override
//			public void run() {
//
//				while (true) {
//					try {
//						checkAndSubmit();
//					} catch (Throwable e) {
//						GameLog.error(LogModule.GmSender, "GiftCodeSenderBm[checkAndSubmit]", "", e);
//					}
//				}
//
//			}
//
//			private void checkAndSubmit() {
//				final GmSender borrowSender = giftSenderPool.borrowSender();
//				if (borrowSender != null) {
//					QuestionObject obj = null;
//					try {
//						obj = Queue.poll(10, TimeUnit.SECONDS);
//					} catch (InterruptedException e) {
//						// do nothing
//					}
//					if (obj != null) {
//						addSendTask(borrowSender, obj);
//					} else {
//						giftSenderPool.returnSender(borrowSender);
//					}
//				}
//			}
//
//			private void addSendTask(final GmSender borrowSender, final QuestionObject obj) {
//				sendService.submit(new Runnable() {
//
//					@SuppressWarnings({ "unchecked", "rawtypes" })
//					@Override
//					public void run() {
//						try {
//							String userId = obj.getUserId();
//							Player player = PlayerMgr.getInstance().find(userId);
//							if (player == null) {
//								return;
//							}
//							PlayerQuestionMgr playerQuestionMgr = player.getPlayerQuestionMgr();
//							// if (obj.requireList) {
//							// List send2 = borrowSender.send2(obj.getContent(),
//							// QueryBaseResponse.class, obj.getOpType());
//							// playerQuestionMgr.processResponse(send2,
//							// obj.getOpType());
//							// } else {
//							QueryBaseResponse send = borrowSender.send(obj.getContent(), QueryBaseResponse.class, obj.getOpType());
//							String result = send.getResult();
//							List deserializeList = FastJsonUtil.deserializeList(result, obj.getResponseClass());
//							playerQuestionMgr.processResponse(deserializeList, obj.getOpType());
//							// }
//	
//						} catch (Exception e) {
//							borrowSender.setAvailable(false);//return pool之后会呗销毁。
//							GameLog.error(LogModule.GmSender, "GiftCodeSenderBm[addSendTask]", "borrowSender.send error", e);
//						} finally {
//							giftSenderPool.returnSender(borrowSender);
//						}
//
//					}
//				});
//			}
//		});

	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T> void submitRequest(Map<String, Object> content, int opType, String userId, boolean requireList, Class<T> responseClass){
		if (!ServerSwitch.isGiftCodeOpen()) {
			return;
		}
		final QuestionObject obj = new QuestionObject(content, opType, userId, requireList, responseClass);
//		Queue.add(obj);
		
		submitService.submit(new Runnable() {
			@Override
			public void run() {

				final GmSender borrowSender = giftSenderPool.borrowSender();
				if (borrowSender != null) {
					if (obj != null) {
						addSendTask(borrowSender, obj);
					}
				}

			}

			private void addSendTask(final GmSender borrowSender, final QuestionObject obj) {
				sendService.submit(new Runnable() {

					@SuppressWarnings({ "unchecked", "rawtypes" })
					@Override
					public void run() {
						try {
							String userId = obj.getUserId();
							Player player = PlayerMgr.getInstance().find(userId);
							if (player == null) {
								return;
							}
							PlayerQuestionMgr playerQuestionMgr = player.getPlayerQuestionMgr();
							// if (obj.requireList) {
							// List send2 = borrowSender.send2(obj.getContent(),
							// QueryBaseResponse.class, obj.getOpType());
							// playerQuestionMgr.processResponse(send2,
							// obj.getOpType());
							// } else {
							QueryBaseResponse send = borrowSender.send(obj.getContent(), QueryBaseResponse.class, obj.getOpType());
							String result = send.getResult();
							List deserializeList = FastJsonUtil.deserializeList(result, obj.getResponseClass());
							playerQuestionMgr.processResponse(deserializeList, obj.getOpType());
							// }
	
						} catch (Exception e) {
							borrowSender.setAvailable(false);//return pool之后会呗销毁。
							GameLog.error(LogModule.GmSender, "GiftCodeSenderBm[addSendTask]", "borrowSender.send error", e);
						} finally {
							giftSenderPool.returnSender(borrowSender);
						}

					}
				});
			}
		});

		
	}
	
	@JsonIgnoreProperties(ignoreUnknown=true)
	public class QuestionObject<T>{
		Map<String, Object> content;
		int opType;
		String userId;
		Class<T> responseClass;
		boolean requireList = false;
		boolean blnSuccuess = false;
		
		
		public QuestionObject(Map<String, Object> content, int opType, String userId, boolean requireList, Class<T> responseClass){
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
