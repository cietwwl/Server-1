package com.rwbase.dao.skill.pojo;

/*
 * @author HC
 * @date 2016年5月2日 下午12:45:27
 * @Description 技能效果
 */
public class SkillEffectCfg {
	private String Id;// 技能Id
	private float CD;// 技能CD
	private float StartCDTime;// 起点CD

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public float getCD() {
		return CD;
	}

	public void setCD(float cD) {
		CD = cD;
	}

	public float getStartCDTime() {
		return StartCDTime;
	}

	public void setStartCDTime(float startCDTime) {
		StartCDTime = startCDTime;
	}
}