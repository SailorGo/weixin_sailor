package com.hyx.common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.opensymphony.webwork.ServletActionContext;

public class MaizuoLogUtil {

	/**
	 * 写log <br/>
	 * linux默认地址 : /data/logs/maizuo.log <br/>
	 * windows默认地址：F:/data/logs/maizuo.log <br/>
	 * 
	 * 1 支付中心 统一支付系统 <br/>
	 * 2 订单中心 统一订单系统 <br/>
	 * 3 订座中心 统一订座系统 <br/>
	 * 4 电子票中心 统一电子票处理系统 <br/>
	 * 5 电影卡中心 统一电影卡系统 <br/>
	 * 6 手机中转server 负责手机端业务接口 <br/>
	 * 7 银行优惠系统 负责银行优惠的接口 <br/>
	 * 8 商品系统 商品系统 <br/>
	 * 9 短信中心 短信的下发 <br/>
	 * 10 前端web 卖座的前端日志 <br/>
	 * 11 用户server 用户信息server<br/>
	 * 12 自助机后台 自助机后台<br/>
	 * 13 现金卡系统 现金卡系统<br/>
	 * 14 Feed server Feed server<br/>
	 * 15 积分中心<br/>
	 * 16 高朋兑票系统
	 * 
	 * @param fromtype
	 *            调用设备类型
	 * @param totype
	 *            被调用设备类型
	 * @param param
	 *            业务调用参数
	 * @param interfaceUri
	 *            业务调用接口名称
	 * @param result
	 *            业务返回结果
	 * @param other
	 *            业务其他信息
	 */
	public static void writeLog(String fromtype, String totype, String param,
			String interfaceUri, String result, String other) {
		/**
		 * 格式 {"@timestamp":"2012-10-31T15:52:54+08:00",
		 * "@source":"192.168.1.203", "@fields":{ "fromtype":1, "totype":1,
		 * "param":"userid=12&cinemaId=152",
		 * "interface":"http://www.maizuo.com/pay.htm", "result":"ok",
		 * "other":"www.maizuo.com" }}
		 */

		if (param != null) {
			param = param.replaceAll("\"", "\'");
			param = param.replaceAll("\n", " ");
		}
		if (interfaceUri != null) {
			interfaceUri = interfaceUri.replaceAll("\"", "\'");
			interfaceUri = interfaceUri.replaceAll("\n", " ");
		}
		if (result != null) {
			result = result.replaceAll("\"", "\'");
			result = result.replaceAll("\n", " ");
		}
		if (other != null) {
			other = other.replaceAll("\"", "\'");
			other = other.replaceAll("\n", " ");
		}
		String jsonLog = "{\"@timestamp\":\"" + getCurTime() + "\","
				+ "\"@source\":\"" + getRealIp() + "\"," + "\"@fields\":{"
				+ "\"fromtype\":\"" + fromtype + "\"," + "\"totype\":\""
				+ totype + "\"," + "\"param\":\"" + param + "\","
				+ "\"interface\":\"" + interfaceUri + "\"," + "\"result\":\""
				+ result + "\"," + "\"other\":\"" + other + "\"" + "}}\n";
		writeFile(jsonLog);
	}

	public static void writeLog(String fromtype, String totype, String param,
			String interfaceUri, String result, String other, String alarmID) {
		/**
		 * 格式 {"@timestamp":"2012-10-31T15:52:54+08:00",
		 * "@source":"192.168.1.203", "@fields":{ "fromtype":1, "totype":1,
		 * "param":"userid=12&cinemaId=152",
		 * "interface":"http://www.maizuo.com/pay.htm", "result":"ok",
		 * "other":"www.maizuo.com" }}
		 */
		if (param != null) {
			param = param.replaceAll("\"", "\'");
			param = param.replaceAll("\n", " ");
		}
		if (interfaceUri != null) {
			interfaceUri = interfaceUri.replaceAll("\"", "\'");
			interfaceUri = interfaceUri.replaceAll("\n", " ");
		}
		if (result != null) {
			result = result.replaceAll("\"", "\'");
			result = result.replaceAll("\n", " ");
		}
		if (other != null) {
			other = other.replaceAll("\"", "\'");
			other = other.replaceAll("\n", " ");
		}
		String jsonLog = "{\"@timestamp\":\"" + getCurTime() + "\","
				+ "\"@source\":\"" + getRealIp() + "\"," + "\"@fields\":{"
				+ "\"fromtype\":\"" + fromtype + "\"," + "\"totype\":\""
				+ totype + "\"," + "\"param\":\"" + param + "\","
				+ "\"interface\":\"" + interfaceUri + "\"," + "\"result\":\""
				+ result + "\"," + "\"alarmID\":\"" + alarmID + "\","
				+ "\"other\":\"" + other + "\"" + "}}\n";
		writeFile(jsonLog);
	}

	private static void writeFile(String jsonLog) {
		String fs = System.getProperties().getProperty("file.separator");
		if (fs.equals("/")) {
			FileWriter fw = null;
			try {
				File file_linux = new File("/data/logs/");
				if (!file_linux.exists()) {
					file_linux.mkdirs();
				}
				File filelogs_linux = new File("/data/logs/maizuo.log");
				if (!filelogs_linux.exists()) {
					filelogs_linux.createNewFile();
				}
				fw = new FileWriter(filelogs_linux, true);
				fw.write(jsonLog);
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (fw != null) {
					try {
						fw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			// windows
			FileWriter fw = null;
			try {
				File file_wp = new File("F:/data/");
				if (!file_wp.exists()) {
					boolean b = file_wp.mkdirs();
					if (b == false)
						return;
				}
				file_wp = new File("F:/data/logs/");
				if (!file_wp.exists()) {
					file_wp.mkdirs();
				}
				File filelogs_linux = new File("F:/data/logs/maizuo.log");
				if (!filelogs_linux.exists()) {
					filelogs_linux.createNewFile();
				}
				fw = new FileWriter(filelogs_linux, true);
				fw.write(jsonLog);
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (fw != null) {
					try {
						fw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static void writeLog(Map<String, Object> map) {
		String mapJson = JsonUtil.toJSON(map);
		String jsonLog = "{\"@timestamp\":\"" + getCurTime() + "\","
				+ "\"@source\":\"" + getRealIp() + "\"," + "\"@fields\":"
				+ mapJson + "}\n";
		writeFile(jsonLog);
	}

	public static String packPars(Map<String, Object> map) {
		HttpServletRequest request = ServletActionContext.getRequest();
		if (map == null)
			map = new HashMap<String, Object>();
		map.put("fromtype", "maizuo");
		map.put("interfaceUri", request.getRequestURI());
		map.put("domain",
				request.getServerName() + ":" + request.getServerPort());
		// String mapJson = com.hyx.tool.JsonConver.mapConverJson(map);
		String mapJson = "";
		String json = "{\"@timestamp\":\"" + getCurTime() + "\","
				+ "\"@source\":\"" + getRealIp() + "\"," + "\"@fields\":"
				+ mapJson + "}";
		System.out.println(json);
		return json;
	}

	/**
	 * 获取当前时间
	 * 
	 * @return
	 */
	public static String getCurTime() {
		Calendar calendar = new GregorianCalendar();
		Date date = calendar.getTime();
		SimpleDateFormat format = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss'+08:00'");
		return format.format(date);
	}

	/**
	 * 获取本机ip
	 * 
	 * @return
	 */
	public static String getRealIp() {
		String localip = null;// 本地IP，如果没有配置外网IP则返回它
		String netip = null;// 外网IP
		try {
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface
					.getNetworkInterfaces();
			InetAddress ip = null;
			boolean finded = false;// 是否找到外网IP
			while (netInterfaces.hasMoreElements() && !finded) {
				NetworkInterface ni = netInterfaces.nextElement();
				Enumeration<InetAddress> address = ni.getInetAddresses();
				while (address.hasMoreElements()) {
					ip = address.nextElement();
					if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
							&& ip.getHostAddress().indexOf(":") == -1) {// 外网IP
						netip = ip.getHostAddress();
						finded = true;
						break;
					} else if (ip.isSiteLocalAddress()
							&& !ip.isLoopbackAddress()
							&& ip.getHostAddress().indexOf(":") == -1) {// 内网IP
						localip = ip.getHostAddress();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (netip != null && !"".equals(netip)) {
			return netip;
		} else {
			return localip;
		}
	}

	/**
	 * 日志方式1
	 * 
	 * @param fromtype
	 * @param totype
	 * @param param
	 * @param interfaceUri
	 * @param result
	 * @param other
	 * @param alarmID
	 * @param processTime
	 * @param errorType
	 */
	public static void writeLog(String fromtype, String totype, String param,
			String interfaceUri, String result, String other, String alarmID,
			long processTime, String errorType) {

		/**
		 * 格式 {"@timestamp":"2012-10-31T15:52:54+08:00",
		 * "@source":"192.168.1.203", "@fields":{"fromtype":1, "totype":1,
		 * "param":"userid=12&cinemaId=152",
		 * "interface":"http://www.maizuo.com/pay.htm", "result":"ok",
		 * "other":"www.maizuo.com", "processTime":35， "errorType":0 }}
		 */

		if (param != null) {
			param = param.replaceAll("\"", "\'");
			param = param.replaceAll("\n", " ");
		}
		if (interfaceUri != null) {
			interfaceUri = interfaceUri.replaceAll("\"", "\'");
			interfaceUri = interfaceUri.replaceAll("\n", " ");
		}
		if (result != null) {
			result = result.replaceAll("\"", "\'");
			result = result.replaceAll("\n", " ");
		}
		if (other != null) {
			other = other.replaceAll("\"", "\'");
			other = other.replaceAll("\n", " ");
		}
		String jsonLog = "{\"@timestamp\":\"" + getCurTime() + "\","
				+ "\"@source\":\"" + getRealIp() + "\"," + "\"@fields\":{"
				+ "\"fromtype\":\"" + fromtype + "\"," + "\"totype\":\""
				+ totype + "\"," + "\"param\":\"" + param + "\","
				+ "\"interface\":\"" + interfaceUri + "\"," + "\"result\":\""
				+ result + "\"," + "\"alarmID\":\"" + alarmID + "\","
				+ "\"other\":\"" + other + "\"," + "\"processTime\":\""
				+ processTime + "\"," + "\"errorType\":\"" + errorType + "\""
				+ "}}\n";
		writeFile(jsonLog);
	}

	public static void main(String[] args) {
		// 调用方法
		MaizuoLogUtil.writeLog("1", "2", "userId=1&cinemaId=2 \n kfjdlsf ",
				"http://www.maizuo.com/pay.htm", "{\"goodsId\":2}", null);
	}
}
