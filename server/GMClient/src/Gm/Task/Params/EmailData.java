package Gm.Task.Params;

import java.util.List;

public class EmailData {	
	private long taskId;
	private String title = "";//标题
	private String content = "";//内容
	private String itemDict = "";//附件列表
	private int coolTime;
	private int expireTime;
	private String conditionList;	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public void replaceContent(List<String> args){
		int index = 0;
		while(args.size() > index){
			String oldStr = "{S" + index + "}";
			if(content.indexOf(oldStr) != -1){//找到
				content = content.replace(oldStr, args.get(index));
			}
			index++;
		}
	}
	public String getItemDict() {
		return itemDict;
	}
	public void setItemDict(String itemDict) {
		this.itemDict = itemDict;
	}
	public int getCoolTime() {
		return coolTime;
	}
	public void setCoolTime(int coolTime) {
		this.coolTime = coolTime;
	}
	public int getExpireTime() {
		return expireTime;
	}
	public void setExpireTime(int expireTime) {
		this.expireTime = expireTime;
	}
	public String getConditionList() {
		return conditionList;
	}
	public void setConditionList(String conditionList) {
		this.conditionList = conditionList;
	}
	public long getTaskId() {
		return taskId;
	}
	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}
	
}
