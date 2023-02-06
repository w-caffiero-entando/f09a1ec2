package org.entando.entando.plugins.jacms.apsadmin.content.executor;

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.Widget;
import com.agiletec.aps.util.ApsProperties;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.apsadmin.content.ContentActionConstants;
import org.entando.entando.aps.system.services.widgettype.IWidgetTypeManager;
import org.entando.entando.aps.system.services.widgettype.WidgetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(MockitoExtension.class)
class PreviewWidgetExecutorAspectTest {

    private static final String WIDGET_CODE = "my_widget";

    @Mock
    private RequestContext reqCtx;
    @Mock
    private IWidgetTypeManager widgetTypeManager;
    @Mock
    private IPage currentPage;
    @Mock
    private Content content;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        Mockito.when(reqCtx.getRequest()).thenReturn(request);
    }

    @Test
    void testNullContentOnSession() {
        PreviewWidgetExecutorAspect aspect = new PreviewWidgetExecutorAspect();
        aspect.checkContentPreview(reqCtx);
        Mockito.verify(reqCtx, Mockito.never()).addExtraParam(Mockito.any(), Mockito.any());
    }

    @Test
    void testCheckContentPreviewNullWidget() {
        PreviewWidgetExecutorAspect aspect = new PreviewWidgetExecutorAspect();
        mockContentOnSession();
        mockGetWidgetTypeManager();
        mockWidgetsOnPage(null);
        aspect.checkContentPreview(reqCtx);
        Mockito.verify(reqCtx, Mockito.never()).addExtraParam(Mockito.any(), Mockito.any());
    }

    @Test
    void testCheckContentPreviewWrongWidgetTypeAction() {
        PreviewWidgetExecutorAspect aspect = new PreviewWidgetExecutorAspect();
        mockContentOnSession();
        mockGetWidgetTypeManager();

        Widget widget = new Widget();
        widget.setTypeCode(WIDGET_CODE);
        mockWidgetsOnPage(widget);

        WidgetType type = new WidgetType();
        type.setCode(WIDGET_CODE);
        type.setAction("wrong");
        Mockito.when(widgetTypeManager.getWidgetType(WIDGET_CODE)).thenReturn(type);

        aspect.checkContentPreview(reqCtx);
        Mockito.verify(reqCtx, Mockito.never()).addExtraParam(Mockito.any(), Mockito.any());
    }

    @Test
    void testCheckContentPreviewValidPageCode() {
        PreviewWidgetExecutorAspect aspect = new PreviewWidgetExecutorAspect();
        mockContentOnSession();
        mockGetWidgetTypeManager();

        Widget widget = new Widget();
        widget.setTypeCode(WIDGET_CODE);
        mockWidgetsOnPage(widget);
        mockValidWidgetType();

        Mockito.when(currentPage.getCode()).thenReturn("pageCode");
        Mockito.when(content.getViewPage()).thenReturn("pageCode");
        Mockito.when(reqCtx.getResponse()).thenReturn(response);
        Mockito.when(reqCtx.getExtraParam("ShowletOutput")).thenReturn(new String[1]);

        aspect.checkContentPreview(reqCtx);

        Mockito.verify(reqCtx).addExtraParam(Mockito.eq(SystemConstants.EXTRAPAR_CURRENT_WIDGET), Mockito.any());
        Mockito.verify(reqCtx).addExtraParam(Mockito.eq(SystemConstants.EXTRAPAR_CURRENT_FRAME), Mockito.eq(0));
    }

    @Test
    void testCheckContentPreviewValidWidgetConfig() {
        PreviewWidgetExecutorAspect aspect = new PreviewWidgetExecutorAspect();
        mockContentOnSession();
        mockGetWidgetTypeManager();

        Widget widget = new Widget();
        widget.setTypeCode(WIDGET_CODE);
        ApsProperties widgetConfig = new ApsProperties();
        widgetConfig.setProperty("contentId", "NWS12");
        widget.setConfig(widgetConfig);
        mockWidgetsOnPage(widget);
        mockValidWidgetType();

        Mockito.when(currentPage.getCode()).thenReturn("pageCode");
        Mockito.when(content.getId()).thenReturn("NWS12");
        Mockito.when(reqCtx.getResponse()).thenReturn(response);
        Mockito.when(reqCtx.getExtraParam("ShowletOutput")).thenReturn(new String[1]);

        aspect.checkContentPreview(reqCtx);

        Mockito.verify(reqCtx).addExtraParam(Mockito.eq(SystemConstants.EXTRAPAR_CURRENT_WIDGET), Mockito.any());
        Mockito.verify(reqCtx).addExtraParam(Mockito.eq(SystemConstants.EXTRAPAR_CURRENT_FRAME), Mockito.eq(0));
    }

    private void mockContentOnSession() {
        request.setParameter("contentOnSessionMarker", "marker");
        String contentAttribute = ContentActionConstants.SESSION_PARAM_NAME_CURRENT_CONTENT_PREXIX + "marker";
        request.getSession().setAttribute(contentAttribute, content);
    }

    private void mockGetWidgetTypeManager() {
        WebApplicationContext wac = Mockito.mock(WebApplicationContext.class);
        Mockito.when(wac.getBean(SystemConstants.WIDGET_TYPE_MANAGER)).thenReturn(widgetTypeManager);
        request.getServletContext().setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
    }

    private void mockWidgetsOnPage(Widget widget) {
        Mockito.when(reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE)).thenReturn(currentPage);
        Mockito.when(currentPage.getWidgets()).thenReturn(new Widget[]{widget});
    }

    private void mockValidWidgetType() {
        WidgetType type = new WidgetType();
        type.setCode(WIDGET_CODE);
        type.setAction("viewerConfig");
        Mockito.when(widgetTypeManager.getWidgetType(WIDGET_CODE)).thenReturn(type);
    }
}
