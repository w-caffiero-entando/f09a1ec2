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
import org.entando.entando.aps.system.services.api.model.StringListApiResponse;
import org.entando.entando.aps.system.services.userprofile.api.ApiUserProfileInterface;
import org.entando.entando.web.utils.OAuth2TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

class ApiRestServerUserProfileTest extends BaseLegacyApiTest {

    @Mock
    private ApiUserProfileInterface apiUserProfileInterface;

    private String accessToken;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        Mockito.when(beanFactory.getBean("ApiUserProfileInterface")).thenReturn(apiUserProfileInterface);
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        accessToken = mockOAuthInterceptor(user);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/core:userProfiles", "/core:userProfiles.xml"})
    void shouldGetUserProfilesXml(String path) throws Throwable {
        mockGetUserProfiles();

        ResultActions result = mockMvc
                .perform(get("/legacy/en" + path)
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

    @ParameterizedTest
    @ValueSource(strings = {"/core:userProfiles", "/core:userProfiles.json"})
    void shouldGetUserProfilesJson(String path) throws Throwable {
        mockGetUserProfiles();

        ResultActions result = mockMvc
                .perform(get("/legacy/en" + path)
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

    private void mockGetUserProfiles() throws Throwable {
        Mockito.when(apiUserProfileInterface.getUserProfiles(Mockito.any())).thenReturn(List.of("admin", "user1"));

        ApiMethod apiMethod = mockApiMethod(ApiMethod.HttpMethod.GET, null, "core:userProfiles");
        Mockito.when(apiMethod.getSpringBeanMethod()).thenReturn("getUserProfiles");
        Mockito.when(apiMethod.getResponseClassName()).thenReturn(StringListApiResponse.class.getCanonicalName());
        Mockito.when(apiMethod.getRequiredPermission()).thenReturn("viewUsers");
        ApiMethodParameter typeCodeParameter = Mockito.mock(ApiMethodParameter.class);
        Mockito.when(typeCodeParameter.getKey()).thenReturn("typeCode");
        Mockito.when(typeCodeParameter.isRequired()).thenReturn(true);
        Mockito.when(apiMethod.getParameters()).thenReturn(List.of(typeCodeParameter));
    }

    @Override
    protected ApiMethod mockApiMethod(ApiMethod.HttpMethod method, String namespace, String resourceName)
            throws Throwable {
        ApiMethod apiMethod = super.mockApiMethod(method, namespace, resourceName);
        Mockito.when(apiMethod.getSpringBean()).thenReturn("ApiUserProfileInterface");
        return apiMethod;
    }
}
