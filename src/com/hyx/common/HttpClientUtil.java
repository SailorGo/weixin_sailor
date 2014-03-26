package com.hyx.common;

import hyxlog.HyxLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientUtil {
	private static Logger logger=LoggerFactory.getLogger(HttpClientUtil.class);

	/**
	 * HTTP GET方法
	 * 
	 * @param urlstr
	 * @return
	 */
	public static String invokeDoGet(String urlstr) {
		URL url;
		StringBuffer sb = new StringBuffer("");
		try {
			url = new URL(urlstr);
			BufferedReader br;
			try {
				br = new BufferedReader(new InputStreamReader(url.openStream(),
						"GB2312"));
				String s = "";
				while ((s = br.readLine()) != null) {
					sb.append(s + "\r\n");
				}
				br.close();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * HTTP GET方法
	 * 
	 * @param url
	 * @param queryString
	 * @return
	 */
	public static String invokeDoGet(String url, String queryString) {
		logger.debug("HTTP接口：{}?{}",url,queryString);
		long start=System.currentTimeMillis();
		String response = "";
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(url);
		try {
			if (StringUtils.isNotBlank(queryString)) {
				method.setQueryString(queryString);
			}
			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				response = method.getResponseBodyAsString();
			} else {
				HyxLog.info("调用HTTP接口异常:"+url+"?"+queryString);
				long end=System.currentTimeMillis();
				System.out.println("耗时："+(end-start)+"ms");
				logger.debug("invokeDoGet error");
				return null;
			}
		} catch (URIException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			method.releaseConnection();
		}
		return response;

	}

	/**
	 * HTTP POST方法
	 * 
	 * @param url
	 * @param data
	 * @return
	 */
	public static String invokeDoPost(String url, NameValuePair[] data) {
		String response = "";
		PostMethod method = null;
		try {
			HttpClient client = new HttpClient();
			method = new PostMethod(url);
			method.getParams().setParameter(
					HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
			method.setRequestBody(data);
			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				response = method.getResponseBodyAsString();
			} else {
				return null;
			}
		} catch (URIException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			method.releaseConnection();
		}
		return response;
	}

	/**
	 * POST请求-JSON格式参数
	 * 
	 * @param url
	 * @param jsonString
	 * @return
	 */
	public static String postJson(String url, String jsonString) {
		try {
			DefaultHttpClient client = new DefaultHttpClient();
			StringEntity entity = new StringEntity(jsonString, "utf-8");
			HttpPost httpost = new HttpPost(url);
			httpost.setHeader("Content-type",
					"application/x-www-form-urlencoded; charset=UTF-8");
			httpost.setHeader("X-Requested-With", "XMLHttpRequest");
			httpost.setEntity(entity);
			HttpResponse response = client.execute(httpost);
			HttpEntity httpEntity = response.getEntity();
			if (httpEntity == null)
				return null;
			return getStringFromHttp(httpEntity);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String getStringFromHttp(HttpEntity entity) {
		StringBuffer buffer = new StringBuffer();
		try {
			// 获取输入流
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					entity.getContent()));
			// 将返回的数据读到buffer中
			String temp = null;
			while ((temp = reader.readLine()) != null) {
				buffer.append(temp);
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer.toString();
	}
}
