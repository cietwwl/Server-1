package com.rw.service.http;

import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.rw.service.http.request.RequestObject;
import com.rw.service.http.response.BaseMsgResponse;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import io.netty.handler.codec.http.HttpHeaders.Values;

@SuppressWarnings("rawtypes")
public class GSResponseMgr {
	
	/**
	 * 处理gm后台发过来的请求
	 * @param request
	 * @param obj
	 * @param ctx
	 */
	public static void processMsg(HttpRequest request, Object obj, ChannelHandlerContext ctx) {
		try {
			RequestObject requestObject = (RequestObject) obj;
			String className = requestObject.getClassName();
			String methodName = requestObject.getMethodName();
			ArrayList<HashMap<Class, Object>> paramList = requestObject
					.getParamList();
			Object result = invokeMethod(paramList, methodName, className);
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream oo = new ObjectOutputStream(bo);
			oo.writeObject(result);
			
			byte[] bytes = bo.toByteArray();
			bo.close();
			oo.close();
			
			FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(bytes));
			response.headers().set(CONTENT_TYPE, "text/plain");
			response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
			if(HttpHeaders.isKeepAlive(request)){
				response.headers().set(CONNECTION, Values.KEEP_ALIVE);
			}
			ctx.write(response);
			ctx.flush();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	

	/**
	 * 反射调用方法
	 * @param list
	 * @param methodName
	 * @param className
	 * @return
	 * @throws Exception
	 */
	private static Object invokeMethod(ArrayList<HashMap<Class, Object>> list, String methodName, String className)throws Exception{
		Class<?> forName = Class.forName(className);
		Object[] args = new Object[list.size()];
		Class[] argsClass = new Class[list.size()];
		int count = 0;
		for (HashMap<Class, Object> map : list) {
			for (Iterator<Entry<Class, Object>> iterator = map.entrySet().iterator(); iterator.hasNext();) {
				Entry<Class, Object> entry = iterator.next();
				Object param = FieldTypeHelper.parseType(entry.getKey(), entry.getValue());
				args[count]= param;
				argsClass[count] = entry.getKey();
			}
			count++;
		}
		Method method = forName.getMethod(methodName, argsClass);
		Object result = method.invoke(null, args);
		return result;
	}
	
	public static BaseMsgResponse PackBaseMsg(String msg){
		BaseMsgResponse baseMsgResponse = new BaseMsgResponse();
		baseMsgResponse.setMsg(msg);
		return baseMsgResponse;
	}
}
