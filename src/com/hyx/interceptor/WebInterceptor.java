package com.hyx.interceptor;

import com.hyx.action.BaseAction;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.AroundInterceptor;

public class WebInterceptor extends AroundInterceptor {
	private static final long serialVersionUID = -1828777883111953871L;

	protected void after(ActionInvocation arg0, String arg1) throws Exception {
	}

	protected void before(ActionInvocation arg0) throws Exception {
	}
	
	public String intercept(ActionInvocation invocation) throws Exception {
		Action action = (Action)invocation.getAction();
		if(action instanceof BaseAction){
			BaseAction baseAction =((BaseAction)action);
			boolean ipflag = baseAction.limitIp();
			if(ipflag==false){
				return "ip_error";
			}
		}
		return super.intercept(invocation);
	}
}
