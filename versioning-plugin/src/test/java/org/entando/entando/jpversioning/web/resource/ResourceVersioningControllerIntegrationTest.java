package org.entando.entando.jpversioning.web.resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.plugins.jacms.aps.system.services.content.IContentManager;
import com.agiletec.plugins.jpversioning.aps.system.services.resource.TrashedResourceManager;
import com.agiletec.plugins.jpversioning.util.JpversioningTestHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import org.entando.entando.ent.exception.EntException;
import org.entando.entando.plugins.jacms.web.resource.request.CreateResourceRequest;
import org.entando.entando.web.AbstractControllerIntegrationTest;
import org.entando.entando.web.utils.OAuth2TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class ResourceVersioningControllerIntegrationTest extends AbstractControllerIntegrationTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String CONTENT = "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
            "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
            "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
            "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
            "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
            "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
            "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
            "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
            "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
            "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
            "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
            "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
            "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x" +
            "a;lsdka;lsdka;lsdka;lsdk;alskd;laskd;aslkd;alsdk;alskda;lskldaskl;sdjodpasu0i9728938701o7i186r890347974209817409823740bgbdf98dw787012378b1789b13281328701b39871029371x";

    @Autowired
    private TrashedResourceManager trashedResourceManager;
    
    @Autowired
    private IContentManager contentManager;
    
    @Autowired
    @Qualifier("portDataSource")
    private DataSource dataSource;
    
    @AfterEach
    public void release() throws EntException {
        JpversioningTestHelper helper = new JpversioningTestHelper(this.dataSource, this.contentManager);
        helper.cleanTrashedResources("66", "67", "68", "69", "70", "71");
    }
    
    @Test
    void testListDeletedAttachments() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String resourceTypeCode = "Attach";

        Map<String, String> params = new HashMap<>();
        params.put("resourceTypeCode", resourceTypeCode);

        listTrashedResources(user, params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(1)));

        ResultActions result = performCreateResource(user, "file", "free", "application/pdf");

        String bodyResult = result.andReturn().getResponse().getContentAsString();
        String resourceId = JsonPath.read(bodyResult, "$.payload.id");

        listTrashedResources(user, params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(1)));

        performDeleteResource(user, resourceTypeCode, resourceId)
                .andExpect(status().isOk());

        listTrashedResources(user, params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(2)))
                .andExpect(jsonPath("$.payload[1].path", is("protected/trashed/" + resourceId + "/0")));
        
        performGetResources(user, "file")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(3)));
        
    }

    @Test
    void testListDeletedAttachmentsPagination() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String resourceTypeCode = "Attach";
        String type = "file";

        Map<String, String> params = new HashMap<>();
        params.put("resourceTypeCode", resourceTypeCode);

        listTrashedResources(user, params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(1)));

        createAndDeleteResource(user, resourceTypeCode, type, params);
        createAndDeleteResource(user, resourceTypeCode, type, params);
        createAndDeleteResource(user, resourceTypeCode, type, params);
        createAndDeleteResource(user, resourceTypeCode, type, params);
        createAndDeleteResource(user, resourceTypeCode, type, params);
        createAndDeleteResource(user, resourceTypeCode, type, params);

        listTrashedResources(user, params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(7)));

        params.put("pageSize", "4");
        params.put("page", "1");

        listTrashedResources(user, params)
                .andExpect(jsonPath("$.payload.size()", is(4)))
                .andExpect(jsonPath("$.metaData.page", is(1)))
                .andExpect(jsonPath("$.metaData.pageSize", is(4)))
                .andExpect(jsonPath("$.metaData.totalItems", is(7)));

        params.clear();
        params.put("resourceTypeCode", resourceTypeCode);
        params.put("pageSize", "4");
        params.put("page", "2");

        listTrashedResources(user, params)
                .andExpect(jsonPath("$.payload.size()", is(3)))
                .andExpect(jsonPath("$.metaData.page", is(2)))
                .andExpect(jsonPath("$.metaData.pageSize", is(4)))
                .andExpect(jsonPath("$.metaData.totalItems", is(7)));

    }
    
    @Test
    void testListDeletedAttachmentsFiltering() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String resourceTypeCode = "Attach";
        String type = "file";

        Map<String, String> params = new HashMap<>();
        params.put("resourceTypeCode", resourceTypeCode);

        listTrashedResources(user, params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(1)));

        createAndDeleteResource(user, resourceTypeCode, type, params);
        createAndDeleteResource(user, resourceTypeCode, type, params);
        createAndDeleteResource(user, resourceTypeCode, type, params);

        listTrashedResources(user, params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(4)));

        params.put("filters[0].attribute", "description");
        params.put("filters[0].value", "file_test.jpeg");

        listTrashedResources(user, params)
                .andExpect(jsonPath("$.payload.size()", is(3)))
                .andExpect(jsonPath("$.payload[0].description", is("file_test.jpeg")))
                .andExpect(jsonPath("$.metaData.filters.size()", is(1)))
                .andExpect(jsonPath("$.metaData.filters[0].attribute", is("description")))
                .andExpect(jsonPath("$.metaData.filters[0].value", is("file_test.jpeg")));

        params.clear();
        params.put("resourceTypeCode", resourceTypeCode);
        params.put("filters[0].attribute", "description");
        params.put("filters[0].value", "wrong description");

        listTrashedResources(user, params)
                .andExpect(jsonPath("$.payload.size()", is(0)))
                .andExpect(jsonPath("$.metaData.filters.size()", is(1)))
                .andExpect(jsonPath("$.metaData.filters[0].attribute", is("description")))
                .andExpect(jsonPath("$.metaData.filters[0].value", is("wrong description")));

    }
    
    @Test
    void testListDeletedImage() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String resourceTypeCode = "Image";
        String type = "image";

        Map<String, String> params = new HashMap<>();
        params.put("resourceTypeCode", resourceTypeCode);

        listTrashedResources(user, params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(5)));

        String resourceId = createAndDeleteResource(user, resourceTypeCode, type, params);

        listTrashedResources(user, params)
                .andExpect(status().isOk()).andExpect(jsonPath("$.payload.size()", is(6)));
        //        .andExpect(jsonPath("$.payload[5].versions[0].path", is("protected/trashed/" + resourceId + "/1")))
        //        .andExpect(jsonPath("$.payload[5].versions[1].path", is("protected/trashed/" + resourceId + "/2")))
        //        .andExpect(jsonPath("$.payload[5].versions[2].path", is("protected/trashed/" + resourceId + "/3")));
    }
    
    @Test
    void testListDeletedImagePagination() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String resourceTypeCode = "Image";
        String type = "image";

        Map<String, String> params = new HashMap<>();
        params.put("resourceTypeCode", resourceTypeCode);

        listTrashedResources(user, params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(5)));

        createAndDeleteResource(user, resourceTypeCode, type, params);
        createAndDeleteResource(user, resourceTypeCode, type, params);
        createAndDeleteResource(user, resourceTypeCode, type, params);
        createAndDeleteResource(user, resourceTypeCode, type, params);
        createAndDeleteResource(user, resourceTypeCode, type, params);

        listTrashedResources(user, params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(10)));

        params.put("pageSize", "4");
        params.put("page", "1");

        listTrashedResources(user, params)
                .andExpect(jsonPath("$.payload.size()", is(4)))
                .andExpect(jsonPath("$.metaData.page", is(1)))
                .andExpect(jsonPath("$.metaData.pageSize", is(4)))
                .andExpect(jsonPath("$.metaData.totalItems", is(10)));

        params.clear();
        params.put("resourceTypeCode", resourceTypeCode);
        params.put("pageSize", "4");
        params.put("page", "3");

        listTrashedResources(user, params)
                .andExpect(jsonPath("$.payload.size()", is(2)))
                .andExpect(jsonPath("$.metaData.page", is(3)))
                .andExpect(jsonPath("$.metaData.pageSize", is(4)))
                .andExpect(jsonPath("$.metaData.totalItems", is(10)));
    }
    
    @Test
    void testListDeletedImageFiltering() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String resourceTypeCode = "Image";
        String type = "image";

        Map<String, String> params = new HashMap<>();
        params.put("resourceTypeCode", resourceTypeCode);

        listTrashedResources(user, params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(5)));

        createAndDeleteResource(user, resourceTypeCode, type, params);

        listTrashedResources(user, params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(6)));

        params.put("filters[0].attribute", "description");
        params.put("filters[0].value", "image_test.jpeg");

        listTrashedResources(user, params)
                .andExpect(jsonPath("$.payload.size()", is(1)))
                .andExpect(jsonPath("$.payload[0].description", is("image_test.jpeg")))
                .andExpect(jsonPath("$.metaData.filters.size()", is(1)))
                .andExpect(jsonPath("$.metaData.filters[0].attribute", is("description")))
                .andExpect(jsonPath("$.metaData.filters[0].value", is("image_test.jpeg")));

        params.clear();
        params.put("resourceTypeCode", resourceTypeCode);
        params.put("filters[0].attribute", "description");
        params.put("filters[0].value", "wrong description");

        listTrashedResources(user, params)
                .andExpect(jsonPath("$.payload.size()", is(0)))
                .andExpect(jsonPath("$.metaData.filters.size()", is(1)))
                .andExpect(jsonPath("$.metaData.filters[0].attribute", is("description")))
                .andExpect(jsonPath("$.metaData.filters[0].value", is("wrong description")));

    }
    
    @Test
    void testTrashedAttachmentsRecover() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String resourceTypeCode = "Attach";

        Map<String, String> params = new HashMap<>();
        params.put("resourceTypeCode", resourceTypeCode);

        listTrashedResources(user, params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(1)));

        ResultActions result = performCreateResource(user, "file", "free", "application/pdf");

        String bodyResult = result.andReturn().getResponse().getContentAsString();
        String resourceId = JsonPath.read(bodyResult, "$.payload.id");

        listTrashedResources(user, params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(1)));

        performGetResources(user, "file")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(4)));

        performDeleteResource(user, resourceTypeCode, resourceId)
                .andExpect(status().isOk());

        result = listTrashedResources(user, params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(2)));

        performGetResources(user, "file")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(3)));

        bodyResult = result.andReturn().getResponse().getContentAsString();
        
        String versionResourceId = JsonPath.read(bodyResult, "$.payload[1].id");

        recoverResource(user, versionResourceId)
                .andExpect(status().isOk());

        listTrashedResources(user, params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(1)));

        performGetResources(user, "file")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(4)));

        performDeleteResource(user, resourceTypeCode, resourceId)
                .andExpect(status().isOk());

    }
    
    @Test
    void testTrashedImageRecover() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String resourceTypeCode = "Image";
        String resourceId = null;
        //String versionResourceId = null;

        Map<String, String> params = new HashMap<>();
        params.put("resourceTypeCode", resourceTypeCode);

        listTrashedResources(user, params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(5)));
        
        performGetResources(user, "image")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(3)));

        ResultActions result = performCreateResource(user, "image", "free", "application/jpeg");

        String bodyResult = result.andReturn().getResponse().getContentAsString();
        resourceId = JsonPath.read(bodyResult, "$.payload.id");

        listTrashedResources(user, params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(5)));

        performGetResources(user, "image")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(4)));

        performDeleteResource(user, resourceTypeCode, resourceId)
                .andExpect(status().isOk());

        result = listTrashedResources(user, params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(6)));

        performGetResources(user, "image")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(3)));

        deleteTrashedResource(user, resourceId)
                .andExpect(status().isOk());

        listTrashedResources(user, params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(5)));

        performGetResources(user, "image")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(3)));

    }
    
    @Test
    void testDeleteTrashedAttachment() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String resourceTypeCode = "Attach";
        String resourceId = null;

        Map<String, String> params = new HashMap<>();
        params.put("resourceTypeCode", resourceTypeCode);

        listTrashedResources(user, params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(1)));

        ResultActions result = performCreateResource(user, "file", "free", "application/pdf");

        String bodyResult = result.andReturn().getResponse().getContentAsString();
        resourceId = JsonPath.read(bodyResult, "$.payload.id");

        listTrashedResources(user, params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(1)));

        performGetResources(user, "file")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(4)));

        performDeleteResource(user, resourceTypeCode, resourceId)
                .andExpect(status().isOk());

        listTrashedResources(user, params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(2)));

        performGetResources(user, "file")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(3)));
        
        deleteTrashedResource(user, resourceId)
                .andExpect(status().isOk());

        listTrashedResources(user, params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(1)));

        performGetResources(user, "file")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(3)));
    }
    
    @Test
    void testDeleteTrashedImage() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String resourceTypeCode = "Image";
        String resourceId = null;

        Map<String, String> params = new HashMap<>();
        params.put("resourceTypeCode", resourceTypeCode);

        listTrashedResources(user, params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(5)));

        ResultActions result = performCreateResource(user, "image", "free", "application/jpeg");

        String bodyResult = result.andReturn().getResponse().getContentAsString();
        resourceId = JsonPath.read(bodyResult, "$.payload.id");

        listTrashedResources(user, params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(5)));

        performGetResources(user, "image")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(4)));

        performDeleteResource(user, resourceTypeCode, resourceId)
                .andExpect(status().isOk());

        listTrashedResources(user, params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(6)));

        performGetResources(user, "image")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(3)));

        deleteTrashedResource(user, resourceId)
                .andExpect(status().isOk());

        listTrashedResources(user, params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(5)));

        performGetResources(user, "image")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(3)));

    }
    
    @Test
    void testGetTrashedAttachment() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String resourceTypeCode = "Attach";
        String resourceId = null;

        Map<String, String> params = new HashMap<>();
        params.put("resourceTypeCode", resourceTypeCode);

        listTrashedResources(user, params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(1)));

        ResultActions result = performCreateResource(user, "file", "free", "application/pdf");

        String bodyResult = result.andReturn().getResponse().getContentAsString();
        resourceId = JsonPath.read(bodyResult, "$.payload.id");

        listTrashedResources(user, params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(1)));

        performGetResources(user, "file")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(4)));

        performDeleteResource(user, resourceTypeCode, resourceId)
                .andExpect(status().isOk());

        listTrashedResources(user, params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(2)));

        performGetResources(user, "file")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(3)));

        result = performDownloadResource(user, resourceId, 0);

        MockHttpServletResponse servletResponse = result.andReturn().getResponse();

        assertThat(servletResponse.getHeader(HttpHeaders.CONTENT_DISPOSITION), is("attachment; filename=file_test.jpeg"));
        assertThat(servletResponse.getHeader(HttpHeaders.CONTENT_TYPE), is("application/pdf"));
        assertThat(servletResponse.getHeader(HttpHeaders.CONTENT_LENGTH), is("2324"));
        assertThat(servletResponse.getContentAsString(), is(CONTENT));

        servletResponse = performDownloadResource(user, resourceId, 1).andReturn().getResponse();
        assertThat(servletResponse.getHeader(HttpHeaders.CONTENT_DISPOSITION), is("attachment; filename=file_test.jpeg"));
        assertThat(servletResponse.getHeader(HttpHeaders.CONTENT_TYPE), is("application/pdf"));
        assertThat(servletResponse.getHeader(HttpHeaders.CONTENT_LENGTH), is("2324"));
        assertThat(servletResponse.getContentAsString(), is(CONTENT));

        servletResponse = performDownloadResource(user, resourceId, 30).andReturn().getResponse();
        assertThat(servletResponse.getHeader(HttpHeaders.CONTENT_DISPOSITION), is("attachment; filename=file_test.jpeg"));
        assertThat(servletResponse.getHeader(HttpHeaders.CONTENT_TYPE), is("application/pdf"));
        assertThat(servletResponse.getHeader(HttpHeaders.CONTENT_LENGTH), is("2324"));
        assertThat(servletResponse.getContentAsString(), is(CONTENT));

        performDownloadResource(user, "-1", 0).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors.size()", is(1)))
                .andExpect(jsonPath("$.errors[0].code", is("3")))
                .andExpect(jsonPath("$.errors[0].message", is("a Trashed resource:  with -1 code could not be found")));
    }
    
    @Test
    void testGetTrashedImage() throws Exception {
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String resourceTypeCode = "Image";
        String resourceId = null;

        Map<String, String> params = new HashMap<>();
        params.put("resourceTypeCode", resourceTypeCode);

        listTrashedResources(user, params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(5)));

        ResultActions result = performCreateResource(user, "image", "free", "application/jpeg");

        String bodyResult = result.andReturn().getResponse().getContentAsString();
        resourceId = JsonPath.read(bodyResult, "$.payload.id");

        listTrashedResources(user, params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(5)));

        performGetResources(user, "image")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(4)));

        performDeleteResource(user, resourceTypeCode, resourceId)
                .andExpect(status().isOk());

        listTrashedResources(user, params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(6)));

        result = performDownloadResource(user, resourceId, 0);

        MockHttpServletResponse servletResponse = result.andReturn().getResponse();

        assertThat(servletResponse.getHeader(HttpHeaders.CONTENT_DISPOSITION), is("attachment; filename=image_test_d0.jpeg"));
        assertThat(servletResponse.getHeader(HttpHeaders.CONTENT_TYPE), is("application/jpeg"));
        assertThat(servletResponse.getHeader(HttpHeaders.CONTENT_LENGTH), is("2324"));
        assertThat(servletResponse.getContentAsString(), is(CONTENT));

        servletResponse = performDownloadResource(user, resourceId, 1).andReturn().getResponse();
        assertThat(servletResponse.getHeader(HttpHeaders.CONTENT_DISPOSITION), is("attachment; filename=image_test_d1.jpeg"));
        assertThat(servletResponse.getHeader(HttpHeaders.CONTENT_TYPE), is("image/jpeg"));

        performDownloadResource(user, "-1", 0).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors.size()", is(1)))
                .andExpect(jsonPath("$.errors[0].code", is("3")))
                .andExpect(jsonPath("$.errors[0].message", is("a Trashed resource:  with -1 code could not be found")));
    }
    
    @Test
    void testResourceVersioningPermissionsContentEditor() throws Exception {

        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, "editor", Permission.CONTENT_EDITOR)
                .build();

        String resourceTypeCode = "Image";

        Map<String, String> params = new HashMap<>();
        params.put("resourceTypeCode", resourceTypeCode);

        listTrashedResources(user, params)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errors.size()", is(1)))
                .andExpect(jsonPath("$.errors[0].code", is("120")))
                .andExpect(jsonPath("$.errors[0].message", is("jack_bauer is not allowed to /plugins/versioning/resources [GET]")));

        user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24")
                .withAuthorization(Group.FREE_GROUP_NAME, "editor", Permission.MANAGE_RESOURCES)
                .build();

        listTrashedResources(user, params)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()", is(4)));
    }
    
    private ResultActions recoverResource(UserDetails user, String versionResourceId) throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = post(
                "/plugins/versioning/resources/{resourceId}/recover", versionResourceId)
                .header("Authorization", "Bearer " + mockOAuthInterceptor(user));

        return mockMvc.perform(requestBuilder)
                .andDo(resultPrint());
    }

    private ResultActions deleteTrashedResource(UserDetails user, String versionResourceId) throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = delete(
                "/plugins/versioning/resources/{resourceId}", versionResourceId)
                .header("Authorization", "Bearer " + mockOAuthInterceptor(user));

        return mockMvc.perform(requestBuilder)
                .andDo(resultPrint());
    }

    private String createAndDeleteResource(UserDetails user, String resourceTypeCode, String type, Map<String, String> params) throws Exception {
        String mimeType = type.equals("image") ? "application/jpeg" : "application/pdf";
        ResultActions result = performCreateResource(user, type, "free", mimeType);
        String bodyResult = result.andReturn().getResponse().getContentAsString();
        String resourceId = JsonPath.read(bodyResult, "$.payload.id");

        performDeleteResource(user, resourceTypeCode, resourceId)
                .andExpect(status().isOk());

        result = listTrashedResources(user, params)
                .andExpect(status().isOk());
        bodyResult = result.andReturn().getResponse().getContentAsString();

        Integer payloadSize = JsonPath.read(bodyResult, "$.payload.size()");
        for (int i = 0; i < payloadSize; i++) {
            if (JsonPath.read(bodyResult, "$.payload[" + i + "].description").equals("image_test.jpeg")
                    || JsonPath.read(bodyResult, "$.payload[" + i + "].description").equals("file_test.jpeg")) {
                return JsonPath.read(bodyResult, "$.payload[" + i + "].id");
            }
        }
        return null;
    }

    private ResultActions listTrashedResources(UserDetails user, Map<String,String> params) throws Exception {
        String accessToken = mockOAuthInterceptor(user);

        final MockHttpServletRequestBuilder requestBuilder = get("/plugins/versioning/resources")
                .header("Authorization", "Bearer " + accessToken);

        for (String key : Optional.ofNullable(params).orElse(new HashMap<>()).keySet()) {
            requestBuilder.param(key, params.get(key));
        }
        requestBuilder.param("sort", "id");
        requestBuilder.param("direction", "ASC");
        return mockMvc.perform(requestBuilder)
                .andDo(resultPrint());
    }

    private ResultActions performDeleteResource(UserDetails user, String type, String resourceId) throws Exception {
        String path = String.format("/plugins/cms/assets/%s", resourceId);

        if (null == user) {
            return mockMvc.perform(get(path));
        }

        String accessToken = mockOAuthInterceptor(user);
        return mockMvc.perform(
                delete(path)
                        .header("Authorization", "Bearer " + accessToken)).andDo(resultPrint());
    }

    private ResultActions performCreateResource(UserDetails user, String type, String group, String mimeType) throws Exception {
        String urlPath = String.format("/plugins/cms/assets", type);

        CreateResourceRequest resourceRequest = new CreateResourceRequest();
        resourceRequest.setType(type);
        resourceRequest.setGroup(group);

        if (null == user) {
            return mockMvc.perform(get(urlPath));
        }

        String accessToken = mockOAuthInterceptor(user);

        MockMultipartFile file;
        if ("image".equals(type)) {
            file = new MockMultipartFile("file", "image_test.jpeg", mimeType, CONTENT.getBytes());
        } else {
            file = new MockMultipartFile("file", "file_test.jpeg", mimeType, CONTENT.getBytes());
        }

        MockHttpServletRequestBuilder request = multipart(urlPath)
                .file(file)
                .param("metadata", MAPPER.writeValueAsString(resourceRequest))
                .header("Authorization", "Bearer " + accessToken);

        return mockMvc.perform(request).andDo(resultPrint());
    }

    private ResultActions performGetResources(UserDetails user, String type) throws Exception {
        String path = "/plugins/cms/assets";

        if (type != null) {
            path += "?type=" + type;
        }

        if (null == user) {
            return mockMvc.perform(get(path));
        }
        
        String accessToken = mockOAuthInterceptor(user);
        return mockMvc.perform(
                get(path)
                        .header("Authorization", "Bearer " + accessToken)).andDo(resultPrint());
    }

    private ResultActions performDownloadResource(UserDetails user, String resourceId, Integer size) throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = get(
                "/plugins/versioning/resources/{resourceId}/{size}", resourceId, size)
                .header("Authorization", "Bearer " + mockOAuthInterceptor(user));

        return mockMvc.perform(requestBuilder)
                .andDo(resultPrint());
    }

}
