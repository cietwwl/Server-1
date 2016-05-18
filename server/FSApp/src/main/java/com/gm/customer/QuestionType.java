package com.gm.customer;

public enum QuestionType {
	Q_CHARGE(1, "充值问题", ""),
	Q_GAME_EXCEPTION(2, "游戏异常", ""),
	Q_FEEDBACK(3, "建议反馈", "感谢您我们游戏的支持，我们将认真查看您的建议并回复，请留意。"),
	Q_REPORT(4, "举报", "亲爱的玩家，您的举报我们已经收到，会有相关人员进行反馈查证，若您非举报类问题，建议选择对应的问题类型提交，以便更好的为您服务，谢谢。")
	;
	
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
}
