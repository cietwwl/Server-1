package com.bm.saloon;

import com.playerdata.Player;

public interface ISaloonBm {

	public void update();

	public SaloonResult enter(String userId, float px, float py);

	public SaloonResult leave(String userId);

	public SaloonResult synAllPlayerInfo(Player player);

	public SaloonResult informPosition(String userId, float px, float py );
	 
}
