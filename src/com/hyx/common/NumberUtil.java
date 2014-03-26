package com.hyx.common;

import java.math.BigDecimal;

/**
 * @author  vicen E-mail:hailors@qq.com
 * @version 创建时间：2014-3-12 下午3:33:57
 * TODO
 */
public class NumberUtil {
	
	/**
	 * 金额单位换算
	 * @param money
	 * @param divisor
	 * @return
	 */
	public static BigDecimal getMoneyConversion(int money,int divisor){
		BigDecimal a=new BigDecimal(money);
		BigDecimal b=new BigDecimal(divisor);
		return a.divide(b);
	}

}


