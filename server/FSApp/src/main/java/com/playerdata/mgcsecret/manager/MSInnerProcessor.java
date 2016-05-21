package com.playerdata.mgcsecret.manager;


public class MSInnerProcessor extends MSConditionJudger{
	
	// 通知排行榜做出排名更改
	protected void informRankModule(){
		
	}
	
	//  处理掉落，这里面包括了秘境货币的特殊处理
	protected void handleDropItem(){
		
	}
	
	// 增加可以购买的箱子
	protected void getCanOpenBoxes(){
		
	}
	
	// 清除可选的buff
	protected void dropSelectableBuff(String chapteID){
		
	}
	
	// 设置玩家最高闯关纪录
	protected void updateSelfMaxStage(String dungeonID){
		
	}
		
	protected float getScoreRatio(int finishStar){
		return 1.0f;
	}
		
	// 提供可以购买的buff
	protected void provideSelectalbeBuff(){
		
	}
		
	// 为下个阶段生成怪物
	protected void generateEnimyForNextStage(){
		
	}
}
