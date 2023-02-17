/*
 * Copyright 2022-Present Entando S.r.l. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.plugins.jpcds.aps.system.storage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode
@ToString
public class CdsFileAttributeViewDto implements Serializable {
    
    public static final String LONG_TIME_PARAM_NAME = "secs_since_epoch";
    
    private String name;
	private Map<String,String> lastModifiedTime;
	private Long size;
	private Boolean directory;
	private String path;
	private Boolean protectedFolder;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("last_modified_time")
    public Map<String, String> getLastModifiedTime() {
        return lastModifiedTime;
    }

    @JsonProperty("last_modified_time")
    public void setLastModifiedTime(Map<String, String> lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    @JsonIgnore
    public Date getDate() {
        if(lastModifiedTime != null) {
            String timeString = lastModifiedTime.get(LONG_TIME_PARAM_NAME);
            if (!StringUtils.isBlank(timeString)) {
                return new Date(Long.valueOf(timeString) * 1000);
            }
        }
        return null;
    }

    public Long getSize() {
        return size;
    }
    public void setSize(Long size) {
        this.size = size;
    }

    public Boolean getDirectory() {
        return directory;
    }
    public void setDirectory(Boolean directory) {
        this.directory = directory;
    }

    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }

    @JsonProperty("protected_folder")
    public Boolean getProtectedFolder() {
        return protectedFolder;
    }

    @JsonProperty("protected_folder")
    public void setProtectedFolder(Boolean protectedFolder) {
        this.protectedFolder = protectedFolder;
    }
    
}
