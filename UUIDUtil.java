package org.cn.net.ssd.wechat.utils;

import java.util.UUID;

/**
 * UUID工具类
 * @ClassName: UUIDUtil
 * @author JiangXincan
 * @date 2018年1月12日 下午5:08:28
 *
 */
public class UUIDUtil {

	/**
	 * 获取UUID
	 * getUUID(UUID)
	 *
	 * @author JiangXincan
	 * @Title: getUUID
	 * @param @return    设定文件
	 * @return String    返回类型
	 * @throws
	 */
	public static String getUUID(){
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
}
