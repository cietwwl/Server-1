package com.rw.service.guide.datamodel;

/**
 * <pre>
 * 新手引导关闭控制
 * 表示这指定等级，完成指定进度后，可以屏蔽新手引导，直到下个指定的等级出现为止
 * </pre>
 * 
 * @author Jamaz
 *
 */
public class GuidanceClosure {

	private int level; // 对应玩家的等级
	private int progress; // 新手引导的步骤，配置为-1，表示这一等级强制打开

	public GuidanceClosure(int level, int progress) {
		super();
		this.level = level;
		this.progress = progress;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

}
