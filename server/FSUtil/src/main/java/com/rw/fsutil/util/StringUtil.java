package com.rw.fsutil.util;

public class StringUtil {

	public static long hashUidToLong(String uid)   
	{
		long result=0;
		for (int i=1;i<5;i++)   
		{   
			long ch= Long.parseLong(String.valueOf(uid.charAt(uid.length()-i)),16);   
			//System.out.println(String.valueOf(uid.charAt(uid.length()-i)));
			result+=ch*power(16, i);
		}	
		return result;
	}
	
	private static long power(int x,int n)
	{
		long result=x;
		for(int i=0;i<n-1;i++)
		{
			result*=x;
		}
		return result;
	}
	
	//转化字符串为十六进制编码
	public static String toHexString(String s)   
	{   
	   String str="";   
	   for (int i=0;i<s.length();i++)   
	   {   
	    int ch = s.charAt(i);   
	    String s4 = Integer.toHexString(ch);   
	    str = str + s4; 
	   }   
	   return "0x" + str;//0x表示十六进制
	}

	//转换十六进制编码为字符串
	public static String toStringHex(String s)
	{
	   if("0x".equals(s.substring(0, 2)))
	   {
	    s =s.substring(2);
	   }
	   byte[] baKeyword = new byte[s.length()/2];
	   for(int i = 0; i < baKeyword.length; i++)
	   {
	      try
	      {
	       baKeyword[i] = (byte)(0xff & Integer.parseInt(s.substring(i*2, i*2+2),16));
	      }
	      catch(Exception e)
	      {
	       e.printStackTrace();
	      }
	   }
	   try 
	   {
	    s = new String(baKeyword, "utf-8");//UTF-16le:Not
	   } 
	   catch (Exception e1) 
	   {
	    e1.printStackTrace();
	   } 
	   return s;
	}

}
