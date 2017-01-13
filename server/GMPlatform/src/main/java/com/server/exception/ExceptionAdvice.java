package com.server.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.server.paramers.RESTRespone;

/**
 * 自定义的异常处理类型，这里面只是定义了一部分，如果还有其他的类型再添加 
 * @author Alex
 * 2017年1月13日 下午6:18:07
 */
@ControllerAdvice
@ResponseBody
public class ExceptionAdvice {

	private Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);
	
	/**
	 * 400 - Bad Resquest
	 * @return
	 */
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public RESTRespone handleHttpMessageNotReadableException(HttpMessageNotReadableException e){
		logger.error("参数解析失败", e);
		return new RESTRespone().failure("could not read message");
	}
	
	
	/**
	 * 405 - Method Not Allowed
	 * @param e
	 * @return
	 */
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public RESTRespone handleHttpMessageNotSupportException(HttpRequestMethodNotSupportedException e){
		logger.error("不支持当前请求方法", e);
		return new RESTRespone().failure("request method not support");
	}
	
	/**
	 * 415 - Unsupported Media Type
	 * @param e
	 * @return
	 */
	@ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public RESTRespone handleHttpMediaTypeNotSupportException(HttpMediaTypeNotSupportedException e){
		logger.error("不支持当前媒体类型", e);
		return new RESTRespone().failure("content type not support");
	}
	
	
	/**
	 * 500 - Internal Server Error
	 * @param e
	 * @return
	 */
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public RESTRespone handleException(Exception e){
		logger.error("服务器内部异常", e);
		return new RESTRespone().failure(e.getMessage());
	}
}
