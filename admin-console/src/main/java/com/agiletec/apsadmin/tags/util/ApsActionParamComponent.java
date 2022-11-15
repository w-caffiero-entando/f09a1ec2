/*
 * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.agiletec.apsadmin.tags.util;

import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.StrutsException;
import org.apache.struts2.components.Component;

import com.agiletec.apsadmin.util.ApsRequestParamsUtil;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * Classe component del tag ApsActionParamTag.
 * @version 1.0
 * @author E.Santoboni
 */
public class ApsActionParamComponent extends Component {
	
	public ApsActionParamComponent(ValueStack stack, HttpServletRequest req) {
		super(stack);
		this._req = req;
	}
	
	@Override
	public boolean end(Writer writer, String body) {
		String actionParam = ApsRequestParamsUtil.createApsActionParam(this.getAction(), this.getParams());
		if (this.getVar() != null) {
            this.getStack().getContext().put(this.getVar(), actionParam);
            // add to the request and page scopes as well
            _req.setAttribute(this.getVar(), actionParam);
        } else {
            try {
            	writer.write(actionParam);
            } catch (IOException e) {
                throw new StrutsException("IOError: " + e.getMessage(), e);
            }
        }
		return true;
	}
	
	public String getAction() {
		return _action;
	}
	public void setAction(String action) {
		String actualValue = this.getActualValue(action);
		this._action = actualValue;
	}
	
	public Properties getParams() {
		return _params;
	}
	public void setParams(Properties properties) {
		this._params = properties;
	}
	
	public String getVar() {
		return _var;
	}
	public void setVar(String var) {
		this._var = var;
	}
	
	public void setParam(String paramName, String value) {
		String actualName = this.getActualValue(paramName);
		String actualValue = this.getActualValue(value);
		if (actualValue != null) {
			this.getParams().setProperty(actualName, actualValue);
		}
	}
	
	private String getActualValue(String value) {
		if (value.startsWith("%{") && value.endsWith("}")) {
			value = value.substring(2, value.length() - 1);
			return (String) getStack().findValue(value, String.class);
        }
		return value;
	}
	
	private HttpServletRequest _req;
	
	private String _action;
	private Properties _params = new Properties();
	
	private String _var;
	
}