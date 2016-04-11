package Gm.Task.Params;

public enum EEmailDeleteType {
	GET_DELETE(1),//领取删除
	DELAY_TIME(2),//延时删除
	DEADLINE_TIME(3);//到期删除
	
	int _value;
	EEmailDeleteType(int value){
		_value = value;
	}
	
	public int getValue(){
		return _value;
	}
	
	public static EEmailDeleteType valueOf(int value){
		EEmailDeleteType result = null;
		for(int i = 0; i < values().length; i++){
			if(values()[i].getValue() == value){
				result = values()[i];
				break;
			}
		}
		return result;
	}
}
