package com.playerdata.mgcsecret.cfg;
import com.playerdata.mgcsecret.data.MSRewardBox;

public class DungeonsDataCfg {
	private String key; //关键字段
	private int stageId; //空间id
	private int levelId; //难度id
	private String fabaoBuff; //怪物buff
	private String buffBonus; //buff
	private String coBox; //普通宝箱
	private MSRewardBox objCoBox = new MSRewardBox();
	private String hiBox; //高级宝箱
	private MSRewardBox objHiBox = new MSRewardBox();
	private String enimy; //敌人
	private int score; //积分
	private int starReward; //简单难度星星奖励
	private int copyId; //关卡ID
	private String drop; //掉落方案

	public String getKey() {
		return key;
	}
	
	public int getStageId() {
		return stageId;
	}
	
	public int getLevelId() {
		return levelId;
	}
	
	public String getFabaoBuff() {
		return fabaoBuff;
	}
	
	public String getBuffBonus() {
		return buffBonus;
	}
	
	public String getCoBox() {
		return coBox;
	}
	
	public String getHiBox() {
		return hiBox;
	}
	
	public String getEnimy() {
	  return enimy;
	}
	
	public int getScore() {
		return score;
	}
	
	public int getStarReward() {
		return starReward;
	}
	
	public int getCopyId() {
	    return copyId;
	}
	
	public String getDrop() {
		return drop;
	}
  
	public int getChapterID(){
		return stageId/100;
	}
	
	public MSRewardBox getObjCoBox() {
		return objCoBox;
	}

	public MSRewardBox getObjHiBox() {
		return objHiBox;
	}

	public void ExtraInitAfterLoad(){
		String[] coboxArr = this.coBox.split(":");
		String[] coCostArr = coboxArr[0].split("_");
		objCoBox.getBoxCost().setItemID(Integer.parseInt(coCostArr[0]));
		objCoBox.getBoxCost().setItemNum(Integer.parseInt(coCostArr[1]));
		objCoBox.setDropStr(coboxArr[1]);
		
		String[] hiboxArr = this.hiBox.split(":");
		String[] hiCostArr = hiboxArr[0].split("_");
		objHiBox.getBoxCost().setItemID(Integer.parseInt(hiCostArr[0]));
		objHiBox.getBoxCost().setItemNum(Integer.parseInt(hiCostArr[1]));
		objHiBox.setDropStr(hiboxArr[1]);
	}
}
