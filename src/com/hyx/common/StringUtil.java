package com.hyx.common;
/**
 * @author  vicen E-mail:hailors@qq.com
 * @version 创建时间：2014-3-7 上午10:23:16
 * TODO
 */
public class StringUtil {
	
	/**
	 * 判断字符串是否不为空或者空字符串
	 * @param str
	 * @return
	 */
	public static boolean isNotNullAndBlank(String str){
		if(str!=null&&str.trim().length()>0){
			return true;
		}else{
			return false;
		}
	}
	
}


