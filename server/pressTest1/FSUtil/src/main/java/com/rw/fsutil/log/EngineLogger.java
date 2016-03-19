package com.rw.fsutil.log;

public interface EngineLogger {
	
	public String getName() ;
	
	public void fatal(String s) ;

	public void info(String s) ;

	public void warn(String s);

	public void warn(String s, Throwable t);

	public void error(String s) ;

	public void error(String s, Throwable t);
}
