package org.cn.net.ssd.wechat.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cn.net.ssd.wechat.config.WeChatConfig;
import org.cn.net.ssd.wechat.utils.wechat.SignUtil;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 *  对接微信服务器控制层
 *  提供get，post方式对接并处理相关信息
 *  @author JiangXincan
 *
 */
@RequestMapping("server")
@RestController
public class WeixinServerController {
	
	@Autowired
	private WeChatConfig weChatConfig;

    /**
     * 接收get请求，用于验证微信服务器对接，服务器签名
     * @param response
     * @param paramMap
     */
    @GetMapping
	public void get(HttpServletResponse response,@RequestParam(required=false) Map<String,Object> paramMap){
		try {
			System.out.println(paramMap);
			//对接服务  对signature、timestamp、nonce、echostr进行验证
			if(paramMap != null && paramMap.get("echostr") != null){
//				boolean bool = SignUtil.checkSignature(WeChatConfig.WECHAT_SIGN_TOKEN,paramMap.get("signature").toString(), paramMap.get("timestamp").toString(), paramMap.get("nonce").toString());
				boolean bool = SignUtil.checkSignature(weChatConfig.getSignToken(),paramMap.get("signature").toString(), paramMap.get("timestamp").toString(), paramMap.get("nonce").toString());
				if(bool){
					response.getWriter().write(paramMap.get("echostr").toString());
					response.getWriter().close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    /**
     * 处理post请求，用于处理处理用户事件及用户消息
     * @param request
     * @param response
     */
    @PostMapping
  	public void post(HttpServletRequest request,HttpServletResponse response){
    	try{
    		ServletInputStream in = request.getInputStream();
			SAXReader reader = new SAXReader(); 
			Document doc = reader.read(in);
			System.out.println("post:" + doc.asXML());
			Element root = doc.getRootElement();
			String msgType = root.elementText("MsgType");
			String result = "";
			if(msgType.equals("event")){
				//处理事件请求
				result = manageEvent(root);
			}else if(msgType.equals("text")){
				//处理关注用户发送的信息
			}
			response.getWriter().write(result);
			response.getWriter().close();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    /**
     * 处理事件请求
     * @param root
     * @return
     * @throws Exception
     */
    private String manageEvent(Element root) throws Exception{
    	String toUserName = root.elementText("ToUserName");       	//接收请求的微信公众号
		String fromUserName = root.elementText("FromUserName");	  	//发出请求的关注用户号
		String result = "";										  	//返回给微信服务器的xml，用于响应
    	String event = root.elementText("Event");					//获取事件类型
		if("subscribe".equals(event)){								//添加关注事件
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("user_name", toUserName);
				//通过公众号获取微信设置
				//获取设置的自动回复信息
				//拼装返回xml信息
				result = "<xml>"
					+"<ToUserName><![CDATA["+fromUserName+"]]></ToUserName>"
					+"<FromUserName><![CDATA["+toUserName+"]]></FromUserName>"
					+"<CreateTime>"+getTimeStamp()+"</CreateTime>"
					+"<MsgType><![CDATA[text]]></MsgType>"
					+"<Content><![CDATA[欢迎关注微信]]></Content>"
					+"</xml>";
		}else if("CLICK".equals(event)){							//菜单按钮点击事件
			String key = root.elementText("EventKey");				//按钮设置的key值，唯一标识
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("key", key);
			
		}
    	return result;
    }
    
    /**
     * 获取时间戳，10位
     * @return
     */
    private String getTimeStamp(){
    	return String.valueOf(System.currentTimeMillis()).substring(0,10);
    }
}
