package com.jstarcraft.example.common.utility;

import javax.servlet.http.HttpServletRequest;

import com.jstarcraft.core.utility.StringUtility;

public class HttpUtility {

	/**
	 * 获取IP地址
	 * 
	 * @param request
	 * @return
	 */
	public static String getIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (StringUtility.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (StringUtility.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (StringUtility.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		// 多级反向代理
		if (!StringUtility.isBlank(ip)) {
			int index = ip.indexOf(",");
			if (index > 0) {
				ip = ip.substring(0, index);
			}
		}
		return ip;
	}

}
