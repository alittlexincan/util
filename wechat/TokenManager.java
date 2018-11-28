package org.cn.net.ssd.wechat.utils.wechat;

import org.apache.log4j.Logger;
import org.cn.net.ssd.wechat.config.WeChatConfig;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;

/**
 * 获取access_token
 * @author JiangXincan
 *
 */
public class TokenManager {
	
	private static Logger log = Logger.getLogger(TokenManager.class);
	
	/**
	 * 获取微信access_token
	 * @param appId
	 * @param appSecret
	 * @return
	 */
	public static String getToken(WeChatConfig weChatConfig){
		String url = WeChatConfig.GET_TOKEN_URL
				+"?grant_type=client_credential"
				+ "&appid="+weChatConfig.getAppId()
				+ "&secret="+weChatConfig.getAppsecret();
		RestTemplate rest = new RestTemplate();
		JSONObject result = rest.getForObject(url, JSONObject.class);
		if(result == null){
			System.out.println(weChatConfig.getAppId()+"  更新失败。");
			log.info(weChatConfig.getAppId() +"：获取access_token失败！");
			return null;
		}
		if(result.containsKey("access_token")){
			log.info(weChatConfig.getAppId() +"：获取access_token成功！");
			WeChatConfig.WECHAT_TOKEN = result.getString("access_token");
			return result.getString("access_token");
		}
		log.error(weChatConfig.getAppId()+"：获取access_token失败！");
		return null;
	}
	
}
