/*
 * Copyright 2023-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.aps.system.services.component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class ComponentDeleteResponse implements Serializable {
    
    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_PARTIAL_SUCCESS = "partialSuccess";
    public static final String STATUS_FAILURE = "failure";
    public static final String STATUS_ERROR = "error";
    
    private String status;
    
    private transient List<Map<String, Object>> components = new ArrayList<>();
    
}
