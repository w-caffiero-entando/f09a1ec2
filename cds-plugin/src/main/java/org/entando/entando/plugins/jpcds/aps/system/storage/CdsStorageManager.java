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

import com.agiletec.aps.util.ApsTenantApplicationUtils;
import com.agiletec.aps.util.FileTextReader;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.entando.entando.aps.system.services.storage.BasicFileAttributeView;
import org.entando.entando.aps.system.services.storage.IStorageManager;
import org.entando.entando.aps.system.services.storage.LocalStorageManager;
import org.entando.entando.aps.system.services.storage.StorageManagerUtil;
import org.entando.entando.aps.system.services.tenants.ITenantManager;
import org.entando.entando.aps.system.services.tenants.TenantConfig;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.ent.exception.EntRuntimeException;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service("StorageManager")
@CdsActive(true)
public class CdsStorageManager implements IStorageManager {
    
    private static final EntLogger logger = EntLogFactory.getSanitizedLogger(CdsStorageManager.class);
    
    private static final String CDS_PUBLIC_URL_TENANT_PARAM = "cdsPublicUrl";
    private static final String CDS_PRIVATE_URL_TENANT_PARAM = "cdsPrivateUrl";
    private static final String CDS_PATH_TENANT_PARAM = "cdsPath";
    private static final String URL_SEP = "/";
    private static final String SECTION_PUBLIC = "public";
    private static final String SECTION_PRIVATE = "protected";
    private final ITenantManager tenantManager;
    private final CdsConfiguration configuration;
    private final CdsRemoteCaller caller;


    @Autowired
    public CdsStorageManager(CdsRemoteCaller caller, ITenantManager tenantManager, CdsConfiguration configuration) {
        this.caller = caller;
        this.tenantManager = tenantManager;
        this.configuration = configuration;
    }



    @Override
    public void createDirectory(String subPath, boolean isProtectedResource) {
        this.create(subPath, isProtectedResource, null);
    }
    
    @Override
    public void saveFile(String subPath, boolean isProtectedResource, InputStream is) throws EntException, IOException {
        this.create(subPath, isProtectedResource, is);
    }
    
    private void create(String subPath, boolean isProtectedResource, InputStream is) {
        try {
            Optional<TenantConfig> config = getTenantConfig();
            this.validateAndReturnResourcePath(config, subPath, isProtectedResource);
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            if (null != is) {
                //added file
                String filename = subPath;
                String path = "";
                int sepIndex = subPath.lastIndexOf(URL_SEP);
                if (sepIndex >= 0) {
                    filename = subPath.substring(sepIndex + 1);
                    path = subPath.substring(0, sepIndex);
                }
                InputStreamResource resource = new InputStreamResource(is);
                body.add("path", path);
                body.add("protected", isProtectedResource);
                body.add("filename", filename);
                body.add("file", resource);
            } else {
                body.add("path", subPath);
                body.add("protected", isProtectedResource);
            }
            
            String url = String.format("%s/upload/", this.extractInternalCdsBaseUrl(config, true));
            CdsCreateResponse[] response = caller.executePostCall(url, body, config, false);

            if (!"OK".equalsIgnoreCase(response[0].getStatus())) {
                throw new EntRuntimeException("Invalid status - Response " + response[0].getStatus());
            }
        } catch (EntRuntimeException ert) {
            throw ert;
        } catch (Exception e) {
            logger.error("Error saving file/directory", e);
            throw new EntRuntimeException("Error saving file/directory", e);
        }
    }
    

    @Override
    public boolean deleteFile(String subPath, boolean isProtectedResource) {
        try {
            Optional<TenantConfig> config = getTenantConfig();
            this.validateAndReturnResourcePath(config, subPath, isProtectedResource);
            String section = this.getInternalSection(isProtectedResource);
            String subPathFixed = (!StringUtils.isBlank(subPath)) ? (subPath.trim().startsWith(URL_SEP) ? subPath.trim().substring(1) : subPath) : "";
            String url = String.format("%s/delete/%s/%s", this.extractInternalCdsBaseUrl(config, true), section, subPathFixed);
            Map<String, String> map = caller.executeDeleteCall(url, config, false);
            return ("OK".equalsIgnoreCase(map.get("status")));
        } catch (EntRuntimeException ert) {
            throw ert;
        } catch (Exception e) {
            logger.error("Error deleting file", e);
            throw new EntRuntimeException("Error deleting file", e);
        }
    }
    
    @Override
    public void deleteDirectory(String subPath, boolean isProtectedResource) throws EntException {
        this.deleteFile(subPath, isProtectedResource); //same behavior
    }

    @Override
    public InputStream getStream(String subPath, boolean isProtectedResource) throws EntException {
        String url = null;
        try {
            Optional<TenantConfig> config = getTenantConfig();
            this.validateAndReturnResourcePath(config, subPath, isProtectedResource);
            String section = this.getInternalSection(isProtectedResource);
            String baseUrl = (isProtectedResource) ? 
                    this.extractInternalCdsBaseUrl(config, isProtectedResource) : 
                    this.getCheckedBaseUrl(config, isProtectedResource);
            String subPathFixed = (!StringUtils.isBlank(subPath)) ? (subPath.trim().startsWith(URL_SEP) ? subPath.trim().substring(1) : subPath) : "";
            url = baseUrl + URL_SEP + section + URL_SEP + subPathFixed;
            return caller.getFile(url,config, isProtectedResource);
        } catch (EntRuntimeException ert) {
            throw ert;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                logger.info("File Not found - uri {}", url);
                return null;
            }  
            logger.error("Error extracting file", e);
            throw new EntException("Error extracting file", e);
        } catch (Exception e) {
            logger.error("Error extracting file", e);
            throw new EntException("Error extracting file", e);
        }
    }

    @Override
    public String getResourceUrl(String subPath, boolean isProtectedResource) {
        try {
            Optional<TenantConfig> config = getTenantConfig();
            return this.validateAndReturnResourcePath(config, subPath, isProtectedResource);
        } catch (Exception e) {
            logger.error("Error extracting resource url", e);
            throw new EntRuntimeException("Error extracting resource url", e);
        }
    }

    @Override
    public boolean exists(String subPath, boolean isProtectedResource) {
        String[] sections = this.extractSections(subPath);
        String[] filenames = this.list(sections[0], isProtectedResource);
        return (null != filenames && Arrays.asList(filenames).contains(sections[1]));
    }

    @Override
    public BasicFileAttributeView getAttributes(String subPath, boolean isProtectedResource) {
        String[] sections = this.extractSections(subPath);
        BasicFileAttributeView[] attributs = this.listAttributes(sections[0], isProtectedResource);
        if (null == attributs) {
            return null;
        }
        for (int i = 0; i < attributs.length; i++) {
            BasicFileAttributeView attr = attributs[i];
            if (attr.getName().equals(sections[1])) {
                return attr;
            }
        }
        return null;
    }
    
    private String[] extractSections(String subPath) {
        String path = "";
        String filename = subPath;
        int sepIndex = subPath.lastIndexOf(URL_SEP);
        if (sepIndex >= 0) {
            path = subPath.substring(0, sepIndex);
            filename = subPath.substring(sepIndex + 1);
        }
        return new String[]{path, filename};
    }

    @Override
    public String[] list(String subPath, boolean isProtectedResource) {
        return this.listString(subPath, isProtectedResource, null);
    }

    @Override
    public String[] listDirectory(String subPath, boolean isProtectedResource) {
        return this.listString(subPath, isProtectedResource, false);
    }

    @Override
    public String[] listFile(String subPath, boolean isProtectedResource) {
        return this.listString(subPath, isProtectedResource, true);
    }
    
    protected String[] listString(String subPath, boolean isProtectedResource, Boolean file) {
        BasicFileAttributeView[] list = this.listAttributes(subPath, isProtectedResource, file);
        List<String> names = Arrays.asList(list).stream()
                .map(bfa -> bfa.getName()).collect(Collectors.toList());
        return names.stream().toArray(String[]::new);
    }
    
    @Override
    public BasicFileAttributeView[] listAttributes(String subPath, boolean isProtectedResource) {
        return this.listAttributes(subPath, isProtectedResource, null);
    }
    
    @Override
    public BasicFileAttributeView[] listDirectoryAttributes(String subPath, boolean isProtectedResource) {
        return this.listAttributes(subPath, isProtectedResource, false);
    }

    @Override
    public BasicFileAttributeView[] listFileAttributes(String subPath, boolean isProtectedResource) {
        return this.listAttributes(subPath, isProtectedResource, true);
    }
    
    private BasicFileAttributeView[] listAttributes(String subPath, boolean isProtectedResource, Boolean file) {
        try {
            Optional<TenantConfig> config = this.getTenantConfig();
            this.validateAndReturnResourcePath(config, subPath, isProtectedResource);
            String subPathFixed = (!StringUtils.isBlank(subPath)) ? (subPath.trim().startsWith(URL_SEP) ? subPath.trim().substring(1) : subPath) : "";
            String section = this.getInternalSection(isProtectedResource);
            String url = String.format("%s/list/%s/%s", this.extractInternalCdsBaseUrl(config, true), section, subPathFixed);
            CdsFileAttributeView[] cdsFileList = caller.getFileAttributeView(url, config);
            List<BasicFileAttributeView> list = Arrays.asList(cdsFileList).stream()
                    .filter(csdf -> (null != file) ? ((file) ? !csdf.getDirectory() : csdf.getDirectory()) : true)
                    .map(csdf -> {
                BasicFileAttributeView bfa = new BasicFileAttributeView();
                bfa.setName(csdf.getName());
                bfa.setDirectory(csdf.getDirectory());
                bfa.setLastModifiedTime(csdf.getDate());
                bfa.setSize(csdf.getSize());
                return bfa;
            }).collect(Collectors.toList());
            Collections.sort(list);
            return list.stream().toArray(BasicFileAttributeView[]::new);
        } catch (EntRuntimeException ert) {
            throw ert;
        } catch (Exception e) {
            logger.error("Error on list attributes", e);
            throw new EntRuntimeException("Error on list attributes", e);
        }
    }
    

    @Override
    public String readFile(String subPath, boolean isProtectedResource) throws EntException {
        try {
            InputStream stream = this.getStream(subPath, isProtectedResource);
            return FileTextReader.getText(stream);
        } catch (EntRuntimeException ert) {
            throw ert;
        } catch (IOException ex) {
            logger.error("Error extracting text", ex);
            throw new EntException("Error extracting text", ex);
        }
    }

    @Override
    public void editFile(String subPath, boolean isProtectedResource, InputStream is) throws EntException {
        try {
            this.saveFile(subPath, isProtectedResource, is);
        } catch (EntRuntimeException ert) {
            throw ert;
        } catch (IOException ex) {
            logger.error("Error editing text", ex);
            throw new EntException("Error editing text", ex);
        }
    }

    @Override
    public String createFullPath(String subPath, boolean isProtectedResource) {
        return this.validateAndReturnResourcePath(this.getTenantConfig(), subPath, isProtectedResource);
    }
    
    private Optional<TenantConfig> getTenantConfig() {
        return ApsTenantApplicationUtils.getTenant()
                .filter(StringUtils::isNotBlank)
                .map(tenantManager::getConfig);
    }
    
    private String getInternalSection(boolean isProtectedResource) {
        return (isProtectedResource) ? SECTION_PRIVATE : SECTION_PUBLIC;
    } 

    private String extractInternalCdsBaseUrl(Optional<TenantConfig> config, boolean privateUrl) {
        String baseUrl = this.getCheckedBaseUrl(config, privateUrl);
        String path = config.flatMap(c -> c.getProperty(CDS_PATH_TENANT_PARAM)).orElse(configuration.getCdsPath());
        path = (path.startsWith(URL_SEP)) ? path : URL_SEP + path;
        String cdsBaseUrl = baseUrl + path;
        return (cdsBaseUrl.endsWith(URL_SEP)) ? cdsBaseUrl.substring(0, cdsBaseUrl.length() - 2) : cdsBaseUrl;
    }
    
    protected String validateAndReturnResourcePath(Optional<TenantConfig> config, String resourceRelativePath, boolean privateUrl) {
        try {
            String baseUrl = this.getCheckedBaseUrl(config, privateUrl) + URL_SEP + this.getInternalSection(privateUrl);
            String fullPath = this.createPath(baseUrl, resourceRelativePath);
            if (!StorageManagerUtil.doesPathContainsPath(baseUrl, fullPath, true)) {
                throw mkPathValidationErr(baseUrl, fullPath);
            }
            return fullPath;
        } catch (EntRuntimeException ert) {
            throw ert;
        } catch (Exception e) {
            logger.error("Error validating path", e);
            throw new EntRuntimeException("Error validating path", e);
        }
    }
    
    private String getCheckedBaseUrl(Optional<TenantConfig> config, boolean usePrivateUrl) {
        String privateUrl = config.flatMap(c -> c.getProperty(CDS_PRIVATE_URL_TENANT_PARAM)).orElse(configuration.getCdpPrivateUrl());
        String publicUrl = config.flatMap(c -> c.getProperty(CDS_PUBLIC_URL_TENANT_PARAM)).orElse(configuration.getCdsPublicUrl());

        String baseUrl = usePrivateUrl ? privateUrl : publicUrl;

        return cleanupUrl(baseUrl);
    }

    private String cleanupUrl(String dirtyUrl) {
        String cleanUrl = dirtyUrl;
        if(dirtyUrl.endsWith(URL_SEP)) {
            cleanUrl = dirtyUrl.substring(0, dirtyUrl.length() - 1);
        }
        return cleanUrl;
    }

	private String createPath(String basePath, String subPath) {
		subPath = (null == subPath) ? "" : subPath;
        basePath = (basePath.endsWith(URL_SEP)) ? basePath.substring(0, basePath.length() - URL_SEP.length() - 1) : basePath;
        subPath = (subPath.startsWith(URL_SEP)) ? subPath.substring(URL_SEP.length()) : subPath;
        return (StringUtils.isBlank(subPath)) ? basePath : basePath + URL_SEP + subPath;
	}

	private EntRuntimeException mkPathValidationErr(String diskRoot, String fullPath) {
		return new EntRuntimeException(
				String.format("Path validation failed: \"%s\" not in \"%s\"", fullPath, diskRoot)
		);
	}









    // FIXME ereditato da LocalStorageManager
    @Override
    public boolean isDirectory(String subPath, boolean isProtectedResource) {
        Boolean isDir = withValidResourcePath(subPath, isProtectedResource, (basePath, fullPath) -> {
            File dir = new File(fullPath);
            if (dir != null) {
                return dir.isDirectory();
            }
            return false;
        });
        return isDir;
    }

    private <T> T withValidResourcePath(String resourceRelativePath, boolean isProtectedResource,
            BiFunction<String, String, T> bip) {
        //-
        resourceRelativePath = (resourceRelativePath == null) ? "" : resourceRelativePath;
        String basePath = (!isProtectedResource) ? configuration.getBaseDiskRoot() : configuration.getProtectedBaseDiskRoot();
        String fullPath = this.createPath(basePath, resourceRelativePath, false);
        try {
            if (StorageManagerUtil.doesPathContainsPath(basePath, fullPath, true)) {
                return bip.apply(basePath, fullPath);
            } else {
                throw mkPathValidationErr(basePath, fullPath);
            }
        } catch (IOException ex) {
            throw mkPathValidationErr(basePath, fullPath);
        }
    }

    private static final String UNIX_SEP = "/";
    private static final String WINDOWS_SEP = "\\";

    private String createPath(String basePath, String subPath, boolean isUrlPath) {
        subPath = (null == subPath) ? "" : subPath;
        String separator = (isUrlPath) ? URL_SEP : File.separator;
        boolean baseEndWithSlash = basePath.endsWith(separator) ||
                basePath.endsWith(UNIX_SEP) ||
                basePath.endsWith(WINDOWS_SEP);
        boolean subPathStartWithSlash = subPath.startsWith(separator);
        if ((baseEndWithSlash && !subPathStartWithSlash) || (!baseEndWithSlash && subPathStartWithSlash)) {
            return basePath + subPath;
        } else if (!baseEndWithSlash && !subPathStartWithSlash) {
            return basePath + separator + subPath;
        } else {
            String base = basePath.substring(0, basePath.length() - File.separator.length());
            return base + subPath;
        }
    }
}
