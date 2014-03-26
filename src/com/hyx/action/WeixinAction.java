package com.hyx.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hyx.common.Constants;
import com.hyx.common.HttpClientUtil;
import com.hyx.common.JsonUtil;
import com.hyx.common.ServerConfig;
import com.hyx.object.AccessTokenMsg;
import com.opensymphony.webwork.ServletActionContext;

public class WeixinAction extends BaseAction {
	private static final long serialVersionUID = -6084029170571217632L;
	private Logger logger = LoggerFactory.getLogger(WeixinAction.class);
	private String signature;
	private String timestamp;
	private String nonce;
	private String echostr;

	public void validate() {
		logger.debug("微信安全验证");
		logger.debug("\nsignature={}\ntimestamp={}\nnonce={}\nechostr={}",
				new String[] { signature, timestamp, nonce, echostr });
		if (timestamp != null && nonce != null) {
			String str[] = new String[] { Constants.WEIXIN_TOKEN, timestamp,
					nonce };
			java.util.Arrays.sort(str);
			String total = "";
			for (String s : str) {
				total += s;
			}
			java.security.MessageDigest sha1;
			try {
				sha1 = java.security.MessageDigest.getInstance("SHA-1");
				sha1.update(total.getBytes());
				byte[] codedBytes = sha1.digest();
				String codedString = new java.math.BigInteger(1, codedBytes)
						.toString(16);
				logger.debug("signature:{}\ncodedString:{}", signature,
						codedString);
				if (codedString.equals(signature)) {
					System.out.println("验证成功!");
					ServletActionContext.getResponse().getWriter()
							.print(echostr);
				} else {
					System.out.println("验证失败!");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("请求参数有误!");
		}
	}

	public void getAccessToken() {
		logger.debug("获取访问令牌");
		String result = HttpClientUtil.invokeDoGet(
				ServerConfig.getString("get_access_token"), "grant_type="
						+ ServerConfig.getString("grant_type") + "&appid="
						+ ServerConfig.getString("appid") + "&secret="
						+ ServerConfig.getString("secret"));

		if (result != null) {
			AccessTokenMsg accessTokenMsg = JsonUtil.json2Bean(result,
					AccessTokenMsg.class);
			if (accessTokenMsg != null) {
				String access_token = accessTokenMsg.getAccess_token();
				if (access_token != null) {
					logger.debug("保存access_token：" + access_token);
					ServletActionContext.getServletContext().setAttribute(
							"access_token", access_token);
					logger.debug("取出access_token："
							+ ServletActionContext.getServletContext()
									.getAttribute("access_token"));
					return;
				}
			}
			logger.debug("获取access_token失败");
		}

	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getNonce() {
		return nonce;
	}

	public void setNonce(String nonce) {
		this.nonce = nonce;
	}

	public String getEchostr() {
		return echostr;
	}

	public void setEchostr(String echostr) {
		this.echostr = echostr;
	}

}
