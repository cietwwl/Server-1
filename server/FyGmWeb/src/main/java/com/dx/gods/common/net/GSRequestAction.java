package com.dx.gods.common.net;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;
import com.rw.service.http.request.RequestObject;

@SuppressWarnings("rawtypes")
public class GSRequestAction {
	
	public static AtomicLong atoNum = new AtomicLong(1);
	private RequestObject requestObject;
	
	public GSRequestAction(){
		requestObject = new RequestObject();
	}
	/**
	 * 传参
	 * @param clazz
	 * @param obj
	 */
	public void pushParams(Class clazz, Object obj){
		requestObject.pushParam(clazz, obj);
	}
	
	/**
	 * 远程调用方法
	 * @param className
	 * @param methodName
	 * @return
	 */
	public Object remoteCall(String strUrl, String className, String methodName)throws Exception {
		ObjectInputStream input = null;
		ObjectOutputStream output = null;
		Object obj = null;
		try {
			requestObject.setClassName(className);
			requestObject.setMethodName(methodName);
			URL url = new URL(strUrl);
			URLConnection connection = url.openConnection();
			connection.setDoOutput(true);
			output = new ObjectOutputStream(connection.getOutputStream());
			output.writeObject(requestObject);
			input = new ObjectInputStream(connection.getInputStream());
			obj = input.readObject();

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception(ex.getMessage()); 
		} finally {
			try {
				output.close();
				input.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return obj;
	}
}
