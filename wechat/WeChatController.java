package org.cn.net.ssd.wechat.controller;

import org.cn.net.ssd.wechat.config.WeChatConfig;
import org.cn.net.ssd.wechat.utils.wechat.TokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

/**
 * 微信设置、配置控制层
 * @author JiangXincan
 *
 */
@RestController
@RequestMapping("wechat")
public class WeChatController {
	
	@Autowired
	private WeChatConfig weChatConfig;

	/**
	 * 获取、设置token
	 * @return
	 */
	@GetMapping("token")
	public JSONObject getToken(){
		JSONObject json = new JSONObject();
		//链接腾讯微信服务器，获取access_token
		String access_token = TokenManager.getToken(weChatConfig);
		if(!StringUtils.isEmpty(access_token)){
			json.put("status", 200);
			json.put("meg", "access_token更新成功");
		}else{
			json.put("status", 500);
			json.put("meg", "access_token更新失败");
		}
		return json;
	}
}
