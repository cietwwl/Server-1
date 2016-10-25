package com.bm.saloon.impl.comImpl;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.bm.saloon.SaloonHelper;
import com.bm.saloon.SaloonResult;
import com.bm.saloon.data.SaloonPlayer;
import com.bm.saloon.data.SaloonPlayerHolder;
import com.bm.saloon.data.SaloonPosition;
import com.bm.saloon.data.SaloonPositionHolder;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rw.netty.UserChannelMgr;

public class SaloonCom {
	
	private int id;

	private ConcurrentHashMap<String, SaloonPosition> postionMap = new ConcurrentHashMap<String, SaloonPosition>();
	
	private BlockingQueue<String> newAddQueue = new LinkedBlockingQueue<String>();
	private BlockingQueue<String> removeQueue = new LinkedBlockingQueue<String>();
	private BlockingQueue<String> updateQueue = new LinkedBlockingQueue<String>();

	public static SaloonCom newInstance(int idP){
		SaloonCom target = new SaloonCom();
		target.id = idP;
		return target;
	}
	
	public int getId() {
		return id;
	}


	private SaloonPositionAction newAddSynAction = new SaloonPositionAction() {			
		@Override
		public void doAction(Player player, SaloonPosition position) {
			SaloonPlayer saloonPlayer = SaloonHelper.getInstance().getSaloonPlayer(position.getId());
			SaloonPlayerHolder.getInstance().synAddData(player, saloonPlayer);
			SaloonPositionHolder.getInstance().synAddData(player, position);
		}
	};

	private SaloonPositionAction newUpdateSynAction = new SaloonPositionAction() {			
		@Override
		public void doAction(Player player, SaloonPosition position) {
			SaloonPositionHolder.getInstance().synUpdateData(player, position);
		}
	};
	private long lastCheckTime = System.currentTimeMillis();
	final private long checkSpan = TimeUnit.SECONDS.toMillis(10);
	public void update() {
		
		handleQueueAction(newAddQueue, newAddSynAction);
		handleQueueAction(updateQueue, newUpdateSynAction);
		long currentTimeMillis = System.currentTimeMillis();
		if(currentTimeMillis - lastCheckTime > checkSpan){
			lastCheckTime = currentTimeMillis;
			checkLogout();
		}
		handleRemoveQueueAction();
		
	}
	
	private void checkLogout() {
		Enumeration<String> userIds = postionMap.keys();
		while(userIds.hasMoreElements()){
			String userId = userIds.nextElement();
			if(UserChannelMgr.isLogout(userId)){
				leave(userId);
			}
		}
		
	}

	private List<Player> getSaloonPlayers(){
		List<Player>  playerList = new ArrayList<Player>();
		for (String userId : postionMap.keySet()) {
			Player player = PlayerMgr.getInstance().findPlayerFromMemory(userId);
			if(player!=null){
				playerList.add(player);
			}
		}
		return playerList;
	}
	
	private void handleQueueAction(BlockingQueue<String> targetQueue, SaloonPositionAction positionAction){
		
		if(!targetQueue.isEmpty()){
			List<Player>  onlineList = getSaloonPlayers();
			String userId = targetQueue.poll();
			while(StringUtils.isNotBlank(userId)){				
				SaloonPosition saloonPosition = postionMap.get(userId);
				if(saloonPosition!=null){
					for (Player playerTmp : onlineList) {
						if(!StringUtils.equals(playerTmp.getUserId(), userId)){
							positionAction.doAction(playerTmp, saloonPosition);
						}
					}				
				}
				userId = targetQueue.poll();
			}
		}
		
	}
	private void handleRemoveQueueAction(){
		
		if(!removeQueue.isEmpty()){
			List<Player>  onlineList = getSaloonPlayers();
			String removeId = removeQueue.poll();
			while(StringUtils.isNotBlank(removeId)){		
				SaloonPosition position = SaloonPosition.newInstance(removeId);
				SaloonPlayer removeItem = SaloonPlayer.newInstance(removeId);
				
				for (Player playerTmp : onlineList) {
					if(!StringUtils.equals(playerTmp.getUserId(), removeId)){
						SaloonPositionHolder.getInstance().synRemoveData(playerTmp, position);
						SaloonPlayerHolder.getInstance().synRemoveData(playerTmp, removeItem);
					}
				}				
				
				removeId = removeQueue.poll();
			}
		}
		
	}
	

	public SaloonResult enter(String userId, float px, float py) {
		SaloonPosition position = SaloonPosition.newInstance(userId, px, py);
		postionMap.putIfAbsent(userId, position);
		newAddQueue.add(userId);
		
		return SaloonResult.newInstance(true);
	}

	public SaloonResult leave(String userId) {
		postionMap.remove(userId);
		removeQueue.add(userId);
		return SaloonResult.newInstance(true);
	}

	public SaloonResult synAllPlayerInfo(Player player) {
		
		List<SaloonPlayer> sPlayerList = new ArrayList<SaloonPlayer>();
		List<SaloonPosition> sPositionList = new ArrayList<SaloonPosition>();
		for (String userId : postionMap.keySet()) {
			sPositionList.add(postionMap.get(userId));
			SaloonPlayer saloonPlayer = SaloonHelper.getInstance().getSaloonPlayer(userId);
			sPlayerList.add(saloonPlayer);
		}
		SaloonPlayerHolder.getInstance().synAllData(player, sPlayerList);
		SaloonPositionHolder.getInstance().synAllData(player, sPositionList);
		
		return SaloonResult.newInstance(true);
	}

	public SaloonResult informPosition(String userId, float px, float py ) {

		if(!postionMap.containsKey(userId)){
			SaloonPosition position = SaloonPosition.newInstance(userId, px, py);
			postionMap.putIfAbsent(userId, position);
		}
		SaloonPosition saloonPosition = postionMap.get(userId);
		saloonPosition.setPx(px);
		saloonPosition.setPy(py);
		updateQueue.add(userId);
		return SaloonResult.newInstance(true);
	}
	

	final private int maxSize = 60;
	public boolean canEnter(){
		return postionMap.size() < maxSize;
	}
	
}
