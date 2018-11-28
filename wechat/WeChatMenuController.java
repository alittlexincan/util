package org.cn.net.ssd.wechat.controller;

import java.util.List;

import org.cn.net.ssd.wechat.config.WeChatConfig;
import org.cn.net.ssd.wechat.entity.WeChatMenu;
import org.cn.net.ssd.wechat.service.IWeChatMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@RestController
@RequestMapping("wechat/btn")
public class WeChatMenuController {
	
	@Autowired
	private IWeChatMenuService weChatMenuService;			//微信菜单
	
	@Autowired
	private RestTemplate restTemplate;
	
	/**
	 * 保存菜单
	 * @param map
	 * @return
	 */
	@PostMapping("save")
	public Object save(@RequestBody WeChatMenu weChatMenu){
		JSONObject json = new JSONObject();
		try {
			weChatMenu = this.weChatMenuService.save(weChatMenu);
			if(weChatMenu !=null){
				json.put("status", 200);  //成功
				//更新菜单
				boolean bool = updateMenuToWeixin();
				if(bool){
					System.out.println("菜单更新成功！");
				}else{
					System.out.println("菜单更新失败");
				}
			}else{
				json.put("status", 500);  //错误
			}
		} catch (Exception e) {
			e.printStackTrace();
			json.put("status", 500);
		}
		return json;
	}
	
	/**
	 * 获取所有菜单
	 * @param map
	 * @return
	 */
	@PostMapping("find")
	public List<WeChatMenu> find(){
		try {
			return this.weChatMenuService.findAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 更新菜单
	 * @return
	 */
	@GetMapping("update")
	public JSONObject updateWeChatMenu(){
		JSONObject json = new JSONObject();
		try {
			boolean bool = updateMenuToWeixin();
			if(bool){
				json.put("status", 200);
				json.put("meg", "菜单更新成功");
			}else{
				json.put("status", 500);
				json.put("meg", "菜单更新失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			json.put("status", 500);
			json.put("meg", "菜单更新失败");
		}
		return json;
	}
	
	/**
	 * 更新菜单到微信服务器
	 * @return
	 * @throws Exception
	 */
	private boolean updateMenuToWeixin() throws Exception{
		List<WeChatMenu> list = this.weChatMenuService.findAll();
		if(list != null && list.size()>0){
			JSONArray btn = new JSONArray();
			for(WeChatMenu menu : list){
				// 如果parentId==0则说明是一级菜单，否则为二级菜单
				if(!menu.getParentId().equals("0")){
					//父菜单，有子菜单
					JSONObject json = new JSONObject();
					json.put("name", menu.getBtnName());
					JSONArray sub_button = new JSONArray();
					for(WeChatMenu child : list){
						if(child.getParentId().equals(menu.getKeyId())){
							//所属子菜单
							JSONObject childMenu = new JSONObject();
							childMenu.put("name", child.getBtnName());
							childMenu.put("type", child.getBtnType());
							if(child.getBtnType().equals("view")){				// 试图菜单按钮
								childMenu.put("url", child.getContent());
							}else if(child.getBtnType().equals("click")){ 		// 点击菜单按钮
								childMenu.put("key", menu.getBtnKey());
							}else if(child.getBtnType().equals("miniprogram")){ // 小程序菜单按钮
								
							}
							sub_button.add(childMenu);
						}
					}
					json.put("sub_button", sub_button);
					btn.add(json);
				}else{
					//没有子菜单
					JSONObject oneMenu = new JSONObject();
					oneMenu.put("name", menu.getBtnName());
					oneMenu.put("type", menu.getBtnType());
					if(menu.getBtnType().equals("view")){// 试图菜单按钮
						oneMenu.put("url", menu.getContent());
					}else if(menu.getBtnType().equals("click")){// 点击菜单按钮
						oneMenu.put("key", menu.getBtnKey());
					}else if(menu.getBtnType().equals("miniprogram")){ // 小程序菜单按钮
						
					}
					btn.add(oneMenu);
				}
			}
			JSONObject json = new JSONObject();
			json.put("button", btn);
			String url = WeChatConfig.WECHAT_UPDATE_MENU+"?access_token="+WeChatConfig.WECHAT_TOKEN;
			
			JSONObject result = this.restTemplate.postForObject(url, json, JSONObject.class);
			if(result.getIntValue("errcode") == 0){
				return true;
			}else{
				System.out.println(result.toJSONString());
			}
		}
		return false;
	}

}
