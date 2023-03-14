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

/**
 * @author E.Santoboni
 */
public final class ApiMethodResult implements Serializable {

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public void addError(String errorCode, String description) {
        LegacyApiError error = new LegacyApiError(errorCode, description);
        this.addError(error);
    }

    public void addError(LegacyApiError error) {
        if (null == this.getErrors()) {
            this.setErrors(new ArrayList<>());
        }
        this.getErrors().add(error);
    }

    public List<LegacyApiError> getErrors() {
        return errors;
    }

    public void setErrors(List<LegacyApiError> errors) {
        this.errors = errors;
    }

    private Object result;
    private List<LegacyApiError> errors;

}
