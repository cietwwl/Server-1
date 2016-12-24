package com.rw.config;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

/**
 * @Author HC
 * @date 2016年12月13日 下午5:09:22
 * @desc
 **/

public class YaoWanLogConfig {
	private static YaoWanLogConfig instance = new YaoWanLogConfig();

	public static YaoWanLogConfig getInstance() {
		return instance;
	}

	private YaoWanLogConfig() {
	}

	private boolean open;
	private String game_name;
	private String secret_key;
	private String default_browser_ver;

	private String install_log_url;
	private String install_log_action_param;
	private String install_log_required_param;

	private String register_log_url;
	private String register_log_action_param;
	private String register_log_required_param;

	private String login_log_url;
	private String login_log_action_param;
	private String login_log_required_param;

	private String charge_log_url;
	private String charge_log_action_param;
	private String charge_log_required_param;

	private static Field[] fields;
	static {
		fields = YaoWanLogConfig.class.getDeclaredFields();
	}

	/**
	 * 启动并初始化数据
	 * 
	 * @return
	 */
	public static void init() {
		if (fields == null) {
			return;
		}

		Resource resource = new ClassPathResource("yaowanlog.properties");
		try {
			Properties properties = PropertiesLoaderUtils.loadProperties(resource);
			if (properties == null) {
				return;
			}

			int modValue = Modifier.PRIVATE;

			for (int i = 0, len = fields.length; i < len; i++) {
				Field field = fields[i];
				if (modValue != field.getModifiers()) {
					continue;
				}

				field.setAccessible(true);
				Object object = properties.get(field.getName());
				if (field.getType() == boolean.class) {
					field.setBoolean(instance, object.toString().equalsIgnoreCase("true"));
				} else {
					field.set(instance, object);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 是否要收集用户数据
	 * 
	 * @return
	 */
	public boolean isOpen() {
		return open;
	}

	/**
	 * 获取游戏的名字
	 * 
	 * @return
	 */
	public String getGame_name() {
		return game_name;
	}

	/**
	 * 获取MD5加密的密钥
	 * 
	 * @return
	 */
	public String getSecret_key() {
		return secret_key;
	}

	/**
	 * 获取默认发送的浏览器版本
	 * 
	 * @return
	 */
	public String getDefault_browser_ver() {
		return default_browser_ver;
	}

	/**
	 * 获取上传安装日志的连接
	 * 
	 * @return
	 */
	public String getInstall_log_url() {
		return install_log_url;
	}

	/**
	 * 获取上传安装日志的固定参数
	 * 
	 * @return
	 */
	public String getInstall_log_action_param() {
		return install_log_action_param;
	}

	/**
	 * 获取上传安装日志的必须参数
	 * 
	 * @return
	 */
	public String getInstall_log_required_param() {
		return install_log_required_param;
	}

	/**
	 * 获取注册用户日志的连接
	 * 
	 * @return
	 */
	public String getRegister_log_url() {
		return register_log_url;
	}

	/**
	 * 获取上传注册用户日志的固定参数
	 * 
	 * @return
	 */
	public String getRegister_log_action_param() {
		return register_log_action_param;
	}

	/**
	 * 获取上传注册用户日志的必须参数
	 * 
	 * @return
	 */
	public String getRegister_log_required_param() {
		return register_log_required_param;
	}

	/**
	 * 登录游戏日志上传地址
	 * 
	 * @return
	 */
	public String getLogin_log_url() {
		return login_log_url;
	}

	/**
	 * 登录日志上传固定参数
	 * 
	 * @return
	 */
	public String getLogin_log_action_param() {
		return login_log_action_param;
	}

	/**
	 * 登录日志上传必须参数
	 * 
	 * @return
	 */
	public String getLogin_log_required_param() {
		return login_log_required_param;
	}

	/**
	 * 充值成功日志上传地址
	 * 
	 * @return
	 */
	public String getCharge_log_url() {
		return charge_log_url;
	}

	/**
	 * 充值成功日志上传固定参数
	 * 
	 * @return
	 */
	public String getCharge_log_action_param() {
		return charge_log_action_param;
	}

	/**
	 * 充值成功日志上传必须参数
	 * 
	 * @return
	 */
	public String getCharge_log_required_param() {
		return charge_log_required_param;
	}
}