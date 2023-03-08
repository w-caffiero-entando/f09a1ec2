package org.entando.entando.aps.system.services.api.server;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import com.agiletec.aps.system.common.entity.model.attribute.JAXBTextAttribute;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.plugins.jacms.aps.system.services.content.model.SymbolicLink;
import com.agiletec.plugins.jacms.aps.system.services.content.model.attribute.JAXBLinkAttribute;
import com.agiletec.plugins.jacms.aps.system.services.content.model.attribute.JAXBLinkValue;
import com.agiletec.plugins.jacms.aps.system.services.content.model.attribute.JAXBResourceAttribute;
import com.agiletec.plugins.jacms.aps.system.services.content.model.attribute.JAXBResourceValue;
import java.util.List;
import java.util.Map;
import org.entando.entando.aps.system.services.api.DefaultJsonTypesProvider;
import org.entando.entando.aps.system.services.api.JsonTypesProvider;
import org.entando.entando.aps.system.services.api.model.ApiMethod;
import org.entando.entando.plugins.jacms.aps.system.services.api.ApiContentInterface;
import org.entando.entando.plugins.jacms.aps.system.services.api.CmsJsonTypesProvider;
import org.entando.entando.plugins.jacms.aps.system.services.api.model.JAXBContent;
import org.entando.entando.plugins.jacms.aps.system.services.api.response.ContentResponse;
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

class ApiRestServerContentTest extends BaseLegacyApiTest {

    @Mock
    private ApiContentInterface apiContentInterface;

    private String accessToken;

    @Override
    protected List<JsonTypesProvider> getJsonTypesProviders() {
        return List.of(new DefaultJsonTypesProvider(), new CmsJsonTypesProvider());
    }

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        Mockito.when(beanFactory.getBean("jacmsApiContentInterface")).thenReturn(apiContentInterface);
        UserDetails user = new OAuth2TestUtils.UserBuilder("jack_bauer", "0x24").grantedToRoleAdmin().build();
        accessToken = mockOAuthInterceptor(user);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/content", "/content.json"})
    void shouldGetContentJson(String path) throws Throwable {
        mockGetContent();

        ResultActions result = mockMvc
                .perform(get("/legacy/en/jacms" + path)
                        .param("id", "NWS4")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken));
        result
                .andExpect(jsonPath("result.item.attribute", hasSize(3)))
                .andExpect(jsonPath("result.item.attribute[0].type", is("Text")))
                .andExpect(jsonPath("result.item.attribute[0].classType", is("JAXBTextAttribute")))
                .andExpect(jsonPath("result.item.attribute[0].name", is("title")))
                .andExpect(jsonPath("result.item.attribute[0].names.en", is("title")))
                .andExpect(jsonPath("result.item.attribute[0].value", is("My title")))
                .andExpect(jsonPath("result.item.attribute[1].type", is("Link")))
                .andExpect(jsonPath("result.item.attribute[1].classType", is("JAXBLinkAttribute")))
                .andExpect(jsonPath("result.item.attribute[1].link.text", is("Entando website")))
                .andExpect(jsonPath("result.item.attribute[1].link.symbolicLink.symbolicDestination",
                        is("#!U;http://www.entando.com/!#")))
                .andExpect(jsonPath("result.item.attribute[2].type", is("Image")))
                .andExpect(jsonPath("result.item.attribute[2].classType", is("JAXBResourceAttribute")))
                .andExpect(jsonPath("result.item.attribute[2].name", is("image")))
                .andExpect(jsonPath("result.item.attribute[2].resource.resourceId", is("entandoAtWork")))
                .andExpect(jsonPath("result.item.attribute[2].resource.text", is("Entando at Work")))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/content", "/content.xml"})
    void shouldGetContentXml(String path) throws Throwable {
        mockGetContent();

        ResultActions result = mockMvc
                .perform(get("/legacy/en/jacms" + path)
                        .param("id", "NWS4")
                        .accept(MediaType.APPLICATION_XML)
                        .header("Authorization", "Bearer " + accessToken));
        result
                .andExpect(xpath("/response/result/item/attributes/attribute[1]/type").string("Text"))
                .andExpect(
                        xpath("/response/result/item/attributes/attribute[1]/@classType").string("JAXBTextAttribute"))
                .andExpect(xpath("/response/result/item/attributes/attribute[1]/name").string("title"))
                .andExpect(xpath("/response/result/item/attributes/attribute[1]/names/en").string("title"))
                .andExpect(xpath("/response/result/item/attributes/attribute[1]/value").string("My title"))
                .andExpect(xpath("/response/result/item/attributes/attribute[2]/type").string("Link"))
                .andExpect(
                        xpath("/response/result/item/attributes/attribute[2]/@classType").string("JAXBLinkAttribute"))
                .andExpect(xpath("/response/result/item/attributes/attribute[2]/link/text").string("Entando website"))
                .andExpect(xpath("/response/result/item/attributes/attribute[2]/link/symbolicLink/symbolicDestination")
                        .string("#!U;http://www.entando.com/!#"))
                .andExpect(xpath("/response/result/item/attributes/attribute[3]/type").string("Image"))
                .andExpect(xpath("/response/result/item/attributes/attribute[3]/@classType")
                        .string("JAXBResourceAttribute"))
                .andExpect(xpath("/response/result/item/attributes/attribute[3]/name").string("image"))
                .andExpect(xpath("/response/result/item/attributes/attribute[3]/resource/resourceId")
                        .string("entandoAtWork"))
                .andExpect(xpath("/response/result/item/attributes/attribute[3]/resource/text")
                        .string("Entando at Work"))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/content", "/content.json"})
    void shouldPostContentJson(String path) throws Throwable {
        mockPostContent();

        String requestPayload = "{\n"
                + "    \"category\": null,\n"
                + "    \"typeCode\": \"NWS\",\n"
                + "    \"mainGroup\": \"free\",\n"
                + "    \"typeDescription\": \"News\",\n"
                + "    \"description\": \"Test news\",\n"
                + "    \"group\": [\n"
                + "        \"free\"\n"
                + "     ],\n"
                + "    \"attribute\": [\n"
                + "        {\n"
                + "            \"classType\" : \"JAXBTextAttribute\",\n"
                + "            \"type\": \"Text\",\n"
                + "            \"value\": \"My title\",\n"
                + "            \"names\": {\n"
                + "                \"en\": \"title\"\n"
                + "            },\n"
                + "            \"name\": \"title\",\n"
                + "            \"description\": null,\n"
                + "            \"role\": [\n"
                + "                \"jacms:title\"\n"
                + "            ]\n"
                + "        },\n"
                + "        {\n"
                + "            \"classType\" : \"JAXBLinkAttribute\",\n"
                + "            \"type\": \"Link\",\n"
                + "            \"link\" : {\n"
                + "                \"text\" : \"Entando website\",\n"
                + "                \"symbolicLink\" : {\n"
                + "                    \"contentDestination\" : null,\n"
                + "                    \"pageDestination\" : null,\n"
                + "                    \"resourceDestination\" : null,\n"
                + "                    \"symbolicDestination\" : \"#!U;http://www.entando.com/!#\"\n"
                + "                }\n"
                + "            },\n"
                + "            \"names\": {\n"
                + "                \"en\": \"link\"\n"
                + "            },\n"
                + "            \"name\": \"link\",\n"
                + "            \"description\": null,\n"
                + "            \"role\": null\n"
                + "        },\n"
                + "        {\n"
                + "            \"classType\" : \"JAXBResourceAttribute\",\n"
                + "            \"type\" : \"Image\",\n"
                + "            \"resource\" : {\n"
                + "                  \"text\" : \"Entando at Work\",\n"
                + "                  \"resourceId\" : \"entandoAtWork\",\n"
                + "                  \"restResourcePath\" : null\n"
                + "            },"
                + "            \"name\": \"image\",\n"
                + "            \"names\": {\n"
                + "                \"en\": \"image\"\n"
                + "            }\n"
                + "        }\n"
                + "     ]\n"
                + "}";

        ResultActions result = mockMvc
                .perform(post("/legacy/en/jacms" + path)
                        .content(requestPayload)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken));
        result
                .andExpect(jsonPath("result", is("SUCCESS")))
                .andExpect(status().isOk());

        verifyAddedContent();
    }

    @ParameterizedTest
    @ValueSource(strings = {"/content", "/content.xml"})
    void shouldPostContentXml(String path) throws Throwable {
        mockPostContent();

        String requestPayload = "<content>\n"
                + "    <attributes>\n"
                + "        <attribute classType=\"JAXBTextAttribute\">\n"
                + "            <value>My title</value>\n"
                + "            <name>title</name>\n"
                + "            <names>\n"
                + "                <en>title</en>\n"
                + "            </names>\n"
                + "            <type>Text</type>\n"
                + "            <description/>\n"
                + "            <roles>\n"
                + "                <role>jacms:title</role>\n"
                + "            </roles>\n"
                + "        </attribute>\n"
                + "        <attribute classType=\"JAXBLinkAttribute\">\n"
                + "            <type>Link</type>\n"
                + "            <name>link</name>\n"
                + "            <names>\n"
                + "                <en>link</en>\n"
                + "            </names>\n"
                + "            <link>\n"
                + "                <text>Entando website</text>\n"
                + "                <symbolicLink>\n"
                + "                    <symbolicDestination>#!U;http://www.entando.com/!#</symbolicDestination>\n"
                + "                </symbolicLink>\n"
                + "            </link>\n"
                + "        </attribute>\n"
                + "        <attribute classType=\"JAXBResourceAttribute\">\n"
                + "            <resource>\n"
                + "                <text>Entando at Work</text>\n"
                + "                <resourceId>entandoAtWork</resourceId>\n"
                + "                <restResourcePath/>\n"
                + "            </resource>\n"
                + "            <name>image</name>\n"
                + "            <names>\n"
                + "                <en>image</en>\n"
                + "            </names>\n"
                + "            <type>Image</type>\n"
                + "            <description/>\n"
                + "            <value/>\n"
                + "        </attribute>\n"
                + "    </attributes>\n"
                + "    <typeCode>NWS</typeCode>\n"
                + "    <mainGroup>free</mainGroup>\n"
                + "    <typeDescription>News</typeDescription>\n"
                + "    <description>Test news</description>\n"
                + "    <groups>\n"
                + "        <group>free</group>\n"
                + "    </groups>\n"
                + "</content>";

        ResultActions result = mockMvc
                .perform(post("/legacy/en/jacms" + path)
                        .content(requestPayload)
                        .contentType(MediaType.APPLICATION_XML)
                        .accept(MediaType.APPLICATION_XML)
                        .header("Authorization", "Bearer " + accessToken));
        result
                .andExpect(xpath("/response/result").string("SUCCESS"))
                .andExpect(status().isOk());

        verifyAddedContent();
    }

    private void verifyAddedContent() throws Throwable {
        ArgumentCaptor<JAXBContent> contentCaptor = ArgumentCaptor.forClass(JAXBContent.class);
        Mockito.verify(apiContentInterface).addContent(contentCaptor.capture(), Mockito.any());

        JAXBContent content = contentCaptor.getValue();
        Assertions.assertEquals(3, content.getAttributes().size());
        Assertions.assertEquals(JAXBTextAttribute.class, content.getAttributes().get(0).getClass());
        JAXBTextAttribute textAttribute = (JAXBTextAttribute) content.getAttributes().get(0);
        Assertions.assertEquals("My title", textAttribute.getText());
        Assertions.assertEquals(JAXBLinkAttribute.class, content.getAttributes().get(1).getClass());
        JAXBLinkAttribute linkAttribute = (JAXBLinkAttribute) content.getAttributes().get(1);
        Assertions.assertEquals("http://www.entando.com/", linkAttribute.getLinkValue().getSymbolicLink().getUrlDest());
        Assertions.assertEquals(JAXBResourceAttribute.class, content.getAttributes().get(2).getClass());
        JAXBResourceAttribute resourceAttribute = (JAXBResourceAttribute) content.getAttributes().get(2);
        Assertions.assertEquals("image", resourceAttribute.getName());
        Assertions.assertEquals("Entando at Work", resourceAttribute.getResource().getText());
        Assertions.assertEquals("entandoAtWork", resourceAttribute.getResource().getResourceId());
    }

    private void mockGetContent() throws Throwable {
        ApiMethod apiMethod = mockApiMethod(ApiMethod.HttpMethod.GET, "jacms", "content");
        Mockito.when(apiMethod.getSpringBeanMethod()).thenReturn("getContent");
        Mockito.when(apiMethod.getResponseClassName()).thenReturn(ContentResponse.class.getCanonicalName());

        JAXBContent content = new JAXBContent();

        JAXBTextAttribute titleAttribute = new JAXBTextAttribute();
        titleAttribute.setType("Text");
        titleAttribute.setName("title");
        titleAttribute.setText("My title");
        titleAttribute.setNames(Map.of("en", "title"));
        content.getAttributes().add(titleAttribute);

        JAXBLinkAttribute linkAttribute = new JAXBLinkAttribute();
        linkAttribute.setType("Link");
        JAXBLinkValue linkValue = new JAXBLinkValue();
        SymbolicLink symbolicLink = new SymbolicLink();
        symbolicLink.setDestinationToUrl("http://www.entando.com/");
        linkValue.setSymbolicLink(symbolicLink);
        linkValue.setText("Entando website");
        linkAttribute.setLinkValue(linkValue);
        content.getAttributes().add(linkAttribute);

        JAXBResourceAttribute resourceAttribute = new JAXBResourceAttribute();
        resourceAttribute.setType("Image");
        resourceAttribute.setName("image");
        JAXBResourceValue resourceValue = new JAXBResourceValue();
        resourceValue.setResourceId("entandoAtWork");
        resourceValue.setText("Entando at Work");
        resourceAttribute.setResource(resourceValue);
        content.getAttributes().add(resourceAttribute);

        Mockito.when(apiContentInterface.getContent(Mockito.any())).thenReturn(content);
    }

    private void mockPostContent() throws Throwable {
        ApiMethod apiMethod = mockApiMethod(ApiMethod.HttpMethod.POST, "jacms", "content");
        Mockito.when(apiMethod.getSpringBeanMethod()).thenReturn("addContent");
        Mockito.when(apiMethod.getExpectedType()).thenReturn(JAXBContent.class);
    }

    @Override
    protected ApiMethod mockApiMethod(ApiMethod.HttpMethod method, String namespace, String resourceName)
            throws Throwable {
        ApiMethod apiMethod = super.mockApiMethod(method, namespace, resourceName);
        Mockito.when(apiMethod.getSpringBean()).thenReturn("jacmsApiContentInterface");
        return apiMethod;
    }
}
