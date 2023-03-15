package org.entando.entando.aps.system.services.api.server;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import com.agiletec.aps.system.common.entity.model.attribute.JAXBTextAttribute;
import com.agiletec.aps.system.services.user.UserDetails;
import java.util.List;
import org.entando.entando.aps.system.services.api.model.ApiMethod;
import org.entando.entando.aps.system.services.api.model.ApiMethod.HttpMethod;
import org.entando.entando.aps.system.services.api.model.ApiMethodParameter;
import org.entando.entando.aps.system.services.api.model.StringListApiResponse;
import org.entando.entando.aps.system.services.userprofile.api.ApiUserProfileInterface;
import org.entando.entando.aps.system.services.userprofile.api.model.JAXBUserProfile;
import org.entando.entando.web.utils.OAuth2TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

class ApiRestServerUserProfileTest extends BaseLegacyApiTest {

    private static final String JSON_REQUEST_BODY = "{\n"
            + "    \"fullname\": \"TestProfile\",\n"
            + "    \"mail\": \"email@example.com\",\n"
            + "    \"id\": \"testprofile\",\n"
            + "    \"attribute\": [\n"
            + "        {\n"
            + "            \"classType\": \"JAXBTextAttribute\",\n"
            + "            \"value\": \"TestProfile\",\n"
            + "            \"name\": \"fullname\",\n"
            + "            \"names\": {\n"
            + "                \"en\": \"fullname\"\n"
            + "            },\n"
            + "            \"type\": \"Monotext\",\n"
            + "            \"description\": \"Full Name\",\n"
            + "            \"role\": [\n"
            + "                \"userprofile:fullname\"\n"
            + "            ]\n"
            + "        }\n"
            + "    ],\n"
            + "    \"typeCode\": \"PFL\",\n"
            + "    \"typeDescription\": \"Test user profile\",\n"
            + "    \"mainGroup\": null,\n"
            + "    \"description\": null,\n"
            + "    \"group\": []\n"
            + "}";

    private static final String XML_REQUEST_BODY = "<userProfile>\n"
            + "    <fullname>TestProfile</fullname>\n"
            + "    <mail>email@example.com</mail>\n"
            + "    <id>testxml</id>\n"
            + "    <attributes>\n"
            + "        <attribute classType=\"JAXBTextAttribute\">\n"
            + "            <value>TestProfile</value>\n"
            + "            <name>fullname</name>\n"
            + "            <names>\n"
            + "                <en>fullname</en>\n"
            + "            </names>\n"
            + "            <type>Monotext</type>\n"
            + "            <description>Full Name</description>\n"
            + "            <roles>\n"
            + "                <role>userprofile:fullname</role>\n"
            + "            </roles>\n"
            + "        </attribute>\n"
            + "    </attributes>\n"
            + "    <typeCode>PFL</typeCode>\n"
            + "    <typeDescription>Test user profile XML</typeDescription>\n"
            + "    <description/>\n"
            + "    <groups/>\n"
            + "    <mainGroup/>\n"
            + "</userProfile>";

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
                .andExpect(jsonPath("result.items.size", is("2")))
                .andExpect(jsonPath("result.items.item[0]", is("admin")))
                .andExpect(jsonPath("result.items.item[1]", is("user1")))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/core:userProfile", "/core:userProfile.json"})
    void shouldPostUserProfilesJson(String path) throws Throwable {
        mockPostUserProfile();

        ResultActions result = mockMvc
                .perform(post("/legacy/en" + path)
                        .content(JSON_REQUEST_BODY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken));
        result
                .andExpect(jsonPath("result", is("SUCCESS")))
                .andExpect(status().isOk());

        ArgumentCaptor<JAXBUserProfile> userProfileCaptor = ArgumentCaptor.forClass(JAXBUserProfile.class);
        Mockito.verify(apiUserProfileInterface).addUserProfile(userProfileCaptor.capture());

        JAXBUserProfile userProfile = userProfileCaptor.getValue();
        Assertions.assertEquals("email@example.com", userProfile.getMail());
        Assertions.assertEquals("TestProfile", ((JAXBTextAttribute) userProfile.getAttributes().get(0)).getText());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/core:userProfile", "/core:userProfile.xml"})
    void shouldPostUserProfilesXml(String path) throws Throwable {
        mockPostUserProfile();

        ResultActions result = mockMvc
                .perform(post("/legacy/en" + path)
                        .content(XML_REQUEST_BODY)
                        .contentType(MediaType.APPLICATION_XML)
                        .accept(MediaType.APPLICATION_XML)
                        .header("Authorization", "Bearer " + accessToken));
        result
                .andExpect(xpath("/response/result").string("SUCCESS"))
                .andExpect(status().isOk());

        ArgumentCaptor<JAXBUserProfile> userProfileCaptor = ArgumentCaptor.forClass(JAXBUserProfile.class);
        Mockito.verify(apiUserProfileInterface).addUserProfile(userProfileCaptor.capture());

        JAXBUserProfile userProfile = userProfileCaptor.getValue();
        Assertions.assertEquals("email@example.com", userProfile.getMail());
        Assertions.assertEquals("TestProfile", ((JAXBTextAttribute) userProfile.getAttributes().get(0)).getText());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/core:userProfile", "/core:userProfile.json"})
    void shouldPutUserProfilesJson(String path) throws Throwable {
        mockPutUserProfile();

        ResultActions result = mockMvc
                .perform(put("/legacy/en" + path)
                        .content(JSON_REQUEST_BODY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken));
        result
                .andExpect(jsonPath("result", is("SUCCESS")))
                .andExpect(status().isOk());

        ArgumentCaptor<JAXBUserProfile> userProfileCaptor = ArgumentCaptor.forClass(JAXBUserProfile.class);
        Mockito.verify(apiUserProfileInterface).updateUserProfile(userProfileCaptor.capture());

        JAXBUserProfile userProfile = userProfileCaptor.getValue();
        Assertions.assertEquals("email@example.com", userProfile.getMail());
        Assertions.assertEquals("TestProfile", ((JAXBTextAttribute) userProfile.getAttributes().get(0)).getText());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/core:userProfile", "/core:userProfile.xml"})
    void shouldPutUserProfilesXml(String path) throws Throwable {
        mockPutUserProfile();

        ResultActions result = mockMvc
                .perform(put("/legacy/en" + path)
                        .content(XML_REQUEST_BODY)
                        .contentType(MediaType.APPLICATION_XML)
                        .accept(MediaType.APPLICATION_XML)
                        .header("Authorization", "Bearer " + accessToken));
        result
                .andExpect(xpath("/response/result").string("SUCCESS"))
                .andExpect(status().isOk());

        ArgumentCaptor<JAXBUserProfile> userProfileCaptor = ArgumentCaptor.forClass(JAXBUserProfile.class);
        Mockito.verify(apiUserProfileInterface).updateUserProfile(userProfileCaptor.capture());

        JAXBUserProfile userProfile = userProfileCaptor.getValue();
        Assertions.assertEquals("email@example.com", userProfile.getMail());
        Assertions.assertEquals("TestProfile", ((JAXBTextAttribute) userProfile.getAttributes().get(0)).getText());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/core:userProfile", "/core:userProfile.json"})
    void shouldDeleteUserProfileJson(String path) throws Throwable {
        mockDeleteUserProfile();

        ResultActions result = mockMvc
                .perform(delete("/legacy/en" + path)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken));
        result
                .andExpect(jsonPath("result", is("SUCCESS")))
                .andExpect(status().isOk());

        Mockito.verify(apiUserProfileInterface).deleteUserProfile(Mockito.any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/core:userProfile", "/core:userProfile.xml"})
    void shouldDeleteUserProfileXml(String path) throws Throwable {
        mockDeleteUserProfile();

        ResultActions result = mockMvc
                .perform(delete("/legacy/en" + path)
                        .accept(MediaType.APPLICATION_XML)
                        .header("Authorization", "Bearer " + accessToken));
        result
                .andExpect(xpath("/response/result").string("SUCCESS"))
                .andExpect(status().isOk());

        Mockito.verify(apiUserProfileInterface).deleteUserProfile(Mockito.any());
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

    private void mockPostUserProfile() throws Throwable {
        ApiMethod apiMethod = mockApiMethod(ApiMethod.HttpMethod.POST, null, "core:userProfile");
        Mockito.when(apiMethod.getSpringBeanMethod()).thenReturn("addUserProfile");
        Mockito.when(apiMethod.getRequiredPermission()).thenReturn("editUserProfile");
        Mockito.when(apiMethod.getExpectedType()).thenReturn(JAXBUserProfile.class);
    }

    private void mockPutUserProfile() throws Throwable {
        ApiMethod apiMethod = mockApiMethod(ApiMethod.HttpMethod.PUT, null, "core:userProfile");
        Mockito.when(apiMethod.getSpringBeanMethod()).thenReturn("updateUserProfile");
        Mockito.when(apiMethod.getRequiredPermission()).thenReturn("editUserProfile");
        Mockito.when(apiMethod.getExpectedType()).thenReturn(JAXBUserProfile.class);
    }

    private void mockDeleteUserProfile() throws Throwable {
        ApiMethod apiMethod = mockApiMethod(HttpMethod.DELETE, null, "core:userProfile");
        Mockito.when(apiMethod.getSpringBeanMethod()).thenReturn("deleteUserProfile");
        Mockito.when(apiMethod.getRequiredPermission()).thenReturn("editUserProfile");
    }

    @Override
    protected ApiMethod mockApiMethod(ApiMethod.HttpMethod method, String namespace, String resourceName)
            throws Throwable {
        ApiMethod apiMethod = super.mockApiMethod(method, namespace, resourceName);
        Mockito.when(apiMethod.getSpringBean()).thenReturn("ApiUserProfileInterface");
        return apiMethod;
    }
}
