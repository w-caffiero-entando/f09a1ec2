package org.entando.entando.aps.system.services.controller.executor;

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.Widget;
import com.agiletec.aps.tags.util.IFrameDecoratorContainer;
import freemarker.template.Template;
import freemarker.template.TemplateModel;
import java.util.ArrayList;
import java.util.List;
import org.entando.entando.aps.system.services.guifragment.GuiFragment;
import org.entando.entando.aps.system.services.guifragment.IGuiFragmentManager;
import org.entando.entando.aps.system.services.widgettype.IWidgetTypeManager;
import org.entando.entando.aps.system.services.widgettype.WidgetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(MockitoExtension.class)
class WidgetExecutorServiceTest {

    private static final String PARENT_WIDGET_CODE = "parent_widget";
    private static final String WIDGET_CODE = "my_widget";

    @Mock
    private IWidgetTypeManager widgetTypeManager;
    @Mock
    private IGuiFragmentManager guiFragmentManager;

    @Mock
    private RequestContext reqCtx;
    @Mock
    private WebApplicationContext wac;

    @InjectMocks
    private WidgetExecutorService service;

    private Widget widget;

    @BeforeEach
    void setUp() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Mockito.when(reqCtx.getRequest()).thenReturn(request);
        request.getServletContext().setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
        Mockito.when(wac.getBean(SystemConstants.WIDGET_TYPE_MANAGER)).thenReturn(widgetTypeManager);
        Mockito.when(wac.getBean(SystemConstants.GUI_FRAGMENT_MANAGER)).thenReturn(guiFragmentManager);

        WidgetType parentType = new WidgetType();
        parentType.setMainGroup(Group.FREE_GROUP_NAME);
        parentType.setCode(PARENT_WIDGET_CODE);

        WidgetType widgetType = new WidgetType();
        widgetType.setMainGroup(Group.FREE_GROUP_NAME);
        widgetType.setCode(WIDGET_CODE);
        widgetType.setParentType(parentType);

        GuiFragment guiFragment = new GuiFragment();
        guiFragment.setWidgetTypeCode(PARENT_WIDGET_CODE);
        guiFragment.setCode("my_gui_fragment");
        guiFragment.setDefaultGui("<p>test</p>");

        Mockito.when(widgetTypeManager.getWidgetType(WIDGET_CODE)).thenReturn(widgetType);
        Mockito.when(guiFragmentManager.getUniqueGuiFragmentByWidgetType(PARENT_WIDGET_CODE)).thenReturn(guiFragment);

        widget = new Widget();
        widget.setTypeCode(WIDGET_CODE);

        ExecutorBeanContainer ebc = Mockito.mock(ExecutorBeanContainer.class);
        Mockito.when(ebc.getTemplateModel()).thenReturn(Mockito.mock(TemplateModel.class));
        Mockito.when(reqCtx.getExtraParam(SystemConstants.EXTRAPAR_EXECUTOR_BEAN_CONTAINER)).thenReturn(ebc);
        Mockito.when(reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_FRAME)).thenReturn(0);
        Mockito.when(reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_WIDGET)).thenReturn(widget);
    }

    @Test
    void testBuildWidgetOutputLogicWidget() throws Exception {

        List<IFrameDecoratorContainer> decorators = new ArrayList<>();

        try (MockedConstruction<Template> construction = Mockito.mockConstruction(Template.class)) {
            service.buildWidgetOutput(reqCtx, widget, decorators);
            Template template = construction.constructed().get(0);
            Mockito.verify(template).process(Mockito.any(), Mockito.any());
        }
    }

    @Test
    void testParallelBuildWidgetsOutput() throws Exception {
        ReflectionTestUtils.setField(service, "parallelWidgetRender", true);

        IPage page = Mockito.mock(IPage.class);
        Mockito.when(page.getWidgets()).thenReturn(new Widget[]{widget});

        Mockito.when(wac.getBeanNamesForType(IFrameDecoratorContainer.class)).thenReturn(new String[]{});

        try (MockedConstruction<Template> construction = Mockito.mockConstruction(Template.class)) {
            service.buildWidgetsOutput(reqCtx, page, new String[]{null});
            Template template = construction.constructed().get(0);
            Mockito.verify(template).process(Mockito.any(), Mockito.any());
        }
    }
}
