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
package org.entando.entando.aps.system.services.api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * @author E.Santoboni
 */
public abstract class AbstractApiResponse implements Serializable {
    
    public void setResult(Object result, String html) {
        AbstractApiResponseResult responseResult = this.createResponseResultInstance();
        responseResult.setMainResult(result);
        responseResult.setHtml(html);
        this.setResult(responseResult);
    }
    
    protected abstract AbstractApiResponseResult createResponseResultInstance();
    
    @XmlElement(name = "error", required = true)
    @XmlElementWrapper(name = "errors")
    public List<LegacyApiError> getErrors() {
        return this._errors;
    }
    
    public void addError(LegacyApiError error) {
        if (null != error) {
			this._errors.add(error);
        }
    }
    
    public void addErrors(List<LegacyApiError> errors) {
        if (null == errors) {
            return;
        }
		this.getErrors().addAll(errors);
    }
    
    public Object getResult() {
        return _result;
    }
    protected void setResult(Object result) {
        this._result = result;
    }
    
    private List<LegacyApiError> _errors = new ArrayList<LegacyApiError>();
    private Object _result;
	
}