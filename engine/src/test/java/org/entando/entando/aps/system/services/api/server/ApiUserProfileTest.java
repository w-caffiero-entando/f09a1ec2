package org.entando.entando.aps.system.services.api.server;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import com.agiletec.aps.system.services.user.UserDetails;
import java.util.List;
import org.entando.entando.aps.system.services.api.model.ApiMethod;
import org.entando.entando.aps.system.services.api.model.ApiMethodParameter;
import org.entando.entando.aps.system.services.userprofile.api.ApiUserProfileInterface;
import org.entando.entando.web.utils.OAuth2TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

class ApiUserProfileTest extends BaseLegacyApiTest {

    @Mock
    private ApiUserProfileInterface apiUserProfileInterface;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        ApiMethod apiMethod = Mockito.mock(ApiMethod.class);
        Mockito.when(apiMethod.getResourceName()).thenReturn("userProfiles");
        Mockito.when(apiMethod.getNamespace()).thenReturn("core");
        Mockito.when(apiMethod.getSpringBean()).thenReturn("ApiUserProfileInterface");
        Mockito.when(apiMethod.getSpringBeanMethod()).thenReturn("getUserProfiles");
        Mockito.when(apiMethod.getResponseClassName())
                .thenReturn("org.entando.entando.aps.system.services.api.model.StringListApiResponse");
        Mockito.when(apiMethod.getRequiredPermission()).thenReturn("viewUsers");
        ApiMethodParameter typeCodeParameter = Mockito.mock(ApiMethodParameter.class);
        Mockito.when(typeCodeParameter.getKey()).thenReturn("typeCode");
        Mockito.when(typeCodeParameter.isRequired()).thenReturn(true);
        Mockito.when(apiMethod.getParameters()).thenReturn(List.of(typeCodeParameter));
        Mockito.when(apiMethod.isActive()).thenReturn(true);
        Mockito.when(apiMethod.getHttpMethod()).thenReturn(ApiMethod.HttpMethod.GET);

        Mockito.when(apiCatalogManager.getMethod(ApiMethod.HttpMethod.GET, null, "core:userProfiles"))
                .thenReturn(apiMethod);

        legacyApiUserExtractor.getUser(Mockito.any());
        Mockito.when(beanFactory.getBean("ApiUserProfileInterface")).thenReturn(apiUserProfileInterface);

        responseBuilder.setApiCatalogManager(apiCatalogManager);
        responseBuilder.setBeanFactory(beanFactory);
    }

    @Test
    void testGetUserProfilesXml() throws Throwable {

        Mockito.when(apiUserProfileInterface.getUserProfiles(Mockito.any())).thenReturn(List.of("admin", "user1"));

        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);

        ResultActions result = mockMvc
                .perform(get("/legacy/en/core:userProfiles")
                        .queryParam("typeCode", "PFL")
                        .accept(MediaType.APPLICATION_XML)
                        .header("Authorization", "Bearer " + accessToken));
        result
                .andDo(print())
                .andExpect(xpath("/response/result/items/size").number(2d))
                .andExpect(xpath("/response/result/items/item[1]").string("admin"))
                .andExpect(xpath("/response/result/items/item[2]").string("user1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetUserProfilesJson() throws Throwable {

        Mockito.when(apiUserProfileInterface.getUserProfiles(Mockito.any())).thenReturn(List.of("admin", "user1"));

        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        String accessToken = mockOAuthInterceptor(user);

        ResultActions result = mockMvc
                .perform(get("/legacy/en/core:userProfiles")
                        .queryParam("typeCode", "PFL")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken));
        result
                .andDo(print())
                .andExpect(jsonPath("result.items.size", is("2")))
                .andExpect(jsonPath("result.items.item[0]", is("admin")))
                .andExpect(jsonPath("result.items.item[1]", is("user1")))
                .andExpect(status().isOk());
    }
}
