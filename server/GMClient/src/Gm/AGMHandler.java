package Gm;

public abstract class AGMHandler {
	protected int opType;
	protected String account;
	protected String password;
	
	public abstract GmRequest createGmRequest();
}
