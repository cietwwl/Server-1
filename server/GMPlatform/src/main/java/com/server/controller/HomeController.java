package com.server.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.server.beans.User;
import com.server.constant.GlobalKey;
import com.server.constant.TipsConstant;
import com.server.paramers.RESTResponse;
import com.server.service.UserService;

@RestController()
@RequestMapping("user")
public class HomeController {

	private Logger log = LoggerFactory.getLogger(HomeController.class);

	@Autowired
	private UserService userSerice;
	
	@Resource
	HttpServletRequest requst;
	/**
	 * 玩家登录
	 * @param user
	 * @return
	 */
	@RequestMapping(value="login", method={RequestMethod.POST})
	public @ResponseBody RESTResponse login(@RequestBody User user){
		log.info("try to login, user name[{}],paw[{}]",user.getAccount(),user.getPassword());
		boolean login = userSerice.doUserLogin(user);
		if(login){
			requst.getSession().setAttribute(GlobalKey.USER_SESSION_KEY, user);
			return new RESTResponse().success();
		}
		return new RESTResponse().failure(TipsConstant.USER_DATA_WRONG);
	}
	
	
	/**
	 * 玩家注册
	 * @param user
	 * @return
	 */
	@RequestMapping(value="register", method={RequestMethod.POST})
	public @ResponseBody RESTResponse register(@RequestBody User user){
		try {
			
			boolean hasAccount = userSerice.checkUserExist(user.getAccount());
			if(hasAccount){
				return new RESTResponse().failure(TipsConstant.ACCOUNT_EXIST);
			}
			boolean suc = userSerice.registerUser(user);
			if(suc){
				//注册成功，让角色登录
				requst.getSession().setAttribute(GlobalKey.USER_SESSION_KEY, user);
				return new RESTResponse().success();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new RESTResponse().failure(TipsConstant.SYSTEM_ERROR);
	}
}
