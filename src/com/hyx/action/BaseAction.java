package com.hyx.action;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.hyx.common.XMLUtil;
import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionSupport;

public class BaseAction extends ActionSupport {
	private static final long serialVersionUID = 1088977811161656747L;
	protected final static String ROOT_NODE_NAME = "Result";
	protected final static String STATUS_NODE_NAME = "Status";
	protected final static String RESULTMSG_NODE_NAME = "ResultMsg";
	protected HttpServletResponse response;
	private int type; // 接入类型,1:查询，2：激活，3：消费

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean limitIp() {
		String ip = "";
		HttpServletRequest request = ServletActionContext.getRequest();
		if (request.getHeader("x-forwarded-for") != null) {
			ip = request.getHeader("x-forwarded-for");
		} else if (request.getHeader("X-Real-IP") != null) {
			ip = request.getHeader("X-Real-IP");
		} else {
			ip = request.getRemoteAddr();
		}
		Map ipMap = ActionContext.getContext().getApplication();
		String ips = "";
		if (!ipMap.isEmpty()) {
			ips = (String) ActionContext.getContext().getApplication()
					.get("IP");
		}
		if (ips == null || ips.length() == 0) {
			InputStream inputStream = this.getClass().getClassLoader()
					.getResourceAsStream("/ipConfig.properties");
			Properties properties = new Properties();
			try {
				properties.load(inputStream);
				ips = (String) properties.get("ip");
				ActionContext.getContext().getApplication().put("IP", ips);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (ips != null && ips.length() > 0) {
			String[] ipArray = ips.split(",");
			for (int i = 0; i < ipArray.length; i++) {
				if (ip.equals(ipArray[i])) {
					return true;
				}
			}
		}
		return false;
	}

	public void sendIPErrorXML() {
		Document document = DocumentHelper.createDocument();
		document.setXMLEncoding("utf-8");
		Element root = document.addElement("error");
		root.addElement(STATUS_NODE_NAME).addText("-1");
		root.addElement(RESULTMSG_NODE_NAME).addText("此IP不允许访问");
		try {
			response = ServletActionContext.getResponse();
			response.setContentType("text/xml; charset=utf-8");
			XMLUtil.writeXml(document, response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
