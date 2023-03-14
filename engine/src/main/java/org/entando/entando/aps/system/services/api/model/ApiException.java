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

import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;

/**
 * @author E.Santoboni
 */
public class ApiException extends Exception {

	private final List<LegacyApiError> errors = new ArrayList<>();

	public ApiException(LegacyApiError error) {
		super(error.getMessage());
		this.getErrors().add(error);
	}
	
	public ApiException(LegacyApiError error, Throwable cause) {
		super(cause);
		this.getErrors().add(error);
	}
	
	public ApiException(List<LegacyApiError> errors, Throwable cause) {
		super(cause);
		this.getErrors().addAll(errors);
	}
	
	public ApiException(List<LegacyApiError> errors) {
		super();
		this.getErrors().addAll(errors);
	}
	
	public ApiException(String errorKey, String message, Throwable cause) {
		super(message, cause);
		this.addError(errorKey);
	}
	
	public ApiException(String errorKey, String message) {
		super(message);
		this.addError(errorKey);
	}
	
	public ApiException(String errorKey, String message, HttpStatus status) {
		super(message);
		this.addError(errorKey, status);
	}
	
	public ApiException(String errorKey, Throwable cause) {
		super(cause);
		this.addError(errorKey);
	}
	
	protected void addError(String key) {
		this.getErrors().add(new LegacyApiError(key, getMessage()));
	}
	
	protected void addError(String key, HttpStatus status) {
		this.getErrors().add(new LegacyApiError(key, getMessage(), status));
	}
	
	public List<LegacyApiError> getErrors() {
		return this.errors;
	}
	
}