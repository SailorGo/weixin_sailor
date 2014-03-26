package com.hyx.common;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.opensymphony.webwork.ServletActionContext;

/**
 * 序列化对象为JSON格式 遵循JSON组织公布标准
 */
public class JsonUtil {

	/**
	 * @param obj
	 *            任意对象
	 * @return String
	 */
	private static String object2Json(Object obj) {
		StringBuilder json = new StringBuilder();
		if (obj == null) {
			json.append("\"\"");
		} else if (obj instanceof String || obj instanceof Integer
				|| obj instanceof Float || obj instanceof Boolean
				|| obj instanceof Short || obj instanceof Double
				|| obj instanceof Long || obj instanceof BigDecimal
				|| obj instanceof BigInteger || obj instanceof Byte) {
			json.append("\"").append(string2Json(obj.toString())).append("\"");
		} else if (obj instanceof Object[]) {
			json.append(array2Json((Object[]) obj));
		} else if (obj instanceof List) {
			json.append(list2Json((List<?>) obj));
		} else if (obj instanceof Map) {
			json.append(map2Json((Map<?, ?>) obj));
		} else if (obj instanceof Set) {
			json.append(set2Json((Set<?>) obj));
		} else {
			json.append(bean2Json(obj));
		}

		return json.toString();
	}

	/**
	 * @param bean
	 *            bean对象
	 * @return String
	 */
	private static String bean2Json(Object bean) {
		StringBuilder json = new StringBuilder();
		json.append("{");
		PropertyDescriptor[] props = null;
		try {
			props = Introspector.getBeanInfo(bean.getClass(), Object.class)
					.getPropertyDescriptors();
		} catch (IntrospectionException e) {
		}
		if (props != null) {
			for (int i = 0; i < props.length; i++) {
				try {
					String name = object2Json(props[i].getName());
					String value = object2Json(props[i].getReadMethod().invoke(
							bean));
					json.append(name);
					json.append(":");
					json.append(value);
					json.append(",");
				} catch (Exception e) {
				}
			}
			json.setCharAt(json.length() - 1, '}');
		} else {
			json.append("}");
		}
		return json.toString();
	}

	/**
	 * @param list
	 *            list对象
	 * @return String
	 */
	private static String list2Json(List<?> list) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (list != null && list.size() > 0) {
			for (Object obj : list) {
				json.append(object2Json(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	/**
	 * @param array
	 *            对象数组
	 * @return String
	 */
	private static String array2Json(Object[] array) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (array != null && array.length > 0) {
			for (Object obj : array) {
				json.append(object2Json(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	/**
	 * @param map
	 *            map对象
	 * @return String
	 */
	private static String map2Json(Map<?, ?> map) {
		StringBuilder json = new StringBuilder();
		json.append("{");
		if (map != null && map.size() > 0) {
			for (Object key : map.keySet()) {
				json.append(object2Json(key));
				json.append(":");
				json.append(object2Json(map.get(key)));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, '}');
		} else {
			json.append("}");
		}
		return json.toString();
	}

	/**
	 * @param set
	 *            集合对象
	 * @return String
	 */
	private static String set2Json(Set<?> set) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (set != null && set.size() > 0) {
			for (Object obj : set) {
				json.append(object2Json(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	/**
	 * @param s
	 *            参数
	 * @return String
	 */
	private static String string2Json(String s) {
		if (s == null)
			return "";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			switch (ch) {
			case '"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '/':
				sb.append("\\/");
				break;
			default:
				if (ch >= '\u0000' && ch <= '\u001F') {
					String ss = Integer.toHexString(ch);
					sb.append("\\u");
					for (int k = 0; k < 4 - ss.length(); k++) {
						sb.append('0');
					}
					sb.append(ss.toUpperCase());
				} else {
					sb.append(ch);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 转化成json
	 */
	public static String toJSON(Object toJsonObject) {
		if (toJsonObject == null) {
			return "[]";
		}
		String json = object2Json(toJsonObject);
		return json;
	}

	/**
	 * 将json字符串转换成java对象,需要Gson包支持 示例:PreBook
	 * preBook=JsonUtil.json2Bean(str,PreBook.class);
	 * 
	 * @param <T>
	 * @param json
	 * @param classOfT
	 * @return
	 * 
	 */
	public static <T> T json2Bean(String json, Class<T> classOfT) {
		Gson gson = new Gson();
		return gson.fromJson(json, classOfT);
	}

	//
	public static <T> T beanToMap(Object bean, Type typeofT) {
		Gson gson = new Gson();
		String jsonStr = bean2Json(bean);
		System.out.println(jsonStr);
		return gson.fromJson(jsonStr, typeofT);
	}

	public static <T> T mapToBean(Map<?, ?> map, Class<T> classOfT) {
		Gson gson = new Gson();
		String jsonStr = map2Json(map);
		return gson.fromJson(jsonStr, classOfT);
	}

	//

	/**
	 * 将json字符串转换成list,需要Gson包支持
	 * 
	 * @param <T>
	 * @param json
	 * @param object
	 * @return
	 */
	public static <T> T json2List(String json, Type typeOfT) {
		Gson gson = new Gson();
		return gson.fromJson(json, typeOfT);
	}

	/**
	 * 向前台输出json数据
	 * 
	 * @param response
	 * @param text
	 * @param contentType
	 */
	private static void render(String text, String contentType) {
		try {
			HttpServletRequest request = ServletActionContext.getRequest();
			HttpServletResponse response = ServletActionContext.getResponse();
			request.setCharacterEncoding("UTF-8");
			response.setHeader("Pragma", "No-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);
			response.setContentType(contentType);
			response.getWriter().write(text);
		} catch (IOException e) {
		}
	}

	/**
	 * 直接输出纯字符串.
	 */
	public static void renderText(String text) {
		render(text, "text/plain;charset=UTF-8");
	}

	/**
	 * 直接输出纯字符串_对参数是Byte类型的
	 */
	public static void renderText(byte text) {
		render(String.valueOf(text), "text/plain;charset=UTF-8");
	}

	/**
	 * 直接输出纯字符串_对参数是Int类型的
	 */
	public static void renderText(int text) {
		render(String.valueOf(text), "text/plain;charset=UTF-8");
	}

	/**
	 * 直接输出纯HTML.
	 */
	public static void renderHtml(String text) {
		render(text, "text/html;charset=UTF-8");
	}

	/**
	 * 直接输出纯XML.
	 */
	public static void renderXML(String text) {
		render(text, "text/xml;charset=UTF-8");
	}

	/**
	 * 直接输出JSON.
	 * 
	 * @param string
	 *            json字符串.
	 * @see #render(HttpServletResponse, String,String)
	 */
	public static void renderJson(String text) {
		render(text, "text/x-json;charset=UTF-8");
	}

	private static enum ParseState {
		NORMAL, ESCAPE, UNICODE_ESCAPE
	}

	/**
	 * 将unicode 转成utf8
	 * 
	 * @param s
	 * @return
	 */
	public static String convertUnicodeEscape(String s) {
		if (null == s || "".equals(s)) {
			return null;
		}
		char[] out = new char[s.length()];

		ParseState state = ParseState.NORMAL;
		int j = 0, k = 0, unicode = 0;
		char c = ' ';
		for (int i = 0; i < s.length(); i++) {
			c = s.charAt(i);
			if (state == ParseState.ESCAPE) {
				if (c == 'u') {
					state = ParseState.UNICODE_ESCAPE;
					unicode = 0;
				} else { // we don't care about other escapes
					out[j++] = '\\';
					out[j++] = c;
					state = ParseState.NORMAL;
				}
			} else if (state == ParseState.UNICODE_ESCAPE) {
				if ((c >= '0') && (c <= '9')) {
					unicode = (unicode << 4) + c - '0';
				} else if ((c >= 'a') && (c <= 'f')) {
					unicode = (unicode << 4) + 10 + c - 'a';
				} else if ((c >= 'A') && (c <= 'F')) {
					unicode = (unicode << 4) + 10 + c - 'A';
				} else {
					throw new IllegalArgumentException(
							"Malformed unicode escape");
				}
				k++;

				if (k == 4) {
					out[j++] = (char) unicode;
					k = 0;
					state = ParseState.NORMAL;
				}
			} else if (c == '\\') {
				state = ParseState.ESCAPE;
			} else {
				out[j++] = c;
			}
		}

		if (state == ParseState.ESCAPE) {
			out[j++] = c;
		}

		return new String(out, 0, j);
	}

	/**
	 * 日期转为指定的时间
	 * 
	 * @param d
	 * @param pattern
	 * @return
	 */
	public static String date2Str(Date d, String pattern) {
		if (null == d) {
			return null;
		}
		DateFormat format = new SimpleDateFormat(pattern);
		String str = format.format(d);
		return str;
	}

	/**
	 * 
	 * @param parameter
	 * @return
	 */
	public static String getParameter(String parameter) {
		HttpServletRequest request = JsonUtil.getRequest();
		return request.getParameter(parameter);
	}

	public static HttpServletRequest getRequest() {
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			request.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return request;
	}

	public static HttpServletResponse getResponse() {
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setCharacterEncoding("UTF-8");
		return response;
	}

	public static String delHTMLTag(String str) {
		String regEx_html = "<[^>]+>";
		Pattern p_style = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
		Matcher m_style = p_style.matcher(str);
		str = m_style.replaceAll("");
		return str;
	}

	/**
	 * 发送url请求
	 * 
	 * by Table
	 * 
	 * 2011-08-17 15:11:45
	 * 
	 * @param url
	 * @return
	 */
	public static String sendRequest(String url) {

		HttpURLConnection conn = null;
		StringBuffer sb = new StringBuffer();

		String msg = "";

		try {
			conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			conn.connect();

			if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						conn.getInputStream(), "UTF-8"));
				String temp = "";
				while (null != (temp = br.readLine())) {
					sb.append(temp);
				}
				br.close();
			}
			msg = sb.toString();
			System.out.println("接口返回:" + msg);

		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (null != conn) {
				conn.disconnect();
			}
		}

		return msg;
	}

	/**
	 * 发送url请求(POST)
	 * 
	 * by Table
	 * 
	 * 2011-08-17 15:11:45
	 * 
	 * @param url
	 * @return
	 */
	public static String postRequest(String url, String Parameter) {
		HttpURLConnection connection = null;
		BufferedReader reader = null;
		StringBuffer sb = new StringBuffer();
		String msg = "";
		// Post请求的url，与get不同的是不需要带参数
		URL postUrl;
		try {
			postUrl = new URL(url);
			// 打开连接
			connection = (HttpURLConnection) postUrl.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			connection.connect();
			DataOutputStream out = new DataOutputStream(
					connection.getOutputStream());

			out.writeBytes(Parameter);
			out.flush();
			out.close();
			reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), "utf-8"));

			if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						connection.getInputStream(), "UTF-8"));
				String temp = "";
				while (null != (temp = br.readLine())) {
					sb.append(temp);
				}
				br.close();
				msg = sb.toString();
				System.out.println("接口返回:" + msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != connection) {
				connection.disconnect();
			}
			if (null != reader) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return msg;
	}
	
	public static void returnJson(int result, String resultMsg) {
		JsonObject json = new JsonObject();
		json.addProperty("Status", result);
		json.addProperty("ResultMsg", resultMsg);
		JsonUtil.renderJson(json.toString());
	}

}
