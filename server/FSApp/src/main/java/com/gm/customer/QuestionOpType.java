package com.gm.customer;

public enum QuestionOpType {
	Question_Query(25000, "玩家反馈查询"),
	Question_Submit(25001, "玩家反馈新增"),
	;
	private int opType;
	private String desc;
	private QuestionOpType(int opType, String desc){
		this.opType = opType;
		this.desc = desc;
	}
	public int getOpType() {
		return opType;
	}
	public String getDesc() {
		return desc;
	}
	
	private static QuestionOpType[] allValues;
	
	public static QuestionOpType getByOpType(int opType){
		if(allValues == null){
			allValues = QuestionOpType.values();
		}
		for (QuestionOpType questionOpType : allValues) {
			if(questionOpType.getOpType() == opType){
				return questionOpType;
			}
		}
		return Question_Query;
	}
}
