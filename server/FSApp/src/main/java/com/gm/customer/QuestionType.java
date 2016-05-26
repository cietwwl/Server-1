package com.gm.customer;

public enum QuestionType {
	Q_CHARGE(1, "充值问题", ""),
	Q_GAME_EXCEPTION(2, "游戏异常", ""),
	Q_FEEDBACK(3, "建议反馈", ""),
	Q_REPORT(4, "举报", "亲爱的玩家，您的举报我们已经收到，会有相关人员进行反馈查证，若您非举报类问题，建议选择对应的问题类型提交，以便更好的为您服务，谢谢。"),
	Q_READFEEDBACK(-1, "阅读回复", "");
	
	private int type;
	private String typeDesc;
	private String autoAnswer;
	private QuestionType(int type, String typeDesc, String autoAnswer){
		this.type = type;
		this.typeDesc = typeDesc;
		this.autoAnswer = autoAnswer;
	}
	
	public int getType() {
		return type;
	}
	public String getTypeDesc() {
		return typeDesc;
	}
	public String getAutoAnswer() {
		return autoAnswer;
	}
	
	private static QuestionType[] allValues;
	
	public static QuestionType getQuestionType(int type){
		if(allValues == null){
			allValues = QuestionType.values();
		}
		for (QuestionType questionType : allValues) {
			if(questionType.getType() == type){
				return questionType;
			}
		}
		return Q_FEEDBACK;
	}
}
