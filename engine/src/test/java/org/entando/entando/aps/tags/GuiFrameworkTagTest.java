/*
 * Copyright 2020-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.aps.tags;

import static javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE;
import static org.mockito.Mockito.when;

import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import org.entando.entando.aps.system.services.controller.executor.ExecutorBeanContainer;
import org.entando.entando.aps.system.services.guifragment.IGuiFragmentManager;
import org.entando.entando.test_utils.UnitTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GuiFrameworkTagTest {
    
    @Mock
    private PageContext pageContext;
    @Mock
    private ServletRequest servletRequest;
    @Mock
    private RequestContext reqCtx;
    @Mock
    private IGuiFragmentManager guiFragmentManager;
    @Mock
    private JspWriter writer;
    @InjectMocks
    private GuiFragmentTag guiFragmentTag;
    
    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.lenient().when(pageContext.getRequest()).thenReturn(this.servletRequest);
        Mockito.lenient().when(servletRequest.getAttribute(RequestContext.REQCTX)).thenReturn(this.reqCtx);
        Mockito.lenient().when(pageContext.getOut()).thenReturn(this.writer);
        this.guiFragmentTag.release();
    }
    
    @Test
    void shouldOkWithNotNullParallel() throws Throwable {
        Map<String,String> envsOrig = System.getenv().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<String,String> envs = (HashMap<String, String>) envsOrig.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        envs.put("PARALLEL_WIDGET_RENDER","true");
        UnitTestUtils.setEnv(envs);

        try (MockedStatic<ApsWebApplicationUtils> apsWebApplication = Mockito.mockStatic(ApsWebApplicationUtils.class)) {
            apsWebApplication.when(
                            () -> ApsWebApplicationUtils.getBean(SystemConstants.GUI_FRAGMENT_MANAGER, this.pageContext))
                    .thenReturn(guiFragmentManager);
            int result = this.guiFragmentTag.doStartTag();
            Assertions.assertEquals(0, result);
        }

        UnitTestUtils.setEnv(envsOrig);
    }

}
