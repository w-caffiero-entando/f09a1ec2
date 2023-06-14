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

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@JsonPropertyOrder({"type", "code", "exist", "usage", "status", "extraProperties", "references"})
public class ComponentUsageDetails extends ComponentUsage implements Serializable {
    
    public ComponentUsageDetails(String type, String code) {
        super.setType(type);
        super.setCode(code);
    }
    
    public ComponentUsageDetails(String type, String code, IComponentDto dto) {
        this(type, code);
        if (null != dto) {
            this.setExist(true);
            this.setStatus(dto.getStatus());
            this.getExtraProperties().putAll(dto.getExtraProperties());
        }
    }
    
    @Getter@Setter
    private boolean exist;
    
    @Getter@Setter
    private List<Map<String, Object>> references = new ArrayList<>();
    
}
