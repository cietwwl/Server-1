package com;

import com.rw.fsutil.util.MD5;

public class Test {

	
	public static void main(String[] args) {
		
		String md5String = MD5.getMD5String("fdafdsafdsafdasfdsafdafdafdsafdsafdasfdsafdafdafdsafdsafdasfdsafdafdafdsafdsafdasfdsafdafdafdsafdsafdasfdsafdafdafdsafdsafdasfdsafdafdafdsafdsafdasfdsafdafdafdsafdsafdasfdsafda");
		
		System.out.println(md5String);
	}
}
