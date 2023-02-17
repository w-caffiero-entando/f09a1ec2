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
package org.entando.entando.plugins.jpcds.aps.system.storage;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.*;

import com.agiletec.aps.util.ApsTenantApplicationUtils;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.entando.entando.aps.system.services.storage.BasicFileAttributeView;
import org.entando.entando.aps.system.services.storage.IStorageManager;
import org.entando.entando.aps.system.services.tenants.ITenantManager;
import org.entando.entando.aps.system.services.tenants.TenantConfig;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.ent.exception.EntRuntimeException;
import org.entando.entando.ent.util.EntLogging.EntLogFactory;
import org.entando.entando.ent.util.EntLogging.EntLogger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;

/**
 * @author S.Loru - E.Santoboni
 */
@ExtendWith(MockitoExtension.class)
class CdsStorageManagerTest {
    private static final EntLogger logger = EntLogFactory.getSanitizedLogger(CdsStorageManagerTest.class);

    @Mock
    private CdsRemoteCaller cdsRemoteCaller;
    @Mock
    private ITenantManager tenantManager;
    @Mock
    private CdsConfiguration cdsConfiguration;
    private IStorageManager cdsStorageManager;

    @BeforeEach
    private void init() throws Exception {
        cdsStorageManager = new CdsStorageManager(cdsRemoteCaller, tenantManager, cdsConfiguration);
    }

    @AfterEach
    public void afterAll() throws Exception {
        Mockito.reset(cdsRemoteCaller, tenantManager, cdsConfiguration);
    }


    @Test
    void shouldCreateDirectory() throws Exception {
        Map<String,String> configMap = Map.of("cdsPublicUrl","http://my-server/tenant1/cms-resources",
                "cdsPrivateUrl","http://cds-kube-service:8081/",
                "cdsPath","/mytenant/api/v1");
        TenantConfig tc = new TenantConfig(configMap);
        Mockito.when(tenantManager.getConfig("my-tenant")).thenReturn(tc);

        CdsCreateResponseDto ret = new CdsCreateResponseDto();
        ret.setStatusOk(true);
        ArgumentCaptor<URI> captor = ArgumentCaptor.forClass(URI.class);
        Mockito.when(cdsRemoteCaller.executePostCall(eq(URI.create("http://cds-kube-service:8081/mytenant/api/v1/upload/")),
                eq("/sub-path-testy"),
                eq(false),
                any(),
                any(),
                eq(false))).thenReturn(ret);

        ApsTenantApplicationUtils.setTenant("my-tenant");
        cdsStorageManager.createDirectory("/sub-path-testy",false);


        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("path", "/sub-path-testy");
        body.add("protected", false);

        Mockito.verify(cdsRemoteCaller, Mockito.times(1))
                .executePostCall(eq(URI.create("http://cds-kube-service:8081/mytenant/api/v1/upload/")),
                        eq("/sub-path-testy"),
                        eq(false),
                        any(),
                        any(),
                        eq(false));

    }

    @Test
    void shouldNotCreateDirectory() throws Exception {
        Map<String,String> configMap = Map.of("cdsPublicUrl","http://my-server/tenant1/cms-resources",
                "cdsPrivateUrl","http://cds-kube-service:8081/",
                "cdsPath","/mytenant/api/v1");
        TenantConfig tc = new TenantConfig(configMap);
        Mockito.when(tenantManager.getConfig("my-tenant")).thenReturn(tc);
        ApsTenantApplicationUtils.setTenant("my-tenant");

        Assertions.assertThatThrownBy(
                ()-> cdsStorageManager.createDirectory("../../sub-path-testy",false)
        ).isInstanceOf(EntRuntimeException.class).hasMessageStartingWith("Path validation failed:");

        Assertions.assertThatThrownBy(
                ()-> cdsStorageManager.createDirectory(null,false)
        ).isInstanceOf(EntRuntimeException.class).hasMessageStartingWith("Error validating path");


        CdsCreateResponseDto ret = new CdsCreateResponseDto();
        ret.setStatusOk(false);
        ArgumentCaptor<URI> captor = ArgumentCaptor.forClass(URI.class);
        Mockito.when(cdsRemoteCaller.executePostCall(any(),
                eq("/sub-path-testy"),
                eq(false),
                any(),
                any(),
                eq(false))).thenReturn(ret);

        ApsTenantApplicationUtils.setTenant("my-tenant");
        Assertions.assertThatThrownBy(
                ()-> cdsStorageManager.createDirectory("/sub-path-testy",false)
        ).isInstanceOf(EntRuntimeException.class).hasMessageStartingWith("Invalid status - Response");

    }

    @Test
    void shouldCreateFile() throws Exception {
        Map<String,String> configMap = Map.of("cdsPublicUrl","http://my-server/tenant1/cms-resources",
                "cdsPrivateUrl","http://cds-kube-service:8081/",
                "cdsPath","/mytenant/api/v1/");
        TenantConfig tc = new TenantConfig(configMap);
        Mockito.when(tenantManager.getConfig("my-tenant")).thenReturn(tc);

        CdsCreateResponseDto ret = new CdsCreateResponseDto();
        ret.setStatusOk(true);
        ArgumentCaptor<URI> captor = ArgumentCaptor.forClass(URI.class);
        Mockito.when(cdsRemoteCaller.executePostCall(any(),
                eq("/sub-path-testy/myfilename"),
                eq(false),
                any(),
                any(),
                eq(false))).thenReturn(ret);

        ApsTenantApplicationUtils.setTenant("my-tenant");
        InputStream is = new ByteArrayInputStream("testo a casos".getBytes(StandardCharsets.UTF_8));
        cdsStorageManager.saveFile("/sub-path-testy/myfilename",false, is);

        Mockito.verify(cdsRemoteCaller, Mockito.times(1))
                .executePostCall(eq(URI.create("http://cds-kube-service:8081/mytenant/api/v1/upload/")),
                        eq("/sub-path-testy/myfilename"),
                        eq(false),
                        any(),
                        any(),
                        eq(false));

    }

    @Test
    void shouldNotDeleteDirectory() throws Exception {
        Map<String,String> configMap = Map.of("cdsPublicUrl","http://my-server/tenant1/cms-resources",
                "cdsPrivateUrl","http://cds-kube-service:8081/",
                "cdsPath","/mytenant/api/v1");
        TenantConfig tc = new TenantConfig(configMap);
        Mockito.when(tenantManager.getConfig("my-tenant")).thenReturn(tc);
        ApsTenantApplicationUtils.setTenant("my-tenant");

        Assertions.assertThatThrownBy(
                ()-> cdsStorageManager.deleteDirectory("../../sub-path-testy",false)
        ).isInstanceOf(EntRuntimeException.class).hasMessageStartingWith("Path validation failed:");

        Assertions.assertThatThrownBy(
                ()-> cdsStorageManager.deleteDirectory(null,false)
        ).isInstanceOf(EntRuntimeException.class).hasMessageStartingWith("Error validating path");

        Mockito.when(cdsRemoteCaller.executeDeleteCall(any(),
                any(),
                eq(false))).thenReturn(false);

        ApsTenantApplicationUtils.setTenant("my-tenant");
        Assertions.assertThatNoException().isThrownBy(() -> cdsStorageManager.deleteDirectory("/sub-path-testy",false));

    }

    @Test
    void shouldBlockPathTraversalWhenCallGetStream() {
        String testFilePath = "../testfolder/test.txt";

        Map<String,String> configMap = Map.of("cdsPublicUrl","http://my-server/tenant1/cms-resources",
                "cdsPrivateUrl","http://cds-kube-service:8081/",
                "cdsPath","/mytenant/api/v1/");
        TenantConfig tc = new TenantConfig(configMap);
        Mockito.when(tenantManager.getConfig("my-tenant")).thenReturn(tc);

        ApsTenantApplicationUtils.setTenant("my-tenant");

        Assertions.assertThatThrownBy(
                ()-> cdsStorageManager.getStream(testFilePath,false)
        ).isInstanceOf(EntRuntimeException.class).hasMessageStartingWith("Path validation failed:");

    }

    @Test
    void shouldManageErrorWhenCallGetStream() throws Exception {
        String testFilePath = "/testfolder/test.txt";

        Map<String,String> configMap = Map.of("cdsPublicUrl","http://my-server/tenant1/cms-resources",
                "cdsPrivateUrl","http://cds-kube-service:8081/",
                "cdsPath","/mytenant/api/v1/");
        TenantConfig tc = new TenantConfig(configMap);
        Mockito.when(tenantManager.getConfig("my-tenant")).thenReturn(tc);

        Mockito.when(cdsRemoteCaller.getFile(any(),
                any(),
                eq(false))).thenReturn(null);


        ApsTenantApplicationUtils.setTenant("my-tenant");

        Assertions.assertThatThrownBy(
                ()-> cdsStorageManager.getStream(null,false)
        ).isInstanceOf(EntRuntimeException.class).hasMessageStartingWith("Error validating path");

        Assertions.assertThatThrownBy(
                ()-> cdsStorageManager.getStream(testFilePath,false)
        ).isInstanceOf(EntException.class).hasMessageStartingWith("Error extracting file");


        Mockito.reset(cdsRemoteCaller);
        Mockito.when(cdsRemoteCaller.getFile(any(),
                any(),
                eq(false))).thenThrow(new HttpClientErrorException(HttpStatus.BAD_GATEWAY));

        Assertions.assertThatThrownBy(
                ()-> cdsStorageManager.getStream(testFilePath,false)
        ).isInstanceOf(EntException.class).hasMessageStartingWith("Error extracting file");

        Mockito.reset(cdsRemoteCaller);
        Mockito.when(cdsRemoteCaller.getFile(any(),
                any(),
                eq(false))).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        Assertions.assertThat(cdsStorageManager.getStream(testFilePath,false)).isNull();

    }

    @Test
    void shouldReturnDataWhenCallGetStream() throws Exception {
        String testFilePath = "/test-folder/test.txt";

        Map<String,String> configMap = Map.of("cdsPublicUrl","http://my-server/tenant1/cms-resources",
                "cdsPrivateUrl","http://cds-kube-service:8081/",
                "cdsPath","/mytenant/api/v1/");
        TenantConfig tc = new TenantConfig(configMap);
        Mockito.when(tenantManager.getConfig("my-tenant")).thenReturn(tc);

        Mockito.when(cdsRemoteCaller.getFile(eq(URI.create("http://my-server/tenant1/cms-resources/public/test-folder/test.txt")),
                any(),
                eq(false))).thenReturn(Optional.ofNullable(new ByteArrayInputStream("text random".getBytes(StandardCharsets.UTF_8))));

        ApsTenantApplicationUtils.setTenant("my-tenant");
        InputStream is = cdsStorageManager.getStream(testFilePath,false);
        Assertions.assertThat(new BufferedReader(new InputStreamReader(is))
                .lines().collect(Collectors.joining(""))).isEqualTo("text random");

        Mockito.reset(cdsRemoteCaller);
        Mockito.when(cdsRemoteCaller.getFile(eq(URI.create("http://cds-kube-service:8081/mytenant/api/v1/protected/test-folder/test.txt")),
                any(),
                eq(true))).thenReturn(Optional.ofNullable(new ByteArrayInputStream("text random".getBytes(StandardCharsets.UTF_8))));

        is = cdsStorageManager.getStream(testFilePath,true);
        Assertions.assertThat(new BufferedReader(new InputStreamReader(is))
                .lines().collect(Collectors.joining(""))).isEqualTo("text random");

    }

    @Test
    void shouldReturnRightUrlWhenCallGetResourceUrl() throws Exception {
        String testFilePath = "/test-folder/test.txt";

        Map<String,String> configMap = Map.of("cdsPublicUrl","http://my-server/tenant1/cms-resources",
                "cdsPrivateUrl","http://cds-tenant1-kube-service:8081/",
                "cdsPath","/mytenant/api/v1/");
        TenantConfig tc = new TenantConfig(configMap);
        Mockito.when(tenantManager.getConfig("my-tenant")).thenReturn(tc);

        ApsTenantApplicationUtils.setTenant("my-tenant");
        String resourceUrl = cdsStorageManager.getResourceUrl(testFilePath,false);
        Assertions.assertThat(resourceUrl).isEqualTo("http://my-server/tenant1/cms-resources/public/test-folder/test.txt");

        resourceUrl = cdsStorageManager.getResourceUrl(testFilePath,true);
        Assertions.assertThat(resourceUrl).isEqualTo("http://cds-tenant1-kube-service:8081/protected/test-folder/test.txt");

        resourceUrl = cdsStorageManager.createFullPath(testFilePath,true);
        Assertions.assertThat(resourceUrl).isEqualTo("http://cds-tenant1-kube-service:8081/protected/test-folder/test.txt");

    }

    @Test
    void shouldManageUrlWhenCallGetResourceUrl() throws Exception {
        Map<String,String> configMap = Map.of("cdsPublicUrl","http://my-server/tenant1/cms-resources",
                "cdsPrivateUrl","http://cds-tenant1-kube-service:8081/",
                "cdsPath","/mytenant/api/v1/");
        TenantConfig tc = new TenantConfig(configMap);
        Mockito.when(tenantManager.getConfig("my-tenant")).thenReturn(tc);

        ApsTenantApplicationUtils.setTenant("my-tenant");

        Assertions.assertThatThrownBy(
                ()-> cdsStorageManager.getResourceUrl("../../test",false)
        ).isInstanceOf(EntRuntimeException.class).hasMessageStartingWith("Error extracting resource url");
    }

    @Test
    void shouldWorkFineWhenCallExists() throws Exception {
        String testFilePath = "/test-folder/test.txt";

        Map<String,String> configMap = Map.of("cdsPublicUrl","http://my-server/tenant1/cms-resources",
                "cdsPrivateUrl","http://cds-tenant1-kube-service:8081/",
                "cdsPath","/mytenant/api/v1/");
        TenantConfig tc = new TenantConfig(configMap);
        Mockito.when(tenantManager.getConfig("my-tenant")).thenReturn(tc);

        ApsTenantApplicationUtils.setTenant("my-tenant");

        Assertions.assertThatThrownBy(
                ()-> cdsStorageManager.exists("../../test",false)
        ).isInstanceOf(EntRuntimeException.class).hasMessageStartingWith("Path validation failed:");


        Mockito.when(cdsRemoteCaller.getFileAttributeView(any(),
                any())).thenReturn(Optional.ofNullable(new CdsFileAttributeViewDto[]{}));
        Assertions.assertThat(cdsStorageManager.exists(testFilePath,false)).isFalse();

        Mockito.reset(cdsRemoteCaller);
        CdsFileAttributeViewDto file = new CdsFileAttributeViewDto();
        file.setName("test.txt");
        Mockito.when(cdsRemoteCaller.getFileAttributeView(eq(URI.create(
                "http://cds-tenant1-kube-service:8081/mytenant/api/v1/list/protected/test-folder")),
                any())).thenReturn(Optional.ofNullable(new CdsFileAttributeViewDto[]{file}));
        Assertions.assertThat(cdsStorageManager.exists(testFilePath,true)).isTrue();

    }

    @Test
    void shouldFilterWorkFineWhenCallList() throws Exception {
        String testFilePath = "/test-folder/";

        Map<String,String> configMap = Map.of("cdsPublicUrl","http://my-server/tenant1/cms-resources",
                "cdsPrivateUrl","http://cds-tenant1-kube-service:8081/",
                "cdsPath","/mytenant/api/v1/");
        TenantConfig tc = new TenantConfig(configMap);
        Mockito.when(tenantManager.getConfig("my-tenant")).thenReturn(tc);

        ApsTenantApplicationUtils.setTenant("my-tenant");

        Assertions.assertThatThrownBy(
                ()-> cdsStorageManager.exists("../../test",false)
        ).isInstanceOf(EntRuntimeException.class).hasMessageStartingWith("Path validation failed:");


        CdsFileAttributeViewDto dir1 = new CdsFileAttributeViewDto();
        dir1.setName("/test");
        dir1.setDirectory(true);
        CdsFileAttributeViewDto file1 = new CdsFileAttributeViewDto();
        file1.setName("test.txt");
        file1.setDirectory(false);
        Mockito.when(cdsRemoteCaller.getFileAttributeView(any(),
                any())).thenReturn(Optional.ofNullable(new CdsFileAttributeViewDto[]{dir1, file1}));
        String[] list = cdsStorageManager.listDirectory(testFilePath,false);
        Assertions.assertThat(list).isNotEmpty();
        Assertions.assertThat(list.length).isEqualTo(1);

        Mockito.reset(cdsRemoteCaller);
        CdsFileAttributeViewDto file = new CdsFileAttributeViewDto();
        file.setName("test.txt");
        file.setDirectory(false);
        Mockito.when(cdsRemoteCaller.getFileAttributeView(eq(URI.create(
                        "http://cds-tenant1-kube-service:8081/mytenant/api/v1/list/protected/test-folder/")),
                any())).thenReturn(Optional.ofNullable(new CdsFileAttributeViewDto[]{file}));
        list = cdsStorageManager.listFile(testFilePath,true);
        Assertions.assertThat(list).isNotEmpty();
        Assertions.assertThat(list.length).isEqualTo(1);

    }

    @Test
    void shouldReadFile() throws Exception {
        String testFilePath = "/testfolder/test.txt";

        Map<String,String> configMap = Map.of("cdsPublicUrl","http://my-server/tenant1/cms-resources",
                "cdsPrivateUrl","http://cds-kube-service:8081/",
                "cdsPath","/mytenant/api/v1/");
        TenantConfig tc = new TenantConfig(configMap);
        Mockito.when(tenantManager.getConfig("my-tenant")).thenReturn(tc);

        Mockito.when(cdsRemoteCaller.getFile(any(),
                any(),
                eq(false))).thenReturn(Optional.ofNullable(new ByteArrayInputStream("text random".getBytes(StandardCharsets.UTF_8))));


        ApsTenantApplicationUtils.setTenant("my-tenant");
        Assertions.assertThat(cdsStorageManager.readFile(testFilePath,false))
                .isEqualTo("text random");

    }


    @Test
    void shouldEditFile() throws Exception {
        Map<String,String> configMap = Map.of("cdsPublicUrl","http://my-server/tenant1/cms-resources",
                "cdsPrivateUrl","http://cds-kube-service:8081/",
                "cdsPath","/mytenant/api/v1/");
        TenantConfig tc = new TenantConfig(configMap);
        Mockito.when(tenantManager.getConfig("my-tenant")).thenReturn(tc);

        CdsCreateResponseDto ret = new CdsCreateResponseDto();
        ret.setStatusOk(true);
        ret.setList(Arrays.asList(new CdsCreateRowResponseDto[]{}));
        ArgumentCaptor<URI> captor = ArgumentCaptor.forClass(URI.class);
        Mockito.when(cdsRemoteCaller.executePostCall(any(),
                eq("/sub-path-testy/myfilename"),
                eq(false),
                any(),
                any(),
                eq(false))).thenReturn(ret);

        ApsTenantApplicationUtils.setTenant("my-tenant");
        InputStream is = new ByteArrayInputStream("testo a casos".getBytes(StandardCharsets.UTF_8));
        cdsStorageManager.editFile("/sub-path-testy/myfilename",false, is);


        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("path", "/sub-path-testy");
        body.add("protected", false);
        body.add("filename", "myfilename");
        body.add("file", new InputStreamResource(is));

        Mockito.verify(cdsRemoteCaller, Mockito.times(1))
                .executePostCall(eq(URI.create("http://cds-kube-service:8081/mytenant/api/v1/upload/")),
                        eq("/sub-path-testy/myfilename"),
                        eq(false),
                        any(),
                        any(),
                        eq(false));

    }

    //@Test
    void testListAttributes() throws Throwable {
        Map<String,String> configMap = Map.of("cdsPublicUrl","http://my-server/tenant1/cms-resources",
                "cdsPrivateUrl","http://cds-kube-service:8081/",
                "cdsPath","/mytenant/api/v1/");
        TenantConfig tc = new TenantConfig(configMap);
        Mockito.when(tenantManager.getConfig("my-tenant")).thenReturn(tc);

        ApsTenantApplicationUtils.setTenant("my-tenant");

        BasicFileAttributeView[] fileAttributes = cdsStorageManager.listAttributes("", false);
        boolean containsCms = false;
        boolean prevDirectory = true;
        String prevName = null;
        for (int i = 0; i < fileAttributes.length; i++) {
            BasicFileAttributeView bfav = fileAttributes[i];
            if (!prevDirectory && bfav.isDirectory()) {
                fail();
            }
            if (bfav.isDirectory() && bfav.getName().equals("cms")) {
                containsCms = true;
            }
            if ((bfav.isDirectory() == prevDirectory) && null != prevName) {
                assertTrue(bfav.getName().compareTo(prevName) > 0);
            }
            prevName = bfav.getName();
            prevDirectory = bfav.isDirectory();
        }
        assertTrue(containsCms);
    }

/*

    //FIXME @Test
    void testListAttributes_2() throws Throwable {
        this.executeTestListAttributes_2();
        EntThreadLocal.set(ITenantManager.THREAD_LOCAL_TENANT_CODE, "tenant");
        this.executeTestListAttributes_2();
        EntThreadLocal.remove(ITenantManager.THREAD_LOCAL_TENANT_CODE);
    }
    
    void executeTestListAttributes_2() throws Throwable {
        BasicFileAttributeView[] fileAttributes = cdsStorageManager.listAttributes("cms" + File.separator, false);
        assertEquals(2, fileAttributes.length);
        int dirCounter = 0;
        int fileCounter = 0;
        for (int i = 0; i < fileAttributes.length; i++) {
            BasicFileAttributeView bfav = fileAttributes[i];
            if (bfav.isDirectory()) {
                dirCounter++;
            } else {
                fileCounter++;
            }
        }
        assertEquals(2, dirCounter);
        assertEquals(0, fileCounter);
        
        fileAttributes = cdsStorageManager.listDirectoryAttributes("cms" + File.separator, false);
        assertEquals(2, fileAttributes.length);
        
        fileAttributes = cdsStorageManager.listFileAttributes("cms" + File.separator, false);
        assertEquals(0, fileAttributes.length);
        
        String[] names = cdsStorageManager.list("cms" + File.separator, false);
        assertEquals(2, names.length);
        
        names = cdsStorageManager.listDirectory("cms" + File.separator, false);
        assertEquals(2, names.length);
        
        names = cdsStorageManager.listFile("cms" + File.separator, false);
        assertEquals(0, names.length);
        
    }

    //FIXME @Test
    void testListAttributes_3() throws Throwable {
        // Non existent directory
        BasicFileAttributeView[] fileAttributes =
                cdsStorageManager.listAttributes("non-existent" + File.separator, false);
        assertEquals(0, fileAttributes.length);

        // Non-directory directory
        fileAttributes =
                cdsStorageManager.listAttributes("conf/security.properties" + File.separator, false);
        assertEquals(0, fileAttributes.length);
    }


    //FIXME @Test
    void testSaveEditDeleteFile() throws Throwable {
        this.executeTestSaveEditDeleteFile(false);
        this.executeTestSaveEditDeleteFile(true);
    }
    
    void executeTestSaveEditDeleteFile(boolean privatePath) throws Throwable {
        String testFilePath = "testfolder/test.txt";
        InputStream stream = null;
        try {
            stream = cdsStorageManager.getStream(testFilePath, privatePath);
            Assertions.assertNull(stream);
            String content = "Content of new text file";
            cdsStorageManager.saveFile(testFilePath, privatePath, new ByteArrayInputStream(content.getBytes()));
            Assertions.assertTrue(cdsStorageManager.exists(testFilePath, privatePath));
            stream = cdsStorageManager.getStream(testFilePath, privatePath);
            Assertions.assertNotNull(stream);
            String extractedString = IOUtils.toString(stream, "UTF-8");
            stream.close();
            Assertions.assertEquals(content, extractedString);
            String newContent = "This is the new content of text file";
            cdsStorageManager.editFile(testFilePath, privatePath, new ByteArrayInputStream(newContent.getBytes()));
            stream = cdsStorageManager.getStream(testFilePath, privatePath);
            String extractedNewString = IOUtils.toString(stream, "UTF-8");
            stream.close();
            Assertions.assertEquals(newContent, extractedNewString);
            String readfileAfterWriteBackup = cdsStorageManager.readFile(testFilePath, privatePath);
            Assertions.assertEquals(extractedNewString.trim(), readfileAfterWriteBackup.trim());
            boolean deleted = cdsStorageManager.deleteFile(testFilePath, privatePath);
            assertTrue(deleted);
            Assertions.assertFalse(cdsStorageManager.exists(testFilePath, privatePath));
            stream = cdsStorageManager.getStream(testFilePath, privatePath);
            Assertions.assertNull(stream);
        } catch (Throwable t) {
            throw t;
        } finally {
            if (null != stream) {
                stream.close();
            }
            cdsStorageManager.deleteDirectory("testfolder/", privatePath);
            InputStream streamBis = cdsStorageManager.getStream(testFilePath, privatePath);
            Assertions.assertNull(streamBis);
        }
        // file not found case
        Assertions.assertFalse(cdsStorageManager.deleteFile("non-existent", false));
    }

    //FIXME @Test
    void testSaveEditDeleteFile_2() throws Throwable {
        String testFilePath = "testfolder_2/architectureUploaded.txt";
        InputStream stream = null;
        try {
            stream = cdsStorageManager.getStream(testFilePath, false);
            Assertions.assertNull(stream);
            File file = new File("src/test/resources/document/architecture.txt");
            cdsStorageManager.saveFile(testFilePath, false, new FileInputStream(file));
            stream = cdsStorageManager.getStream(testFilePath, false);
            Assertions.assertNotNull(stream);
            
            String originalText = FileTextReader.getText("src/test/resources/document/architecture.txt");
            String extractedText = FileTextReader.getText(stream);
            Assertions.assertEquals(originalText, extractedText);
            
            boolean deleted = cdsStorageManager.deleteFile(testFilePath, false);
            assertTrue(deleted);
            stream = cdsStorageManager.getStream(testFilePath, false);
            Assertions.assertNull(stream);
        } catch (Throwable t) {
            throw t;
        } finally {
            if (null != stream) {
                stream.close();
            }
            cdsStorageManager.deleteDirectory("testfolder_2/", false);
            InputStream streamBis = cdsStorageManager.getStream(testFilePath, false);
            Assertions.assertNull(streamBis);
        }
        // file not found case
        Assertions.assertFalse(cdsStorageManager.deleteFile("non-existent", false));
    }

    //FIXME @Test
    void testCreateDeleteFile_ShouldBlockPathTraversals() throws Throwable {
        String testFilePath = "../../testfolder/test.txt";
        String content = "Content of new text file";
        EntRuntimeException exc1 = Assertions.assertThrows(EntRuntimeException.class, () -> {
            this.cdsStorageManager.saveFile(testFilePath, false, new ByteArrayInputStream(content.getBytes()));
        });
        assertThat(exc1.getMessage(), CoreMatchers.startsWith("Path validation failed"));
        EntRuntimeException exc2 = Assertions.assertThrows(EntRuntimeException.class, () -> {
            this.cdsStorageManager.deleteFile(testFilePath, false);
        });
        assertThat(exc2.getMessage(), CoreMatchers.startsWith("Path validation failed"));
    }

    //FIXME  @Test
    void testCreateDeleteDir() throws EntException {
        String directoryName = "testfolder";
        String subDirectoryName = "subfolder";
        Assertions.assertFalse(this.cdsStorageManager.exists(directoryName, false));
        try {
            this.cdsStorageManager.createDirectory(directoryName + File.separator + subDirectoryName, false);
            assertTrue(this.cdsStorageManager.exists(directoryName, false));
            String[] listDirectory = this.cdsStorageManager.listDirectory(directoryName, false);
            assertEquals(1, listDirectory.length);
            listDirectory = this.cdsStorageManager.listDirectory(directoryName + File.separator + subDirectoryName, false);
            assertEquals(0, listDirectory.length);
        } finally {
            this.cdsStorageManager.deleteDirectory(directoryName, false);
            Assertions.assertFalse(this.cdsStorageManager.exists(directoryName, false));
        }
    }

    //FIXME @Test
    public void testExists() throws Exception {
        String folder = "jpversioning/trashedresources";
        Assertions.assertFalse(this.cdsStorageManager.exists(folder, false));
    }

    //FIXME @Test
    void testGetResourceUrl() throws Throwable {
        String expected = "http://cds.10-219-168-112.nip.io/primary/public";
        String expectedProtected = "http://cds.10-219-168-112.nip.io/protected";
        Assertions.assertEquals(expectedProtected, this.cdsStorageManager.createFullPath("", true));
        Assertions.assertEquals(expected, this.cdsStorageManager.createFullPath("", false));
        EntThreadLocal.set(ITenantManager.THREAD_LOCAL_TENANT_CODE, "tenant");
        Assertions.assertEquals(expectedProtected, this.cdsStorageManager.createFullPath("", true));
        Assertions.assertEquals(expected, this.cdsStorageManager.createFullPath("", false));
        EntThreadLocal.remove(ITenantManager.THREAD_LOCAL_TENANT_CODE);
    }
    
    /*
    @Test
    void testCreateDeleteDir_ShouldHandleFailureCases() throws EntException {
        String baseFolder = "non-existent";
        String endingFolder = "dir-to-create";
        String fullPath = baseFolder + File.separator + endingFolder;
        Functions.FailableBiConsumer<String, Boolean, EntException> assertExists = (p, exp) ->
                assertEquals(exp, (Boolean) localStorageManager.exists(p, false));

        // Simple fail by duplication or not found
        try {
            localStorageManager.createDirectory(fullPath, false);
            assertExists.accept(fullPath, true);
            localStorageManager.createDirectory(fullPath, false);
            assertExists.accept(fullPath, true);
            localStorageManager.deleteDirectory(fullPath, false);
            assertExists.accept(fullPath, false);
        } finally {
            localStorageManager.deleteDirectory(baseFolder, false);
            assertFalse(localStorageManager.exists(baseFolder, false));
        }
    }

    @Test
    void testCreateDeleteDirectory_ShouldBlockPathTraversals() throws Throwable {
        EntRuntimeException exc1 = Assertions.assertThrows(EntRuntimeException.class, () -> {
            this.localStorageManager.createDirectory("/../../../dev/mydir", false);
        });
        assertThat(exc1.getMessage(), CoreMatchers.startsWith("Path validation failed"));

        EntRuntimeException exc2 = Assertions.assertThrows(EntRuntimeException.class, () -> {
            this.localStorageManager.deleteDirectory("/../../../dev/mydir", false);
        });
        assertThat(exc2.getMessage(), CoreMatchers.startsWith("Path validation failed"));
        this.localStorageManager.createDirectory("target/mydir", false);
        BasicFileAttributeView[] attributes = this.localStorageManager.listAttributes("target/mydir", false);
        assertNotNull(attributes);
        assertEquals(0, attributes.length);
        this.localStorageManager.deleteDirectory("target/mydir", false);
        this.localStorageManager.deleteDirectory("target", false);
    }
*/

}
